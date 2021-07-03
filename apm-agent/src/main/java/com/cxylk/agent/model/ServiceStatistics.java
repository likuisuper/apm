package com.cxylk.agent.model;

import java.io.Serializable;

/**
 * @Classname ServiceStatistics
 * @Description 应用内部性能采集
 * @Author likui
 * @Date 2021/6/16 15:11
 **/
public class ServiceStatistics extends Statistics implements Serializable {
    private Long begin;
    private Long end;
    private Long useTime;
    private String errorMsg;
    private String errorType;
    private String serviceName; //服务名称
    private String simpleName; //服务简称
    private String methodName; //方法名称

    public Long getBegin() {
        return begin;
    }

    public void setBegin(Long begin) {
        this.begin = begin;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }



    public Long getUseTime() {
        return useTime;
    }

    public void setUseTime(Long useTime) {
        this.useTime = useTime;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
