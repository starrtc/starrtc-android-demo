package com.starrtc.demo.demo.im.group;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.starrtc.demo.R;
import com.starrtc.demo.demo.BaseActivity;
import com.starrtc.starrtcsdk.core.utils.StringUtils;

public class MessageGroupCreateActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_group_create);
        ((TextView)findViewById(R.id.title_text)).setText("创建新群组");
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
                String roomName = ((EditText)findViewById(R.id.targetid_input)).getText().toString();
                if(StringUtils.isNotEmpty(roomName)){
                    Intent intent = new Intent(MessageGroupCreateActivity.this, MessageGroupActivity.class);
                    intent.putExtra(MessageGroupActivity.TYPE,MessageGroupActivity.GROUP_NAME);
                    intent.putExtra(MessageGroupActivity.GROUP_NAME,roomName);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}


