package com.starrtc.demo.demo;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;

import com.starrtc.demo.R;
import com.starrtc.demo.listener.XHVoipP2PManagerListener;
import com.starrtc.demo.serverAPI.InterfaceUrls;
import com.starrtc.demo.utils.AEvent;
import com.starrtc.demo.utils.IEventListener;
import com.starrtc.demo.listener.XHChatManagerListener;
import com.starrtc.starrtcsdk.api.XHClient;
import com.starrtc.demo.listener.XHGroupManagerListener;
import com.starrtc.demo.listener.XHLoginManagerListener;
import com.starrtc.starrtcsdk.api.XHConstants;
import com.starrtc.starrtcsdk.api.XHCustomConfig;
import com.starrtc.demo.listener.XHVoipManagerListener;
import com.starrtc.starrtcsdk.apiInterface.IXHErrorCallback;
import com.starrtc.starrtcsdk.apiInterface.IXHResultCallback;
import com.starrtc.starrtcsdk.core.StarRtcCore;
import com.starrtc.starrtcsdk.socket.StarSocket;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SplashActivity extends Activity implements IEventListener {
    private boolean isLogin = false;
    private final boolean checkNetState = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        checkPermission();


    }

    private int times = 0;
    private final int REQUEST_PHONE_PERMISSIONS = 0;
    private void checkPermission(){
        times++;
        final List<String> permissionsList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if ((checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE)!= PackageManager.PERMISSION_GRANTED)) permissionsList.add(Manifest.permission.ACCESS_NETWORK_STATE);
            if ((checkSelfPermission(Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED)) permissionsList.add(Manifest.permission.READ_PHONE_STATE);
            if ((checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)) permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if ((checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)) permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            if ((checkSelfPermission(Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)) permissionsList.add(Manifest.permission.CAMERA);
            if ((checkSelfPermission(Manifest.permission.BLUETOOTH)!= PackageManager.PERMISSION_GRANTED)) permissionsList.add(Manifest.permission.BLUETOOTH);
            if ((checkSelfPermission(Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED)) permissionsList.add(Manifest.permission.RECORD_AUDIO);
            if (permissionsList.size() != 0){
                if(times==1){
                    requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                            REQUEST_PHONE_PERMISSIONS);
                }else{
                    new AlertDialog.Builder(this)
                            .setCancelable(true)
                            .setTitle("提示")
                            .setMessage("获取不到授权，APP将无法正常使用，请允许APP获取权限！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                                REQUEST_PHONE_PERMISSIONS);
                                    }
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            finish();
                        }
                    }).show();
                }
            }else{
                init();
            }
        }else{
            init();
        }
    }

    private void init(){
        isLogin = StarRtcCore.getInstance().getIsOnline();
        if(!isLogin){
            MLOC.init(getApplicationContext());
            if(MLOC.userId.equals("")){
                MLOC.userId = ""+(new Random().nextInt(900000)+100000);
                MLOC.saveUserId(MLOC.userId);
            }
            addListener();
            //初始化


            XHCustomConfig customConfig =  XHCustomConfig.getInstance();
            customConfig.setAppId(MLOC.agentId);
            customConfig.setLoginServerUrl(MLOC.STAR_LOGIN_URL);
            customConfig.setChatroomScheduleUrl(MLOC.CHAT_ROOM_SCHEDULE_URL);
            customConfig.setLiveSrcScheduleUrl(MLOC.LIVE_SRC_SCHEDULE_URL);
            customConfig.setLiveVdnScheduleUrl(MLOC.LIVE_VDN_SCHEDULE_URL);
            customConfig.setImScheduleUrl(MLOC.IM_SCHEDULE_URL);
            customConfig.setVoipServerUrl(MLOC.VOIP_SERVER_URL);
            customConfig.initSDK(this, MLOC.userId, new IXHErrorCallback() {
                @Override
                public void error(final String errMsg, Object data) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MLOC.showMsg(errMsg);
                        }
                    });
                }
            });
            int logConfig = 1 | 2 | 4 | 8 | 16;
//            int logConfig = 2 | 4 | 16;
//            int logConfig = 0;
            customConfig.setLogLevel(logConfig,1);
//            StarLog.setDebug(false);
//            MLOC.setDebug(false);

            XHClient.getInstance().getChatManager().addListener(new XHChatManagerListener());
            XHClient.getInstance().getGroupManager().addListener(new XHGroupManagerListener());
            XHClient.getInstance().getVoipManager().addListener(new XHVoipManagerListener());
            XHClient.getInstance().getVoipP2PManager().addListener(new XHVoipP2PManagerListener());
            XHClient.getInstance().getLoginManager().addListener(new XHLoginManagerListener());
        }
        startAnimation();
        checkNetworkConnectAndLogin();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  final String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        checkPermission();
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


    private void loginPublicTest(){
        XHClient.getInstance().getLoginManager().loginPublic( new IXHResultCallback() {
            @Override
            public void success(Object data) {
                isLogin = true;
            }

            @Override
            public void failed(final String errMsg) {
                MLOC.d("",errMsg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MLOC.showMsg(SplashActivity.this,errMsg);
                    }
                });
            }
        });
    }

    private void addListener(){
        AEvent.addListener(AEvent.AEVENT_LOGIN,this);
    }
    private void removeListener(){
        AEvent.removeListener(AEvent.AEVENT_LOGIN,this);
    }

    @Override
    public void dispatchEvent(String aEventID, boolean success, Object eventObj) {
        switch (aEventID){
            case AEvent.AEVENT_LOGIN:
                if(success){
                    MLOC.d("", (String) eventObj);
                    XHClient.getInstance().getLoginManager().login(MLOC.authKey, new IXHResultCallback() {
                        @Override
                        public void success(Object data) {
                            isLogin = true;
                        }

                        @Override
                        public void failed(final String errMsg) {
                            MLOC.d("",errMsg);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MLOC.showMsg(SplashActivity.this,errMsg);
                                }
                            });
                        }
                    });
                }else{
                    MLOC.d("", (String) eventObj);
                }
                break;
        }
    }

    @SuppressLint("WrongConstant")
    private void startAnimation(){
        isLogin = true;
        final View eye = findViewById(R.id.eye);
        eye.setAlpha(0.2f);
        final View black = findViewById(R.id.black_view);
        final View white = findViewById(R.id.white_view);

        final ObjectAnimator va = ObjectAnimator.ofFloat(eye,"alpha",0.2f,1f);
        va.setDuration(1000);
        va.setRepeatCount(ValueAnimator.INFINITE);
        va.setRepeatMode(Animation.REVERSE);
        va.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                 if(isLogin){
                     va.cancel();
                    ObjectAnimator va1 = ObjectAnimator.ofFloat(white,"alpha",0f,1f);
                    ObjectAnimator va2 = ObjectAnimator.ofFloat(black,"alpha",1f,0f);

                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.setDuration(1500);
                    animatorSet.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            new Handler(){
                                @Override
                                public void handleMessage(Message msg){
                                    removeListener();
                                    startActivity(new Intent(SplashActivity.this,StarAvDemoActivity.class));
                                    finish();
                                }

                            }.sendEmptyMessageDelayed(0,500);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    animatorSet.playTogether(va1,va2);
                    animatorSet.start();
                }
            }
        });
        va.start();

    }
}
