package com.cxylk.test.collect;

import com.cxylk.agent.ApmContext;
import com.cxylk.agent.UserService;
import com.cxylk.agent.collect.ServiceCollect;
import com.cxylk.test.MockInstrumentation;
import com.cxylk.test.UserServiceTest;
import javassist.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

/**
 * @Classname ServiceCollectTest
 * @Description TODO
 * @Author likui
 * @Date 2021/6/16 16:13
 **/
public class ServiceCollectTest {
    private ServiceCollect serviceCollect;

    @Before
    public void init(){
        MockInstrumentation instrumentation = new MockInstrumentation();
        Properties properties=new Properties();
        properties.put("service.include","com.cxylk.agent1.*&com.cxylk.test.*");
        properties.put("service.exclude","com.cxylk.test1.*");
        ApmContext apmContext = new ApmContext(properties, instrumentation);
        serviceCollect=new ServiceCollect(apmContext, instrumentation);
    }

    @Test
    public void collectTest() throws IOException, CannotCompileException, NotFoundException {
        String name="com.cxylk.test.UserServiceTest";
        byte[] bytes = serviceCollect.transform(ServiceCollectTest.class.getClassLoader(), name);
        ClassPool pool=new ClassPool();
        pool.insertClassPath(new ByteArrayClassPath(name,bytes));
        pool.get(name).toClass();
        new UserServiceTest().getUser("111","lk");
    }
}
