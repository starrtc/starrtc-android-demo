package com.starrtc.staravdemo.demo.listener;

import com.starrtc.staravdemo.utils.AEvent;
import com.starrtc.starrtcsdk.im.listener.IStarIMChatroomListener;
import com.starrtc.starrtcsdk.im.message.StarIMMessage;

/**
 * Created by zhangjt on 2017/12/18.
 */

public class DemoChatroomListener implements IStarIMChatroomListener {
    @Override
    public void chatroomCreateOK(String roomId, int maxContentLen) {
        AEvent.notifyListener(AEvent.AEVENT_CHATROOM_CREATE_OK,true,roomId+":"+maxContentLen);
    }

    @Override
    public void chatroomJoinOK(String roomId, int maxContentLen) {
        AEvent.notifyListener(AEvent.AEVENT_CHATROOM_JOIN_OK,true,roomId+":"+maxContentLen);
    }

    @Override
    public void chatroomCreateFailed(String errString) {
        AEvent.notifyListener(AEvent.AEVENT_CHATROOM_CREATE_FAILED,true,errString);
    }

    @Override
    public void chatroomJoinFailed(String roomId, String errString) {
        AEvent.notifyListener(AEvent.AEVENT_CHATROOM_JOIN_FAILED,true,errString);
    }

    @Override
    public void chatRoomErr(String errString) {
        AEvent.notifyListener(AEvent.AEVENT_CHATROOM_ERROR,true,errString);
    }

    @Override
    public void chatroomStopOK() {
        AEvent.notifyListener(AEvent.AEVENT_CHATROOM_STOP_OK,true,"");
    }

    @Override
    public void chatroomDeleteOK(String roomId) {
        AEvent.notifyListener(AEvent.AEVENT_CHATROOM_DELETE_OK,true,roomId);
    }

    @Override
    public void chatroomDeleteFailed(String roomId, String errString) {
        AEvent.notifyListener(AEvent.AEVENT_CHATROOM_DELETE_FAILED,true,roomId);
    }

    @Override
    public void chatroomBanToSendMsgOK(String banUserId, int banTime) {
        AEvent.notifyListener(AEvent.AEVENT_CHATROOM_BAN_USER_OK,true,banUserId);
    }

    @Override
    public void chatroomBanToSendMsgFailed(String banUserId, int banTime, String errString) {
        AEvent.notifyListener(AEvent.AEVENT_CHATROOM_BAN_USER_FAILED,true,banUserId+":"+errString);
    }

    @Override
    public void chatroomKickOutOK(String kickOutUserId) {
        AEvent.notifyListener(AEvent.AEVENT_CHATROOM_KICK_OUT_OK,true,kickOutUserId);
    }

    @Override
    public void chatroomSendMsgNoFee() {
        AEvent.notifyListener(AEvent.AEVENT_CHATROOM_SEND_MSG_NO_FEE,true,"");
    }

    @Override
    public void chatroomKickOutFailed(String kickOutUserId, String errString) {
        AEvent.notifyListener(AEvent.AEVENT_CHATROOM_KICK_OUT_FAILED,true,kickOutUserId+":"+errString);
    }

    @Override
    public void chatroomSendMsgBanned(int remainTimeSec) {
        AEvent.notifyListener(AEvent.AEVENT_CHATROOM_SELF_BANNED,true,remainTimeSec);
    }

    @Override
    public void chatroomKickedOut() {
        AEvent.notifyListener(AEvent.AEVENT_CHATROOM_SELF_KICKED,true,"");
    }

    @Override
    public void chatroomGetNewMsg(StarIMMessage msgData) {
        AEvent.notifyListener(AEvent.AEVENT_CHATROOM_REV_MSG,true,msgData);
    }

    @Override
    public void chatroomGetNewPrivateMsg(StarIMMessage msgData) {
        AEvent.notifyListener(AEvent.AEVENT_CHATROOM_REV_PRIVATE_MSG,true,msgData);
    }

    @Override
    public void getRoomOnlineNumber(String roomId, int number) {
        AEvent.notifyListener(AEvent.AEVENT_CHATROOM_GET_ONLINE_NUMBER,true,number);
    }

    @Override
    public void sendMessageSuccess(int msgIndx) {
        AEvent.notifyListener(AEvent.AEVENT_CHATROOM_SEND_MSG_SUCCESS,true,msgIndx);
    }

    @Override
    public void sendMessageFailed(int msgIndx) {
        AEvent.notifyListener(AEvent.AEVENT_CHATROOM_SEND_MSG_FAILED,true,msgIndx);
    }
}
