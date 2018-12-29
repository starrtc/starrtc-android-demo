package com.starrtc.demo.demo.videomeeting;

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

public class VideoMeetingCreateActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_meeting_create);
        ((TextView)findViewById(R.id.title_text)).setText("创建视频会议");
        findViewById(R.id.title_left_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.title_left_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.switch_type).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.switch_type).setSelected(!findViewById(R.id.switch_type).isSelected());
            }
        });
        findViewById(R.id.yes_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputId = ((EditText)findViewById(R.id.targetid_input)).getText().toString();
//                XHConstants.XHMeetingType type =
//                        findViewById(R.id.switch_type).isSelected()?
//                                XHConstants.XHMeetingType.XHMeetingTypeGlobalPublic:
//                                XHConstants.XHMeetingType.XHMeetingTypeLoginPublic;
                XHConstants.XHMeetingType type = XHConstants.XHMeetingType.XHMeetingTypeGlobalPublic;

                if(TextUtils.isEmpty(inputId)){
                    MLOC.showMsg(VideoMeetingCreateActivity.this,"id不能为空");
                }else{
                    Intent intent = new Intent(VideoMeetingCreateActivity.this, VideoMeetingActivity.class);
                    intent.putExtra(VideoMeetingActivity.MEETING_NAME,inputId);
                    intent.putExtra(VideoMeetingActivity.MEETING_CREATER,MLOC.userId);
                    intent.putExtra(VideoMeetingActivity.MEETING_TYPE,type);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
