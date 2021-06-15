package com.cxylk.agent;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @Classname C3P0DemoTest
 * @Description 监控c3p0数据源
 * @Author likui
 * @Date 2021/6/15 17:53
 **/
public class C3P0DemoTest {
    ComboPooledDataSource dataSource;

    public C3P0DemoTest(){
        dataSource=new ComboPooledDataSource("mysql");
    }

    public void exec(String sql) throws SQLException {
        Connection conn = dataSource.getConnection();
        boolean b = conn.createStatement().execute(sql);
        conn.close();
    }

    public static void main(String[] args) throws IOException {
        C3P0DemoTest s=new C3P0DemoTest();
        while (true){
            byte[] bytes=new byte[1024];
            int size=System.in.read(bytes);
            String sql=new String(bytes,0,size);
            System.out.println(sql);
            try {
                s.exec(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
