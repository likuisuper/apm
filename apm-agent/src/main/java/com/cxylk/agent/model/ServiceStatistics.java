package com.cxylk.agent.model;

import java.io.Serializable;

/**
 * @Classname ServiceStatistics
 * @Description 应用内部性能采集
 * @Author likui
 * @Date 2021/6/16 15:11
 **/
public class ServiceStatistics extends Statistics implements Serializable {
    //异常信息
    private String errorMsg;
    //异常类型
    private String errorType;
    //服务名称
    private String serviceName;
    //服务简称
    private String simpleName;
    //方法名
    private String methodName;

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

    @Override
    public String toString() {
        return "ServiceStatistics{" + "errorMsg='" + errorMsg + '\'' +
                ", errorType='" + errorType + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", simpleName='" + simpleName + '\'' +
                ", methodName='" + methodName + '\'' +
                '}'+super.toString();
    }
}
