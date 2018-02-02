package com.starrtc.staravdemo.demo.listener;

import com.starrtc.staravdemo.demo.MLOC;
import com.starrtc.staravdemo.utils.AEvent;
import com.starrtc.starrtcsdk.im.listener.IStarUserStatusListener;

/**
 * Created by zhangjt on 2017/8/18.
 */

public class DemoUserStatusListener implements IStarUserStatusListener {
    private String TEXTTAG= "DemoUserStatusListener";

    @Override
    public void loginSuccess() {
        MLOC.d(TEXTTAG,"AEVENT_USER_LOGIN_SUCCESS");
        AEvent.notifyListener(AEvent.AEVENT_USER_LOGIN_SUCCESS,true,"");
    }

    @Override
    public void loginFailed() {
        MLOC.d(TEXTTAG,"AEVENT_USER_LOGIN_FAILED");
        AEvent.notifyListener(AEvent.AEVENT_USER_LOGIN_FAILED,true,"");
    }

    @Override
    public void userKickedOut() {
        MLOC.d(TEXTTAG,"AEVENT_USER_KICKED");
        AEvent.notifyListener(AEvent.AEVENT_USER_KICKED,true,"");
    }

    @Override
    public void userTokenExpired() {
        MLOC.d(TEXTTAG,"AEVENT_USER_TOKEN_EXPIRED");
        AEvent.notifyListener(AEvent.AEVENT_USER_TOKEN_EXPIRED,true,"");
    }

    @Override
    public void userOnline() {
        MLOC.d(TEXTTAG,"AEVENT_USER_ONLINE");
        AEvent.notifyListener(AEvent.AEVENT_USER_ONLINE,true,"");
    }

    @Override
    public void userOffline() {
        MLOC.d(TEXTTAG,"AEVENT_USER_OFFLINE");
        AEvent.notifyListener(AEvent.AEVENT_USER_OFFLINE,true,"");
    }
}
