package com.starrtc.staravdemo.demo;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.starrtc.staravdemo.R;
import com.starrtc.staravdemo.demo.im.IMDemoActivity;
import com.starrtc.staravdemo.demo.listener.DemoChatroomListener;
import com.starrtc.staravdemo.demo.listener.DemoGroupListener;
import com.starrtc.staravdemo.demo.setting.SettingActivity;
import com.starrtc.staravdemo.demo.videolive.VideoLiveListActivity;
import com.starrtc.staravdemo.demo.listener.DemoErrorListener;
import com.starrtc.staravdemo.demo.listener.DemoLiveListener;
import com.starrtc.staravdemo.demo.listener.DemoC2CListener;
import com.starrtc.staravdemo.demo.listener.DemoUserStatusListener;
import com.starrtc.staravdemo.demo.listener.DemoVoipListener;
import com.starrtc.staravdemo.demo.serverAPI.InterfaceUrls;
import com.starrtc.staravdemo.demo.test.LoopTestActivity;
import com.starrtc.staravdemo.demo.videomeeting.VideoMeetingListActivity;
import com.starrtc.staravdemo.demo.voip.VoipActivity;
import com.starrtc.staravdemo.demo.voip.VoipReadyActivity;
import com.starrtc.staravdemo.utils.AEvent;
import com.starrtc.staravdemo.utils.IEventListener;
import com.starrtc.starrtcsdk.StarManager;
import com.starrtc.starrtcsdk.im.message.StarIMMessage;

public class StarAvDemoActivity extends Activity implements View.OnClickListener, IEventListener {

    private boolean isLogin = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star_av_demo);
        ((TextView)findViewById(R.id.app_name)).setText(R.string.app_name);
        MLOC.init(getApplicationContext());

        AEvent.addListener(AEvent.AEVENT_VOIP_REV_CALLING,this);

        StarManager.getInstance().init(getApplicationContext());
        StarManager.getInstance().addC2CListener(new DemoC2CListener())
                .addGroupListener(new DemoGroupListener())
                .addUserStatusListener(new DemoUserStatusListener())
                .addVoipMessageListener(new DemoVoipListener())
                .addLiveMessageListener(new DemoLiveListener())
                .addErrorListener(new DemoErrorListener())
                .addChatroomListener(new DemoChatroomListener());
        MLOC.userId = MLOC.loadSharedData(getApplicationContext(),"userId");
        if(MLOC.userId.equals("")){
            MLOC.userId = "demo"+ new Random().nextInt(100)+ new Random().nextInt(100);
            MLOC.saveSharedData(getApplicationContext(),"userId",MLOC.userId);
        }

        ((TextView)findViewById(R.id.userId)).setText(MLOC.userId);
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        findViewById(R.id.button4).setOnClickListener(this);
        findViewById(R.id.button5).setOnClickListener(this);
        findViewById(R.id.button6).setOnClickListener(this);
        findViewById(R.id.button7).setOnClickListener(this);

        InterfaceUrls.demoLogin(MLOC.userId);
        new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(!isLogin){
                    InterfaceUrls.demoLogin(MLOC.userId);
                }
            }

        }.sendEmptyMessageDelayed(0,5000);
        checkPermission();
    }


    private int times = 0;
    private final int REQUEST_PHONE_PERMISSIONS = 0;
    private void checkPermission(){
        times++;
        final List<String> permissionsList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            if ((checkSelfPermission(Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED)) permissionsList.add(Manifest.permission.READ_PHONE_STATE);
            if ((checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)) permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if ((checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)) permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            if ((checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED)) permissionsList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            if ((checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)) permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            if ((checkSelfPermission(Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)) permissionsList.add(Manifest.permission.CAMERA);
            if ((checkSelfPermission(Manifest.permission.BLUETOOTH)!= PackageManager.PERMISSION_GRANTED)) permissionsList.add(Manifest.permission.BLUETOOTH);
            if ((checkSelfPermission(Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED)) permissionsList.add(Manifest.permission.RECORD_AUDIO);

            if (permissionsList.size() != 0){
                if(times==1){
                    requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                            REQUEST_PHONE_PERMISSIONS);
                }else{
                    new android.support.v7.app.AlertDialog.Builder(this)
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
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        checkPermission();
    }


    @Override
    public void onBackPressed(){

    }

    @Override
    public void onResume(){
        super.onResume();
        AEvent.addListener(AEvent.AEVENT_LOGIN,this);
        AEvent.addListener(AEvent.AEVENT_USER_LOGIN_SUCCESS,this);
        AEvent.addListener(AEvent.AEVENT_USER_LOGIN_FAILED,this);
    }
    @Override
    public void onPause(){
        super.onPause();
        AEvent.removeListener(AEvent.AEVENT_LOGIN,this);
        AEvent.removeListener(AEvent.AEVENT_USER_LOGIN_SUCCESS,this);
        AEvent.removeListener(AEvent.AEVENT_USER_LOGIN_FAILED,this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button1:
                startActivity(new Intent(this,VoipReadyActivity.class));
                break;
            case R.id.button2:
                startActivity(new Intent(this,VideoMeetingListActivity.class));
                break;
            case R.id.button3:
                Intent intent3 = new Intent(this, VideoLiveListActivity.class);
                startActivity(intent3);
                break;
            case R.id.button4:
                startActivity(new Intent(this,LoopTestActivity.class));
                break;
            case R.id.button5:
                StarManager.getInstance().logout();
                AEvent.removeListener(AEvent.AEVENT_VOIP_REV_CALLING,this);
                finish();
                break;
            case R.id.button6:
                Intent intent6 = new Intent(this, SettingActivity.class);
                startActivity(intent6);
                break;
            case R.id.button7:
                Intent intent7= new Intent(this, IMDemoActivity.class);
                startActivity(intent7);
                break;

        }
    }

    @Override
    public void dispatchEvent(String aEventID, boolean success, Object eventObj) {
        switch (aEventID){
            case AEvent.AEVENT_LOGIN:
                if(success){
                    MLOC.d("", (String) eventObj);
                    StarManager.getInstance().login(getApplicationContext(), MLOC.agentId, MLOC.userId, MLOC.authKey);
                }else{
                    MLOC.d("", (String) eventObj);
                }
                break;
            case AEvent.AEVENT_VOIP_REV_CALLING:
                if(success){
                    StarIMMessage msg = (StarIMMessage) eventObj;
                    Intent intent = new Intent(StarAvDemoActivity.this,VoipActivity.class);
                    intent.putExtra("targetId",msg.fromId);
                    intent.putExtra(VoipActivity.ACTION,VoipActivity.RING);
                    startActivity(intent);
                }
                break;
            case AEvent.AEVENT_USER_LOGIN_SUCCESS:
                if(success){
                    MLOC.d("","StarSdk登录成功");
                    uiHandler.sendEmptyMessage(0);
                }
                break;
            case AEvent.AEVENT_USER_LOGIN_FAILED:
                if(success){
                    MLOC.d("","StarSdk登录失败");
                }
                break;
        }
    }

    private Handler uiHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0:
                    isLogin = true;
                    findViewById(R.id.loading).setVisibility(View.INVISIBLE);
                    break;
            }
            super.handleMessage(msg);
        }
    };
}
