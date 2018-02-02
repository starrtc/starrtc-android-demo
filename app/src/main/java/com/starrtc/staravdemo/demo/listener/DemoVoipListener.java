package com.starrtc.staravdemo.demo.listener;

import com.starrtc.staravdemo.demo.MLOC;
import com.starrtc.staravdemo.utils.AEvent;
import com.starrtc.starrtcsdk.im.listener.IStarVoipListener;
import com.starrtc.starrtcsdk.im.message.StarIMMessage;

/**
 * Created by zhangjt on 2017/8/21.
 */

public class DemoVoipListener implements IStarVoipListener {
    private String TEXTTAG= "DemoVoipListener";
    public  String  voipTargetId    = null;

    @Override
    public void onCalling(StarIMMessage msg) {
        MLOC.d(TEXTTAG,"onCalling");
        AEvent.notifyListener(AEvent.AEVENT_VOIP_REV_CALLING,true,msg);
    }

    @Override
    public void onRefused(StarIMMessage msg) {
        MLOC.d(TEXTTAG,"onRefused");
        AEvent.notifyListener(AEvent.AEVENT_VOIP_REV_REFUSED,true,msg);
    }

    @Override
    public void onHangup(StarIMMessage msg) {
        MLOC.d(TEXTTAG,"onHangup");
        AEvent.notifyListener(AEvent.AEVENT_VOIP_REV_HANGUP,true,msg);
    }

    @Override
    public void onBusy(StarIMMessage msg) {
        MLOC.d(TEXTTAG,"onBusy");
        AEvent.notifyListener(AEvent.AEVENT_VOIP_REV_BUSY,true,msg);
    }

    @Override
    public void onConnect(StarIMMessage msg) {
        MLOC.d(TEXTTAG,"onConnect");
        AEvent.notifyListener(AEvent.AEVENT_VOIP_REV_CONNECT,true,msg);
    }

    @Override
    public void onError(String code) {
        MLOC.d(TEXTTAG,"onError");
        AEvent.notifyListener(AEvent.AEVENT_VOIP_REV_ERROR,true,code);
    }

    @Override
    public void onStop(String code) {
        MLOC.d(TEXTTAG,"onStop");
        AEvent.notifyListener(AEvent.AEVENT_VOIP_ON_STOP,true,code);
    }

    @Override
    public void onGotTargetViewSize(int width, int height) {
        MLOC.d(TEXTTAG,"onGotTargetViewSize");
        AEvent.notifyListener(AEvent.AEVENT_VOIP_GOT_TARGET_SIZE,true,width+"_"+height);
    }
}
