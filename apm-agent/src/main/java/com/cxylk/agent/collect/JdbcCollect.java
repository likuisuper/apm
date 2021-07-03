package com.cxylk.agent.collect;

import com.cxylk.agent.ApmContext;
import com.cxylk.agent.ICollect;
import com.cxylk.agent.model.JdbcStatistics;
import javassist.*;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * @Classname JdbcCollect
 * @Description jdbc性能采集。jdbc执行过程：sql请求->jdbc Template或ORM->JDBC->Data Source->Driver->数据库
 *              不对使用场景做假设，所以对JDBC API进行动态代理拦截
 *              底层机制：基于对指定的Driver做插桩实现，完成对Connection对象的代理，再基于代理Connection完成对Statement对象代理获取SQL指标数据
 * @Author likui
 * @Date 2021/6/19 16:24
 **/
public class JdbcCollect extends AbstractByteTransformCollect implements ICollect {
    //需要拦截的目标类
//    String target="com.mysql.cj.jdbc.NonRegisteringDriver";
    //拦截pgsql
//    String target="org.postgresql.Driver";

    public static JdbcCollect INSTANCE;

    private ApmContext apmContext;

    public JdbcCollect(ApmContext apmContext, Instrumentation instrumentation){
        super(instrumentation);
        INSTANCE=this;
        this.apmContext=apmContext;
    }

    private final static String[] CONNECTION_AGENT_METHODS = new String[]{"prepareStatement"};
    private final static String[] PREPARED_STATEMENT_METHODS = new String[]{"execute", "executeUpdate", "executeQuery"};
    private static final String beginSrc;
    private static final String endSrc;
    private static final String errorSrc;

    static {
        // connect
        beginSrc = "com.cxylk.agent.collect.JdbcCollect inst=com.cxylk.agent.collect.JdbcCollect.INSTANCE;";
        errorSrc = "inst.error(null,e);";
        endSrc = "result=inst.proxyConnection((java.sql.Connection)result);";
    }


    public void sendStatistics(JdbcStatistics stat) {

    }

    /**
     * 生成Connection代理。参数connection是connect方法的返回值
     */
    public Connection proxyConnection(Connection connection){
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
    public PreparedStatement proxyStatement(PreparedStatement statement,JdbcStatistics statistics){
        return (PreparedStatement) Proxy.newProxyInstance(JdbcCollect.class.getClassLoader(),
                new Class[]{PreparedStatement.class},
                new StatementHandler(statement,statistics));
    }

    @Override
    public byte[] transform(ClassLoader loader, String className) throws Exception {
        if ("com.mysql.cj.jdbc.NonRegisteringDriver".equals(className)||"org.postgresql.Driver".equals(className)) {
            CtClass ctclass = super.toCtClass(loader, className);
            AgentByteBuild byteLoad = new AgentByteBuild(className, loader, ctclass);
            CtMethod connectMethod = ctclass.getMethod("connect", "(Ljava/lang/String;Ljava/util/Properties;)Ljava/sql/Connection;");
            AgentByteBuild.MethodSrcBuild build = new AgentByteBuild.MethodSrcBuild();
            build.setBeginSrc(beginSrc);
            build.setErrorSrc(errorSrc);
            build.setEndSrc(endSrc);
            byteLoad.updateMethod(connectMethod, build);
            return byteLoad.toBytecode();
        }
        return null;
    }

    /**
     * 具体对Connection的拦截实现
     */
    public class ConnectionHandler implements InvocationHandler{
        private final Connection connection;

        private ConnectionHandler(Connection connection) {
            this.connection = connection;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            boolean isTargetMethod = false;
            for (String agentm : CONNECTION_AGENT_METHODS) {
                if (agentm.equals(method.getName())) {
                    isTargetMethod = true;
                }
            }
            Object result = null;
            JdbcStatistics jdbcStat = null;
            try {
                if (isTargetMethod) { // 获取PreparedStatement 开始统计
                    jdbcStat = (JdbcStatistics) begin(null, null);
                    jdbcStat.jdbcUrl = connection.getMetaData().getURL();
                    jdbcStat.sql = (String) args[0];
                }
                result = method.invoke(connection, args);
                // 代理 PreparedStatement
                if (isTargetMethod && result instanceof PreparedStatement) {
                    PreparedStatement ps = (PreparedStatement) result;
                    result = proxyStatement(ps, jdbcStat);
                }
            } catch (Throwable e) {
                JdbcCollect.this.error(jdbcStat, e);
                JdbcCollect.this.end(jdbcStat);
                throw e;
            }
            return result;
        }
    }

    /**
     * 具体对PreparedStatement的拦截实现
     */
    public class StatementHandler implements InvocationHandler{
        private final PreparedStatement statement;
        private final JdbcStatistics jdbcStat;

        public StatementHandler(PreparedStatement statement, JdbcStatistics jdbcStat) {
            this.statement = statement;
            this.jdbcStat = jdbcStat;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            boolean isTargetMethod = false;
            for (String agentm : PREPARED_STATEMENT_METHODS) {
                if (agentm.equals(method.getName())) {
                    isTargetMethod = true;
                    break;
                }
            }
            //拦截赋值的set方法
            if(method.getName().startsWith("set")&&method.getParameterCount()==2){
                jdbcStat.params.add(new JdbcStatistics.ParamValues((Integer) args[0],args[1]));
            }

            Object result = null;
            try {
                result = method.invoke(statement, args);
            } catch (Throwable e) {
                if (isTargetMethod) {
                    JdbcCollect.this.error(jdbcStat, e);
                }
                throw e;
            } finally {
                if (isTargetMethod) {
                    JdbcCollect.this.end(jdbcStat);
                }
            }
            return result;
        }
    }

    public JdbcStatistics begin(String jdbcUrl,String sql){
        JdbcStatistics jdbcStatistics=new JdbcStatistics();
        jdbcStatistics.begin=System.currentTimeMillis();
        jdbcStatistics.setModelType("jdbc");
        return jdbcStatistics;
    }

    public void error(JdbcStatistics statistics,Throwable error){
        if (statistics != null) {
            statistics.error = error.getMessage();
            statistics.errorType = error.getClass().getName();
            if (error instanceof InvocationTargetException) {
                statistics.errorType = ((InvocationTargetException) error).getTargetException().getClass().getName();
                statistics.error = ((InvocationTargetException) error).getTargetException().getMessage();
            }
        }
    }

    public void end(JdbcStatistics statistics){
        statistics.end=System.currentTimeMillis();
        statistics.useTime = statistics.end- statistics.begin;
        if (statistics.jdbcUrl != null) {
            statistics.databaseName = getDbName(statistics.jdbcUrl);
        }
        this.apmContext.submitCollectResult(statistics);
    }

    private static String getDbName(String url) {
        int index = url.indexOf("?"); //$NON-NLS-1$
        if (index != -1) {
            String paramString = url.substring(index + 1, url.length());
            url = url.substring(0, index);
        }
        String dbName = url.substring(url.lastIndexOf("/") + 1);
        return dbName;
    }
}
