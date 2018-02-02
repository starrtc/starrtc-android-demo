package com.starrtc.staravdemo.demo.videolive;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.starrtc.staravdemo.R;
import com.starrtc.staravdemo.demo.MLOC;
import com.starrtc.starrtcsdk.utils.StringUtils;

public class VideoLiveCreateActivity extends Activity {

    private EditText vEditText;
    private String liveId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_live_create);
        vEditText = (EditText) findViewById(R.id.id_input);
        liveId = "live_"+ MLOC.userId;
        vEditText.setText(liveId);

        findViewById(R.id.create_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String liveName = vEditText.getText().toString();
                if(StringUtils.isNotEmpty(liveName)){
                    Intent intent = new Intent(VideoLiveCreateActivity.this, VideoLiveActivity.class);
                    intent.putExtra(VideoLiveActivity.CREATER_ID,MLOC.userId);
                    intent.putExtra(VideoLiveActivity.LIVE_ID,liveName);
                    startActivity(intent);
                }
            }
        });

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
