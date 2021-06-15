package com.cxylk.track;

import java.util.Date;

/**
 * @Classname Span
 * @Description 追踪的属性
 * @Author likui
 * @Date 2021/6/14 23:07
 **/
public class Span {
    //链路ID
    private String linkId;

    //方法进入时间
    private Date enterTime;

    public Span(String linkId) {
        this.linkId = linkId;
        this.enterTime=new Date();
    }

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public Date getEnterTime() {
        return enterTime;
    }

    public void setEnterTime(Date enterTime) {
        this.enterTime = enterTime;
    }
}
