package com.cxylk.agent.collect;

import com.cxylk.agent.model.HttpStatistics;
import javassist.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Map;

/**
 * @Classname HttpCollect
 * @Description TODO
 * @Author likui
 * @Date 2021/6/17 11:10
 **/
public class HttpCollect {
    // 采集目标
    // 1.DispatchServlet
    // 2.@control 下的方法
    // 3.采集 javax.servlet.service()
    private static final String TARGET_CLASS = "javax.servlet.http.HttpServlet";
    private static final String TARGET_METHOD = "service";

    public HttpCollect() {
    }

    public void transform(Instrumentation instrumentation) {
        instrumentation.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader,
                                    String className,
                                    Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain,
                                    byte[] classfileBuffer) {
                if (!TARGET_CLASS.replaceAll("\\.", "/")
                        .equals(className)) {
                    return null;
                }

                try {
                    return buildClass(loader);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    public byte[] buildClass(ClassLoader loader) throws Exception {
        ClassPool pool = new ClassPool();
        pool.insertClassPath(new LoaderClassPath(loader));
        CtClass ctClass = pool.get(TARGET_CLASS);
        CtMethod oldMethod = ctClass.getDeclaredMethod(TARGET_METHOD);
        CtMethod newMethod = CtNewMethod.copy(oldMethod, ctClass, null);
        oldMethod.setName(oldMethod.getName() + "$agent");
        //HttpServlet.service()'

        String beginSrc = "Object stat=com.cxylk.agent.collect.HttpCollect.begin($args);";
        String errorSrc = "com.cxylk.agent.collect.HttpCollect.error(e,stat);";
        String endSrc = "com.cxylk.agent.collect.HttpCollect.end(stat);";
        newMethod.setBody(String.format(voidSource, beginSrc, TARGET_METHOD, errorSrc, endSrc));
        ctClass.addMethod(newMethod);
        return ctClass.toBytecode();
    }

    // url,client IP
    public static HttpStatistics begin(Object args[]) {
        System.out.println(Arrays.toString(args));

        //这样写是会报错找不到HttpServletRequest，因为双亲委派的机制
//        HttpServletRequest request= (HttpServletRequest) args[0];
        HttpStatistics httpStatistics = new HttpStatistics();
        httpStatistics.setBeginTime(System.currentTimeMillis());
        //采用适配器进行反射获取
        HttpServletRequestAdapter adapter = new HttpServletRequestAdapter(args[0]);
        httpStatistics.setUrl(adapter.getRequestURI());
        httpStatistics.setClientIp( adapter.getClientIp());
        return httpStatistics;
    }

    public static void end(Object obj) {
        HttpStatistics stat= (HttpStatistics) obj;
        ((HttpStatistics) obj).setUseTime(System.currentTimeMillis()-stat.getBeginTime());
        System.out.println(stat);
    }

    public static void error(Throwable error,Object obj) {
        HttpStatistics stat= (HttpStatistics) obj;
        stat.setError(error.getMessage());
        System.out.println(stat);
    }

    final static String voidSource = "{\n"
            + "%s"
            + "        try {\n"
            + "            %s$agent($$);\n"
            + "        } catch (Throwable e) {\n"
            + "%s"
            + "            throw e;\n"
            + "        }finally{\n"
            + "%s"
            + "        }\n"
            + "}\n";


    private static class HttpServletRequestAdapter {
        private final Object target;
        private final Method _getRequestURI;
        private final Method _getRequestURL;
        private final Method _getParameterMap;
        private final Method _getMethod;
        private final Method _getHeader;
        private final Method _getRemoteAddr;
        private final static String targetClassName = "javax.servlet.http.HttpServletRequest";

        public HttpServletRequestAdapter(Object target) {
            this.target = target;
            try {
                Class<?> targetClass = target.getClass().getClassLoader().loadClass(targetClassName);
                _getRequestURI = targetClass.getMethod("getRequestURI");
                _getParameterMap = targetClass.getMethod("getParameterMap");
                _getMethod = targetClass.getMethod("getMethod");
                _getHeader = targetClass.getMethod("getHeader", String.class);
                _getRemoteAddr = targetClass.getMethod("getRemoteAddr");
                _getRequestURL = targetClass.getMethod("getRequestURL");
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("error :" + e.getMessage() + ". probable cause the target is not belong javax.servlet.http.HttpServletRequest ");
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("error :" + e.getMessage() + ". probable cause the target is not belong javax.servlet.http.HttpServletRequest ");
            }
        }


        public String getRequestURI() {
            try {
                return (String) _getRequestURI.invoke(target);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public String getRequestURL() {
            try {
                return _getRequestURL.invoke(target).toString();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public Map<String, String[]> getParameterMap() {
            try {
                return (Map<String, String[]>) _getParameterMap.invoke(target);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public String getMethod() {
            try {
                return (String) _getMethod.invoke(target);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public String getHeader(String name) {
            try {
                return (String) _getHeader.invoke(target, name);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public String getRemoteAddr() {
            try {
                return (String) _getRemoteAddr.invoke(target);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public String getClientIp() {
            String ip = getHeader("x-forwarded-for");
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = getRemoteAddr();
            }
            return ip;
        }

    }
}
