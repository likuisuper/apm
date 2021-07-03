package com.cxylk.agent.model;

import java.io.Serializable;

/**
 * @Classname Statistics
 * @Description 应用外部环境采集
 * @Author likui
 * @Date 2021/6/16 15:07
 **/
public class Statistics implements Serializable {
    private long recordTime;
    private String modelType;
    private String hostIp;
    private String hostName;
    //链路id
    private String traceId;

    public long getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(long recordTime) {
        this.recordTime = recordTime;
    }

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}
