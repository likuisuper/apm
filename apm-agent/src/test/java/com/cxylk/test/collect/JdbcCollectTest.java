package com.cxylk.test.collect;

import com.cxylk.agent.ApmContext;
import com.cxylk.agent.collect.JdbcCollect;
import com.cxylk.test.MockInstrumentation;
import javassist.ByteArrayClassPath;
import javassist.ClassPool;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.*;
import java.util.Properties;

/**
 * @Classname JdbcCollectTest
 * @Description jdbc采集测试
 * @Author likui
 * @Date 2021/6/19 17:59
 **/
public class JdbcCollectTest {

    private JdbcCollect jdbcCollect;

    @Before
    public void init(){
        MockInstrumentation instrumentation = new MockInstrumentation();
        Properties properties=new Properties();
//        properties.put("service.include","com.cxylk.agent1.*&com.cxylk.test.*");
//        properties.put("service.exclude","com.cxylk.test1.*");
        ApmContext apmContext = new ApmContext(properties, instrumentation);
        jdbcCollect=new JdbcCollect(apmContext, instrumentation);
    }

    @Test
    public void testBatch() throws Exception {
        buildTest();
        for (int i = 0; i < 130; i++) {
            pgsqlTest();
        }
    }

    @Test
    public void buildTest() throws Exception {
//        String name = "com.mysql.cj.jdbc.NonRegisteringDriver";
        String name = "org.postgresql.Driver";
        byte[] classBytes = jdbcCollect.transform(
                JdbcCollectTest.class.getClassLoader(), name);
        ClassPool pool = new ClassPool();
        pool.insertClassPath(new ByteArrayClassPath(name, classBytes));
        pool.get(name).toClass();
        Class.forName(name);
        pgsqlTest();
//        mysqlTest();
    }

    @Test
    public void pgsqlTest() throws Exception {

        Connection conn = DriverManager
                .getConnection(
                        "jdbc:postgresql://192.168.1.192:5432/folio?currentSchema=diku_mod_combine_catalogue",
                        "folio", "folio123");
        PreparedStatement statment = conn
                .prepareStatement("select * from task where title=?");
        statment.setString(1,"情商");
        ResultSet resultSet = statment.executeQuery();
        while (resultSet.next()) {
            System.out.println(resultSet.getString(3));
        }
        statment.close();
        conn.close();
    }

    @Test
    public void mysqlTest() throws Exception {

        Connection conn = DriverManager
                .getConnection(
                        "jdbc:mysql://127.0.0.1:3306/mybatis",
                        "root", "root");
        PreparedStatement statment = conn
                .prepareStatement("select * from user");
        ResultSet resultSet = statment.executeQuery();
        while (resultSet.next()) {
            System.out.println(resultSet.getString(1));
        }
        statment.close();
        conn.close();
    }
}
