package com.cxylk.agent;

import com.cxylk.agent.collect.HttpCollect;
import com.cxylk.agent.collect.JdbcCollect;
import com.cxylk.agent.collect.ServiceCollect;
import com.cxylk.agent.filter.JSONFormat;
import com.cxylk.agent.output.SimpleOutput;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @Classname ApmContext
 * @Description APM上下文，将collect、filter、output关联起来
 * @Author likui
 * @Date 2021/6/20 20:32
 **/
public class ApmContext {
    private Instrumentation instrumentation;
    private Properties properties;
    List<ICollect> collects = new ArrayList();
    IFilter filter;
    IOutput output;

    public ApmContext(Properties properties, Instrumentation instrumentation) {
        if(properties==null){
            throw new RuntimeException("properties 不能为空");
        }
        this.properties = properties;
        this.instrumentation = instrumentation;
        // 注册采集器 IOC
        collects.add(new ServiceCollect(this, instrumentation));
        collects.add(new JdbcCollect(this, instrumentation));
        collects.add(new HttpCollect(this,instrumentation));
        //filter 注册
        filter = new JSONFormat();
        //输出器注册
        output = new SimpleOutput(properties);
    }

    // 递交采集结果
    public void submitCollectResult(Object value) {
        // TODO 基于线程后台执行
        value = filter.doFilter(value);
        output.out(value);
    }

    public String getConfig(String key) {
        return properties.getProperty(key);
    }

    public List<ICollect> getCollects() {
        return collects;
    }
}
