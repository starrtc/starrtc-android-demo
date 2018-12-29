package com.starrtc.demo.demo.voip;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.TextView;

import com.starrtc.demo.R;
import com.starrtc.demo.demo.BaseActivity;
import com.starrtc.demo.demo.MLOC;
import com.starrtc.demo.database.CoreDB;
import com.starrtc.demo.database.HistoryBean;
import com.starrtc.demo.ui.CircularCoverView;
import com.starrtc.demo.utils.AEvent;
import com.starrtc.demo.utils.ColorUtils;
import com.starrtc.demo.utils.DensityUtils;
import com.starrtc.starrtcsdk.api.XHClient;
import com.starrtc.starrtcsdk.api.XHConstants;
import com.starrtc.starrtcsdk.api.XHVoipManager;
import com.starrtc.starrtcsdk.apiInterface.IXHResultCallback;
import com.starrtc.starrtcsdk.core.audio.StarRTCAudioManager;

import java.text.SimpleDateFormat;
import java.util.Set;

public class VoipAudioActivity extends BaseActivity implements View.OnClickListener {

    private XHVoipManager voipManager;
    private Chronometer timer;

    public static String ACTION = "ACTION";
    public static String RING = "RING";
    public static String CALLING = "CALLING";
    private String action;
    private String targetId;

    private StarRTCAudioManager starRTCAudioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_voip_audio);

        starRTCAudioManager = StarRTCAudioManager.create(this.getApplicationContext());
        starRTCAudioManager.start(new StarRTCAudioManager.AudioManagerEvents() {
            @Override
            public void onAudioDeviceChanged(StarRTCAudioManager.AudioDevice selectedAudioDevice, Set<StarRTCAudioManager.AudioDevice> availableAudioDevices) {
                MLOC.d("onAudioDeviceChanged ",selectedAudioDevice.name());
            }
        });

        voipManager = XHClient.getInstance().getVoipManager();
        voipManager.setRtcMediaType(XHConstants.XHRtcMediaTypeEnum.STAR_RTC_MEDIA_TYPE_AUDIO_ONLY);
        voipManager.setDeviceDirection(XHConstants.XHDeviceDirectionEnum.STAR_DEVICE_DIRECTION_HOME_BOTTOM);
        addListener();

        targetId = getIntent().getStringExtra("targetId");
        action = getIntent().getStringExtra(ACTION);
        timer = (Chronometer) findViewById(R.id.timer);


        ((TextView)findViewById(R.id.targetid_text)).setText(targetId);
        findViewById(R.id.head_bg).setBackgroundColor(ColorUtils.getColor(VoipAudioActivity.this,targetId));
        ((CircularCoverView)findViewById(R.id.head_cover)).setCoverColor(Color.parseColor("#000000"));
        int cint = DensityUtils.dip2px(VoipAudioActivity.this,45);
        ((CircularCoverView)findViewById(R.id.head_cover)).setRadians(cint, cint, cint, cint,0);

        findViewById(R.id.hangup).setOnClickListener(this);


        voipManager.setupView(this,null, null, new IXHResultCallback() {
            @Override
            public void success(Object data) {
                MLOC.d("newVoip","setupView success");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(action.equals(CALLING)){
                            MLOC.d("newVoip","call");
                            voipManager.call(targetId, new IXHResultCallback() {
                                @Override
                                public void success(Object data) {
                                    MLOC.d("newVoip","call success");
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
                });
            }

            @Override
            public void failed(String errMsg) {
                MLOC.d("newVoip","setupView failed");
                stopAndFinish();
            }
        });
        if(action.equals(CALLING)){
            showCallingView();
        }
    }

    public void addListener(){
        AEvent.addListener(AEvent.AEVENT_VOIP_INIT_COMPLETE,this);
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_BUSY,this);
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_REFUSED,this);
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_HANGUP,this);
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_CONNECT,this);
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_ERROR,this);
    }

    public void removeListener(){
        MLOC.canPickupVoip = true;
        AEvent.removeListener(AEvent.AEVENT_VOIP_INIT_COMPLETE,this);
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_BUSY,this);
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_REFUSED,this);
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_HANGUP,this);
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_CONNECT,this);
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_ERROR,this);
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
        MLOC.setHistory(historyBean,true);
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
        new AlertDialog.Builder(VoipAudioActivity.this).setCancelable(true)
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
                                stopAndFinish();
                            }

                            @Override
                            public void failed(final String errMsg) {
                                MLOC.d("","AEVENT_VOIP_ON_STOP errMsg:"+errMsg);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        MLOC.showMsg(VoipAudioActivity.this,errMsg);
                                    }
                                });
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MLOC.d("","对方线路忙");
                        MLOC.showMsg(VoipAudioActivity.this,"对方线路忙");
                        stopAndFinish();
                    }
                });
                break;
            case AEvent.AEVENT_VOIP_REV_REFUSED:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MLOC.d("","对方拒绝通话");
                        MLOC.showMsg(VoipAudioActivity.this,"对方拒绝通话");
                        stopAndFinish();
                    }
                });
                break;
            case AEvent.AEVENT_VOIP_REV_HANGUP:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MLOC.d("","对方已挂断");
                        MLOC.showMsg(VoipAudioActivity.this,"对方已挂断");
                        timer.stop();
                        stopAndFinish();
                    }
                });
                break;
            case AEvent.AEVENT_VOIP_REV_CONNECT:
                MLOC.d("","对方允许通话");
                showTalkingView();
                break;
            case AEvent.AEVENT_VOIP_REV_ERROR:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MLOC.d("",(String) eventObj);
                        stopAndFinish();
                    }
                });
                break;
        }
    }

    private void showCallingView(){
        findViewById(R.id.calling_txt).setVisibility(View.VISIBLE);
        findViewById(R.id.timer).setVisibility(View.INVISIBLE);
    }

    private void showTalkingView(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.calling_txt).setVisibility(View.INVISIBLE);
                findViewById(R.id.timer).setVisibility(View.VISIBLE);
                timer.setBase(SystemClock.elapsedRealtime());
                timer.start();
            }
        });
    }

    private void onPickup(){
        voipManager.accept(targetId, new IXHResultCallback() {
            @Override
            public void success(Object data) {
                MLOC.d("newVoip","onPickup OK ");
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
            case R.id.hangup:
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
        }
    }

    private void stopAndFinish(){
        if(starRTCAudioManager !=null){
            starRTCAudioManager.stop();
        }
        VoipAudioActivity.this.finish();
    }
}
