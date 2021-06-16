package com.cxylk.test.collect;

import com.cxylk.agent.UserService;
import com.cxylk.agent.collect.ServiceCollect;
import com.cxylk.test.UserServiceTest;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;
import org.junit.Test;

/**
 * @Classname ServiceCollectTest
 * @Description TODO
 * @Author likui
 * @Date 2021/6/16 16:13
 **/
public class ServiceCollectTest {
    /**
     * 没有集成javaagent
     * @throws NotFoundException
     * @throws CannotCompileException
     * @throws InterruptedException
     */
    @Test
    public void test1() throws NotFoundException, CannotCompileException, InterruptedException {
        ServiceCollect collect=new ServiceCollect("com.cxylk.agent");
        CtClass ctClass = collect.buildCtClass(ServiceCollectTest.class.getClassLoader(), "com.cxylk.agent.UserService");
        ctClass.toClass();
        new UserService().sayHello();
//        new UserService().sayHi(null,0,null);
    }

    /**
     * 使用javaagent方式运行
     * @throws InterruptedException
     */
    @Test
    public void test2() throws InterruptedException {
        //不能调用com.cxylk.agent下面的类，否则会报堆栈溢出，即发生递归
//        new UserService().sayHi(null,111,null);
        //将UserService放在测试包下
        //但是这样的话就只能将VM 参数写死成 com.cxylk.test.UserServiceTest，只扫描当前类
        new UserServiceTest().sayHi(null,111,null);
    }
}
