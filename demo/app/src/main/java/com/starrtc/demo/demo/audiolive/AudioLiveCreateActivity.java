package com.starrtc.demo.demo.audiolive;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.starrtc.demo.R;
import com.starrtc.demo.demo.BaseActivity;
import com.starrtc.demo.demo.MLOC;
import com.starrtc.starrtcsdk.api.XHConstants;

public class AudioLiveCreateActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_live_create);
        ((TextView)findViewById(R.id.title_text)).setText("创建互动语音直播间");
        findViewById(R.id.title_left_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.title_left_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.yes_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputId = ((EditText)findViewById(R.id.targetid_input)).getText().toString();
                XHConstants.XHLiveType type = XHConstants.XHLiveType.XHLiveTypeGlobalPublic;

                if(TextUtils.isEmpty(inputId)){
                    MLOC.showMsg(AudioLiveCreateActivity.this,"id不能为空");
                }else{
                    Intent intent = new Intent(AudioLiveCreateActivity.this, AudioLiveActivity.class);
                    intent.putExtra(AudioLiveActivity.LIVE_TYPE,type);
                    intent.putExtra(AudioLiveActivity.LIVE_NAME,inputId);
                    intent.putExtra(AudioLiveActivity.CREATER_ID,MLOC.userId);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
