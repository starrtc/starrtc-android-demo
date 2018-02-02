package com.starrtc.staravdemo.demo.listener;

import com.starrtc.staravdemo.utils.AEvent;
import com.starrtc.starrtcsdk.im.listener.IStarIMGroupListener;
import com.starrtc.starrtcsdk.im.message.StarIMMessage;

/**
 * Created by zhangjt on 2017/8/18.
 */

public class DemoGroupListener implements IStarIMGroupListener {
    @Override
    public void onNewMessage(StarIMMessage msg) {
        AEvent.notifyListener(AEvent.AEVENT_GROUP_REV_MSG,true,msg);
    }

    @Override
    public void onGroupCreateSuccess(String groupId) {
        AEvent.notifyListener(AEvent.AEVENT_GROUP_CREATE_SUCCESS,true,groupId);
    }

    @Override
    public void onGroupCreateFailed(String error) {
        AEvent.notifyListener(AEvent.AEVENT_GROUP_CREATE_FAILED,true,error);
    }

    @Override
    public void onGroupDeleteSuccess(String data) {
        AEvent.notifyListener(AEvent.AEVENT_GROUP_DELETE_SUCCESS,true,data);
    }

    @Override
    public void onGroupDeleteFailed(String data) {
        AEvent.notifyListener(AEvent.AEVENT_GROUP_DELETE_FAILED,true,data);
    }

    @Override
    public void onGroupAddUserSuccess(String data) {
        AEvent.notifyListener(AEvent.AEVENT_GROUP_ADD_USER_SUCCESS,true,data);
    }

    @Override
    public void onGroupAddUserFailed(String data) {
        AEvent.notifyListener(AEvent.AEVENT_GROUP_ADD_USER_FAILED,true,data);
    }

    @Override
    public void onGroupDeleteUserSuccess(String data) {
        AEvent.notifyListener(AEvent.AEVENT_GROUP_DELETE_USER_SUCCESS,true,data);
    }

    @Override
    public void onGroupDeleteUserFailed(String data) {
        AEvent.notifyListener(AEvent.AEVENT_GROUP_DELETE_USER_FAILED,true,data);
    }

    @Override
    public void onGroupSetPushModeSuccess(String data) {
        AEvent.notifyListener(AEvent.AEVENT_GROUP_SET_PUSH_MODE_SUCCESS,true,data);
    }

    @Override
    public void onGroupSetPushModeFailed(String data) {
        AEvent.notifyListener(AEvent.AEVENT_GROUP_SET_PUSH_MODE_FAILED,true,data);
    }

    @Override
    public void onGroupSendMessageSuccess(int msgIndex) {
        AEvent.notifyListener(AEvent.AEVENT_GROUP_SEND_MESSAGE_SUCCESS,true,msgIndex);
    }

    @Override
    public void onGroupSendMessageFailed(int msgIndex) {
        AEvent.notifyListener(AEvent.AEVENT_GROUP_SEND_MESSAGE_FAILED,true,msgIndex);
    }
}
