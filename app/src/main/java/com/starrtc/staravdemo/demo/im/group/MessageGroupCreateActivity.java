package com.starrtc.staravdemo.demo.im.group;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.starrtc.staravdemo.R;
import com.starrtc.starrtcsdk.utils.StringUtils;

public class MessageGroupCreateActivity extends Activity {

    private EditText vEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_group_create);
        vEditText = (EditText) findViewById(R.id.id_input);

        findViewById(R.id.create_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roomName = vEditText.getText().toString();
                if(StringUtils.isNotEmpty(roomName)){
                    Intent intent = new Intent(MessageGroupCreateActivity.this, MessageGroupActivity.class);
                    intent.putExtra(MessageGroupActivity.TYPE,MessageGroupActivity.GROUP_NAME);
                    intent.putExtra(MessageGroupActivity.GROUP_NAME,roomName);
                    startActivity(intent);
                    finish();
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
