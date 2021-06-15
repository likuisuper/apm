package com.cxylk;

import com.cxylk.plugin.IPlugin;
import com.cxylk.plugin.InterceptPoint;
import com.cxylk.plugin.PluginFactory;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;

import java.lang.instrument.Instrumentation;
import java.util.List;

/**
 * @Classname MyAgent
 * @Description TODO
 * @Author likui
 * @Date 2021/6/14 23:31
 **/
public class MyAgent {
    //jvm首先尝试在代理类上调用该方法
    public static void premain(String agentArgs, Instrumentation inst){
        System.out.println("基于javaagent的链路追踪");
        System.out.println("==========================================\r\n");
        //获取代理构建器
        AgentBuilder agentBuilder=new AgentBuilder.Default();
        List<IPlugin> pluginGroup = PluginFactory.pluginGroup;
        for (IPlugin plugin : pluginGroup) {
            //获取监控点
            InterceptPoint[] interceptPoints = plugin.buildInterceptPoint();
            for (InterceptPoint point : interceptPoints) {
                AgentBuilder.Transformer transformer=((builder, typeDescription, classLoader, module) -> {
                    builder=builder.visit(Advice.to(plugin.adviceClass()).on(point.buildMethodsMatcher()));
                    return builder;
                });
                agentBuilder=agentBuilder.type(point.buildTypesMatcher()).transform(transformer).asDecorator();
            }
        }

        //监听
        AgentBuilder.Listener listener=new AgentBuilder.Listener() {
            @Override
            public void onDiscovery(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {

            }

            @Override
            public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded, DynamicType dynamicType) {
                System.out.println("onTransformation："+typeDescription);
            }

            @Override
            public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded) {

            }

            @Override
            public void onError(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded, Throwable throwable) {

            }

            @Override
            public void onComplete(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {

            }
        };
        agentBuilder.with(listener).installOn(inst);
    }
}
