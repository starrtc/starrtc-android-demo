package com.starrtc.demo.listener;

import com.starrtc.demo.utils.AEvent;
import com.starrtc.starrtcsdk.apiInterface.IXHSuperRoomManagerListener;
import com.starrtc.starrtcsdk.core.im.message.XHIMMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class XHSuperRoomManagerListener implements IXHSuperRoomManagerListener {

    @Override
    public void onActorJoined(String liveID, String actorID) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id",liveID);
            jsonObject.put("actorID",actorID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AEvent.notifyListener(AEvent.AEVENT_SUPER_ROOM_ADD_UPLOADER,true,jsonObject);
    }

    @Override
    public void onActorLeft(String liveID, String actorID) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id",liveID);
            jsonObject.put("actorID",actorID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AEvent.notifyListener(AEvent.AEVENT_SUPER_ROOM_REMOVE_UPLOADER,true,jsonObject);
    }

    @Override
    public void onSuperRoomError(String liveID, String error) {
        AEvent.notifyListener(AEvent.AEVENT_SUPER_ROOM_ERROR,true,error);
    }

    @Override
    public void onMembersUpdated(int number) {
        AEvent.notifyListener(AEvent.AEVENT_SUPER_ROOM_GET_ONLINE_NUMBER,true,number);
    }

    @Override
    public void onSelfKicked() {
        AEvent.notifyListener(AEvent.AEVENT_SUPER_ROOM_SELF_KICKED,true,"");
    }

    @Override
    public void onSelfMuted(int seconds) {
        AEvent.notifyListener(AEvent.AEVENT_SUPER_ROOM_SELF_BANNED,true,seconds);
    }

    @Override
    public void onCommandToStopPlay(String liveID) {
        AEvent.notifyListener(AEvent.AEVENT_SUPER_ROOM_SELF_COMMANDED_TO_STOP,true,liveID);
    }

    @Override
    public void onReceivedMessage(XHIMMessage message) {
        AEvent.notifyListener(AEvent.AEVENT_SUPER_ROOM_REV_MSG,true,message);
    }

    @Override
    public void onReceivePrivateMessage(XHIMMessage message) {
        AEvent.notifyListener(AEvent.AEVENT_SUPER_ROOM_REV_PRIVATE_MSG,true,message);
    }

    @Override
    public void onReceiveRealtimeData(byte[] data, String upId) {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data",data);
            jsonObject.put("upId",upId);
            AEvent.notifyListener(AEvent.AEVENT_SUPER_ROOM_REV_REALTIME_DATA,true,jsonObject);
        } catch (JSONException e) {
            AEvent.notifyListener(AEvent.AEVENT_SUPER_ROOM_REV_REALTIME_DATA,false,data);
            e.printStackTrace();
        }
    }

    @Override
    public void onPushStreamError(String err) {
        AEvent.notifyListener(AEvent.AEVENT_SUPER_ROOM_PUSH_STREAM_ERROR,true,err);
    }
}
