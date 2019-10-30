package com.starrtc.demo.demo.p2p;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.starrtc.demo.R;
import com.starrtc.demo.demo.BaseActivity;
import com.starrtc.demo.utils.AEvent;
import com.starrtc.demo.utils.StarNetUtil;
import com.starrtc.starrtcsdk.api.XHClient;
import com.starrtc.starrtcsdk.api.XHCustomConfig;

public class VoipP2PDemoActivity extends BaseActivity {
    private boolean onListening = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voip_p2p_main);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        addListener();
        ((TextView)findViewById(R.id.ip_addr)).setText(StarNetUtil.getIP(this));
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VoipP2PDemoActivity.this, VoipP2PCreateActivity.class));
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        if(!onListening){
            onListening = true;
            XHCustomConfig.getInstance(this).initStarDirectLink();
        }
    }

    @Override
    public void onBackPressed(){
        removeListener();
        onListening = false;
        XHCustomConfig.getInstance(this).stopStarDircetLink();
        finish();
    }

    @Override
    public void onRestart(){
        super.onRestart();
        addListener();
    }

    private void addListener(){
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_CALLING,this);
    }

    private void removeListener(){
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_CALLING,this);
    }
}
