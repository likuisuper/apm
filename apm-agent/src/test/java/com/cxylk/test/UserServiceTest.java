package com.cxylk.test;

/**
 * @Classname UserService
 * @Description TODO
 * @Author likui
 * @Date 2021/3/20 23:01
 **/
public class UserServiceTest {
    public void sayHello(){
        System.out.println("hello word");
    }

    public String  sayHi(String name,int age,Object other) throws InterruptedException {
        Thread.sleep(100);
        System.out.println("hello hi");
        return null;
    }
}