package com.cxylk.agent.collect;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;
import javassist.NotFoundException;

import java.lang.instrument.Instrumentation;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @Classname AbstractByteTransformCollect
 * @Description 不同模块性能采集抽象类
 * @Author likui
 * @Date 2021/6/20 15:48
 **/
public abstract class AbstractByteTransformCollect {
    private static Map<ClassLoader, ClassPool> classPoolMap=new HashMap<>();
    private static Logger logger=Logger.getLogger(AbstractByteTransformCollect.class.getName());

    public AbstractByteTransformCollect(Instrumentation instrumentation){
        instrumentation.addTransformer(((loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {
            if(loader==null){
                return null;
            }
            if(className==null){
                return null;
            }
            className=className.trim().replace("/",".");
            try {
                return AbstractByteTransformCollect.this.transform(loader,className);
            } catch (Exception e) {
                logger.log(Level.SEVERE,"类插桩转换失败",e);
            }
            return null;
        }));
    }

    public abstract byte[] transform(ClassLoader loader,String className) throws Exception;

    protected static CtClass toCtClass(ClassLoader loader,String className) throws NotFoundException {
        if(!classPoolMap.containsKey(loader)){
            ClassPool classPool=new ClassPool();
            classPool.insertClassPath(new LoaderClassPath(loader));
            classPoolMap.put(loader,classPool);
        }
        ClassPool classPool = classPoolMap.get(loader);
        className = className.replaceAll("/", ".");
        return classPool.get(className);
    }
}
