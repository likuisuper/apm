package com.cxylk.agent.collect;

import com.cxylk.agent.ApmContext;
import com.cxylk.agent.ICollect;
import com.cxylk.agent.common.WildcardMatcher;
import com.cxylk.agent.model.ServiceStatistics;
import javassist.*;
import java.io.IOException;
import java.lang.instrument.Instrumentation;



/**
 * @Classname ServiceCollect
 * @Description 服务信息采集
 * @Author likui
 * @Date 2021/6/16 15:16
 **/
public class ServiceCollect extends AbstractByteTransformCollect implements ICollect {
    public static ServiceCollect INSTANCE;
    private final ApmContext context;
    private WildcardMatcher excludeMatcher=null; // 排除非哪些类
    private  WildcardMatcher includeMatcher=null;// 包含哪些类
    private String include;
    private String exclude;

    private static final String beginSrc;
    private static final String endSrc;
    private static final String errorSrc;

    static {
        StringBuilder sbuilder = new StringBuilder();
        sbuilder.append("com.cxylk.agent.collect.ServiceCollect instance= ");
        sbuilder.append("com.cxylk.agent.collect.ServiceCollect.INSTANCE;\r\n");
        sbuilder.append("com.cxylk.agent.model.ServiceStatistics statistic =instance.begin(\"%s\",\"%s\");");
        beginSrc = sbuilder.toString();
        sbuilder = new StringBuilder();
        sbuilder.append("instance.end(statistic);");
        endSrc = sbuilder.toString();
        sbuilder = new StringBuilder();
        sbuilder.append("instance.error(statistic,e);");
        errorSrc = sbuilder.toString();
    }

    // include
    // exclude
    public ServiceCollect(ApmContext context, Instrumentation instrumentation) {
        super(instrumentation);
        this.context = context;
        if(context.getConfig("service.include")!=null){
            includeMatcher=new WildcardMatcher(context.getConfig("service.include"));
        }else{
            System.err.println("[error]未配置 'service.include'参数无法监控service服务方法");
        }
        if(context.getConfig("service.exclude")!=null) {
            excludeMatcher = new WildcardMatcher(context.getConfig("service.exclude"));
        }


        INSTANCE = this;
    }

    public ServiceStatistics begin(String className, String methodName) {
        ServiceStatistics bean = new ServiceStatistics();
        bean.setRecordTime(System.currentTimeMillis());
//        bean.setHostIp();
//        bean.setHostName();
        bean.setBegin(System.currentTimeMillis());
        bean.setServiceName(className);
        bean.setMethodName(methodName);
        bean.setSimpleName(className.substring(className.lastIndexOf(".")));
        bean.setModelType("service");
        return bean;
    }

    public void error(ServiceStatistics bean, Throwable e) {
        bean.setErrorType(e.getClass().getSimpleName());
        bean.setErrorMsg(e.getMessage());
    }

    public void end(ServiceStatistics bean) {
        bean.setEnd(System.currentTimeMillis());
        bean.setUseTime(bean.getEnd() - bean.getBegin());
        context.submitCollectResult(bean);
    }


    @Override
    public byte[] transform(ClassLoader loader, String className) throws CannotCompileException, NotFoundException, IOException {

        if (includeMatcher == null) {
            return null;
        } else if (!includeMatcher.matches(className)) { // 包含指定类
            return null;
        } else if (excludeMatcher != null && excludeMatcher.matches(className)) { // 排除指定类
            return null;
        }


        CtClass ctclass = toCtClass(loader, className);
        if (ctclass.isInterface()) {// 排除接口
            return null;
        }
        AgentByteBuild byteBuild = new AgentByteBuild(className, loader, ctclass);
        CtMethod[] methods = ctclass.getDeclaredMethods();
        for (CtMethod m : methods) {
            // 屏蔽非公共方法
            if (!Modifier.isPublic(m.getModifiers())) {
                continue;
            }
            // 屏蔽静态方法
            if (Modifier.isStatic(m.getModifiers())) {
                continue;
            }
            // 屏蔽本地方法
            if (Modifier.isNative(m.getModifiers())) {
                continue;
            }
            AgentByteBuild.MethodSrcBuild build = new AgentByteBuild.MethodSrcBuild();
            build.setBeginSrc(String.format(beginSrc, className, m.getName()));
            build.setEndSrc(endSrc);
            build.setErrorSrc(errorSrc);
            byteBuild.updateMethod(m, build);
        }
        return byteBuild.toBytecode();
    }
}
