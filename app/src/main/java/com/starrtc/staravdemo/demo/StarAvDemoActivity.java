package com.starrtc.staravdemo.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.starrtc.staravdemo.R;
import com.starrtc.staravdemo.demo.im.IMDemoActivity;
import com.starrtc.staravdemo.demo.setting.SettingActivity;
import com.starrtc.staravdemo.demo.test.LoopTestActivity;
import com.starrtc.staravdemo.demo.videolive.VideoLiveListActivity;
import com.starrtc.staravdemo.demo.videomeeting.VideoMeetingListActivity;
import com.starrtc.staravdemo.demo.voip.VoipListActivity;
import com.starrtc.staravdemo.demo.voip.VoipRingingActivity;
import com.starrtc.staravdemo.utils.AEvent;
import com.starrtc.staravdemo.utils.IEventListener;
import com.starrtc.starrtcsdk.api.XHClient;
import com.starrtc.starrtcsdk.core.StarRtcCore;

public class StarAvDemoActivity extends Activity implements View.OnClickListener, IEventListener {

    private boolean isOnline = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star_rtc_main);
        ((TextView)findViewById(R.id.title_text)).setText(R.string.app_name);
        MLOC.init(getApplicationContext());
        addListener();
        MLOC.userId = MLOC.loadSharedData(getApplicationContext(),"userId");

        ((TextView)findViewById(R.id.userinfo_id)).setText(MLOC.userId);
        findViewById(R.id.btn_video_size).setOnClickListener(this);
        findViewById(R.id.btn_main_voip).setOnClickListener(this);
        findViewById(R.id.btn_main_meeting).setOnClickListener(this);
        findViewById(R.id.btn_main_live).setOnClickListener(this);
        findViewById(R.id.btn_main_loop).setOnClickListener(this);
        findViewById(R.id.btn_main_logout).setOnClickListener(this);
        findViewById(R.id.btn_test_speed).setOnClickListener(this);

    }

    @Override
    public void onBackPressed(){

    }

    @Override
    public void onResume(){
        super.onResume();
        if(MLOC.userId==null){
            startActivity(new Intent(StarAvDemoActivity.this,SplashActivity.class));
            finish();
        }
        isOnline = StarRtcCore.getInstance().getIsOnline();
        if(isOnline){
            findViewById(R.id.loading).setVisibility(View.INVISIBLE);
        }else{
            findViewById(R.id.loading).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRestart(){
        super.onRestart();
        addListener();
    }

    private void addListener(){
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_CALLING,this);
        AEvent.addListener(AEvent.AEVENT_USER_ONLINE,this);
        AEvent.addListener(AEvent.AEVENT_USER_OFFLINE,this);
    }

    @Override
    public void onStop(){
        super.onStop();
        AEvent.removeListener(AEvent.AEVENT_USER_ONLINE,this);
        AEvent.removeListener(AEvent.AEVENT_USER_OFFLINE,this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_main_voip:
                startActivity(new Intent(this,VoipListActivity.class));
                break;
            case R.id.btn_main_meeting:
                startActivity(new Intent(this,VideoMeetingListActivity.class));
                break;
            case R.id.btn_main_live:
                Intent intent3 = new Intent(this, VideoLiveListActivity.class);
                startActivity(intent3);
                break;
            case R.id.btn_main_loop:
                startActivity(new Intent(this,LoopTestActivity.class));
                break;
            case R.id.btn_main_logout:
                XHClient.getInstance().getLoginManager().logout();
                AEvent.removeListener(AEvent.AEVENT_VOIP_REV_CALLING,this);
                finish();
                break;
            case R.id.btn_test_speed:
                Intent intent6 = new Intent(this, SettingActivity.class);
                startActivity(intent6);
                break;
            case R.id.btn_video_size:
                Intent intent7= new Intent(this, IMDemoActivity.class);
                startActivity(intent7);
                break;
        }
    }

    @Override
    public void dispatchEvent(String aEventID, boolean success, Object eventObj) {
        switch (aEventID){
            case AEvent.AEVENT_VOIP_REV_CALLING:
                if(success){
                    Intent intent = new Intent(StarAvDemoActivity.this,VoipRingingActivity.class);
                    intent.putExtra("targetId",eventObj.toString());
                    startActivity(intent);
                }
                break;
            case AEvent.AEVENT_USER_OFFLINE:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.loading).setVisibility(View.VISIBLE);
                    }
                });
                break;
            case AEvent.AEVENT_USER_ONLINE:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.loading).setVisibility(View.INVISIBLE);
                    }
                });
                break;
        }
    }
}
