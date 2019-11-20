package com.starrtc.demo.serverAPI;

import android.os.AsyncTask;
import android.os.Bundle;

import com.starrtc.demo.demo.MLOC;
import com.starrtc.demo.demo.im.group.MessageGroupInfo;
import com.starrtc.demo.utils.AEvent;
import com.starrtc.demo.utils.ICallback;
import com.starrtc.demo.utils.StarHttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by zhangjt on 2017/8/17.
 */

public class InterfaceUrls {
    //存列表
    public static void demoSaveToList(String userId,int listType,String id,String data){
        String url = MLOC.LIST_SAVE_URL;
        String params = "";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId",userId);
            jsonObject.put("listType",listType);
            jsonObject.put("roomId",id);
            jsonObject.put("data",data);
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
    //刪除列表
    public static void demoDeleteFromList(String userId,int listType,String id){
        String url = MLOC.LIST_DELETE_URL;
        String params = "";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId",userId);
            jsonObject.put("listType",listType);
            jsonObject.put("roomId",id);
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
    //查询列表
    public static void demoQueryList(String listType){
        String url = MLOC.LIST_QUERY_URL;
        String params = "";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("listTypes",listType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params = jsonObject.toString();

        StarHttpUtil httpPost = new StarHttpUtil(StarHttpUtil.REQUEST_METHOD_POST);
        httpPost.addListener(new ICallback() {
            @Override
            public void callback(boolean reqSuccess, String statusCode, String data) {
                if(reqSuccess && statusCode.equals("1")){
                    try {
                        JSONArray jsonArray = new JSONArray(data);
                        if(jsonArray!=null&&jsonArray.length()>0){
                            AEvent.notifyListener(AEvent.AEVENT_GOT_LIST,true,jsonArray);
                        }else{
                            AEvent.notifyListener(AEvent.AEVENT_GOT_LIST,false,data);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        AEvent.notifyListener(AEvent.AEVENT_GOT_LIST,false,e.getMessage());
                    }
                }else{
                    AEvent.notifyListener(AEvent.AEVENT_GOT_LIST,false,data);
                }
            }
        });
        Bundle bundle = new Bundle();
        bundle.putString(StarHttpUtil.URL,url);
        bundle.putString(StarHttpUtil.DATA,params);
        httpPost.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,bundle);
    }

    //转发rtsp流
    public static void demoPushStreamUrl(String userId,String server, String name, String chatroomId, int listtype, String streamType, String streamUrl){
        String url = "http://"+server+"/start?userId="+userId+"&streamType="+streamType+"&streamUrl="+streamUrl+"&roomLiveType="+listtype+"&roomId="+chatroomId+"&extra="+name;
        String params = "";
        StarHttpUtil httpGet = new StarHttpUtil(StarHttpUtil.REQUEST_METHOD_GET);
        httpGet.addListener(new ICallback() {
            @Override
            public void callback(boolean reqSuccess, String statusCode, String data) {
                if(reqSuccess){
                    AEvent.notifyListener(AEvent.AEVENT_RTSP_FORWARD,true,data);
                }else{
                    AEvent.notifyListener(AEvent.AEVENT_RTSP_FORWARD,false,statusCode+" "+data);
                }
            }
        });
        Bundle bundle = new Bundle();
        bundle.putString(StarHttpUtil.URL,url);
        bundle.putString(StarHttpUtil.DATA,params);
        httpGet.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,bundle);
    }
    //恢复转发rtsp流 //url://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov
    public static void demoResumePushRtsp(String userId,String server,String channelId,String rtsp,String streamType){
        String url = "http://"+server+"/start?userId="+userId+"&streamType="+streamType+"&streamUrl="+rtsp+"&channelId="+channelId;
        String params = "";
        StarHttpUtil httpGet = new StarHttpUtil(StarHttpUtil.REQUEST_METHOD_GET);
        httpGet.addListener(new ICallback() {
            @Override
            public void callback(boolean reqSuccess, String statusCode, String data) {
                if(reqSuccess){
                    AEvent.notifyListener(AEvent.AEVENT_RTSP_RESUME,true,null);
                }else{
                    AEvent.notifyListener(AEvent.AEVENT_RTSP_RESUME,false,statusCode+" "+data);
                }
            }
        });
        Bundle bundle = new Bundle();
        bundle.putString(StarHttpUtil.URL,url);
        bundle.putString(StarHttpUtil.DATA,params);
        httpGet.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,bundle);
    }
    //停止转发rtsp流 //aisee.f3322.org:19932
    public static void demoStopPushRtsp(String userId,String server,String channelId){
        String url = "http://"+server+"/close?userId="+userId+"&channelId="+channelId;
        String params = "";
        StarHttpUtil httpGet = new StarHttpUtil(StarHttpUtil.REQUEST_METHOD_GET);
        httpGet.addListener(new ICallback() {
            @Override
            public void callback(boolean reqSuccess, String statusCode, String data) {
                if(reqSuccess){
                    AEvent.notifyListener(AEvent.AEVENT_RTSP_STOP,true,null);
                }else{
                    AEvent.notifyListener(AEvent.AEVENT_RTSP_STOP,false,statusCode+" "+data);
                }
            }
        });
        Bundle bundle = new Bundle();
        bundle.putString(StarHttpUtil.URL,url);
        bundle.putString(StarHttpUtil.DATA,params);
        httpGet.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,bundle);
    }
    //删除rtsp流记录
    public static void demoDeleteRtsp(String userId,String server,String channelId){
        String url = "http://"+server+"/delete?userId="+userId+"&channelId="+channelId;
        String params = "";
        StarHttpUtil httpGet = new StarHttpUtil(StarHttpUtil.REQUEST_METHOD_GET);
        httpGet.addListener(new ICallback() {
            @Override
            public void callback(boolean reqSuccess, String statusCode, String data) {
                if(reqSuccess){
                    AEvent.notifyListener(AEvent.AEVENT_RTSP_DELETE,true,null);
                }else{
                    AEvent.notifyListener(AEvent.AEVENT_RTSP_DELETE,false,statusCode+" "+data);
                }
            }
        });
        Bundle bundle = new Bundle();
        bundle.putString(StarHttpUtil.URL,url);
        bundle.putString(StarHttpUtil.DATA,params);
        httpGet.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,bundle);
    }

    //获取IM群列表
    public static void demoQueryImGroupList(String userId){
        String url = MLOC.IM_GROUP_LIST_URL+"?userId="+userId;
        String params = "";
        StarHttpUtil httpGet = new StarHttpUtil(StarHttpUtil.REQUEST_METHOD_GET);
        httpGet.addListener(new ICallback() {
            @Override
            public void callback(boolean reqSuccess, String statusCode, String data) {
                if(reqSuccess&&statusCode.equals("1")){
                    try {
                        //{"status":1,"data":[{"groupName":"\u5403\u918b","creator":"448999","groupId":"100391"}]}
                        JSONArray datas = new JSONArray(data);
                        if(datas!=null&&datas.length()>0){
                            ArrayList<MessageGroupInfo> list = new ArrayList<>();
                            for(int i = 0;i<datas.length();i++){
                                JSONObject item = datas.getJSONObject(i);
                                MessageGroupInfo mgi = new MessageGroupInfo();
                                mgi.createrId = item.getString("creator");
                                mgi.groupId = item.getString("groupId");
                                mgi.groupName = item.getString("groupName");
                                list.add(mgi);
                            }
                            AEvent.notifyListener(AEvent.AEVENT_GROUP_GOT_LIST,true,list);
                        }else{
                            AEvent.notifyListener(AEvent.AEVENT_GROUP_GOT_LIST,false,data);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        AEvent.notifyListener(AEvent.AEVENT_GROUP_GOT_LIST,false,data);
                    }
                }else{
                    AEvent.notifyListener(AEvent.AEVENT_GROUP_GOT_LIST,false,statusCode+" "+data);
                }
            }
        });
        Bundle bundle = new Bundle();
        bundle.putString(StarHttpUtil.URL,url);
        bundle.putString(StarHttpUtil.DATA,params);
        httpGet.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,bundle);
    }

    //获取IM群成员列表+免打扰状态
    public static void demoQueryImGroupInfo(String userId,String groupId){
        String url = MLOC.IM_GROUP_INFO_URL+"?userId="+userId+"&groupId="+groupId;
        String params = "";
        StarHttpUtil httpGet = new StarHttpUtil(StarHttpUtil.REQUEST_METHOD_GET);
        httpGet.addListener(new ICallback() {
            @Override
            public void callback(boolean reqSuccess, String statusCode, String data) {
                if(reqSuccess&&statusCode.equals("1")){
                    try {
                        //{"status":1,"data":{"userIdList":"448999","isIgnore":"0"}}
                        JSONObject jsonObject = new JSONObject(data);
                        if(jsonObject!=null){
                            AEvent.notifyListener(AEvent.AEVENT_GROUP_GOT_MEMBER_LIST,true,jsonObject);
                        }else{
                            AEvent.notifyListener(AEvent.AEVENT_GROUP_GOT_MEMBER_LIST,false,data);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        AEvent.notifyListener(AEvent.AEVENT_GROUP_GOT_MEMBER_LIST,false,data);
                    }

                }else{
                    AEvent.notifyListener(AEvent.AEVENT_GROUP_GOT_MEMBER_LIST,false,statusCode+" "+data);
                }
            }
        });
        Bundle bundle = new Bundle();
        bundle.putString(StarHttpUtil.URL,url);
        bundle.putString(StarHttpUtil.DATA,params);
        httpGet.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,bundle);
    }
}
