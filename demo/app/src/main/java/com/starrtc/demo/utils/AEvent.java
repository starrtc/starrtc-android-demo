package com.starrtc.demo.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;

public class AEvent {
    private static List<EventObj> callBackList = new ArrayList<EventObj>();

    private static Handler mHandler;

    public static void setHandler(Handler handler){
        mHandler = handler;
    }

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
                return;
                //i--;
            }
        }
    }

    public static void notifyListener(final String eventID, final boolean success, final Object object){

        if (Looper.myLooper() == Looper.getMainLooper()) {
            // UI线程
            int i;
            EventObj event;
            for(i=0;i<callBackList.size();i++){
                event = callBackList.get(i);
                if(event.eventID.equals(eventID)){
                    event.eventListener.dispatchEvent(eventID,success,object);
                }
            }
        } else {
            // 非UI线程
            if(mHandler!=null){
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        int i;
                        EventObj event;
                        for(i=0;i<callBackList.size();i++){
                            event = callBackList.get(i);
                            if(event.eventID.equals(eventID)){
                                event.eventListener.dispatchEvent(eventID,success,object);
                            }
                        }
                    }
                });
            }else{
                int i;
                EventObj event;
                for(i=0;i<callBackList.size();i++){
                    event = callBackList.get(i);
                    if(event.eventID.equals(eventID)){
                        event.eventListener.dispatchEvent(eventID,success,object);
                    }
                }
            }
        }

    }

    private static class EventObj{
        IEventListener eventListener;
        String eventID;
    }

    //事件类型在这里定义
    public static final String AEVENT_LOGIN                     = "AEVENT_LOGIN";
    public static final String AEVENT_LOGOUT                    = "AEVENT_LOGOUT";
    public static final String AEVENT_RESET                     = "AEVENT_RESET";

    public static final String AEVENT_GROUP_GOT_LIST            = "AEVENT_GROUP_GOT_LIST";
    public static final String AEVENT_GROUP_GOT_MEMBER_LIST     = "AEVENT_GROUP_GOT_MEMBER_LIST";
    public static final String AEVENT_GOT_ONLINE_USER_LIST      = "AEVENT_GOT_ONLINE_USER_LIST";

    public static final String AEVENT_VOIP_INIT_COMPLETE        = "AEVENT_VOIP_INIT_COMPLETE";
    public static final String AEVENT_VOIP_REV_CALLING          = "AEVENT_VOIP_REV_CALLING";
    public static final String AEVENT_VOIP_REV_CALLING_AUDIO    = "AEVENT_VOIP_REV_CALLING_AUDIO";
    public static final String AEVENT_VOIP_REV_REFUSED          = "AEVENT_VOIP_REV_REFUSED";
    public static final String AEVENT_VOIP_REV_HANGUP           = "AEVENT_VOIP_REV_HANGUP";
    public static final String AEVENT_VOIP_REV_BUSY             = "AEVENT_VOIP_REV_BUSY";
    public static final String AEVENT_VOIP_REV_MISS             = "AEVENT_VOIP_REV_MISS";
    public static final String AEVENT_VOIP_REV_CONNECT          = "AEVENT_VOIP_REV_CONNECT";
    public static final String AEVENT_VOIP_REV_ERROR            = "AEVENT_VOIP_REV_ERROR";
    public static final String AEVENT_VOIP_REV_REALTIME_DATA    = "AEVENT_VOIP_REV_REALTIME_DATA";
    public static final String AEVENT_VOIP_P2P_REV_CALLING      = "AEVENT_VOIP_P2P_REV_CALLING";
    public static final String AEVENT_VOIP_P2P_REV_CALLING_AUDIO= "AEVENT_VOIP_P2P_REV_CALLING_AUDIO";
    public static final String AEVENT_VOIP_TRANS_STATE_CHANGED  = "AEVENT_VOIP_TRANS_STATE_CHANGED";

    public static final String AEVENT_LIVE_ADD_UPLOADER         = "AEVENT_LIVE_ADD_UPLOADER";
    public static final String AEVENT_LIVE_REMOVE_UPLOADER      = "AEVENT_LIVE_REMOVE_UPLOADER";
    public static final String AEVENT_LIVE_ERROR                = "AEVENT_LIVE_ERROR";
    public static final String AEVENT_LIVE_GET_ONLINE_NUMBER    = "AEVENT_LIVE_GET_ONLINE_NUMBER";
    public static final String AEVENT_LIVE_SELF_KICKED          = "AEVENT_LIVE_SELF_KICKED";
    public static final String AEVENT_LIVE_SELF_BANNED          = "AEVENT_LIVE_SELF_BANNED";
    public static final String AEVENT_LIVE_REV_MSG              = "AEVENT_LIVE_REV_MSG";
    public static final String AEVENT_LIVE_REV_PRIVATE_MSG      = "AEVENT_LIVE_REV_PRIVATE_MSG";
    public static final String AEVENT_LIVE_APPLY_LINK           = "AEVENT_LIVE_APPLY_LINK";
    public static final String AEVENT_LIVE_APPLY_LINK_RESULT    = "AEVENT_LIVE_APPLY_LINK_RESULT";
    public static final String AEVENT_LIVE_INVITE_LINK           = "AEVENT_LIVE_INVITE_LINK";
    public static final String AEVENT_LIVE_INVITE_LINK_RESULT    = "AEVENT_LIVE_INVITE_LINK_RESULT";
    public static final String AEVENT_LIVE_SELF_COMMANDED_TO_STOP  = "AEVENT_LIVE_SELF_COMMANDED_TO_STOP";
    public static final String AEVENT_LIVE_REV_REALTIME_DATA    = "AEVENT_LIVE_REV_REALTIME_DATA";
    public static final String AEVENT_LIVE_PUSH_STREAM_ERROR    = "AEVENT_LIVE_PUSH_STREAM_ERROR";

    public static final String AEVENT_SUPER_ROOM_ADD_UPLOADER         = "AEVENT_SUPER_ROOM_ADD_UPLOADER";
    public static final String AEVENT_SUPER_ROOM_REMOVE_UPLOADER      = "AEVENT_SUPER_ROOM_REMOVE_UPLOADER";
    public static final String AEVENT_SUPER_ROOM_ERROR                = "AEVENT_SUPER_ROOM_ERROR";
    public static final String AEVENT_SUPER_ROOM_GET_ONLINE_NUMBER    = "AEVENT_SUPER_ROOM_GET_ONLINE_NUMBER";
    public static final String AEVENT_SUPER_ROOM_SELF_KICKED          = "AEVENT_SUPER_ROOM_SELF_KICKED";
    public static final String AEVENT_SUPER_ROOM_SELF_BANNED          = "AEVENT_SUPER_ROOM_SELF_BANNED";
    public static final String AEVENT_SUPER_ROOM_REV_MSG              = "AEVENT_SUPER_ROOM_REV_MSG";
    public static final String AEVENT_SUPER_ROOM_REV_PRIVATE_MSG      = "AEVENT_SUPER_ROOM_REV_PRIVATE_MSG";
    public static final String AEVENT_SUPER_ROOM_APPLY_LINK           = "AEVENT_SUPER_ROOM_APPLY_LINK";
    public static final String AEVENT_SUPER_ROOM_SELF_COMMANDED_TO_STOP  = "AEVENT_SUPER_ROOM_SELF_COMMANDED_TO_STOP";
    public static final String AEVENT_SUPER_ROOM_REV_REALTIME_DATA    = "AEVENT_SUPER_ROOM_REV_REALTIME_DATA";
    public static final String AEVENT_SUPER_ROOM_PUSH_STREAM_ERROR    = "AEVENT_SUPER_ROOM_PUSH_STREAM_ERROR";

    public static final String AEVENT_MEETING_ADD_UPLOADER          = "AEVENT_MEETING_ADD_UPLOADER";
    public static final String AEVENT_MEETING_REMOVE_UPLOADER       = "AEVENT_MEETING_REMOVE_UPLOADER";
    public static final String AEVENT_MEETING_ERROR                 = "AEVENT_MEETING_ERROR";
    public static final String AEVENT_MEETING_GET_ONLINE_NUMBER     = "AEVENT_MEETING_GET_ONLINE_NUMBER";
    public static final String AEVENT_MEETING_SELF_KICKED           = "AEVENT_MEETING_SELF_KICKED";
    public static final String AEVENT_MEETING_SELF_BANNED           = "AEVENT_MEETING_SELF_BANNED";
    public static final String AEVENT_MEETING_REV_MSG               = "AEVENT_MEETING_REV_MSG";
    public static final String AEVENT_MEETING_REV_PRIVATE_MSG       = "AEVENT_MEETING_REV_PRIVATE_MSG";
    public static final String AEVENT_MEETING_REV_REALTIME_DATA     = "AEVENT_MEETING_REV_REALTIME_DATA";
    public static final String AEVENT_MEETING_PUSH_STREAM_ERROR     = "AEVENT_MEETING_PUSH_STREAM_ERROR";

    public static final String AEVENT_ECHO_FIN                  = "AEVENT_ECHO_FIN";

    public static final String AEVENT_CHATROOM_ERROR            ="AEVENT_CHATROOM_ERROR";
    public static final String AEVENT_CHATROOM_STOP_OK          ="AEVENT_CHATROOM_STOP_OK";
    public static final String AEVENT_CHATROOM_DELETE_OK        ="AEVENT_CHATROOM_DELETE_OK";
    public static final String AEVENT_CHATROOM_SELF_BANNED      ="AEVENT_CHATROOM_SELF_BANNED";
    public static final String AEVENT_CHATROOM_SELF_KICKED      ="AEVENT_CHATROOM_SELF_KICKED";
    public static final String AEVENT_CHATROOM_REV_MSG          ="AEVENT_CHATROOM_REV_MSG";
    public static final String AEVENT_CHATROOM_REV_PRIVATE_MSG  ="AEVENT_CHATROOM_REV_PRIVATE_MSG";
    public static final String AEVENT_CHATROOM_GET_ONLINE_NUMBER="AEVENT_CHATROOM_GET_ONLINE_NUMBER";

    public static final String AEVENT_C2C_REV_MSG               ="AEVENT_C2C_REV_MSG";
    public static final String AEVENT_GROUP_REV_MSG             ="AEVENT_GROUP_REV_MSG";
    public static final String AEVENT_REV_SYSTEM_MSG             ="AEVENT_REV_SYSTEM_MSG";

    public static final String AEVENT_CONN_DEATH               ="AEVENT_CONN_DEATH";
    public static final String AEVENT_USER_KICKED               ="AEVENT_USER_KICKED";
    public static final String AEVENT_USER_ONLINE               ="AEVENT_USER_ONLINE";
    public static final String AEVENT_USER_OFFLINE              ="AEVENT_USER_OFFLINE";

    public static final String AEVENT_RTSP_FORWARD              ="AEVENT_RTSP_FORWARD";
    public static final String AEVENT_RTSP_STOP                 ="AEVENT_RTSP_STOP";
    public static final String AEVENT_RTSP_RESUME               ="AEVENT_RTSP_RESUME";
    public static final String AEVENT_RTSP_DELETE               ="AEVENT_RTSP_DELETE";

    public static final String AEVENT_GOT_LIST                  ="AEVENT_GOT_LIST";
}
