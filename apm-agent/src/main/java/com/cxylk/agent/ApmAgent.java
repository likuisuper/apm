package com.cxylk.agent;

import com.cxylk.agent.collect.HttpCollect;
import com.cxylk.agent.collect.ServiceCollect;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.util.Properties;

/**
 * @Classname ApmAgent
 * @Description javaagent入口
 * @Author likui
 * @Date 2021/6/16 16:53
 **/
public class ApmAgent {
    public static void premain(String arg, Instrumentation instrumentation){
        Properties properties = new Properties();
        // 装载配置文件
        if (arg != null && !arg.trim().equals("")) {
            try {
                properties.load(new ByteArrayInputStream(
                        arg.replaceAll(",", "\n").getBytes()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ApmContext context = new ApmContext(properties, instrumentation);
    }



//    public static void premain(String args, Instrumentation instrumentation){
//        new ServiceCollect(args).transform(instrumentation);
//        //加入HTTP采集器
//        new HttpCollect().transform(instrumentation);
//        // jvm
//        // 外部文件配置
//        // 开发环境
//        // agent conf/conf.conf
//        Properties configByJvm = getConfigByJvm(args);
//        Properties configFiles = getConfigFiles();
//        configFiles.putAll(configByJvm);
//    }

//    private static Properties getConfigByJvm(String arg){
//        Properties properties=new Properties();
//        //装载配置文件
//        if (arg!=null&&!"".equals(arg.trim())) {
//            try {
//                //key1=value1,
//                //key2=value2
//                properties.load(new ByteArrayInputStream(arg.replaceAll(",","\n").getBytes()));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return properties;
//    }
//
//    /**
//     * 读取agent文件
//     * @return
//     */
//    private static Properties getConfigFiles(){
//        URL url = ApmAgent.class.getProtectionDomain().getCodeSource().getLocation();
//        File file=new File(new File(url.getFile()).getParentFile(),"conf/apm.conf");
//        if(!file.exists()||file.isDirectory()){
//            return new Properties();
//        }
//        Properties properties=new Properties();
//        try {
//            properties.load(new FileInputStream(file));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return properties;
//    }
}
