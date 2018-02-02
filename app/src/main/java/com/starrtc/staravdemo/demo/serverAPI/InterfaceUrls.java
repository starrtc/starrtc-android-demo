package com.starrtc.staravdemo.demo.serverAPI;

import android.os.AsyncTask;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.starrtc.staravdemo.demo.MLOC;
import com.starrtc.staravdemo.demo.im.chatroom.ChatroomInfo;
import com.starrtc.staravdemo.demo.im.group.MessageGroupInfo;
import com.starrtc.staravdemo.demo.videolive.LiveInfo;
import com.starrtc.staravdemo.demo.videomeeting.MeetingInfo;
import com.starrtc.staravdemo.utils.AEvent;
import com.starrtc.staravdemo.utils.ICallback;
import com.starrtc.staravdemo.utils.StarHttpUtil;

/**
 * Created by zhangjt on 2017/8/17.
 */

public class InterfaceUrls {
    public static final String BASE_URL = "https://api.starrtc.com";
    public static final String LOGIN_URL = BASE_URL+"/demo/authKey";
//    public static final String LOGIN_URL = BASE_URL+"/basic/authKey";

    public static final String MEETING_LIST_URL = BASE_URL+"/demo/meeting/list";
    public static final String LIVE_LIST_URL = BASE_URL+"/demo/live/list";
    public static final String LIVE_SET_CHAT_URL = BASE_URL+"/demo/live/set_chat";
    public static final String CHATROOM_LIST_URL = BASE_URL+"/demo/chat/list";
//    public static final String GROUP_LIST_URL = BASE_URL+"/demo/group/list";
    public static final String GROUP_LIST_URL = BASE_URL+"/demo/group/list_all";
    public static final String GROUP_MEMBERS_URL = BASE_URL+"/demo/group/members";

    public static void demoLogin(String userId){
        String url = LOGIN_URL+"?userid="+userId;
        String params = "";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("starUid",userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params = jsonObject.toString();

        StarHttpUtil httpPost = new StarHttpUtil(StarHttpUtil.REQUEST_METHOD_GET);
        httpPost.addListener(new ICallback() {
            @Override
            public void callback(boolean reqSuccess, String statusCode, String data) {
                if(reqSuccess){
                    if(statusCode.equals("1")){
                        MLOC.authKey = data;
                        MLOC.starUid = MLOC.agentId+"_"+MLOC.userId;
                        AEvent.notifyListener(AEvent.AEVENT_LOGIN,true,"登录成功");
                        return;
                    }
                }
                AEvent.notifyListener(AEvent.AEVENT_LOGIN,false,"登录失败！");
            }
        });
        Bundle bundle = new Bundle();
        bundle.putString(StarHttpUtil.URL,url);
        bundle.putString(StarHttpUtil.DATA,params);
        httpPost.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,bundle);
    }
    //会议室列表
    public static void demoRequestMeetingList(){
        String url = MEETING_LIST_URL;
        String params = "";
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("agentId",agentId);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        params = jsonObject.toString();

        StarHttpUtil httpPost = new StarHttpUtil(StarHttpUtil.REQUEST_METHOD_GET);
        httpPost.addListener(new ICallback() {
            @Override
            public void callback(boolean reqSuccess, String statusCode, String data) {
                if(reqSuccess){
                    //{"status":1,"data":[{"roomId":"meeting_demo93","channelId":"10Ko5gjDr@3eaaGb","userId":"demo92"},{"roomId":"meeting_demo92","channelId":"10Ko5gjDr@3eaaGa","userId":"demo92"}]}
                    if(statusCode.equals("1")){
                        try {
                            JSONArray datas = new JSONArray(data);
                            ArrayList<MeetingInfo> res = new ArrayList<MeetingInfo>();
                            for (int i = 0;i<datas.length();i++){
                                MeetingInfo meetingInfo = new MeetingInfo();
                                meetingInfo.createrId = datas.getJSONObject(i).getString("userId");
                                meetingInfo.meetingId = datas.getJSONObject(i).getString("roomId");
                                meetingInfo.channelId = datas.getJSONObject(i).getString("channelId");
                                res.add(meetingInfo);
                            }
                            AEvent.notifyListener(AEvent.AEVENT_MEETING_GOT_LIST,true,res);
                            return;
                        } catch (JSONException e) {
                            AEvent.notifyListener(AEvent.AEVENT_MEETING_GOT_LIST,false,"数据解析失败");
                            e.printStackTrace();
                        }
                    }
                }
                AEvent.notifyListener(AEvent.AEVENT_MEETING_GOT_LIST,false,"数据解析失败");

            }
        });
        Bundle bundle = new Bundle();
        bundle.putString(StarHttpUtil.URL,url);
        bundle.putString(StarHttpUtil.DATA,params);
        httpPost.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,bundle);
    }

    //聊天室列表
    public static void demoRequestChatroomList(){
        String url = CHATROOM_LIST_URL;
        String params = "";
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("agentId",agentId);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        params = jsonObject.toString();

        StarHttpUtil httpPost = new StarHttpUtil(StarHttpUtil.REQUEST_METHOD_GET);
        httpPost.addListener(new ICallback() {
            @Override
            public void callback(boolean reqSuccess, String statusCode, String data) {
                if(reqSuccess){
                    //{"status":1,"data":[{"roomId":"meeting_demo93","channelId":"10Ko5gjDr@3eaaGb","userId":"demo92"},{"roomId":"meeting_demo92","channelId":"10Ko5gjDr@3eaaGa","userId":"demo92"}]}
                    if(statusCode.equals("1")){
                        try {
                            JSONArray datas = new JSONArray(data);
                            ArrayList<ChatroomInfo> res = new ArrayList<ChatroomInfo>();
                            for (int i = 0;i<datas.length();i++){
                                ChatroomInfo chatroomInfo = new ChatroomInfo();
                                chatroomInfo.createrId = datas.getJSONObject(i).getString("userId");
                                chatroomInfo.roomId = datas.getJSONObject(i).getString("roomId");
                                chatroomInfo.roomName = datas.getJSONObject(i).getString("name");
                                res.add(chatroomInfo);
                            }
                            AEvent.notifyListener(AEvent.AEVENT_CHATROOM_GOT_LIST,true,res);
                            return;
                        } catch (JSONException e) {
                            AEvent.notifyListener(AEvent.AEVENT_CHATROOM_GOT_LIST,false,"数据解析失败");
                            e.printStackTrace();
                        }
                    }
                }
                AEvent.notifyListener(AEvent.AEVENT_CHATROOM_GOT_LIST,false,"数据解析失败");

            }
        });
        Bundle bundle = new Bundle();
        bundle.putString(StarHttpUtil.URL,url);
        bundle.putString(StarHttpUtil.DATA,params);
        httpPost.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,bundle);
    }

    //群列表
    public static void demoRequestGroupList(String userid){
        String url = GROUP_LIST_URL+"?userid="+userid;
        String params = "";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userid",userid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params = jsonObject.toString();

        StarHttpUtil httpPost = new StarHttpUtil(StarHttpUtil.REQUEST_METHOD_GET);
        httpPost.addListener(new ICallback() {
            @Override
            public void callback(boolean reqSuccess, String statusCode, String data) {
                if(reqSuccess){
                    //{"status":1,"data":[{"roomId":"meeting_demo93","channelId":"10Ko5gjDr@3eaaGb","userId":"demo92"},{"roomId":"meeting_demo92","channelId":"10Ko5gjDr@3eaaGa","userId":"demo92"}]}
                    if(statusCode.equals("1")){
                        try {
                            JSONArray datas = new JSONArray(data);
                            ArrayList<MessageGroupInfo> res = new ArrayList<MessageGroupInfo>();
                            for (int i = 0;i<datas.length();i++){
                                MessageGroupInfo groupInfo = new MessageGroupInfo();
                                groupInfo.createrId = datas.getJSONObject(i).getString("creator");
                                groupInfo.groupId = datas.getJSONObject(i).getString("groupId");
                                groupInfo.groupName = datas.getJSONObject(i).getString("groupName");
                                res.add(groupInfo);
                            }
                            AEvent.notifyListener(AEvent.AEVENT_GROUP_GOT_LIST,true,res);
                            return;
                        } catch (JSONException e) {
                            AEvent.notifyListener(AEvent.AEVENT_GROUP_GOT_LIST,false,"数据解析失败");
                            e.printStackTrace();
                        }
                    }
                }
                AEvent.notifyListener(AEvent.AEVENT_GROUP_GOT_LIST,false,"数据解析失败");

            }
        });
        Bundle bundle = new Bundle();
        bundle.putString(StarHttpUtil.URL,url);
        bundle.putString(StarHttpUtil.DATA,params);
        httpPost.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,bundle);
    }
    //群成员列表
    public static void demoRequestGroupMembers(String groupId){
        String url = GROUP_MEMBERS_URL+"?groupId="+groupId;
        String params = "";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("groupId",groupId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params = jsonObject.toString();

        StarHttpUtil httpPost = new StarHttpUtil(StarHttpUtil.REQUEST_METHOD_GET);
        httpPost.addListener(new ICallback() {
            @Override
            public void callback(boolean reqSuccess, String statusCode, String data) {
                if(reqSuccess){
                    if(statusCode.equals("1")){
                        try {
                            JSONArray datas = new JSONArray(data);
                            ArrayList<String> res = new ArrayList<String>();
                            for (int i = 0;i<datas.length();i++){
                                String uid = datas.getJSONObject(i).getString("userId");
                                res.add(uid);
                            }
                            AEvent.notifyListener(AEvent.AEVENT_GROUP_GOT_MEMBER_LIST,true,res);
                            return;
                        } catch (JSONException e) {
                            AEvent.notifyListener(AEvent.AEVENT_GROUP_GOT_MEMBER_LIST,false,"数据解析失败");
                            e.printStackTrace();
                        }
                    }
                }
                AEvent.notifyListener(AEvent.AEVENT_GROUP_GOT_MEMBER_LIST,false,"数据解析失败");

            }
        });
        Bundle bundle = new Bundle();
        bundle.putString(StarHttpUtil.URL,url);
        bundle.putString(StarHttpUtil.DATA,params);
        httpPost.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,bundle);
    }

    //互动直播列表
    public static void demoRequestLiveList(){
        String url = LIVE_LIST_URL;
        String params = "";
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("agentId",agentId);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        params = jsonObject.toString();

        StarHttpUtil httpPost = new StarHttpUtil(StarHttpUtil.REQUEST_METHOD_GET);
        httpPost.addListener(new ICallback() {
            @Override
            public void callback(boolean reqSuccess, String statusCode, String data) {
                if(reqSuccess){
                    //{"status":1,
                    // "data":[
                    // {"liveId":"live_demo8140","channelId":"10Ko5gjD7vD5aaaa","chatroomId":"","userId":"demo8140"},
                    // {"liveId":"live_demo4087","channelId":"10Ko5gjDzrGxaaea","chatroomId":"chatroom123","userId":"demo4087"},
                    // {"liveId":"live_demo360","channelId":"10Ko5gjDzQDfaaCa","chatroomId":"","userId":"demo360"}
                    // ]
                    // }
                    if(statusCode.equals("1")){
                        try {
                            JSONArray datas = new JSONArray(data);
                            ArrayList<LiveInfo> res = new ArrayList<LiveInfo>();
                            for (int i = 0;i<datas.length();i++){
                                LiveInfo videoLiveInfo = new LiveInfo();
                                videoLiveInfo.createrId = datas.getJSONObject(i).getString("userId");
                                videoLiveInfo.liveId = datas.getJSONObject(i).getString("liveId");
                                videoLiveInfo.channelId = datas.getJSONObject(i).getString("channelId");
                                videoLiveInfo.chatroomId = datas.getJSONObject(i).getString("chatroomId");
                                res.add(videoLiveInfo);
                            }
                            AEvent.notifyListener(AEvent.AEVENT_LIVE_GOT_LIST,true,res);
                            return;
                        } catch (JSONException e) {
                            AEvent.notifyListener(AEvent.AEVENT_LIVE_GOT_LIST,false,"数据解析失败");
                            e.printStackTrace();
                        }
                    }
                }
                AEvent.notifyListener(AEvent.AEVENT_LIVE_GOT_LIST,false,"数据解析失败");

            }
        });
        Bundle bundle = new Bundle();
        bundle.putString(StarHttpUtil.URL,url);
        bundle.putString(StarHttpUtil.DATA,params);
        httpPost.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,bundle);
    }
    //互动直播列表
    public static void demoReportChatAndLive(String channelId,String chatroomId){
        String url = LIVE_SET_CHAT_URL;
        String params = "";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("channel_id",channelId);
            jsonObject.put("chatroom_id",chatroomId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params = jsonObject.toString();

        StarHttpUtil httpPost = new StarHttpUtil(StarHttpUtil.REQUEST_METHOD_POST);
        httpPost.addListener(new ICallback() {
            @Override
            public void callback(boolean reqSuccess, String statusCode, String data) {
//                    {
//                        "status":1, "data":"success"
//                    }
            }
        });
        Bundle bundle = new Bundle();
        bundle.putString(StarHttpUtil.URL,url);
        bundle.putString(StarHttpUtil.DATA,params);
        httpPost.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,bundle);
    }



}
