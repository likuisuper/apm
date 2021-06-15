package com.cxylk.plugin.impl.jvm;

import com.cxylk.plugin.IPlugin;
import com.cxylk.plugin.InterceptPoint;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * @Classname JvmPlugin
 * @Description jvm监控
 * @Author likui
 * @Date 2021/6/14 22:48
 **/
public class JvmPlugin implements IPlugin {
    @Override
    public String name() {
        return "jvm";
    }

    @Override
    public InterceptPoint[] buildInterceptPoint() {
        return new InterceptPoint[]{
                new InterceptPoint() {
                    @Override
                    public ElementMatcher<TypeDescription> buildTypesMatcher() {
                        return ElementMatchers.nameStartsWith("com.cxylk.test");
                    }

                    @Override
                    public ElementMatcher<MethodDescription> buildMethodsMatcher() {
                        return ElementMatchers.isMethod()
                                .and(ElementMatchers.any())
                                .and(ElementMatchers.not(ElementMatchers.nameStartsWith("main")));
                    }
                }
        };
    }

    @Override
    public Class adviceClass() {
        return JvmAdvice.class;
    }
}
