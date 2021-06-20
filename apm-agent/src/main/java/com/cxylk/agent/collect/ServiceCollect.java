package com.cxylk.agent.collect;

import com.alibaba.fastjson.JSON;
import com.cxylk.agent.model.ServiceStatistics;
import javassist.*;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;


/**
 * @Classname ServiceCollect
 * @Description 服务信息采集
 * @Author likui
 * @Date 2021/6/16 15:16
 **/
public class ServiceCollect {
    //目标包名
    private String targetPackage;

    public ServiceCollect(String targetPackage) {
        this.targetPackage = targetPackage;
    }

    public void transform(Instrumentation instrumentation){
        instrumentation.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                if(className==null){
                    return null;
                }
                if(!className.startsWith(targetPackage.replaceAll("\\.","/"))){
                    return null;
                }
                try {
                    return buildCtClass(loader,className.replace("/",".")).toBytecode();
                } catch (NotFoundException | CannotCompileException | IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    public CtClass buildCtClass(ClassLoader loader, String className) throws NotFoundException {
        ClassPool classPool=new ClassPool();
        //要装载的类可能不在当前类的classloader下
        classPool.insertClassPath(new LoaderClassPath(loader));
        CtClass ctClass = classPool.get(className);
        CtMethod[] declaredMethods = ctClass.getDeclaredMethods();
        for (CtMethod ctMethod : declaredMethods) {
            //屏蔽非公共方法
            if(!Modifier.isPublic(ctMethod.getModifiers())){
                continue;
            }
            //屏蔽静态方法
            if(Modifier.isStatic(ctMethod.getModifiers())){
                continue;
            }
            //屏蔽native方法
            if(Modifier.isNative(ctMethod.getModifiers())){
                continue;
            }
            try {
                buildMethod(ctClass,ctMethod);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return ctClass;
    }

    private void buildMethod(CtClass ctClass, CtMethod oldMethod) throws CannotCompileException, NotFoundException {
        CtMethod newMethod = CtNewMethod.copy(oldMethod, ctClass, null);
        oldMethod.setName(oldMethod.getName()+"$agent");
        String beginSrc=String.format("Object start=com.cxylk.agent.collect.ServiceCollect.begin(\"%s\",\"%s\");",
                ctClass.getName(),oldMethod.getName());
        String errSrc="com.cxylk.agent.collect.ServiceCollect.error(e,start);";
        String endSrc="com.cxylk.agent.collect.ServiceCollect.end(start);";
        String template=oldMethod.getReturnType().getName().equals("void")?voidSource:source;
        //将代码块插入方法
        newMethod.setBody(String.format(template,beginSrc,newMethod.getName(),errSrc,endSrc));
        ctClass.addMethod(newMethod);
    }

    /**
     * 方法执行前，必须是public，否则在插入代码块时报错
     * @param className
     * @param methodName
     * @return
     */
    public static ServiceStatistics begin(String className, String methodName){
        ServiceStatistics bean=new ServiceStatistics();
        bean.setBeginTime(System.currentTimeMillis());
        bean.setServiceName(className);
        bean.setMethodName(methodName);
        bean.setSimpleName(className.substring(className.lastIndexOf(".")));
        bean.setModelType("service");
        return bean;
    }

    /**
     * 捕捉异常，public
     * @param e
     * @param obj
     */
    public static void error(Throwable e, Object obj){
        ServiceStatistics bean=(ServiceStatistics)obj;
        bean.setErrorMsg(e.getMessage());
        bean.setErrorType(e.getClass().getSimpleName());
    }

    /**
     * 方法结束之前，public
     * @param obj
     */
    public static void end(Object obj){
        ServiceStatistics bean=(ServiceStatistics)obj;
        bean.setUseTime(System.currentTimeMillis()-bean.getBeginTime());
        System.out.println(JSON.toJSONString(obj));
    }

    /**
     * 带返回值
     */
    private static String source="{\n"
            +"%s"
            +"      Object result=null;\n"
            +"      try{\n"
            +"          result=($w)%s$agent($$);\n"
            +"      }catch(Throwable e){\n"
            +"%s"
            +"          throw e;\n"
            +"      }finally{\n"
            +"%s"
            +"      }\n"
            +"      return ($r)result;\n"
            +"}\n";

    /**
     * 不带返回值
     */
    private static String voidSource="{\n"
            +"%s"
            +"      try{\n"
            +"          %s$agent($$);\n"
            +"      }catch(Throwable e){\n"
            +"%s"
            +"          throw e;\n"
            +"      }finally{\n"
            +"%s"
            +"      }\n"
            +"}\n";
}
