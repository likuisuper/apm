package com.cxylk.test.collect;

import com.cxylk.agent.ApmAgent;
import com.cxylk.agent.collect.JdbcCollect;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Properties;

/**
 * @Classname JdbcCollectTest
 * @Description jdbc采集测试
 * @Author likui
 * @Date 2021/6/19 17:59
 **/
public class JdbcCollectTest {

    @Test
    public void proxyTest() throws SQLException, ClassNotFoundException, NotFoundException, CannotCompileException {
        JdbcCollect collect=new JdbcCollect();
        CtClass ctClass = collect.buildClass("com.mysql.cj.jdbc.NonRegisteringDriver", JdbcCollectTest.class.getClassLoader());
        ctClass.toClass();
        sqlTest();
    }

    @Test
    public void sqlTest() throws SQLException, ClassNotFoundException {
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement("select * from user");
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()){
            System.out.println(resultSet.getString(1));
        }
        statement.close();
    }

    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/mybatis",
                "root", "root");
    }
}
