package com.cxylk.agent.collect;

import com.cxylk.agent.model.JdbcStatistics;
import javassist.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * @Classname JdbcCollect
 * @Description jdbc性能采集。jdbc执行过程：sql请求->jdbc Template或ORM->JDBC->Data Source->Driver->数据库
 *              不对使用场景做假设，所以对JDBC API进行动态代理拦截
 *              底层机制：基于对指定的Driver做插桩实现，完成对Connection对象的代理，再基于代理Connection完成对Statement对象代理获取SQL指标数据
 * @Author likui
 * @Date 2021/6/19 16:24
 **/
public class JdbcCollect {
    //需要拦截的目标类
    String target="com.mysql.cj.jdbc.NonRegisteringDriver";

    public CtClass buildClass(String className, ClassLoader classLoader) throws NotFoundException, CannotCompileException {
        if(!target.equals(className)){
            throw new RuntimeException("fail param");
        }
        ClassPool classPool=new ClassPool();
        classPool.insertClassPath(new LoaderClassPath(classLoader));
        CtClass ctClass = classPool.get(target);
        //获取目标类中的connect方法，对该方法进行拦截
        CtMethod oldMethod = ctClass.getMethod("connect", "(Ljava/lang/String;Ljava/util/Properties;)Ljava/sql/Connection;");
        CtMethod newMethod = CtNewMethod.copy(oldMethod, ctClass, null);
        newMethod.setName(newMethod.getName()+"$agent");
        ctClass.addMethod(newMethod);
        oldMethod.setBody(source);
        return ctClass;
    }

    /**
     * 生成Connection代理。参数connection是connect方法的返回值
     */
    public static Connection proxyConnection(Connection connection){
        return (Connection) Proxy.newProxyInstance(JdbcCollect.class.getClassLoader(),
                new Class[]{Connection.class},
                new ConnectionHandler(connection));
    }

    /**
     * 生成PreparedStatement对象代理
     * @param statement PreparedStatement对象
     * @param statistics Jdbc统计对象，这个对象是调用begin返回出来的，这样才能在发送异常和关闭的时候对要统计的方法关联起来，保证是一个方法
     * @return
     */
    public static PreparedStatement proxyStatement(PreparedStatement statement,JdbcStatistics statistics){
        return (PreparedStatement) Proxy.newProxyInstance(JdbcCollect.class.getClassLoader(),
                new Class[]{PreparedStatement.class},
                new StatementHandler(statement,statistics));
    }

    /**
     * 具体对Connection的拦截实现
     */
    public static class ConnectionHandler implements InvocationHandler{
        Connection target;

        public ConnectionHandler(Connection target){
            this.target=target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object result;
            //执行原有逻辑
            result = method.invoke(target, args);
            //对PrepareStatement方法进行代理
            if ("prepareStatement".equals(method.getName())) {
                PreparedStatement statement= (PreparedStatement) result;
                //执行begin方法。args[0]表示prepareStatement方法的第一个参数也就是sql
                JdbcStatistics statistics = begin(target.getMetaData().getURL(), (String) args[0]);
                //进一步代理
                result = proxyStatement(statement, statistics);
            }
            return result;
        }
    }

    /**
     * 具体对PreparedStatement的拦截实现
     */
    public static class StatementHandler implements InvocationHandler{
        //statement是和sql绑定的，用完一次就销毁了，所以不会出现线程安全问题
        Statement statement;
        //需要维护一个Jdbc统计信息，这样在出现异常和关闭的时候才能和要统计的方法关联起来
        JdbcStatistics statistics;
        public StatementHandler(Statement statement,JdbcStatistics statistics){
            this.statement=statement;
            this.statistics=statistics;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object result;
            try {
                try {
                    result = method.invoke(statement, args);
                }  catch (InvocationTargetException e) {
                    if("executeQuery".equals(method.getName())){
                        //当执行的方法是executeQuery出现异常时，进入error方法
                        error(e.getTargetException(),statistics);
                    }
                    throw e;
                }
            } catch (Throwable e) {
                throw e;
            }
            //如果调用了close方法，那么执行end方法
            if("close".equals(method.getName())){
                end(statistics);
            }
            return result;
        }
    }

    public static JdbcStatistics begin(String jdbcUrl,String sql){
        JdbcStatistics jdbcStatistics=new JdbcStatistics(sql,jdbcUrl);
        jdbcStatistics.setBeginTime(System.currentTimeMillis());
        return jdbcStatistics;
    }

    public static void error(Throwable error,JdbcStatistics statistics){
        statistics.setError(error.getMessage());
        System.out.println(statistics);
    }

    public static void end(JdbcStatistics statistics){
        statistics.setUseTime(System.currentTimeMillis()-statistics.getBeginTime());
        System.out.println(statistics);
    }

    final static String source = "{\n"
            + "        java.sql.Connection result=null;\n"
            + "       try {\n"
            + "            result=($w)connect$agent($$);\n" //执行原有逻辑
            + "			result=com.cxylk.agent.collect.JdbcCollect.proxyConnection(result);" // 封装代理connection
            + "        } catch (Throwable e) {\n"
            + "            throw e;\n"
            + "        }finally{\n"
            + "        }\n"
            + "        return ($r) result;\n" +
            "}\n";
}
