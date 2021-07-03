package com.cxylk.test;

import org.junit.Test;

/**
 * @Classname Hello
 * @Description TODO
 * @Author likui
 * @Date 2021/6/16 13:43
 **/
public class Hello {
    @Test
    public void test(){
        String className="com.cxylk.agent";
        System.out.println(className.substring(className.lastIndexOf(".")));
        System.out.println(System.getProperty("log"));
    }
}
