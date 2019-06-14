package com.starrtc.demo.demo.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.starrtc.demo.demo.MLOC;
import com.starrtc.demo.demo.beauty.DemoVideoSourceCallback;
import com.starrtc.demo.demo.p2p.VoipP2PRingingActivity;
import com.starrtc.demo.demo.voip.VoipAudioRingingActivity;
import com.starrtc.demo.demo.voip.VoipRingingActivity;
import com.starrtc.demo.listener.XHChatManagerListener;
import com.starrtc.demo.listener.XHGroupManagerListener;
import com.starrtc.demo.listener.XHLoginManagerListener;
import com.starrtc.demo.listener.XHVoipManagerListener;
import com.starrtc.demo.listener.XHVoipP2PManagerListener;
import com.starrtc.demo.serverAPI.InterfaceUrls;
import com.starrtc.demo.utils.AEvent;
import com.starrtc.demo.utils.IEventListener;
import com.starrtc.starrtcsdk.api.XHClient;
import com.starrtc.starrtcsdk.api.XHCustomConfig;
import com.starrtc.starrtcsdk.apiInterface.IXHErrorCallback;
import com.starrtc.starrtcsdk.apiInterface.IXHResultCallback;
import com.starrtc.starrtcsdk.core.videosrc.XHVideoSourceManager;

import java.util.Random;


/**
 * Created by zhangjt on 2017/8/6.
 */

public class KeepLiveService extends Service implements IEventListener {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initSDK();
        return super.onStartCommand(intent, flags, startId);
    }

    private void initSDK(){
        MLOC.init(this);
        if(MLOC.SERVER_TYPE.equals(MLOC.SERVER_TYPE_PUBLIC)){
            init();
        }else{
            initFree();
        }
    }

    private boolean isLogin = false;
    private final boolean checkNetState = false;
    //正常SDK登录
    private void init(){
        MLOC.d("KeepLiveService","initFree");

        isLogin = XHClient.getInstance().getIsOnline();
        if(!isLogin){
            if(MLOC.userId.equals("")){
                MLOC.userId = ""+(new Random().nextInt(900000)+100000);
                MLOC.saveUserId(MLOC.userId);
            }
            addListener();
            //初始化 完整版
            XHCustomConfig customConfig =  XHCustomConfig.getInstance();
            customConfig.setAppId(MLOC.agentId);
            customConfig.setLoginServerUrl(MLOC.STAR_LOGIN_URL);
            customConfig.setChatroomScheduleUrl(MLOC.CHAT_ROOM_SCHEDULE_URL);
            customConfig.setLiveSrcScheduleUrl(MLOC.LIVE_SRC_SCHEDULE_URL);
            customConfig.setLiveVdnScheduleUrl(MLOC.LIVE_VDN_SCHEDULE_URL);
            customConfig.setImScheduleUrl(MLOC.IM_SCHEDULE_URL);
            customConfig.setVoipServerUrl(MLOC.VOIP_SCHEDULE_URL);
            customConfig.initSDK(this, MLOC.userId, new IXHErrorCallback() {
                @Override
                public void error(final String errMsg, Object data) {
                    MLOC.showMsg(KeepLiveService.this,errMsg);
                }
            },new Handler());
//            customConfig.setLogDirPath(Environment.getExternalStorageDirectory().getPath()+"/starrtcLog");
            customConfig.setDefConfigOpenGLESEnable(false);
//            customConfig.setDefConfigCamera2Enable(false);
            XHClient.getInstance().getChatManager().addListener(new XHChatManagerListener());
            XHClient.getInstance().getGroupManager().addListener(new XHGroupManagerListener());
            XHClient.getInstance().getVoipManager().addListener(new XHVoipManagerListener());
            XHClient.getInstance().getVoipP2PManager().addListener(new XHVoipP2PManagerListener());
            XHClient.getInstance().getLoginManager().addListener(new XHLoginManagerListener());
            XHVideoSourceManager.getInstance().setVideoSourceCallback(new DemoVideoSourceCallback());
            checkNetworkConnectAndLogin();
        }

    }

    //开放版SDK初始化
    private void initFree(){
        MLOC.d("KeepLiveService","initFree");
        isLogin = XHClient.getInstance().getIsOnline();
        if(!isLogin){
            if(MLOC.userId.equals("")){
                MLOC.userId = ""+(new Random().nextInt(900000)+100000);
                MLOC.saveUserId(MLOC.userId);
            }
            addListener();
            //初始化 开放版 无调度 直接指定Server地址
            XHCustomConfig customConfig =  XHCustomConfig.getInstance();
            customConfig.setChatroomServerUrl(MLOC.CHATROOM_SERVER_URL);
            customConfig.setLiveSrcServerUrl(MLOC.LIVE_SRC_SERVER_URL);
            customConfig.setLiveVdnServerUrl(MLOC.LIVE_VDN_SERVER_URL);
            customConfig.setImServereUrl(MLOC.IM_SERVER_URL);
            customConfig.setVoipServerUrl(MLOC.VOIP_SERVER_URL);
            customConfig.initSDKForFree(this, MLOC.userId, new IXHErrorCallback() {
                @Override
                public void error(final String errMsg, Object data) {
                    MLOC.showMsg(KeepLiveService.this,errMsg);
                }
            },new Handler());
//        customConfig.setLogDirPath(Environment.getExternalStorageDirectory().getPath()+"/starrtcLog");
            customConfig.setDefConfigOpenGLESEnable(false);
            customConfig.setDefConfigCamera2Enable(false);
            XHClient.getInstance().getChatManager().addListener(new XHChatManagerListener());
            XHClient.getInstance().getGroupManager().addListener(new XHGroupManagerListener());
            XHClient.getInstance().getVoipManager().addListener(new XHVoipManagerListener());
            XHClient.getInstance().getVoipP2PManager().addListener(new XHVoipP2PManagerListener());
            XHClient.getInstance().getLoginManager().addListener(new XHLoginManagerListener());
            XHVideoSourceManager.getInstance().setVideoSourceCallback(new DemoVideoSourceCallback());

            XHClient.getInstance().getLoginManager().loginFree(new IXHResultCallback() {
                @Override
                public void success(Object data) {
                    isLogin = true;
                }
                @Override
                public void failed(final String errMsg) {
                    MLOC.d("KeepLiveService",errMsg);
                    MLOC.showMsg(KeepLiveService.this,errMsg);
                }
            });
        }

    }
    private void checkNetworkConnectAndLogin(){
        if(isConnectInternet(this)){
            InterfaceUrls.demoLogin(MLOC.userId);
//                loginPublicTest();
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkNetworkConnectAndLogin();
                }
            },3000);
        }
    }

    private void loginPublicTest(){
        XHClient.getInstance().getLoginManager().loginPublic( new IXHResultCallback() {
            @Override
            public void success(Object data) {
                isLogin = true;
            }

            @Override
            public void failed(final String errMsg) {
                MLOC.d("",errMsg);
                MLOC.showMsg(KeepLiveService.this,errMsg);
            }
        });
    }

    public boolean isConnectInternet(Context mContext) {
        if(!checkNetState){
            return true;
        }else{
            ConnectivityManager conManager = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                return networkInfo.isAvailable();
            }
            return false;
        }
    }

    @Override
    public void dispatchEvent(String aEventID, boolean success, Object eventObj) {
        switch (aEventID){
            case AEvent.AEVENT_VOIP_REV_CALLING:
                if(MLOC.canPickupVoip){
                    Intent intent = new Intent(this, VoipRingingActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                    intent.putExtra("targetId",eventObj.toString());
                    startActivity(intent);
                }else{
                    MLOC.hasNewVoipMsg = true;
                }
                break;
            case AEvent.AEVENT_VOIP_REV_CALLING_AUDIO:
                if(MLOC.canPickupVoip){
                    Intent intent = new Intent(this, VoipAudioRingingActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                    intent.putExtra("targetId",eventObj.toString());
                    startActivity(intent);
                }else{
                    MLOC.hasNewVoipMsg = true;
                }
                break;
            case AEvent.AEVENT_VOIP_P2P_REV_CALLING:
                if(MLOC.canPickupVoip){
                    Intent intent = new Intent(this, VoipP2PRingingActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                    intent.putExtra("targetId",eventObj.toString());
                    startActivity(intent);
                }
                break;
            case AEvent.AEVENT_VOIP_P2P_REV_CALLING_AUDIO:
                if(MLOC.canPickupVoip){
                    Intent intent = new Intent(this, VoipP2PRingingActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                    intent.putExtra("targetId",eventObj.toString());
                    startActivity(intent);
                }
                break;
            case AEvent.AEVENT_C2C_REV_MSG:
                MLOC.hasNewC2CMsg = true;
                break;
            case AEvent.AEVENT_GROUP_REV_MSG:
                MLOC.hasNewGroupMsg = true;
                break;
            case AEvent.AEVENT_LOGIN:
                if(success){
                    MLOC.d("KeepLiveService", (String) eventObj);
                    XHClient.getInstance().getLoginManager().login(MLOC.authKey, new IXHResultCallback() {
                        @Override
                        public void success(Object data) {
                            isLogin = true;
                        }
                        @Override
                        public void failed(final String errMsg) {
                            MLOC.d("KeepLiveService",errMsg);
                            MLOC.showMsg(KeepLiveService.this,errMsg);
                        }
                    });
                }else{
                    MLOC.d("KeepLiveService", (String) eventObj);
                }
                break;
            case AEvent.AEVENT_LOGOUT:
                removeListener();
                this.stopSelf();
                break;

        }
    }


    private void addListener(){
        AEvent.addListener(AEvent.AEVENT_LOGIN,this);
        AEvent.addListener(AEvent.AEVENT_LOGOUT,this);
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_CALLING,this);
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_CALLING_AUDIO,this);
        AEvent.addListener(AEvent.AEVENT_VOIP_P2P_REV_CALLING,this);
        AEvent.addListener(AEvent.AEVENT_C2C_REV_MSG,this);
        AEvent.addListener(AEvent.AEVENT_GROUP_REV_MSG,this);
    }
    private void removeListener(){
        AEvent.removeListener(AEvent.AEVENT_LOGIN,this);
        AEvent.removeListener(AEvent.AEVENT_LOGOUT,this);
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_CALLING,this);
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_CALLING_AUDIO,this);
        AEvent.removeListener(AEvent.AEVENT_VOIP_P2P_REV_CALLING,this);
        AEvent.removeListener(AEvent.AEVENT_C2C_REV_MSG,this);
        AEvent.removeListener(AEvent.AEVENT_GROUP_REV_MSG,this);
    }

}
