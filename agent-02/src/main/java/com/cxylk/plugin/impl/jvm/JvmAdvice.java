package com.cxylk.plugin.impl.jvm;
import net.bytebuddy.asm.Advice;

/**
 * @Classname JvmAdvice
 * @Description jvm拦截器
 * @Author likui
 * @Date 2021/6/14 22:45
 **/
public class JvmAdvice {
    @Advice.OnMethodExit
    public static void exit(){
        JvmStack.printMemoryInfo();
        JvmStack.printGCInfo();
    }
}
