package com.starrtc.staravdemo.demo.listener;

import com.starrtc.staravdemo.utils.AEvent;
import com.starrtc.starrtcsdk.apiInterface.IXHGroupManagerListener;
import com.starrtc.starrtcsdk.core.im.message.XHIMMessage;

public class XHGroupManagerListener implements IXHGroupManagerListener {
    @Override
    public void onMembersUpdeted(String groupID, int number) {

    }

    @Override
    public void onSelfKicked(String groupID) {

    }

    @Override
    public void onGroupDeleted(String groupID) {

    }

    @Override
    public void onReceivedMessage(XHIMMessage message) {
        //msg.fromId 消息来源
        //msg.targetId 目标ID
        //msg.contentData 消息体
        //msg.time 消息发送的时间
        //msg.atList 群内@某个用户 多人用逗号“,”分隔
        AEvent.notifyListener(AEvent.AEVENT_GROUP_REV_MSG,true,message);
    }
}
