package com.starrtc.demo.demo.videolive;

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

public class VideoLiveCreateActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_live_create);
        ((TextView)findViewById(R.id.title_text)).setText("创建互动直播");
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
                if(TextUtils.isEmpty(inputId)){
                    MLOC.showMsg(VideoLiveCreateActivity.this,"id不能为空");
                }else{
                    Intent intent = new Intent(VideoLiveCreateActivity.this, VideoLiveActivity.class);
                    intent.putExtra(VideoLiveActivity.LIVE_TYPE,XHConstants.XHLiveType.XHLiveTypeGlobalPublic);
                    intent.putExtra(VideoLiveActivity.LIVE_NAME,inputId);
                    intent.putExtra(VideoLiveActivity.CREATER_ID,MLOC.userId);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
