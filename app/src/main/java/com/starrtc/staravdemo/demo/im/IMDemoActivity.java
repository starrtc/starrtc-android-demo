package com.starrtc.staravdemo.demo.im;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.starrtc.staravdemo.R;
import com.starrtc.staravdemo.demo.im.c2c.C2CActivity;
import com.starrtc.staravdemo.demo.im.chatroom.ChatroomListActivity;
import com.starrtc.staravdemo.demo.im.group.MessageGroupListActivity;

public class IMDemoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imdemo);
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.c2c_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IMDemoActivity.this, C2CActivity.class));
            }
        });
        findViewById(R.id.chatroom_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IMDemoActivity.this, ChatroomListActivity.class));
            }
        });
        findViewById(R.id.group_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IMDemoActivity.this, MessageGroupListActivity.class));
            }
        });
    }
}
