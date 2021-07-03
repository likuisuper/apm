package com.cxylk.agent.model;

import java.util.ArrayList;

/**
 * @Classname JdbcStatistics
 * @Description jdbc统计信息
 * @Author likui
 * @Date 2021/6/19 17:07
 **/
public class JdbcStatistics extends Statistics implements java.io.Serializable {
    public Long begin;// 时间戳
    public Long end;
    public Long useTime;
    // jdbc url
    public String jdbcUrl;
    // sql 语句
    public String sql;
    // 数据库名称
    public String databaseName;

    public String error;
    public String errorType;
    // 是否经过预处理
    public String preman;

    public ArrayList<ParamValues> params=new ArrayList();


    public JdbcStatistics() {

    }

    public static class ParamValues{
        public int index;
        public Object value;
        public ParamValues(int index, Object value) {
            this.index = index;
            this.value = value;
        }
    }
}
