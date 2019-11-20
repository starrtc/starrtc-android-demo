package com.starrtc.demo.listener;

import com.starrtc.demo.utils.AEvent;
import com.starrtc.starrtcsdk.apiInterface.IXHMeetingManagerListener;
import com.starrtc.starrtcsdk.core.im.message.XHIMMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class XHMeetingManagerListener implements IXHMeetingManagerListener {
    @Override
    public void onJoined(String meetingID, String userID) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("meetingID",meetingID);
            jsonObject.put("userID",userID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AEvent.notifyListener(AEvent.AEVENT_MEETING_ADD_UPLOADER,true,jsonObject);
    }

    @Override
    public void onLeft(String meetingID, String userID) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("meetingID",meetingID);
            jsonObject.put("userID",userID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AEvent.notifyListener(AEvent.AEVENT_MEETING_REMOVE_UPLOADER,true,jsonObject);
    }

    @Override
    public void onMeetingError(String meetingID, String error) {
        AEvent.notifyListener(AEvent.AEVENT_MEETING_ERROR,true,error);
    }

    @Override
    public void onMembersUpdated(int membersNumber) {
        AEvent.notifyListener(AEvent.AEVENT_MEETING_GET_ONLINE_NUMBER,true,membersNumber);
    }

    @Override
    public void onSelfKicked() {
        AEvent.notifyListener(AEvent.AEVENT_MEETING_SELF_KICKED,true,"");
    }

    @Override
    public void onSelfMuted(int seconds) {
        AEvent.notifyListener(AEvent.AEVENT_MEETING_SELF_BANNED,true,seconds);
    }

    @Override
    public void onReceivedMessage(XHIMMessage message) {
        AEvent.notifyListener(AEvent.AEVENT_MEETING_REV_MSG,true,message);
    }

    @Override
    public void onReceivePrivateMessage(XHIMMessage message) {
        AEvent.notifyListener(AEvent.AEVENT_MEETING_REV_PRIVATE_MSG,true,message);
    }

    @Override
    public void onReceiveRealtimeData(byte[] data, String upId) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data",data);
            jsonObject.put("upId",upId);
            AEvent.notifyListener(AEvent.AEVENT_MEETING_REV_REALTIME_DATA,true,jsonObject);
        } catch (JSONException e) {
            AEvent.notifyListener(AEvent.AEVENT_MEETING_REV_REALTIME_DATA,false,data);
            e.printStackTrace();
        }
    }

    @Override
    public void onPushStreamError(String err) {
        AEvent.notifyListener(AEvent.AEVENT_MEETING_PUSH_STREAM_ERROR,true,err);
    }
}
