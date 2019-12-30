package com.starrtc.demo.listener;

import com.starrtc.demo.utils.AEvent;
import com.starrtc.starrtcsdk.api.XHConstants;
import com.starrtc.starrtcsdk.apiInterface.IXHLoginManagerListener;

public class XHLoginManagerListener implements IXHLoginManagerListener {

    @Override
    public void onConnectionStateChanged(XHConstants.XHSDKConnectionState state) {
        if(state == XHConstants.XHSDKConnectionState.SDKConnectionStateDisconnect){
            //用户掉线，可以自动恢复
            AEvent.notifyListener(AEvent.AEVENT_USER_OFFLINE,true,"");
        }else if(state == XHConstants.XHSDKConnectionState.SDKConnectionStateReconnect){
            //用户在线
            AEvent.notifyListener(AEvent.AEVENT_USER_ONLINE,true,"");
        }else if(state == XHConstants.XHSDKConnectionState.SDKConnectionDeath){
            //用户掉线，无法自动恢复
            AEvent.notifyListener(AEvent.AEVENT_CONN_DEATH,true,"");
        }
    }

    @Override
    public void onKickedByOtherDeviceLogin() {
        AEvent.notifyListener(AEvent.AEVENT_USER_KICKED,true,"");
    }

    @Override
    public void onLogout() {

    }
}
