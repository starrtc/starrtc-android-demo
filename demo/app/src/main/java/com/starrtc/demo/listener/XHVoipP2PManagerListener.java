package com.starrtc.demo.listener;

import com.starrtc.demo.utils.AEvent;
import com.starrtc.starrtcsdk.apiInterface.IXHVoipP2PManagerListener;
import com.starrtc.starrtcsdk.socket.StarErrorCode;

public class XHVoipP2PManagerListener implements IXHVoipP2PManagerListener {
    @Override
    public void onCalling(String fromID) {
        AEvent.notifyListener(AEvent.AEVENT_VOIP_P2P_REV_CALLING,true,fromID);
    }

    @Override
    public void onAudioCalling(String fromID) {
        AEvent.notifyListener(AEvent.AEVENT_VOIP_P2P_REV_CALLING_AUDIO,true,fromID);
    }

    @Override
    public void onCancled(String fromID) {

    }

    @Override
    public void onRefused(String fromID) {
        AEvent.notifyListener(AEvent.AEVENT_VOIP_REV_REFUSED,true,fromID);
    }

    @Override
    public void onBusy(String fromID) {
        AEvent.notifyListener(AEvent.AEVENT_VOIP_REV_BUSY,true,fromID);
    }

    @Override
    public void onConnected(String fromID) {
        AEvent.notifyListener(AEvent.AEVENT_VOIP_REV_CONNECT,true,fromID);
    }

    @Override
    public void onHangup(String fromID) {
        AEvent.notifyListener(AEvent.AEVENT_VOIP_REV_HANGUP,true,fromID);
    }

    @Override
    public void onError(String errorCode) {
        AEvent.notifyListener(AEvent.AEVENT_VOIP_REV_ERROR,true, StarErrorCode.getErrorCode(errorCode));
    }

    @Override
    public void onReceiveRealtimeData(byte[] data) {

    }
}
