package com.starrtc.staravdemo.utils;

import java.util.ArrayList;
import java.util.List;
public class AEvent {
    private static List<EventObj> callBackList = new ArrayList<EventObj>();


    public static void addListener(String eventID, IEventListener eventListener){
        for(EventObj eventObj:callBackList){
            if(eventObj.eventID.equals(eventID)&&eventObj.eventListener.getClass()==eventListener.getClass()) {
                return;
            }
        }
        EventObj event = new EventObj();
        event.eventListener = eventListener;
        event.eventID = eventID;
        callBackList.add(event);
    }

    public static void removeListener(String eventID, IEventListener eventListener){
        int i;
        EventObj event;
        for(i=0;i<callBackList.size();i++){
            event = callBackList.get(i);
            if(event.eventID.equals(eventID) && event.eventListener == eventListener){
                callBackList.remove(i);
                i--;
            }
        }
    }

    public static void notifyListener(String eventID, boolean success, Object object){
        int i;
        EventObj event;
        for(i=0;i<callBackList.size();i++){
            event = callBackList.get(i);
            if(event.eventID.equals(eventID)){
                event.eventListener.dispatchEvent(eventID,success,object);
            }
        }
    }

    private static class EventObj{
        IEventListener eventListener;
        String eventID;
    }

    //事件类型在这里定义
    public static final String AEVENT_LOGIN                     = "AEVENT_LOGIN";

    public static final String AEVENT_MEETING_GOT_LIST          = "AEVENT_MEETING_GOT_LIST";
    public static final String AEVENT_LIVE_GOT_LIST             = "AEVENT_LIVE_GOT_LIST";
    public static final String AEVENT_CHATROOM_GOT_LIST         = "AEVENT_CHATROOM_GOT_LIST";
    public static final String AEVENT_GROUP_GOT_LIST             = "AEVENT_GROUP_GOT_LIST";
    public static final String AEVENT_GROUP_GOT_MEMBER_LIST     = "AEVENT_GROUP_GOT_MEMBER_LIST";

    public static final String AEVENT_VOIP_REV_CALLING          = "AEVENT_VOIP_REV_CALLING";
    public static final String AEVENT_VOIP_REV_REFUSED          = "AEVENT_VOIP_REV_REFUSED";
    public static final String AEVENT_VOIP_REV_HANGUP           = "AEVENT_VOIP_REV_HANGUP";
    public static final String AEVENT_VOIP_REV_BUSY             = "AEVENT_VOIP_REV_BUSY";
    public static final String AEVENT_VOIP_REV_CONNECT          = "AEVENT_VOIP_REV_CONNECT";
    public static final String AEVENT_VOIP_REV_ERROR            = "AEVENT_VOIP_REV_ERROR";
    public static final String AEVENT_VOIP_ON_STOP              = "AEVENT_VOIP_ON_STOP";
    public static final String AEVENT_VOIP_GOT_TARGET_SIZE      = "AEVENT_VOIP_GOT_TARGET_SIZE";

    public static final String AEVENT_LIVE_INIT_COMPLETE         = "AEVENT_LIVE_INIT_COMPLETE";
    public static final String AEVENT_LIVE_JOIN_OK = "AEVENT_LIVE_JOIN_OK";
    public static final String AEVENT_LIVE_ADD_UPLOADER         = "AEVENT_LIVE_ADD_UPLOADER";
    public static final String AEVENT_LIVE_REMOVE_UPLOADER      = "AEVENT_LIVE_REMOVE_UPLOADER";
    public static final String AEVENT_LIVE_RESIZE_ALL_VIDEO      = "AEVENT_LIVE_RESIZE_ALL_VIDEO";
    public static final String AEVENT_LIVE_ERROR                = "AEVENT_LIVE_ERROR";
    public static final String AEVENT_LIVE_STOP_OK              = "AEVENT_LIVE_STOP_OK";

    public static final String AEVENT_ECHO_FIN                  = "AEVENT_ECHO_FIN";

    public static final String AEVENT_CHATROOM_CREATE_OK        ="AEVENT_CHATROOM_CREATE_OK";
    public static final String AEVENT_CHATROOM_JOIN_OK          ="AEVENT_CHATROOM_JOIN_OK";
    public static final String AEVENT_CHATROOM_CREATE_FAILED    ="AEVENT_CHATROOM_CREATE_FAILED";
    public static final String AEVENT_CHATROOM_JOIN_FAILED      ="AEVENT_CHATROOM_JOIN_FAILED";
    public static final String AEVENT_CHATROOM_ERROR            ="AEVENT_CHATROOM_ERROR";
    public static final String AEVENT_CHATROOM_STOP_OK          ="AEVENT_CHATROOM_STOP_OK";
    public static final String AEVENT_CHATROOM_DELETE_OK        ="AEVENT_CHATROOM_DELETE_OK";
    public static final String AEVENT_CHATROOM_DELETE_FAILED    ="AEVENT_CHATROOM_DELETE_FAILED";
    public static final String AEVENT_CHATROOM_BAN_USER_OK      ="AEVENT_CHATROOM_BAN_USER_OK";
    public static final String AEVENT_CHATROOM_BAN_USER_FAILED  ="AEVENT_CHATROOM_BAN_USER_FAILED";
    public static final String AEVENT_CHATROOM_KICK_OUT_OK      ="AEVENT_CHATROOM_KICK_OUT_OK";
    public static final String AEVENT_CHATROOM_KICK_OUT_FAILED  ="AEVENT_CHATROOM_KICK_OUT_FAILED";
    public static final String AEVENT_CHATROOM_SELF_BANNED      ="AEVENT_CHATROOM_SELF_BANNED";
    public static final String AEVENT_CHATROOM_SELF_KICKED      ="AEVENT_CHATROOM_SELF_KICKED";
    public static final String AEVENT_CHATROOM_REV_MSG          ="AEVENT_CHATROOM_REV_MSG";
    public static final String AEVENT_CHATROOM_REV_PRIVATE_MSG  ="AEVENT_CHATROOM_REV_PRIVATE_MSG";
    public static final String AEVENT_CHATROOM_GET_ONLINE_NUMBER="AEVENT_CHATROOM_GET_ONLINE_NUMBER";
    public static final String AEVENT_CHATROOM_SEND_MSG_NO_FEE  ="AEVENT_CHATROOM_SEND_MSG_NO_FEE";
    public static final String AEVENT_CHATROOM_SEND_MSG_SUCCESS  ="AEVENT_CHATROOM_SEND_MSG_SUCCESS";
    public static final String AEVENT_CHATROOM_SEND_MSG_FAILED  ="AEVENT_CHATROOM_SEND_MSG_FAILED";

    public static final String AEVENT_C2C_REV_MSG               ="AEVENT_C2C_REV_MSG";
    public static final String AEVENT_C2C_SEND_MESSAGE_SUCCESS  ="AEVENT_C2C_SEND_MESSAGE_SUCCESS";
    public static final String AEVENT_C2C_SEND_MESSAGE_FAILED   ="AEVENT_C2C_SEND_MESSAGE_FAILED";

    public static final String AEVENT_GROUP_CREATE_SUCCESS      ="AEVENT_GROUP_CREATE_SUCCESS";
    public static final String AEVENT_GROUP_CREATE_FAILED       ="AEVENT_GROUP_CREATE_FAILED";
    public static final String AEVENT_GROUP_DELETE_SUCCESS      ="AEVENT_GROUP_DELETE_SUCCESS";
    public static final String AEVENT_GROUP_DELETE_FAILED       ="AEVENT_GROUP_DELETE_FAILED";
    public static final String AEVENT_GROUP_REV_MSG             ="AEVENT_GROUP_REV_MSG";
    public static final String AEVENT_GROUP_SET_PUSH_MODE_SUCCESS ="AEVENT_GROUP_SET_PUSH_MODE_SUCCESS";
    public static final String AEVENT_GROUP_SET_PUSH_MODE_FAILED ="AEVENT_GROUP_SET_PUSH_MODE_FAILED";
    public static final String AEVENT_GROUP_ADD_USER_SUCCESS     ="AEVENT_GROUP_ADD_USER_SUCCESS";
    public static final String AEVENT_GROUP_ADD_USER_FAILED     ="AEVENT_GROUP_ADD_USER_FAILED";
    public static final String AEVENT_GROUP_DELETE_USER_SUCCESS ="AEVENT_GROUP_DELETE_USER_SUCCESS";
    public static final String AEVENT_GROUP_DELETE_USER_FAILED  ="AEVENT_GROUP_DELETE_USER_FAILED";
    public static final String AEVENT_GROUP_SEND_MESSAGE_SUCCESS="AEVENT_GROUP_SEND_MESSAGE_SUCCESS";
    public static final String AEVENT_GROUP_SEND_MESSAGE_FAILED="AEVENT_GROUP_SEND_MESSAGE_FAILED";

    public static final String AEVENT_USER_LOGIN_SUCCESS        ="AEVENT_USER_LOGIN_SUCCESS";
    public static final String AEVENT_USER_LOGIN_FAILED         ="AEVENT_USER_LOGIN_FAILED";
    public static final String AEVENT_USER_KICKED               ="AEVENT_USER_KICKED";
    public static final String AEVENT_USER_TOKEN_EXPIRED        ="AEVENT_USER_TOKEN_EXPIRED";
    public static final String AEVENT_USER_ONLINE               ="AEVENT_USER_ONLINE";
    public static final String AEVENT_USER_OFFLINE              ="AEVENT_USER_OFFLINE";
}
