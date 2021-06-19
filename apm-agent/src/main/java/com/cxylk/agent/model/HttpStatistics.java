package com.cxylk.agent.model;

/**
 * @Classname HttpStatistics
 * @Description Http应用性能采集
 * @Author likui
 * @Date 2021/6/17 11:09
 **/
public class HttpStatistics extends Statistics{
    private String url;
    private String clientIp;
    private String error;
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "HttpStatistics{" +
                "url='" + url + '\'' +
                ", clientIp='" + clientIp + '\'' +
                '}'+super.toString();
    }
}
