package com.cxylk.test;

/**
 * @Classname ApiTest
 * @Description 链路追踪    VM options:-javaagent:xxx.jar=args
 * @Author likui
 * @Date 2021/6/15 13:12
 **/
public class ApiTest {
    public static void main(String[] args) {
        //线程1
        new Thread(()->new ApiTest().http_lt1("张三")).start();

        //线程2
        new Thread(()->new ApiTest().http_lt2("李四")).start();
    }

    public void http_lt1(String name){
        try {
            Thread.sleep((long) (Math.random()*500));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("测试结果：hi1 "+name);
        http_lt2(name);
    }

    public void http_lt2(String name) {
        try {
            Thread.sleep((long)(Math.random()*500));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("测试结果：hi2 "+name);
        http_lt3(name);
    }

    public void http_lt3(String name) {
        try {
            Thread.sleep((long)(Math.random()*500));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("测试结果：hi3 "+name);
    }
}
