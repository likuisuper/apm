package com.cxylk.agent;

import javassist.*;

import java.io.IOException;
import java.lang.instrument.*;
import java.security.ProtectionDomain;

/**
 * @Classname AgentMain
 * @Description 通过javaagent实现字节码插桩
 * @Author likui
 * @Date 2021/3/20 22:44
 **/
public class AgentMain {
    /**
     * 方法入口名一定不能是其他，只能是premain
     * @param arg
     * @param instrumentation
     */
    public static void premain(String arg, Instrumentation instrumentation) throws UnmodifiableClassException, ClassNotFoundException, NotFoundException, CannotCompileException, IOException {
        System.out.println("hello premain:"+arg);
        UserService userService=new UserService();


        //可以使用redefineClasses方法重新定义一个类，跳过类装载，动态进行装载
        // 配置文件中需使Can-Redefine-Classes为true
        ClassPool classPool=ClassPool.getDefault();
        CtClass ctClass = classPool.get("com.cxylk.agent.UserService");
        CtMethod sayHello = ctClass.getDeclaredMethod("sayHello");
        sayHello.insertAfter("System.out.println(System.currentTimeMillis());");
        //重新定义一个类的时候添加一个新的方法此时是会报错的
        CtMethod ctMethod = CtNewMethod.make("public Object id(Object obj) { return obj; }", ctClass);
        ctMethod.setName("newMethod");
        ctClass.addMethod(ctMethod);
        instrumentation.redefineClasses(new ClassDefinition(UserService.class,ctClass.toBytecode()));
        new UserService().sayHello();
        //调用的是重写定义类之前的sayHello方法，但是输出的内容是重写定义的，因为字节码指令已经变了
        userService.sayHello();
    }

    public static void premain_backup1(String arg, Instrumentation instrumentation) throws UnmodifiableClassException, ClassNotFoundException, NotFoundException, CannotCompileException, IOException {
        System.out.println("hello premain:"+arg);
        UserService.class.getTypeName();//执行到这里就会装载UserService，所以下面输出的类不会再有它
        /**
         * 类装载 过滤器
         * 监控应用内部 SQL RPC HTTP
         * 类装载过程 只会装载在premain方法之前没有被装载过的类，比如String已经被装载过了，那么就不会再输出
         */
        instrumentation.addTransformer(new ClassFileTransformer() {
            //类装载过滤器
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
//                System.out.println(className);
                return new byte[0];
            }
        },true);

        //类只会被装载一次，可以使用下面的方法重新装载。需要在配置文件中让Can-Retransform-Classes为true
        //并且addTransformer的第二个参数为true
        //此时再运行就会发现输出的类中又有UserService了
        instrumentation.retransformClasses(UserService.class);

        //可以使用redefineClasses方法重新定义一个类，跳过类装载，动态进行装载
        // 配置文件中需使Can-Redefine-Classes为true
        ClassPool classPool=ClassPool.getDefault();
        CtClass ctClass = classPool.get("com.cxylk.agent.UserService");
        CtMethod sayHello = ctClass.getDeclaredMethod("sayHello");
        sayHello.insertAfter("System.out.println(System.currentTimeMillis());");
        instrumentation.redefineClasses(new ClassDefinition(UserService.class,ctClass.toBytecode()));
    }
}
