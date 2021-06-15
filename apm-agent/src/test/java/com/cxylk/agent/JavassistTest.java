package com.cxylk.agent;

import javassist.*;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * @Classname JavassistTest
 * @Description TODO
 * @Author likui
 * @Date 2021/6/15 16:16
 **/
public class JavassistTest {
    @Test
    public void test() throws NotFoundException, CannotCompileException, InterruptedException, IOException {
        //类池，往类池装载类。有两种写法
        //1.全写
//        ClassPool classPool=new ClassPool();
        //装载系统classloader下的类到类池中
//        classPool.appendSystemPath();
        //2.直接获取默认的类池，它会自动调用appendSystemPath()
        ClassPool classPool=ClassPool.getDefault();
        //获取指定的类，注意不能直接写成UserService.class.getName()这种，这么写就表示这个类已经被加载过了
        CtClass ctClass = classPool.get("com.cxylk.agent.UserService");
        //获取指定方法
        CtMethod sayHello = ctClass.getDeclaredMethod("sayHi");
        //拷贝原方法重新生成一个新方法，解决原方法插入代码时因为都是以代码块插入而造成局部变量访问不到的问题
        CtMethod newMethod = CtNewMethod.copy(sayHello, ctClass, null);
        //改变原来方法的名称
        sayHello.setName(sayHello.getName()+"$agent");

        newMethod.setBody("{long begin=System.currentTimeMillis();\n" +
                "        sayHi$agent($$);\n" +
                "        long end=System.currentTimeMillis();\n" +
                "        System.out.println(end-begin);"+
                "        Object a=\"lk\";"+
                "        return ($r)$3;"+
                "        }"
        );
        //加入该方法
        ctClass.addMethod(newMethod);
        //把修改之后的类 装载到JVM
        ctClass.toClass();
        byte[] bytes = ctClass.toBytecode();
        Files.write(new File(System.getProperty("user.dir")+"/target/classes/com/cxylk/agent/JavassistForUser.class").toPath(),bytes);
        new UserService().sayHi("lk",23,null);
    }
}
