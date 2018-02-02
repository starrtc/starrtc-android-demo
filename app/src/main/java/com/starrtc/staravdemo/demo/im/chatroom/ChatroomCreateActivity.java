package com.starrtc.staravdemo.demo.im.chatroom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.starrtc.staravdemo.R;
import com.starrtc.starrtcsdk.utils.StringUtils;

public class ChatroomCreateActivity extends Activity {

    private EditText vEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom_create);
        vEditText = (EditText) findViewById(R.id.id_input);

        findViewById(R.id.create_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roomName = vEditText.getText().toString();
                if(StringUtils.isNotEmpty(roomName)){
                    Intent intent = new Intent(ChatroomCreateActivity.this, ChatroomActivity.class);
                    intent.putExtra(ChatroomActivity.TYPE,ChatroomActivity.CHATROOM_NAME);
                    intent.putExtra(ChatroomActivity.CHATROOM_NAME,roomName);
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
