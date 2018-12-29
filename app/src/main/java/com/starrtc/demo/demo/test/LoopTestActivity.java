package com.starrtc.demo.demo.test;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.starrtc.demo.R;
import com.starrtc.demo.demo.BaseActivity;
import com.starrtc.demo.demo.MLOC;
import com.starrtc.starrtcsdk.api.XHConstants;
import com.starrtc.starrtcsdk.core.StarRtcCore;
import com.starrtc.starrtcsdk.core.audio.StarRTCAudioManager;
import com.starrtc.starrtcsdk.core.player.StarPlayer;

import java.util.Set;

public class LoopTestActivity extends BaseActivity implements View.OnClickListener {

    private StarPlayer selfPlayer;
    private StarPlayer selfSmallPlayer;
    private StarPlayer targetPlayer;
    private StarPlayer targetSmallPlayer;

    private TextView vVideoSizeText;
    private TextView vVideoFpsText;
    private TextView vMediaConfigText;

    private StarRTCAudioManager starRTCAudioManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        if(dm.heightPixels>dm.widthPixels){
            setContentView(R.layout.activity_loop_p);
        }else{
            setContentView(R.layout.activity_loop_l);
        }

        starRTCAudioManager = StarRTCAudioManager.create(this.getApplicationContext());
        starRTCAudioManager.start(new StarRTCAudioManager.AudioManagerEvents() {
            @Override
            public void onAudioDeviceChanged(StarRTCAudioManager.AudioDevice selectedAudioDevice, Set<StarRTCAudioManager.AudioDevice> availableAudioDevices) {
                MLOC.d("onAudioDeviceChanged ",selectedAudioDevice.name());
            }
        });


        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.info_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(findViewById(R.id.info_box).getVisibility()==View.VISIBLE){
                    findViewById(R.id.info_box).setVisibility(View.INVISIBLE);
                }else{
                    findViewById(R.id.info_box).setVisibility(View.VISIBLE);
                }
            }
        });
        findViewById(R.id.switch_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StarRtcCore.getInstance().switchCamera();
            }
        });

        vVideoSizeText = (TextView) findViewById(R.id.video_size);
        vVideoFpsText = (TextView) findViewById(R.id.video_fps);
        vMediaConfigText = (TextView) findViewById(R.id.media_config);
        vVideoSizeText.setText(StarRtcCore.videoConfig_videoSize);
        vMediaConfigText.setText(StarRtcCore.videoConfig_mediaEncodeConfig);

        final Handler mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what){
                    case 0:
                        int fps = msg.getData().getInt("fpsBig");
                        vVideoFpsText.setText("upId = "+ StarRtcCore.keepWatch_upId
                                +" | fps = "+fps
                        );
                        break;
                }
                return false;
            }
        });

        findViewById(R.id.self_gl_view).setOnClickListener(this);
        findViewById(R.id.self_small_gl_view).setOnClickListener(this);
        findViewById(R.id.target_gl_view).setOnClickListener(this);
        findViewById(R.id.target_small_gl_view).setOnClickListener(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true)
                try {
                    int fps = (int) StarRtcCore.fpsQueue.take();
                    if(fps == -1) return;
                    Message msg = new Message();
                    msg.what = 0;
                    Bundle b = new Bundle();
                    b.putInt("fpsBig",fps);
                    msg.setData(b);
                    mHandler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        selfPlayer = (StarPlayer) findViewById(R.id.self_gl_view);
        selfSmallPlayer = (StarPlayer) findViewById(R.id.self_small_gl_view);
        targetPlayer = (StarPlayer) findViewById(R.id.target_gl_view);
        targetSmallPlayer = (StarPlayer) findViewById(R.id.target_small_gl_view);
        selfSmallPlayer.setZOrderMediaOverlay(true);
        targetSmallPlayer.setZOrderMediaOverlay(true);

        targetPlayer.setVideoSize(StarRtcCore.bigVideoW, StarRtcCore.bigVideoH);
        selfPlayer.setVideoSize(StarRtcCore.bigVideoW, StarRtcCore.bigVideoH);

        if(StarRtcCore.smallVideoH ==0|| StarRtcCore.smallVideoW ==0){
            selfSmallPlayer.setVisibility(View.GONE);
            targetSmallPlayer.setVisibility(View.GONE);
            StarRtcCore.getInstance().initLoopTest(this,
                    targetPlayer,0,
                    selfPlayer,2, XHConstants.XHDeviceDirectionEnum.STAR_DEVICE_DIRECTION_HOME_RIHGT);
        }else{
            if(dm.heightPixels>dm.widthPixels){
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) selfSmallPlayer.getLayoutParams();
                lp.height = (int)((float) StarRtcCore.smallVideoH/(float) StarRtcCore.smallVideoW*lp.width);
                selfSmallPlayer.setLayoutParams(lp);
                selfSmallPlayer.setVideoSize(StarRtcCore.smallVideoW, StarRtcCore.smallVideoH);

                RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) targetSmallPlayer.getLayoutParams();
                lp2.height = (int)((float) StarRtcCore.smallVideoH/(float) StarRtcCore.smallVideoW*lp.width);
                targetSmallPlayer.setLayoutParams(lp2);
                targetSmallPlayer.setVideoSize(StarRtcCore.smallVideoW, StarRtcCore.smallVideoH);
            }else{
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) selfSmallPlayer.getLayoutParams();
                lp.width = (int)((float) StarRtcCore.smallVideoH/(float) StarRtcCore.smallVideoW *lp.height);
                selfSmallPlayer.setLayoutParams(lp);
                selfSmallPlayer.setVideoSize(StarRtcCore.smallVideoH, StarRtcCore.smallVideoW);

                RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) targetSmallPlayer.getLayoutParams();
                lp2.width = (int)((float) StarRtcCore.smallVideoH/(float) StarRtcCore.smallVideoW *lp2.height);
                targetSmallPlayer.setLayoutParams(lp2);
                targetSmallPlayer.setVideoSize(StarRtcCore.smallVideoH, StarRtcCore.smallVideoW);
            }
            StarRtcCore.getInstance().initLoopTest(this,
                    targetPlayer,0,
                    targetSmallPlayer,1,
                    selfPlayer,2,
                    selfSmallPlayer,3,
                    XHConstants.XHDeviceDirectionEnum.STAR_DEVICE_DIRECTION_HOME_RIHGT);
        }
    }

    @Override
    public void onBackPressed() {
        StarRtcCore.getInstance().stopLoopTest();
        StarRtcCore.stopKeepWatch();
        if(starRTCAudioManager!=null){
            starRTCAudioManager.stop();
        }
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.target_gl_view:
                StarRtcCore.startKeepWatch(0);
                break;
            case R.id.target_small_gl_view:
                StarRtcCore.startKeepWatch(1);
                break;
            case R.id.self_gl_view:
                StarRtcCore.startKeepWatch(2);
                break;
            case R.id.self_small_gl_view:
                StarRtcCore.startKeepWatch(3);
                break;
        }
    }
}
