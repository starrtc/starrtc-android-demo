package com.starrtc.demo.demo.p2p;

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
import com.starrtc.demo.ui.CircularCoverView;
import com.starrtc.demo.utils.AEvent;
import com.starrtc.demo.utils.ColorUtils;
import com.starrtc.demo.utils.DensityUtils;
import com.starrtc.starrtcsdk.api.XHClient;
import com.starrtc.starrtcsdk.api.XHConstants;
import com.starrtc.starrtcsdk.api.XHVoipP2PManager;
import com.starrtc.starrtcsdk.apiInterface.IXHResultCallback;
import com.starrtc.starrtcsdk.core.audio.StarRTCAudioManager;
import com.starrtc.starrtcsdk.core.player.StarPlayer;

import java.util.Set;

public class VoipP2PActivity extends BaseActivity implements View.OnClickListener {

    String TAG = "VOIP P2P VoipP2PActivity";
    private XHVoipP2PManager voipP2PManager;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);

        starRTCAudioManager = StarRTCAudioManager.create(this);
        starRTCAudioManager.start(new StarRTCAudioManager.AudioManagerEvents() {
            @Override
            public void onAudioDeviceChanged(StarRTCAudioManager.AudioDevice selectedAudioDevice, Set<StarRTCAudioManager.AudioDevice> availableAudioDevices) {
            }
        });

        setContentView(R.layout.activity_voip_p2p);
        voipP2PManager = XHClient.getInstance().getVoipP2PManager();
        voipP2PManager.setRtcMediaType(XHConstants.XHRtcMediaTypeEnum.STAR_RTC_MEDIA_TYPE_VIDEO_AND_AUDIO);
        addListener();

        targetId = getIntent().getStringExtra("targetId");
        action = getIntent().getStringExtra(ACTION);

        MLOC.d(TAG,"targetId " + targetId);

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

        ((TextView)findViewById(R.id.targetid_text)).setText(targetId);
        findViewById(R.id.head_bg).setBackgroundColor(ColorUtils.getColor(VoipP2PActivity.this,targetId));
        ((CircularCoverView)findViewById(R.id.head_cover)).setCoverColor(Color.parseColor("#000000"));
        int cint = DensityUtils.dip2px(VoipP2PActivity.this,45);
        ((CircularCoverView)findViewById(R.id.head_cover)).setRadians(cint, cint, cint, cint,0);

        findViewById(R.id.calling_hangup).setOnClickListener(this);
        findViewById(R.id.talking_hangup).setOnClickListener(this);

        if(action.equals(CALLING)){
            showCallingView();
            MLOC.d(TAG,"call");
            voipP2PManager.call(this,targetId, new IXHResultCallback() {
                @Override
                public void success(Object data) {
                    MLOC.d(TAG,"call success");
                }
                @Override
                public void failed(String errMsg) {
                    MLOC.d(TAG,"call failed");
                    VoipP2PActivity.this.stopAndFinish();
                }
            });
        }else{
            onPickup();
            showTalkingView();
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
        MLOC.canPickupVoip = true;
        super.onDestroy();
    }

    @Override
    public void onBackPressed(){
        new AlertDialog.Builder(VoipP2PActivity.this).setCancelable(true)
                .setTitle("是否挂断?")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        timer.stop();
                        voipP2PManager.hangup(new IXHResultCallback() {
                            @Override
                            public void success(Object data) {
                                stopAndFinish();
                            }

                            @Override
                            public void failed(final String errMsg) {
                                MLOC.d(TAG,"AEVENT_VOIP_ON_STOP errMsg:"+errMsg);
                                MLOC.showMsg(VoipP2PActivity.this,errMsg);
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
                MLOC.d(TAG,"对方线路忙");
                MLOC.showMsg(VoipP2PActivity.this,"对方线路忙");
                VoipP2PActivity.this.stopAndFinish();
                break;
            case AEvent.AEVENT_VOIP_REV_REFUSED:
                MLOC.d(TAG,"对方拒绝通话");
                MLOC.showMsg(VoipP2PActivity.this,"对方拒绝通话");
                VoipP2PActivity.this.stopAndFinish();
                break;
            case AEvent.AEVENT_VOIP_REV_HANGUP:
                MLOC.d(TAG,"对方已挂断");
                MLOC.showMsg(VoipP2PActivity.this,"对方已挂断");
                timer.stop();
                VoipP2PActivity.this.stopAndFinish();
                break;
            case AEvent.AEVENT_VOIP_REV_CONNECT:
                MLOC.d(TAG,"对方允许通话");
                showTalkingView();
                break;
            case AEvent.AEVENT_VOIP_REV_ERROR:
                MLOC.d(TAG,(String) eventObj);
                VoipP2PActivity.this.stopAndFinish();
                break;
        }
    }

    private void setupViews(){
        voipP2PManager.setupView(selfPlayer, targetPlayer, new IXHResultCallback() {
            @Override
            public void success(Object data) {
                MLOC.d(TAG,"setupView success");
                MLOC.d(TAG,"onPickup");

            }
            @Override
            public void failed(String errMsg) {
                MLOC.d(TAG,"setupView failed");
                VoipP2PActivity.this.stopAndFinish();
            }
        });
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
        voipP2PManager.accept(this,targetId,null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.calling_hangup:
                voipP2PManager.cancel(new IXHResultCallback() {
                    @Override
                    public void success(Object data) {
                        MLOC.d(TAG,"cancel success");
                        VoipP2PActivity.this.stopAndFinish();
                    }

                    @Override
                    public void failed(String errMsg) {
                        MLOC.d(TAG,"cancel success");
                        VoipP2PActivity.this.stopAndFinish();
                    }
                });
                break;
            case R.id.talking_hangup:
                voipP2PManager.hangup(new IXHResultCallback() {
                    @Override
                    public void success(Object data) {
                        MLOC.d(TAG,"hangup success");
                        VoipP2PActivity.this.stopAndFinish();
                    }

                    @Override
                    public void failed(String errMsg) {
                        MLOC.d(TAG,"hangup failed");
                        VoipP2PActivity.this.stopAndFinish();
                    }
                });
                break;

        }
    }

    private void stopAndFinish(){
        if(starRTCAudioManager!=null){
            starRTCAudioManager.stop();
        }
        removeListener();
        finish();
    }
}
