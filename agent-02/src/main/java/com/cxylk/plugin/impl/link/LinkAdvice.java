package com.cxylk.plugin.impl.link;

import com.cxylk.track.Span;
import com.cxylk.track.TrackContext;
import com.cxylk.track.TrackManager;
import net.bytebuddy.asm.Advice;

import java.util.UUID;

/**
 * @Classname LinkAdvice
 * @Description 链路拦截
 * @Author likui
 * @Date 2021/6/14 22:56
 **/
public class LinkAdvice {
    @Advice.OnMethodEnter()
    public static void enter(@Advice.Origin("#t") String className,@Advice.Origin("#m") String methodName){
        Span currentSpan= TrackManager.getCurrentSpan();
        if(null==currentSpan){
            String linkId = UUID.randomUUID().toString();
            TrackContext.setLinkId(linkId);
        }
        TrackManager.createEntrySpan();
    }

    @Advice.OnMethodExit()
    public static void exit(@Advice.Origin("#t") String className,@Advice.Origin("#m") String methodName){
        Span exitSpan=TrackManager.getExitSpan();
        if(null==exitSpan){
            return;
        }
        System.out.println("链路追踪(MQ)："+exitSpan.getLinkId()+" "+className+"."+methodName+" 耗时："+(System.currentTimeMillis()-exitSpan.getEnterTime().getTime())+"ms");
    }
}
