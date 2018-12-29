package com.starrtc.demo.serverAPI;

import android.os.AsyncTask;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.starrtc.demo.demo.MLOC;
import com.starrtc.demo.demo.audiolive.AudioLiveInfo;
import com.starrtc.demo.demo.im.chatroom.ChatroomInfo;
import com.starrtc.demo.demo.im.group.MessageGroupInfo;
import com.starrtc.demo.demo.miniclass.MiniClassInfo;
import com.starrtc.demo.demo.videolive.LiveInfo;
import com.starrtc.demo.demo.videomeeting.MeetingInfo;
import com.starrtc.demo.utils.AEvent;
import com.starrtc.demo.utils.ICallback;
import com.starrtc.demo.utils.StarHttpUtil;

/**
 * Created by zhangjt on 2017/8/17.
 */

public class InterfaceUrls {
    public static String BASE_URL;
    //获取authKey
    public static String LOGIN_URL;
    //会议室列表
    public static String MEETING_LIST_URL;
    //直播列表
    public static String LIVE_LIST_URL;
    //音频直播列表
    public static String AUDIO_LIVE_LIST_URL;
    //小班课列表
    public static String MINI_CLASS_LIST_URL;
    //上报直播间使用的聊天室ID（直播里的文字聊天用了一个聊天室）
    public static String LIVE_SET_CHAT_URL;
    //聊天室列表
    public static String CHATROOM_LIST_URL;
    //自己加入的群列表
    public static String GROUP_LIST_URL;
    //群成员列表
    public static String GROUP_MEMBERS_URL;

    //上报直播
    public static String REPORT_LIVE_INFO_URL;
    //上报语音直播
    public static String REPORT_AUDIO_LIVE_INFO_URL;
    //上报小班课
    public static String REPORT_MINI_CLASS_INFO_URL;
    //上报会议
    public static String REPORT_MEETING_INFO_URL;
    //上报聊天室
    public static String REPORT_CHATROOM_INFO_URL;

    public static void setBaseUrl(String baseUrl) {
        BASE_URL = baseUrl;
        LOGIN_URL = BASE_URL+"/authKey";
        MEETING_LIST_URL = BASE_URL+"/meeting/list";
        LIVE_LIST_URL = BASE_URL+"/live/list";
        AUDIO_LIVE_LIST_URL = BASE_URL+"/audio/list";
        MINI_CLASS_LIST_URL = BASE_URL+"/class/list";
        LIVE_SET_CHAT_URL = BASE_URL+"/live/set_chat";
        CHATROOM_LIST_URL = BASE_URL+"/chat/list";
        GROUP_LIST_URL = BASE_URL+"/group/list_all";
        GROUP_MEMBERS_URL = BASE_URL+"/group/members";
        REPORT_LIVE_INFO_URL = BASE_URL+"/live/store";
        REPORT_AUDIO_LIVE_INFO_URL = BASE_URL+"/audio/store";
        REPORT_MINI_CLASS_INFO_URL = BASE_URL+"/class/store";
        REPORT_MEETING_INFO_URL = BASE_URL+"/meeting/store";
        REPORT_CHATROOM_INFO_URL = BASE_URL+"/chat/store";
    }

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
        StarHttpUtil httpPost = new StarHttpUtil(StarHttpUtil.REQUEST_METHOD_GET);
        httpPost.addListener(new ICallback() {
            @Override
            public void callback(boolean reqSuccess, String statusCode, String data) {
                if(reqSuccess){
                    if(statusCode.equals("1")){
                        try {
                            JSONArray datas = new JSONArray(data);
                            ArrayList<MeetingInfo> res = new ArrayList<MeetingInfo>();
                            for (int i = 0;i<datas.length();i++){
                                MeetingInfo meetingInfo = new MeetingInfo();
                                meetingInfo.createrId = datas.getJSONObject(i).getString("Creator");
                                meetingInfo.meetingName = datas.getJSONObject(i).getString("Name");
                                meetingInfo.meetingId = datas.getJSONObject(i).getString("ID");
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

    //小班课列表
    public static void demoRequestMiniClassList(){
        String url = MINI_CLASS_LIST_URL;
        String params = "";
        StarHttpUtil httpPost = new StarHttpUtil(StarHttpUtil.REQUEST_METHOD_GET);
        httpPost.addListener(new ICallback() {
            @Override
            public void callback(boolean reqSuccess, String statusCode, String data) {
                if(reqSuccess){
                    if(statusCode.equals("1")){
                        try {
                            JSONArray datas = new JSONArray(data);
                            ArrayList<MiniClassInfo> res = new ArrayList<MiniClassInfo>();
                            for (int i = 0;i<datas.length();i++){
                                MiniClassInfo meetingInfo = new MiniClassInfo();
                                meetingInfo.createrId = datas.getJSONObject(i).getString("Creator");
                                meetingInfo.meetingName = datas.getJSONObject(i).getString("Name");
                                meetingInfo.meetingId = datas.getJSONObject(i).getString("ID");
                                res.add(meetingInfo);
                            }
                            AEvent.notifyListener(AEvent.AEVENT_MINI_CLASS_GOT_LIST,true,res);
                            return;
                        } catch (JSONException e) {
                            AEvent.notifyListener(AEvent.AEVENT_MINI_CLASS_GOT_LIST,false,"数据解析失败");
                            e.printStackTrace();
                        }
                    }
                }
                AEvent.notifyListener(AEvent.AEVENT_MINI_CLASS_GOT_LIST,false,"数据解析失败");

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
        StarHttpUtil httpPost = new StarHttpUtil(StarHttpUtil.REQUEST_METHOD_GET);
        httpPost.addListener(new ICallback() {
            @Override
            public void callback(boolean reqSuccess, String statusCode, String data) {
                if(reqSuccess){
                    if(statusCode.equals("1")){
                        try {
                            JSONArray datas = new JSONArray(data);
                            ArrayList<ChatroomInfo> res = new ArrayList<ChatroomInfo>();
                            for (int i = 0;i<datas.length();i++){
                                ChatroomInfo chatroomInfo = new ChatroomInfo();
                                chatroomInfo.createrId = datas.getJSONObject(i).getString("Creator");
                                chatroomInfo.roomId = datas.getJSONObject(i).getString("ID");
                                chatroomInfo.roomName = datas.getJSONObject(i).getString("Name");
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

        StarHttpUtil httpPost = new StarHttpUtil(StarHttpUtil.REQUEST_METHOD_GET);
        httpPost.addListener(new ICallback() {
            @Override
            public void callback(boolean reqSuccess, String statusCode, String data) {
                if(reqSuccess){
                    if(statusCode.equals("1")){
                        try {
                            JSONArray datas = new JSONArray(data);
                            ArrayList<LiveInfo> res = new ArrayList<LiveInfo>();
                            for (int i = 0;i<datas.length();i++){
                                LiveInfo videoLiveInfo = new LiveInfo();
                                videoLiveInfo.createrId = datas.getJSONObject(i).getString("Creator");
                                videoLiveInfo.liveName = datas.getJSONObject(i).getString("Name");
                                videoLiveInfo.liveId = datas.getJSONObject(i).getString("ID");
                                videoLiveInfo.isLiveOn = datas.getJSONObject(i).getString("liveState");
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
    //音频直播列表
    public static void demoRequestAudioLiveList(){
        String url = AUDIO_LIVE_LIST_URL;
        String params = "";

        StarHttpUtil httpPost = new StarHttpUtil(StarHttpUtil.REQUEST_METHOD_GET);
        httpPost.addListener(new ICallback() {
            @Override
            public void callback(boolean reqSuccess, String statusCode, String data) {
                if(reqSuccess){
                    if(statusCode.equals("1")){
                        try {
                            JSONArray datas = new JSONArray(data);
                            ArrayList<AudioLiveInfo> res = new ArrayList<AudioLiveInfo>();
                            for (int i = 0;i<datas.length();i++){
                                AudioLiveInfo videoLiveInfo = new AudioLiveInfo();
                                videoLiveInfo.createrId = datas.getJSONObject(i).getString("Creator");
                                videoLiveInfo.liveName = datas.getJSONObject(i).getString("Name");
                                videoLiveInfo.liveId = datas.getJSONObject(i).getString("ID");
                                videoLiveInfo.isLiveOn = datas.getJSONObject(i).getString("liveState");
                                res.add(videoLiveInfo);
                            }
                            AEvent.notifyListener(AEvent.AEVENT_AUDIO_LIVE_GOT_LIST,true,res);
                            return;
                        } catch (JSONException e) {
                            AEvent.notifyListener(AEvent.AEVENT_AUDIO_LIVE_GOT_LIST,false,"数据解析失败");
                            e.printStackTrace();
                        }
                    }
                }
                AEvent.notifyListener(AEvent.AEVENT_AUDIO_LIVE_GOT_LIST,false,"数据解析失败");

            }
        });
        Bundle bundle = new Bundle();
        bundle.putString(StarHttpUtil.URL,url);
        bundle.putString(StarHttpUtil.DATA,params);
        httpPost.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,bundle);
    }
    //互动直播
    public static void demoReportLive(String liveID,String liveName,String creatorID){
        String url = REPORT_LIVE_INFO_URL;
        String params = "";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ID",liveID);
            jsonObject.put("Name",liveName);
            jsonObject.put("Creator",creatorID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params = jsonObject.toString();

        StarHttpUtil httpPost = new StarHttpUtil(StarHttpUtil.REQUEST_METHOD_POST);
        httpPost.addListener(new ICallback() {
            @Override
            public void callback(boolean reqSuccess, String statusCode, String data) {

            }
        });
        Bundle bundle = new Bundle();
        bundle.putString(StarHttpUtil.URL,url);
        bundle.putString(StarHttpUtil.DATA,params);
        httpPost.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,bundle);
    }
    //上报语音直播
    public static void demoReportAudioLive(String liveID,String liveName,String creatorID){
        String url = REPORT_AUDIO_LIVE_INFO_URL;
        String params = "";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ID",liveID);
            jsonObject.put("Name",liveName);
            jsonObject.put("Creator",creatorID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params = jsonObject.toString();

        StarHttpUtil httpPost = new StarHttpUtil(StarHttpUtil.REQUEST_METHOD_POST);
        httpPost.addListener(new ICallback() {
            @Override
            public void callback(boolean reqSuccess, String statusCode, String data) {

            }
        });
        Bundle bundle = new Bundle();
        bundle.putString(StarHttpUtil.URL,url);
        bundle.putString(StarHttpUtil.DATA,params);
        httpPost.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,bundle);
    }
    //上报小班课
    public static void demoReportMiniClass(String liveID,String liveName,String creatorID){
        String url = REPORT_MINI_CLASS_INFO_URL;
        String params = "";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ID",liveID);
            jsonObject.put("Name",liveName);
            jsonObject.put("Creator",creatorID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params = jsonObject.toString();

        StarHttpUtil httpPost = new StarHttpUtil(StarHttpUtil.REQUEST_METHOD_POST);
        httpPost.addListener(new ICallback() {
            @Override
            public void callback(boolean reqSuccess, String statusCode, String data) {

            }
        });
        Bundle bundle = new Bundle();
        bundle.putString(StarHttpUtil.URL,url);
        bundle.putString(StarHttpUtil.DATA,params);
        httpPost.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,bundle);
    }

    //会议室
    public static void demoReportMeeting(String liveID,String liveName,String creatorID){
        String url = REPORT_MEETING_INFO_URL;
        String params = "";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ID",liveID);
            jsonObject.put("Name",liveName);
            jsonObject.put("Creator",creatorID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params = jsonObject.toString();

        StarHttpUtil httpPost = new StarHttpUtil(StarHttpUtil.REQUEST_METHOD_POST);
        httpPost.addListener(new ICallback() {
            @Override
            public void callback(boolean reqSuccess, String statusCode, String data) {

            }
        });
        Bundle bundle = new Bundle();
        bundle.putString(StarHttpUtil.URL,url);
        bundle.putString(StarHttpUtil.DATA,params);
        httpPost.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,bundle);
    }

    //聊天室
    public static void demoReportChatroom(String liveID,String liveName,String creatorID){
        String url = REPORT_CHATROOM_INFO_URL;
        String params = "";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ID",liveID);
            jsonObject.put("Name",liveName);
            jsonObject.put("Creator",creatorID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params = jsonObject.toString();

        StarHttpUtil httpPost = new StarHttpUtil(StarHttpUtil.REQUEST_METHOD_POST);
        httpPost.addListener(new ICallback() {
            @Override
            public void callback(boolean reqSuccess, String statusCode, String data) {

            }
        });
        Bundle bundle = new Bundle();
        bundle.putString(StarHttpUtil.URL,url);
        bundle.putString(StarHttpUtil.DATA,params);
        httpPost.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,bundle);
    }



}
