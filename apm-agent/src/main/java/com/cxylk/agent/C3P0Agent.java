package com.cxylk.agent;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import javassist.*;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.net.InetSocketAddress;
import java.security.ProtectionDomain;
import java.util.concurrent.Executors;

/**
 * @Classname C3P0Agent
 * @Description TODO
 * @Author likui
 * @Date 2021/6/15 18:02
 **/
public class C3P0Agent {
    static String targetClass = "com.mchange.v2.c3p0.ComboPooledDataSource";

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        instrumentation.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                byte[] result = null;
                if (className != null && className.replace("/", ".").equals(targetClass)) {
                    ClassPool pool = new ClassPool();
                    pool.insertClassPath(new LoaderClassPath(loader));
                    try {
                        CtClass ctClass = pool.get(targetClass);
                        ctClass.getConstructor("()V").insertAfter("System.getProperties().put(\"c3p0Source$agent\",$0);");
                        result = ctClass.toBytecode();
                        new C3P0Agent().openHttpServer();
                    } catch (NotFoundException | CannotCompileException | IOException e) {
                        e.printStackTrace();
                    }
                }
                return result;
            }
        });
    }

    public String getStatus() {
        Object source2 = System.getProperties().get("c3p0Source$agent");
        if (source2 == null) {
            return "未初始任何c3p0数据源";
        }
        return source2.toString();
    }

    public void openHttpServer() throws IOException {
        InetSocketAddress address=new InetSocketAddress(5555);
        HttpServer server=HttpServer.create(address,0);
        server.createContext("/", new HttpHandler());
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        System.out.println("Server is listening on port 5555");

    }

    private class HttpHandler implements com.sun.net.httpserver.HttpHandler{
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            Headers responseHeaders=httpExchange.getResponseHeaders();
            responseHeaders.set("Content-Type","text/plain;charset=UTF-8");
            httpExchange.sendResponseHeaders(200,0);
            OutputStream responseBody = httpExchange.getResponseBody();
            //输出c3p0状态
            responseBody.write(C3P0Agent.this.getStatus().getBytes());
            responseBody.flush();
            responseBody.close();
        }
    }
}
