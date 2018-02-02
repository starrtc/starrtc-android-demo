package com.starrtc.staravdemo.demo.listener;

import com.starrtc.staravdemo.utils.AEvent;
import com.starrtc.starrtcsdk.im.listener.IStarIMErrorListener;

/**
 * Created by zhangjt on 2017/8/18.
 */

public class DemoErrorListener implements IStarIMErrorListener {
    @Override
    public boolean onError(int code,String msg) {
        if(code==10000){
            AEvent.notifyListener(AEvent.AEVENT_ECHO_FIN,true,msg);
        }
        return false;
    }
}
