package com.cxylk.plugin.impl.jvm;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.Arrays;
import java.util.List;

/**
 * @Classname JvmStack
 * @Description 基于JavaAgent的全链路监控-JVM内存与GC信息
 * @Author likui
 * @Date 2021/6/14 20:36
 **/
public class JvmStack {
    //用于将字节转换为MB，1MB=1024*1024字节
    private static final long MB=1048576L;

    /**
     * 打印堆区和非堆区的内存使用情况
     */
    public static void printMemoryInfo(){
        MemoryMXBean memoryMXBean= ManagementFactory.getMemoryMXBean();
        //返回堆的当前内存使用情况
        MemoryUsage heapMemory=memoryMXBean.getHeapMemoryUsage();

        //commited就是实际分配了多少内存
        String info=String.format("\n堆：  init：%s\t max：%s\t used：%s\t committed：%s\t use rate：%s\n",
                heapMemory.getInit()/MB+"MB", heapMemory.getMax()/MB+"MB",
                heapMemory.getUsed()/MB+"MB",heapMemory.getCommitted()/MB+"MB",
                heapMemory.getUsed()*100/heapMemory.getCommitted()+"%");
        System.out.print(info);

        //获取非堆的内存使用情况
        MemoryUsage noHeapMemory=memoryMXBean.getNonHeapMemoryUsage();
        info=String.format("非堆：init：%s\t max：%s\t used：%s\t committed：%s\t use rate：%s\n",
                noHeapMemory.getInit()/MB+"MB",noHeapMemory.getMax()/MB+"MB",
                noHeapMemory.getUsed()/MB+"MB",noHeapMemory.getCommitted()/MB+"MB",
                noHeapMemory.getUsed()*100/noHeapMemory.getCommitted()+"%");
        System.out.println(info);
    }

    public static void printGCInfo(){
        List<GarbageCollectorMXBean> garbages=ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean garbage : garbages) {
            String info=String.format("name：%s\t count：%s\t time：%s\t pool name：%s",
                    garbage.getName(),garbage.getCollectionCount(),
                    garbage.getCollectionTime(), Arrays.deepToString(garbage.getMemoryPoolNames()));
            System.out.println(info);
        }
    }
}
