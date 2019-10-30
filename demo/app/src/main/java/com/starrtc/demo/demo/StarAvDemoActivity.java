package com.starrtc.demo.demo;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.starrtc.demo.R;
import com.starrtc.demo.demo.audiolive.AudioLiveListActivity;
import com.starrtc.demo.demo.im.IMDemoActivity;
import com.starrtc.demo.demo.miniclass.MiniClassListActivity;
import com.starrtc.demo.demo.service.KeepLiveService;
import com.starrtc.demo.demo.setting.SettingActivity;
import com.starrtc.demo.demo.superroom.SuperRoomListActivity;
import com.starrtc.demo.demo.videolive.VideoLiveListActivity;
import com.starrtc.demo.demo.videomeeting.VideoMeetingListActivity;
import com.starrtc.demo.demo.voip.VoipListActivity;
import com.starrtc.starrtcsdk.api.XHClient;


public class StarAvDemoActivity extends BaseActivity implements View.OnClickListener {

    private boolean isOnline = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star_rtc_main);
        ((TextView)findViewById(R.id.title_text)).setText(R.string.app_name);
        MLOC.userId = MLOC.loadSharedData(getApplicationContext(),"userId");
        ((ImageView)findViewById(R.id.userinfo_head)).setImageResource(MLOC.getHeadImage(this,MLOC.userId));
        ((TextView)findViewById(R.id.userinfo_id)).setText(MLOC.userId);
        findViewById(R.id.btn_main_im).setOnClickListener(this);
        findViewById(R.id.btn_main_voip).setOnClickListener(this);
        findViewById(R.id.btn_main_meeting).setOnClickListener(this);
        findViewById(R.id.btn_main_live).setOnClickListener(this);
        findViewById(R.id.btn_main_setting).setOnClickListener(this);
        findViewById(R.id.btn_main_class).setOnClickListener(this);
        findViewById(R.id.btn_main_audio).setOnClickListener(this);
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(MLOC.hasLogout){
            finish();
            MLOC.hasLogout = false;
            return;
        }
        if(MLOC.userId==null){
            startActivity(new Intent(StarAvDemoActivity.this,SplashActivity.class));
            finish();
        }
        isOnline = XHClient.getInstance().getIsOnline();
        if(isOnline){
            findViewById(R.id.loading).setVisibility(View.INVISIBLE);
        }else{
            findViewById(R.id.loading).setVisibility(View.VISIBLE);
        }
        findViewById(R.id.voip_new).setVisibility(MLOC.hasNewVoipMsg?View.VISIBLE:View.INVISIBLE);
        findViewById(R.id.im_new).setVisibility((MLOC.hasNewC2CMsg|| MLOC.hasNewGroupMsg)?View.VISIBLE:View.INVISIBLE);
    }

    @Override
    public void onRestart(){
        super.onRestart();
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
            case R.id.btn_main_setting:
                Intent intent6 = new Intent(this, SettingActivity.class);
                startActivity(intent6);
                break;
            case R.id.btn_main_im:
                Intent intent7= new Intent(this, IMDemoActivity.class);
                startActivity(intent7);
                break;
            case R.id.btn_main_class:
                Intent intent8= new Intent(this, MiniClassListActivity.class);
                startActivity(intent8);
                break;
            case R.id.btn_main_audio:
                Intent intent9= new Intent(this, SuperRoomListActivity.class);
                startActivity(intent9);
                break;
        }
    }

}
