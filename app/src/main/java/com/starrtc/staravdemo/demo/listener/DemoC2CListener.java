package com.starrtc.staravdemo.demo.listener;

import com.starrtc.staravdemo.utils.AEvent;
import com.starrtc.starrtcsdk.im.listener.IStarIMC2CListener;
import com.starrtc.starrtcsdk.im.message.StarIMMessage;

/**
 * Created by zhangjt on 2017/8/18.
 */

public class DemoC2CListener implements IStarIMC2CListener {

    @Override
    public void onNewMessages(StarIMMessage msg) {
        AEvent.notifyListener(AEvent.AEVENT_C2C_REV_MSG,true,msg);
    }

    @Override
    public void onSendMessageSuccess(int msgIndex) {
        AEvent.notifyListener(AEvent.AEVENT_C2C_SEND_MESSAGE_SUCCESS,true,msgIndex);
    }

    @Override
    public void onSendMessageFailed(int msgIndex) {
        AEvent.notifyListener(AEvent.AEVENT_C2C_SEND_MESSAGE_FAILED,true,msgIndex);
    }
}
