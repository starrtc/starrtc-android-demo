package com.starrtc.demo.demo.im;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.starrtc.demo.R;
import com.starrtc.demo.demo.BaseActivity;
import com.starrtc.demo.demo.MLOC;
import com.starrtc.demo.demo.im.c2c.C2CListActivity;
import com.starrtc.demo.demo.im.chatroom.ChatroomListActivity;
import com.starrtc.demo.demo.im.group.MessageGroupListActivity;
import com.starrtc.starrtcsdk.api.XHClient;
import com.starrtc.starrtcsdk.apiInterface.IXHResultCallback;

public class IMDemoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imdemo);
        ((TextView)findViewById(R.id.title_text)).setText("IM演示");
        findViewById(R.id.title_left_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.title_left_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.c2c_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IMDemoActivity.this, C2CListActivity.class));
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
        XHClient.getInstance().getAliveUserNum(new IXHResultCallback() {
            @Override
            public void success(Object data) {
                MLOC.d("!!!!!!!!!!!!!",data.toString());
            }

            @Override
            public void failed(String errMsg) {
                MLOC.d("!!!!!!!!!!!!!",errMsg.toString());
            }
        });
        XHClient.getInstance().getAliveUserList(1,new IXHResultCallback() {
            @Override
            public void success(Object data) {
                MLOC.d("!!!!!!!!!!!!!",data.toString());
            }

            @Override
            public void failed(String errMsg) {
                MLOC.d("!!!!!!!!!!!!!",errMsg.toString());
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        findViewById(R.id.c2c_new).setVisibility(MLOC.hasNewC2CMsg?View.VISIBLE:View.INVISIBLE);
        findViewById(R.id.group_new).setVisibility(MLOC.hasNewGroupMsg?View.VISIBLE:View.INVISIBLE);
    }

    @Override
    public void dispatchEvent(String aEventID, boolean success, final Object eventObj) {
        super.dispatchEvent(aEventID,success,eventObj);
        onResume();
    }

}
