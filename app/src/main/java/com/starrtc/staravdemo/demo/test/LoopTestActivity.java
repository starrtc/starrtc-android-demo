package com.starrtc.staravdemo.demo.test;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.starrtc.staravdemo.R;
import com.starrtc.starrtcsdk.StarManager;
import com.starrtc.starrtcsdk.player.StarPlayer;
import com.starrtc.starrtcsdk.player.StarPlayerScaleType;

public class LoopTestActivity extends Activity implements View.OnClickListener {

    private StarPlayer selfPlayer;
    private StarPlayer selfSmallPlayer;
    private StarPlayer targetPlayer;
    private StarPlayer targetSmallPlayer;

    private TextView vVideoSizeText;
    private TextView vVideoFpsText;
    private TextView vMediaConfigText;
    private int encodeLevel = 1;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loop_test);

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
                StarManager.getInstance().switchCamera();
            }
        });
        findViewById(R.id.encode_lvl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(encodeLevel<3){
                    encodeLevel++;
                }else{
                    encodeLevel = 1;
                }
                StarManager.getInstance().encodeLevelDown(encodeLevel);
                ((TextView)findViewById(R.id.encode_lvl)).setText("lv_"+encodeLevel);
            }
        });
        ((TextView)findViewById(R.id.encode_lvl)).setText("lv_"+encodeLevel);

        vVideoSizeText = (TextView) findViewById(R.id.video_size);
        vVideoFpsText = (TextView) findViewById(R.id.video_fps);
        vMediaConfigText = (TextView) findViewById(R.id.media_config);
        vVideoSizeText.setText(StarManager.keepWatch_videoSize);
        vMediaConfigText.setText(StarManager.keepWatch_mediaEncodeConfig);

        final Handler mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what){
                    case 0:
                        int fps = msg.getData().getInt("fpsBig");
                        vVideoFpsText.setText("upId = "+StarManager.keepWatch_upId
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
                    int fps = (int) StarManager.fpsQueue.take();
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

        targetPlayer.setScalType(StarManager.bigVideoW,StarManager.bigVideoH, StarPlayerScaleType.DRAW_TYPEDRAW_TYPE_CENTER_TOP);
        selfPlayer.setScalType(StarManager.bigVideoW,StarManager.bigVideoH, StarPlayerScaleType.DRAW_TYPEDRAW_TYPE_CENTER_TOP);
        if(StarManager.smallVideoH ==0|| StarManager.smallVideoW ==0){
            selfSmallPlayer.setVisibility(View.GONE);
            targetSmallPlayer.setVisibility(View.GONE);

            StarManager.getInstance().initLoopTest(getApplicationContext(),
                    targetPlayer,0,
                    selfPlayer,2);

        }else{
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) selfSmallPlayer.getLayoutParams();
            lp.height = (int)((float)StarManager.smallVideoH/(float)StarManager.smallVideoW*lp.width);
            selfSmallPlayer.setLayoutParams(lp);
            selfSmallPlayer.setScalType(StarManager.smallVideoW,StarManager.smallVideoH, StarPlayerScaleType.DRAW_TYPEDRAW_TYPE_CENTER_TOP);
            RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) targetSmallPlayer.getLayoutParams();
            lp2.height = (int)((float)StarManager.smallVideoH/(float)StarManager.smallVideoW*lp.width);
            targetSmallPlayer.setLayoutParams(lp2);
            targetSmallPlayer.setScalType(StarManager.smallVideoW,StarManager.smallVideoH, StarPlayerScaleType.DRAW_TYPEDRAW_TYPE_CENTER_TOP);
            StarManager.getInstance().initLoopTest(getApplicationContext(),
                    targetPlayer,0,
                    targetSmallPlayer,1,
                    selfPlayer,2,
                    selfSmallPlayer,3);
        }

    }

    @Override
    public void onBackPressed() {
        StarManager.getInstance().stopLoopTest();
        StarManager.stopKeepWatch();
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.target_gl_view:
                StarManager.startKeepWatch(0);
                break;
            case R.id.target_small_gl_view:
                StarManager.startKeepWatch(1);
                break;
            case R.id.self_gl_view:
                StarManager.startKeepWatch(2);
                break;
            case R.id.self_small_gl_view:
                StarManager.startKeepWatch(3);
                break;
        }
    }
}
