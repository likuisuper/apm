package com.cxylk.plugin;

/**
 * @Classname IPlugin
 * @Description 监控组件
 * @Author likui
 * @Date 2021/6/14 22:28
 **/
public interface IPlugin {
    /**
     * 名称
     */
    String name();

    /**
     * 监控点
     */
    InterceptPoint[] buildInterceptPoint();

    /**
     * 拦截器类
     */
    Class adviceClass();
}
