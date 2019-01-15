package com.starrtc.demo.demo.p2p;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.starrtc.demo.R;
import com.starrtc.demo.demo.BaseActivity;
import com.starrtc.demo.demo.MLOC;
import com.starrtc.demo.ui.CircularCoverView;
import com.starrtc.demo.utils.AEvent;
import com.starrtc.demo.utils.ColorUtils;
import com.starrtc.demo.utils.DensityUtils;
import com.starrtc.starrtcsdk.api.XHClient;
import com.starrtc.starrtcsdk.apiInterface.IXHResultCallback;

public class VoipP2PRingingActivity extends BaseActivity implements View.OnClickListener {

    private String targetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_voip_p2p_ringing);
        addListener();

        targetId = getIntent().getStringExtra("targetId");
        findViewById(R.id.ring_hangoff).setOnClickListener(this);
        findViewById(R.id.ring_pickup).setOnClickListener(this);
        ((TextView)findViewById(R.id.targetid_text)).setText(targetId);
        findViewById(R.id.head_bg).setBackgroundColor(ColorUtils.getColor(VoipP2PRingingActivity.this,targetId));
        ((CircularCoverView)findViewById(R.id.head_cover)).setCoverColor(Color.parseColor("#000000"));
        int cint = DensityUtils.dip2px(VoipP2PRingingActivity.this,45);
        ((CircularCoverView)findViewById(R.id.head_cover)).setRadians(cint, cint, cint, cint,0);
    }

    public void addListener(){
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_HANGUP,this);
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_ERROR,this);
    }

    public void removeListener(){
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_HANGUP,this);
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_ERROR,this);
    }

    @Override
    public void dispatchEvent(final String aEventID, boolean success, final Object eventObj) {
        super.dispatchEvent(aEventID,success,eventObj);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (aEventID){
                    case AEvent.AEVENT_VOIP_REV_HANGUP:
                        MLOC.d("","对方已挂断");
                        MLOC.showMsg(VoipP2PRingingActivity.this,"对方已挂断");
                        finish();
                        break;
                    case AEvent.AEVENT_VOIP_REV_ERROR:
                        MLOC.showMsg(VoipP2PRingingActivity.this, (String) eventObj);
                        finish();
                        break;
                }
            }
        });
    }

    @Override
    public void onRestart(){
        super.onRestart();
        addListener();
    }

    @Override
    public void onStop(){
        super.onStop();
        removeListener();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ring_hangoff:
                XHClient.getInstance().getVoipP2PManager().refuse(new IXHResultCallback() {
                    @Override
                    public void success(Object data) {
                        finish();
                    }

                    @Override
                    public void failed(String errMsg) {
                        finish();
                    }
                });
                break;
            case R.id.ring_pickup:{
                Intent intent = new Intent(VoipP2PRingingActivity.this,VoipP2PActivity.class);
                intent.putExtra("targetId",targetId);
                intent.putExtra(VoipP2PActivity.ACTION,VoipP2PActivity.RING);
                startActivity(intent);
                finish();
                break;
            }

        }
    }
}
