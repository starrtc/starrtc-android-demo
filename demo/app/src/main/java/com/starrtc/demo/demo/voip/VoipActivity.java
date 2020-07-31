package com.starrtc.demo.demo.voip;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import com.starrtc.demo.R;
import com.starrtc.demo.demo.BaseActivity;
import com.starrtc.demo.demo.MLOC;
import com.starrtc.demo.database.CoreDB;
import com.starrtc.demo.database.HistoryBean;
import com.starrtc.demo.utils.AEvent;
import com.starrtc.starrtcsdk.api.XHClient;
import com.starrtc.starrtcsdk.api.XHConstants;
import com.starrtc.starrtcsdk.api.XHCustomConfig;
import com.starrtc.starrtcsdk.api.XHSDKHelper;
import com.starrtc.starrtcsdk.api.XHVoipManager;
import com.starrtc.starrtcsdk.apiInterface.IXHResultCallback;
import com.starrtc.starrtcsdk.core.audio.StarRTCAudioManager;
import com.starrtc.starrtcsdk.core.player.StarPlayer;
import com.starrtc.starrtcsdk.core.pusher.XHCameraRecorder;
import com.starrtc.starrtcsdk.core.pusher.XHScreenRecorder;

import java.text.SimpleDateFormat;
import java.util.Set;

public class VoipActivity extends BaseActivity implements View.OnClickListener {

    private XHVoipManager voipManager;
    private StarPlayer targetPlayer;
    private StarPlayer selfPlayer;
    private Chronometer timer;
    public static String ACTION = "ACTION";
    public static String RING = "RING";
    public static String CALLING = "CALLING";
    private String action;
    private String targetId;
    private Boolean isTalking = false;
    private StarRTCAudioManager starRTCAudioManager;
    private XHSDKHelper xhsdkHelper;

//    private PushUVCTest pushUVCTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        starRTCAudioManager = StarRTCAudioManager.create(this.getApplicationContext());
        starRTCAudioManager.start(new StarRTCAudioManager.AudioManagerEvents() {
            @Override
            public void onAudioDeviceChanged(StarRTCAudioManager.AudioDevice selectedAudioDevice, Set availableAudioDevices) {
                MLOC.d("onAudioDeviceChanged ",selectedAudioDevice.name());
            }
        });
        starRTCAudioManager.setDefaultAudioDevice(StarRTCAudioManager.AudioDevice.SPEAKER_PHONE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_voip);
        voipManager = XHClient.getInstance().getVoipManager();
        voipManager.setRecorder(new XHCameraRecorder());
        voipManager.setRtcMediaType(XHConstants.XHRtcMediaTypeEnum.STAR_RTC_MEDIA_TYPE_VIDEO_AND_AUDIO);
        addListener();
        targetId = getIntent().getStringExtra("targetId");
        action = getIntent().getStringExtra(ACTION);
        targetPlayer = (StarPlayer) findViewById(R.id.voip_surface_target);
        selfPlayer = (StarPlayer) findViewById(R.id.voip_surface_self);
        selfPlayer.setZOrderMediaOverlay(true);
        timer = (Chronometer) findViewById(R.id.timer);
        targetPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isTalking){
                    findViewById(R.id.talking_view).setVisibility(findViewById(R.id.talking_view).getVisibility()==View.VISIBLE?View.INVISIBLE:View.VISIBLE);
                }
            }
        });

//        final XHCustomRecorder recorder = new XHCustomRecorder(480,480,0,false);
//        voipManager.setRecorder(recorder);
//        pushUVCTest = new PushUVCTest(recorder);
//        pushUVCTest.startRecoder();


        ((TextView)findViewById(R.id.targetid_text)).setText(targetId);
        ((ImageView)findViewById(R.id.head_img)).setImageResource(MLOC.getHeadImage(VoipActivity.this,targetId));
        findViewById(R.id.calling_hangup).setOnClickListener(this);
        findViewById(R.id.talking_hangup).setOnClickListener(this);
        findViewById(R.id.switch_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voipManager.switchCamera();
            }
        });
        findViewById(R.id.screen_btn).setOnClickListener(this);
        findViewById(R.id.mic_btn).setSelected(true);
        findViewById(R.id.mic_btn).setOnClickListener(this);
        findViewById(R.id.camera_btn).setSelected(true);
        findViewById(R.id.camera_btn).setOnClickListener(this);
        findViewById(R.id.speaker_on_btn).setOnClickListener(this);
        findViewById(R.id.speaker_off_btn).setOnClickListener(this);

        if(action.equals(CALLING)){
            showCallingView();
            MLOC.d("newVoip","call");
            xhsdkHelper = new XHSDKHelper();
            xhsdkHelper.setDefaultCameraId(1);
            xhsdkHelper.startPerview(this,((StarPlayer)findViewById(R.id.voip_surface_target)));

            voipManager.call(this,targetId, new IXHResultCallback() {
                @Override
                public void success(Object data) {
                    xhsdkHelper.stopPerview();
                    xhsdkHelper = null;
                    MLOC.d("newVoip","call success! RecSessionId:"+data);
                }
                @Override
                public void failed(String errMsg) {
                    MLOC.d("newVoip","call failed");
                    stopAndFinish();
                }
            });
        }else{
            MLOC.d("newVoip","onPickup");
            onPickup();
        }
    }

    private void setupViews(){
        voipManager.setupView(selfPlayer, targetPlayer, new IXHResultCallback() {
            @Override
            public void success(Object data) {
                MLOC.d("newVoip","setupView success");
            }

            @Override
            public void failed(String errMsg) {
                MLOC.d("newVoip","setupView failed");
                stopAndFinish();
            }
        });
    }

    public void addListener(){
        AEvent.addListener(AEvent.AEVENT_VOIP_INIT_COMPLETE,this);
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_BUSY,this);
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_REFUSED,this);
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_HANGUP,this);
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_CONNECT,this);
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_ERROR,this);
        AEvent.addListener(AEvent.AEVENT_VOIP_TRANS_STATE_CHANGED,this);
    }

    public void removeListener(){
        MLOC.canPickupVoip = true;
        AEvent.removeListener(AEvent.AEVENT_VOIP_INIT_COMPLETE,this);
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_BUSY,this);
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_REFUSED,this);
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_HANGUP,this);
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_CONNECT,this);
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_ERROR,this);
        AEvent.removeListener(AEvent.AEVENT_VOIP_TRANS_STATE_CHANGED,this);
    }

    @Override
    public void onResume(){
        super.onResume();
        MLOC.canPickupVoip = false;
        HistoryBean historyBean = new HistoryBean();
        historyBean.setType(CoreDB.HISTORY_TYPE_VOIP);
        historyBean.setLastTime(new SimpleDateFormat("MM-dd HH:mm").format(new java.util.Date()));
        historyBean.setConversationId(targetId);
        historyBean.setNewMsgCount(1);
        MLOC.addHistory(historyBean,true);
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onRestart(){
        super.onRestart();
        addListener();
    }

    @Override
    public void onDestroy(){
        removeListener();
        super.onDestroy();
    }

    @Override
    public void onBackPressed(){
        new AlertDialog.Builder(VoipActivity.this).setCancelable(true)
                .setTitle("是否挂断?")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        timer.stop();
                        voipManager.hangup(new IXHResultCallback() {
                            @Override
                            public void success(Object data) {
                                removeListener();
                                stopAndFinish();
                            }

                            @Override
                            public void failed(final String errMsg) {
                                MLOC.d("","AEVENT_VOIP_ON_STOP errMsg:"+errMsg);
                                MLOC.showMsg(VoipActivity.this,errMsg);
                            }
                        });
                    }
                 }
        ).show();
    }

    @Override
    public void dispatchEvent(String aEventID, boolean success, final Object eventObj) {
        super.dispatchEvent(aEventID,success,eventObj);
        switch (aEventID){
            case AEvent.AEVENT_VOIP_REV_BUSY:
                MLOC.d("","对方线路忙");
                MLOC.showMsg(VoipActivity.this,"对方线路忙");
                if(xhsdkHelper!=null){
                    xhsdkHelper.stopPerview();
                    xhsdkHelper = null;
                }
                stopAndFinish();
                break;
            case AEvent.AEVENT_VOIP_REV_REFUSED:
                MLOC.d("","对方拒绝通话");
                MLOC.showMsg(VoipActivity.this,"对方拒绝通话");
                if(xhsdkHelper!=null){
                    xhsdkHelper.stopPerview();
                    xhsdkHelper = null;
                }
                stopAndFinish();
                break;
            case AEvent.AEVENT_VOIP_REV_HANGUP:
                MLOC.d("","对方已挂断");
                MLOC.showMsg(VoipActivity.this,"对方已挂断");
                timer.stop();
                stopAndFinish();
                break;
            case AEvent.AEVENT_VOIP_REV_CONNECT:
                MLOC.d("","对方允许通话");
                showTalkingView();
                break;
            case AEvent.AEVENT_VOIP_REV_ERROR:
                MLOC.d("",(String) eventObj);
                if(xhsdkHelper!=null){
                    xhsdkHelper.stopPerview();
                    xhsdkHelper = null;
                }
                stopAndFinish();
                break;
            case AEvent.AEVENT_VOIP_TRANS_STATE_CHANGED:
                findViewById(R.id.state).setBackgroundColor(((int)eventObj==0)?0xFFFFFF00:0xFF299401);
                break;
        }
    }


    private void showCallingView(){
        findViewById(R.id.calling_view).setVisibility(View.VISIBLE);
        findViewById(R.id.talking_view).setVisibility(View.GONE);
    }

    private void showTalkingView(){
        isTalking = true;
        findViewById(R.id.calling_view).setVisibility(View.GONE);
        findViewById(R.id.talking_view).setVisibility(View.VISIBLE);
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();
        setupViews();
    }

    private void onPickup(){
        voipManager.accept(this,targetId, new IXHResultCallback() {
            @Override
            public void success(Object data) {
                MLOC.d("newVoip","onPickup OK! RecSessionId:"+data);
            }
            @Override
            public void failed(String errMsg) {
                MLOC.d("newVoip","onPickup failed ");
                stopAndFinish();
            }
        });
        showTalkingView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.calling_hangup:
                voipManager.cancel(new IXHResultCallback() {
                    @Override
                    public void success(Object data) {
                        stopAndFinish();
                    }

                    @Override
                    public void failed(String errMsg) {
                        stopAndFinish();
                    }
                });
                if(xhsdkHelper!=null){
                    xhsdkHelper.stopPerview();
                    xhsdkHelper = null;
                }
                break;
            case R.id.talking_hangup:
                voipManager.hangup(new IXHResultCallback() {
                    @Override
                    public void success(Object data) {
                        stopAndFinish();
                    }

                    @Override
                    public void failed(String errMsg) {
                        stopAndFinish();
                    }
                });
                break;
            case R.id.screen_btn:
                if(!XHCustomConfig.getInstance(this).getHardwareEnable()){
                    MLOC.showMsg(this,"需要打开硬编模式");
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (mRecorder != null) {
                        findViewById(R.id.screen_btn).setSelected(false);
                        voipManager.resetRecorder(new XHCameraRecorder());
                        mRecorder = null;
                    } else {
                        if(mMediaProjectionManager==null){
                            mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
                        }
                        Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
                        startActivityForResult(captureIntent, REQUEST_CODE);
                    }
                }else{
                    MLOC.showMsg(this,"系统版本过低，无法使用录屏功能");
                }
                break;
            case R.id.camera_btn:
                if(findViewById(R.id.camera_btn).isSelected()){
                    findViewById(R.id.camera_btn).setSelected(false);
                    voipManager.setVideoEnable(false);
                }else{
                    findViewById(R.id.camera_btn).setSelected(true);
                    voipManager.setVideoEnable(true);
                }
                break;
            case R.id.mic_btn:
                if(findViewById(R.id.mic_btn).isSelected()){
                    findViewById(R.id.mic_btn).setSelected(false);
                    voipManager.setAudioEnable(false);
                }else{
                    findViewById(R.id.mic_btn).setSelected(true);
                    voipManager.setAudioEnable(true);
                }
                break;
            case R.id.speaker_on_btn:
//                starRTCAudioManager.selectAudioDevice(StarRTCAudioManager.AudioDevice.SPEAKER_PHONE);
                starRTCAudioManager.setSpeakerphoneOn(true);
                findViewById(R.id.speaker_on_btn).setSelected(true);
                findViewById(R.id.speaker_off_btn).setSelected(false);
                break;
            case R.id.speaker_off_btn:
//                starRTCAudioManager.selectAudioDevice(StarRTCAudioManager.AudioDevice.EARPIECE);
                starRTCAudioManager.setSpeakerphoneOn(false);
                findViewById(R.id.speaker_on_btn).setSelected(false);
                findViewById(R.id.speaker_off_btn).setSelected(true);
                break;
        }
    }

    private static final int REQUEST_CODE = 1;
    private MediaProjectionManager mMediaProjectionManager;
    private XHScreenRecorder mRecorder;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mRecorder = new XHScreenRecorder(this,resultCode,data);
        voipManager.resetRecorder(mRecorder);
        findViewById(R.id.screen_btn).setSelected(true);
    }

    private void stopAndFinish(){
        if(starRTCAudioManager !=null){
            starRTCAudioManager.stop();
        }
        VoipActivity.this.finish();
    }

}
