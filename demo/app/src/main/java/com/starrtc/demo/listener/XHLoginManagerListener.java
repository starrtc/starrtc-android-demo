package com.starrtc.demo.listener;

import com.starrtc.demo.utils.AEvent;
import com.starrtc.starrtcsdk.api.XHConstants;
import com.starrtc.starrtcsdk.apiInterface.IXHLoginManagerListener;

public class XHLoginManagerListener implements IXHLoginManagerListener {

    @Override
    public void onConnectionStateChanged(XHConstants.XHSDKConnectionState state) {
        if(state == XHConstants.XHSDKConnectionState.SDKConnectionStateDisconnect){
            AEvent.notifyListener(AEvent.AEVENT_USER_OFFLINE,true,"");
        }else if(state == XHConstants.XHSDKConnectionState.SDKConnectionStateReconnect){
            AEvent.notifyListener(AEvent.AEVENT_USER_ONLINE,true,"");
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
