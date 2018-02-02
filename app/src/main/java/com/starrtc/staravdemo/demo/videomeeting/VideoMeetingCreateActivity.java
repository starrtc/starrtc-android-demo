package com.starrtc.staravdemo.demo.videomeeting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.starrtc.staravdemo.R;
import com.starrtc.staravdemo.demo.MLOC;
import com.starrtc.starrtcsdk.utils.StringUtils;

public class VideoMeetingCreateActivity extends Activity {

    private EditText vEditText;
    private String meetingId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_meeting_create);
        vEditText = (EditText) findViewById(R.id.id_input);
        meetingId = "meeting_"+ MLOC.userId;
        vEditText.setText(meetingId);

        findViewById(R.id.create_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String meetingName = vEditText.getText().toString();
                if(StringUtils.isNotEmpty(meetingName)){
                    Intent intent = new Intent(VideoMeetingCreateActivity.this, VideoMeetingActivity.class);
                    intent.putExtra(VideoMeetingActivity.MEETING_ID,meetingName);
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
