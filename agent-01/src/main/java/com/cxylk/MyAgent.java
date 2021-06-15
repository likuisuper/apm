package com.cxylk;

import java.lang.instrument.Instrumentation;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Classname MyAgent
 * @Description java-agent链路监控
 * @Author likui
 * @Date 2021/6/14 20:57
 **/
public class MyAgent {
    public static void premain(String agentArgs, Instrumentation inst){
        System.out.println("this is my agent："+agentArgs);

        //创建一个定时执行任务的线程池，每5s执行一次
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            JvmStack.printMemoryInfo();
            JvmStack.printGCInfo();
            System.out.println("===============================");
        },0,5000, TimeUnit.MILLISECONDS);
    }
}
