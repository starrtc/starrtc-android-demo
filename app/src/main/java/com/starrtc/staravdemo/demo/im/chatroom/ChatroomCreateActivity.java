package com.starrtc.staravdemo.demo.im.chatroom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.starrtc.staravdemo.R;
import com.starrtc.staravdemo.demo.BaseActivity;
import com.starrtc.staravdemo.demo.MLOC;
import com.starrtc.starrtcsdk.api.XHConstants;

public class ChatroomCreateActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom_create);
        ((TextView)findViewById(R.id.title_text)).setText("创建新聊天室");
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
                XHConstants.XHChatroomType type =
                        findViewById(R.id.switch_type).isSelected()?
                                XHConstants.XHChatroomType.XHChatroomTypePublic:
                                XHConstants.XHChatroomType.XHChatroomTypeLogin;
                if(TextUtils.isEmpty(inputId)){
                    MLOC.showMsg(ChatroomCreateActivity.this,"id不能为空");
                }else{
                    Intent intent = new Intent(ChatroomCreateActivity.this,ChatroomActivity.class);
                    intent.putExtra(ChatroomActivity.TYPE,ChatroomActivity.CHATROOM_NAME);
                    intent.putExtra(ChatroomActivity.CHATROOM_NAME,inputId);
                    intent.putExtra(ChatroomActivity.CHATROOM_TYPE,type);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
