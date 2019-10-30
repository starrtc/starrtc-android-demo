package com.starrtc.demo.demo.superroom;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.starrtc.demo.R;
import com.starrtc.demo.demo.BaseActivity;
import com.starrtc.demo.demo.MLOC;
import com.starrtc.demo.demo.audiolive.AudioLiveActivity;
import com.starrtc.starrtcsdk.api.XHConstants;

public class SuperRoomCreateActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_room_create);
        ((TextView)findViewById(R.id.title_text)).setText("创建对讲机房间");
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
                XHConstants.XHSuperRoomType type = XHConstants.XHSuperRoomType.XHSuperRoomTypeGlobalPublic;
                if(TextUtils.isEmpty(inputId)){
                    MLOC.showMsg(SuperRoomCreateActivity.this,"id不能为空");
                }else{
                    Intent intent = new Intent(SuperRoomCreateActivity.this, SuperRoomActivity.class);
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
