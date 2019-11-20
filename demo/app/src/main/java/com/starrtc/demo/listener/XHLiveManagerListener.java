package com.starrtc.demo.listener;

import com.starrtc.demo.utils.AEvent;
import com.starrtc.starrtcsdk.api.XHConstants;
import com.starrtc.starrtcsdk.apiInterface.IXHLiveManagerListener;
import com.starrtc.starrtcsdk.core.im.message.XHIMMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class XHLiveManagerListener implements IXHLiveManagerListener {

    @Override
    public void onActorJoined(String liveID, String actorID) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id",liveID);
            jsonObject.put("actorID",actorID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AEvent.notifyListener(AEvent.AEVENT_LIVE_ADD_UPLOADER,true,jsonObject);
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
        AEvent.notifyListener(AEvent.AEVENT_LIVE_REMOVE_UPLOADER,true,jsonObject);
    }

    @Override
    public void onApplyBroadcast(String liveID, String applyUserID) {
        AEvent.notifyListener(AEvent.AEVENT_LIVE_APPLY_LINK,true,applyUserID);
    }

    @Override
    public void onApplyResponsed(String liveID, XHConstants.XHLiveJoinResult result) {
        AEvent.notifyListener(AEvent.AEVENT_LIVE_APPLY_LINK_RESULT,true,result);
    }

    @Override
    public void onInviteBroadcast(String liveID, String inviteUserID) {
        AEvent.notifyListener(AEvent.AEVENT_LIVE_INVITE_LINK,true,inviteUserID);
    }

    @Override
    public void onInviteResponsed(String liveID, XHConstants.XHLiveJoinResult result) {
        AEvent.notifyListener(AEvent.AEVENT_LIVE_INVITE_LINK_RESULT,true,result);
    }

    @Override
    public void onLiveError(String liveID, String error) {
        AEvent.notifyListener(AEvent.AEVENT_LIVE_ERROR,true,error);
    }

    @Override
    public void onMembersUpdated(int number) {
        AEvent.notifyListener(AEvent.AEVENT_LIVE_GET_ONLINE_NUMBER,true,number);
    }

    @Override
    public void onSelfKicked() {
        AEvent.notifyListener(AEvent.AEVENT_LIVE_SELF_KICKED,true,"");
    }

    @Override
    public void onSelfMuted(int seconds) {
        AEvent.notifyListener(AEvent.AEVENT_LIVE_SELF_BANNED,true,seconds);
    }

    @Override
    public void onCommandToStopPlay(String liveID) {
        AEvent.notifyListener(AEvent.AEVENT_LIVE_SELF_COMMANDED_TO_STOP,true,liveID);
    }

    @Override
    public void onReceivedMessage(XHIMMessage message) {
        AEvent.notifyListener(AEvent.AEVENT_LIVE_REV_MSG,true,message);
    }

    @Override
    public void onReceivePrivateMessage(XHIMMessage message) {
        AEvent.notifyListener(AEvent.AEVENT_LIVE_REV_PRIVATE_MSG,true,message);
    }

    @Override
    public void onReceiveRealtimeData(byte[] data, String upId) {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data",data);
            jsonObject.put("upId",upId);
            AEvent.notifyListener(AEvent.AEVENT_LIVE_REV_REALTIME_DATA,true,jsonObject);
        } catch (JSONException e) {
            AEvent.notifyListener(AEvent.AEVENT_LIVE_REV_REALTIME_DATA,false,data);
            e.printStackTrace();

        }
    }

    @Override
    public void onPushStreamError(String err) {
        AEvent.notifyListener(AEvent.AEVENT_LIVE_PUSH_STREAM_ERROR,true,err);
    }
}
