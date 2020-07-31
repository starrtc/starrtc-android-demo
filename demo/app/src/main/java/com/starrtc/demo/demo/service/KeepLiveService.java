package com.starrtc.demo.demo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.Nullable;

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
        removeListener();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initSDK();
        return super.onStartCommand(intent, flags, startId);
    }

    private void initSDK(){
        MLOC.init(this);
        initFree();
    }

    private boolean isLogin = false;
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

            XHCustomConfig customConfig =  XHCustomConfig.getInstance(this);
            customConfig.setChatroomServerUrl(MLOC.CHATROOM_SERVER_URL);
            customConfig.setLiveSrcServerUrl(MLOC.LIVE_SRC_SERVER_URL);
            customConfig.setLiveVdnServerUrl(MLOC.LIVE_VDN_SERVER_URL);
            customConfig.setImServerUrl(MLOC.IM_SERVER_URL);
            customConfig.setVoipServerUrl(MLOC.VOIP_SERVER_URL);
//            customConfig.setLogEnable(false); //关闭SDK调试日志
//            customConfig.setDefConfigOpenGLESEnable(false);
//            customConfig.setDefConfigCameraId(1);//设置默认摄像头方向  0后置  1前置
//            customConfig.setDefConfigVideoSize(XHConstants.XHCropTypeEnum.STAR_VIDEO_CONFIG_360BW_640BH_180SW_320SH);
//            customConfig.setLogDirPath(Environment.getExternalStorageDirectory().getPath()+"/starrtcLog");
//            customConfig.setDefConfigCamera2Enable(false);
//            StarCamera.setFrameBufferEnable(false);
            customConfig.initSDKForFree(MLOC.userId, new IXHErrorCallback() {
                @Override
                public void error(final String errMsg, Object data) {
                    MLOC.e("KeepLiveService","error:"+errMsg);
                    MLOC.showMsg(KeepLiveService.this,errMsg);
                }
            },new Handler());

            XHClient.getInstance().getChatManager().addListener(new XHChatManagerListener());
            XHClient.getInstance().getGroupManager().addListener(new XHGroupManagerListener());
            XHClient.getInstance().getVoipManager().addListener(new XHVoipManagerListener());
            XHClient.getInstance().getVoipP2PManager().addListener(new XHVoipP2PManagerListener());
            XHClient.getInstance().getLoginManager().addListener(new XHLoginManagerListener());
            XHVideoSourceManager.getInstance().setVideoSourceCallback(new DemoVideoSourceCallback());

            XHClient.getInstance().getLoginManager().loginFree(new IXHResultCallback() {
                @Override
                public void success(Object data) {
                    MLOC.d("KeepLiveService","loginSuccess");
                    isLogin = true;
                }
                @Override
                public void failed(final String errMsg) {
                    MLOC.d("KeepLiveService","loginFailed "+errMsg);
                    MLOC.showMsg(KeepLiveService.this,errMsg);
                }
            });
        }
    }

    @Override
    public void dispatchEvent(String aEventID, boolean success, Object eventObj) {
        switch (aEventID){
            case AEvent.AEVENT_VOIP_REV_CALLING:{
                Intent intent = new Intent(this, VoipRingingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                intent.putExtra("targetId",eventObj.toString());
                startActivity(intent);
            }
            break;
            case AEvent.AEVENT_VOIP_REV_CALLING_AUDIO:{
                Intent intent = new Intent(this, VoipAudioRingingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                intent.putExtra("targetId",eventObj.toString());
                startActivity(intent);
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
            case AEvent.AEVENT_REV_SYSTEM_MSG:
                MLOC.hasNewC2CMsg = true;
                break;
            case AEvent.AEVENT_GROUP_REV_MSG:
                MLOC.hasNewGroupMsg = true;
                break;
            case AEvent.AEVENT_LOGOUT:
                removeListener();
                this.stopSelf();
                break;
            case AEvent.AEVENT_USER_KICKED:
            case AEvent.AEVENT_CONN_DEATH:
                MLOC.d("KeepLiveService","AEVENT_USER_KICKED OR AEVENT_CONN_DEATH");
                XHClient.getInstance().getLoginManager().loginFree(new IXHResultCallback() {
                    @Override
                    public void success(Object data) {
                        MLOC.d("KeepLiveService","loginSuccess");
                        isLogin = true;
                    }
                    @Override
                    public void failed(final String errMsg) {
                        MLOC.d("KeepLiveService","loginFailed "+errMsg);
                        MLOC.showMsg(KeepLiveService.this,errMsg);
                    }
                });
                break;
        }
    }

    private void addListener(){
        AEvent.addListener(AEvent.AEVENT_LOGOUT,this);
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_CALLING,this);
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_CALLING_AUDIO,this);
        AEvent.addListener(AEvent.AEVENT_VOIP_P2P_REV_CALLING,this);
        AEvent.addListener(AEvent.AEVENT_C2C_REV_MSG,this);
        AEvent.addListener(AEvent.AEVENT_REV_SYSTEM_MSG,this);
        AEvent.addListener(AEvent.AEVENT_GROUP_REV_MSG,this);
        AEvent.addListener(AEvent.AEVENT_USER_KICKED,this);
        AEvent.addListener(AEvent.AEVENT_CONN_DEATH,this);
    }

    private void removeListener(){
        AEvent.removeListener(AEvent.AEVENT_LOGOUT,this);
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_CALLING,this);
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_CALLING_AUDIO,this);
        AEvent.removeListener(AEvent.AEVENT_VOIP_P2P_REV_CALLING,this);
        AEvent.removeListener(AEvent.AEVENT_C2C_REV_MSG,this);
        AEvent.removeListener(AEvent.AEVENT_REV_SYSTEM_MSG,this);
        AEvent.removeListener(AEvent.AEVENT_GROUP_REV_MSG,this);
        AEvent.removeListener(AEvent.AEVENT_USER_KICKED,this);
        AEvent.removeListener(AEvent.AEVENT_CONN_DEATH,this);
    }

}
