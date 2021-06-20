package com.cxylk.bean;

import java.io.Serializable;

/**
 * @Classname User
 * @Description TODO
 * @Author likui
 * @Date 2021/6/19 19:52
 **/
public class User implements Serializable {
    private String name;
    private Integer id;


    public User(String name, Integer id) {
        this.name = name;
        this.id = id;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
