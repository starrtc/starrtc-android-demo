package com.starrtc.staravdemo.demo.voip;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.starrtc.staravdemo.R;
import com.starrtc.staravdemo.demo.MLOC;
import com.starrtc.staravdemo.utils.AEvent;
import com.starrtc.staravdemo.utils.IEventListener;
import com.starrtc.starrtcsdk.im.callback.IStarCallback;
import com.starrtc.starrtcsdk.live.StarLiveConfig;
import com.starrtc.starrtcsdk.player.StarPlayer;
import com.starrtc.starrtcsdk.player.StarPlayerScaleType;
import com.starrtc.starrtcsdk.StarManager;
import com.starrtc.starrtcsdk.pusher.ScreenRecorder;
import com.starrtc.starrtcsdk.utils.StarLog;

public class VoipActivity extends Activity implements IEventListener, View.OnClickListener {

    private StarPlayer targetPlayer;
    private StarPlayer selfPlayer;

    public static String ACTION = "ACTION";
    public static String RING = "RING";
    public static String CALLING = "CALLING";
    private String action;
    private String targetId;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_voip);
        userId = MLOC.starUid;
        targetId = getIntent().getStringExtra("targetId");
        action = getIntent().getStringExtra(ACTION);

        targetPlayer = (StarPlayer) findViewById(R.id.voip_surface_target);
        selfPlayer = (StarPlayer) findViewById(R.id.voip_surface_self);
        selfPlayer.setZOrderMediaOverlay(true);

        targetPlayer.setScalType(StarManager.bigVideoW,StarManager.bigVideoH, StarPlayerScaleType.DRAW_TYPEDRAW_TYPE_CENTER_TOP);
        selfPlayer.setScalType(StarManager.bigVideoW,StarManager.bigVideoH, StarPlayerScaleType.DRAW_TYPEDRAW_TYPE_CENTER_TOP);
        findViewById(R.id.calling_hangoff).setOnClickListener(this);
        findViewById(R.id.ring_hangoff).setOnClickListener(this);
        findViewById(R.id.ring_pickup).setOnClickListener(this);
        findViewById(R.id.talking_hangoff).setOnClickListener(this);
        findViewById(R.id.switch_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StarManager.getInstance().switchCamera();
            }
        });

        findViewById(R.id.screen_btn).setOnClickListener(this);
        addListener();
        initVoip();

    }

    public void addListener(){
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_BUSY,this);
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_REFUSED,this);
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_HANGUP,this);
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_CONNECT,this);
        AEvent.addListener(AEvent.AEVENT_VOIP_ON_STOP,this);
        AEvent.addListener(AEvent.AEVENT_VOIP_GOT_TARGET_SIZE,this);
    }

    public void removeListener(){
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_BUSY,this);
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_REFUSED,this);
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_HANGUP,this);
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_CONNECT,this);
        AEvent.removeListener(AEvent.AEVENT_VOIP_ON_STOP,this);
        AEvent.removeListener(AEvent.AEVENT_VOIP_GOT_TARGET_SIZE,this);
    }

    @Override
    public void onStart(){
        super.onStart();
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
                        StarManager.getInstance().voipHangup(MLOC.starUid,targetId);
                    }
                 }
        ).show();
    }

    @Override
    public void dispatchEvent(String aEventID, boolean success, Object eventObj) {
        switch (aEventID){

            case AEvent.AEVENT_VOIP_REV_BUSY:
                MLOC.d("","对方线路忙");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MLOC.showMsg(VoipActivity.this,"对方线路忙");
                    }
                });
                StarManager.getInstance().voipStop(false);
                break;
            case AEvent.AEVENT_VOIP_REV_REFUSED:
                MLOC.d("","对方拒绝通话");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MLOC.showMsg(VoipActivity.this,"对方拒绝通话");
                    }
                });
                StarManager.getInstance().voipStop(false);
                break;
            case AEvent.AEVENT_VOIP_REV_HANGUP:
                MLOC.d("","对方已挂断");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MLOC.showMsg(VoipActivity.this,"对方已挂断");
                    }
                });
                StarManager.getInstance().voipStop(false);
                break;
            case AEvent.AEVENT_VOIP_REV_CONNECT:
                MLOC.d("","对方允许通话");
                uiHandler.sendEmptyMessage(0);
                break;
            case AEvent.AEVENT_VOIP_ON_STOP:
                removeListener();
                StarManager.getInstance().voipStop(false);
                finish();
                break;
            case AEvent.AEVENT_VOIP_GOT_TARGET_SIZE:
                MLOC.d("","收到对方的画面大小");
                Message msg = new Message();
                msg.what = 1;
                Bundle b = new Bundle();
                b.putString("size",eventObj.toString());
                msg.setData(b);
                uiHandler.sendMessage(msg);
                break;
        }
    }


    private void initVoip(){
        boolean isCaller = action.equals(CALLING);
        StarManager.getInstance().initVoip(isCaller,
                MLOC.userId,
                targetId,
                this,
                new StarLiveConfig(),
                targetPlayer,
                selfPlayer,
                new IStarCallback() {
            @Override
            public void callback(boolean reqSuccess, String statusCode, String data) {
                if (reqSuccess){
                    if(action.equals(CALLING)){
                        StarManager.getInstance().voipCall(MLOC.userId,targetId);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(action.equals(CALLING)){
                                showCallingView();
                            }else{
                                showRingView();
                            }
                        }
                    });
                }else{
                    MLOC.showMsg("initEncoder ERROR!!!");
                    removeListener();
                    finish();
                }
            }
        });
    }

    private void showCallingView(){
        findViewById(R.id.calling_view).setVisibility(View.VISIBLE);
    }

    private void showRingView(){
        findViewById(R.id.ring_view).setVisibility(View.VISIBLE);
    }

    private void showTalkingView(){
        findViewById(R.id.calling_view).setVisibility(View.INVISIBLE);
        findViewById(R.id.ring_view).setVisibility(View.INVISIBLE);
        findViewById(R.id.talking_view).setVisibility(View.VISIBLE);
    }

    private void onPickup(){
        StarManager.getInstance().voipConnect(MLOC.starUid,targetId);
        uiHandler.sendEmptyMessage(0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ring_hangoff:
                StarManager.getInstance().voipRefuse(MLOC.starUid,targetId);
                break;
            case R.id.ring_pickup:
                onPickup();
                break;
            case R.id.calling_hangoff:
                StarManager.getInstance().voipHangup(MLOC.starUid,targetId);
                break;
            case R.id.talking_hangoff:
                StarManager.getInstance().voipHangup(MLOC.starUid,targetId);
                break;
            case R.id.screen_btn:
                if(mMediaProjectionManager==null){
                    mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
                }
                if (mRecorder != null) {
                    ((TextView)findViewById(R.id.screen_btn)).setText("屏");
                    mRecorder.quit();
                    mRecorder = null;
                    StarManager.getInstance().voipShareCamera();
                } else {
                    Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
                    startActivityForResult(captureIntent, REQUEST_CODE);
                }
                break;
        }
    }



    private Handler uiHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0:
                    showTalkingView();
                    break;
                case 1:
                    try {
                        String data = msg.getData().getString("size");
                        int w = 0,h = 0;
                        String[] wh = data.toString().split("\\_");
                        w = Integer.parseInt(wh[0]);
                        h = Integer.parseInt(wh[1]);
                        targetPlayer.setScalType(w,h, StarPlayerScaleType.DRAW_TYPEDRAW_TYPE_CENTER_TOP);
                    }catch (Exception e){
                        StarLog.d(""," resizeTargetView Error:"+e.getMessage());
                    }
                    break;
                case 2:
                   showRingView();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private static final int REQUEST_CODE = 1;
    private MediaProjectionManager mMediaProjectionManager;
    private ScreenRecorder mRecorder;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        MediaProjection mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
        if (mediaProjection == null) {
            Log.e("@@", "media projection is null");
            return;
        }

        ((TextView)findViewById(R.id.screen_btn)).setText("摄");

        // video size
        final int width = StarManager.bigVideoW;
        final int height = StarManager.bigVideoH;
        final int bitrate = StarManager.bitRateBig*1000;

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        final int dpi = metrics.densityDpi;
        mRecorder = new ScreenRecorder(width, height, bitrate, dpi, mediaProjection);
        StarManager.getInstance().voipShareScreen(mRecorder);
//        moveTaskToBack(true);
    }

}
