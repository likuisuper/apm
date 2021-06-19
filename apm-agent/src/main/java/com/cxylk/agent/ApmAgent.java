package com.cxylk.agent;

import com.cxylk.agent.collect.HttpCollect;
import com.cxylk.agent.collect.ServiceCollect;

import java.lang.instrument.Instrumentation;

/**
 * @Classname ApmAgent
 * @Description TODO
 * @Author likui
 * @Date 2021/6/16 16:53
 **/
public class ApmAgent {
    public static void premain(String args, Instrumentation instrumentation){
        new ServiceCollect(args).transform(instrumentation);
        //加入HTTP采集器
        new HttpCollect().transform(instrumentation);
    }
}
