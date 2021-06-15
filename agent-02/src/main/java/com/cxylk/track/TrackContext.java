package com.cxylk.track;

/**
 * @Classname TrackContext
 * @Description TODO
 * @Author likui
 * @Date 2021/6/14 23:09
 **/
public class TrackContext {
    private static final ThreadLocal<String> trackLocal=new ThreadLocal<>();

    public static void clear(){
        trackLocal.remove();
    }

    public static void setLinkId(String linkId){
        trackLocal.set(linkId);
    }

    public static String getLinkId(){
        return trackLocal.get();
    }
}
