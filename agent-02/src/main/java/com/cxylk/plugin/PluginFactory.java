package com.cxylk.plugin;

import com.cxylk.plugin.impl.jvm.JvmPlugin;
import com.cxylk.plugin.impl.link.LinkPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * @Classname PluginFactory
 * @Description 目前是使用静态工厂调用，实际开发中可以把工厂做成动态配置化
 * @Author likui
 * @Date 2021/6/14 22:41
 **/
public class PluginFactory {
    public static List<IPlugin> pluginGroup=new ArrayList<>();

    static {
        //链路监控
        pluginGroup.add(new LinkPlugin());
        //JVM监控
        pluginGroup.add(new JvmPlugin());
    }
}
