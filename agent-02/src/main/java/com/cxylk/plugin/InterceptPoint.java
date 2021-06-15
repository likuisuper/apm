package com.cxylk.plugin;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * @Classname InterceptPoint
 * @Description 拦截点
 * @Author likui
 * @Date 2021/6/14 22:30
 **/
public interface InterceptPoint {
    /**
     * 类匹配规则
     */
    ElementMatcher<TypeDescription> buildTypesMatcher();

    /**
     * 方法匹配规则
     */
    ElementMatcher<MethodDescription> buildMethodsMatcher();
}
