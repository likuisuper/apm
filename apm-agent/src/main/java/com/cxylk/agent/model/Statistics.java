package com.cxylk.agent.model;

/**
 * @Classname Statistics
 * @Description 应用外部环境采集
 * @Author likui
 * @Date 2021/6/16 15:07
 **/
public class Statistics {
    //开始时间
    private long beginTime;
    //用时
    private long useTime;
    //类型
    private String modelType;
    //主机ip
    private String hostIp;
    //主机名
    private String hostName;
    //追踪ID
    private String traceId;

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public long getUseTime() {
        return useTime;
    }

    public void setUseTime(long useTime) {
        this.useTime = useTime;
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

    @Override
    public String toString() {
        return "Statistics{" + "beginTime=" + beginTime + "ms" +
                ", useTime=" + useTime + "ms" +
                ", modelType='" + modelType + '\'' +
                ", hostIp='" + hostIp + '\'' +
                ", hostName='" + hostName + '\'' +
                ", traceId='" + traceId + '\'' +
                '}';
    }
}
