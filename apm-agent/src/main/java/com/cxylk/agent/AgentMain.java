package com.cxylk.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

/**
 * @Classname AgentMain
 * @Description 通过javaagent实现字节码插桩
 * @Author likui
 * @Date 2021/3/20 22:44
 **/
public class AgentMain {
    /**
     * 方法入口名一定不能是其他，只能是premain
     * @param arg
     * @param instrumentation
     */
    public static void premain(String arg, Instrumentation instrumentation) {
        System.out.println("hello premain:"+arg);
        /**
         * 类装载 过滤器
         * 监控应用内部 SQL RPC HTTP
         * 类装载过程
         */
        instrumentation.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                System.out.println(className);
                return new byte[0];
            }
        });
    }
}
