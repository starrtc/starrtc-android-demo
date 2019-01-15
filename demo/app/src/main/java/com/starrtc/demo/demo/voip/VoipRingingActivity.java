package com.starrtc.demo.demo.voip;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
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
import com.starrtc.starrtcsdk.apiInterface.IXHResultCallback;

import java.text.SimpleDateFormat;

public class VoipRingingActivity extends BaseActivity implements View.OnClickListener {

    private String targetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_voip_ringing);
        addListener();

        targetId = getIntent().getStringExtra("targetId");
        findViewById(R.id.ring_hangoff).setOnClickListener(this);
        findViewById(R.id.ring_pickup).setOnClickListener(this);
        findViewById(R.id.ring_pickup_audio).setOnClickListener(this);
        ((TextView)findViewById(R.id.targetid_text)).setText(targetId);
        findViewById(R.id.head_bg).setBackgroundColor(ColorUtils.getColor(VoipRingingActivity.this,targetId));
        ((CircularCoverView)findViewById(R.id.head_cover)).setCoverColor(Color.parseColor("#000000"));
        int cint = DensityUtils.dip2px(VoipRingingActivity.this,45);
        ((CircularCoverView)findViewById(R.id.head_cover)).setRadians(cint, cint, cint, cint,0);

        HistoryBean historyBean = new HistoryBean();
        historyBean.setType(CoreDB.HISTORY_TYPE_VOIP);
        historyBean.setLastTime(new SimpleDateFormat("MM-dd HH:mm").format(new java.util.Date()));
        historyBean.setConversationId(targetId);
        historyBean.setNewMsgCount(1);
        MLOC.setHistory(historyBean,true);

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
                        MLOC.showMsg(VoipRingingActivity.this,"对方已挂断");
                        finish();
                        break;
                    case AEvent.AEVENT_VOIP_REV_ERROR:
                        MLOC.showMsg(VoipRingingActivity.this, (String) eventObj);
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
                XHClient.getInstance().getVoipManager().refuse(new IXHResultCallback() {
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
                Intent intent = new Intent(VoipRingingActivity.this,VoipActivity.class);
                intent.putExtra("targetId",targetId);
                intent.putExtra(VoipActivity.ACTION,VoipActivity.RING);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.ring_pickup_audio: {
                Intent intent = new Intent(VoipRingingActivity.this, VoipAudioActivity.class);
                intent.putExtra("targetId", targetId);
                intent.putExtra(VoipAudioActivity.ACTION, VoipAudioActivity.RING);
                startActivity(intent);
                finish();
                break;
            }
        }
    }
}
