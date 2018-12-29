package com.starrtc.demo.demo.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.starrtc.demo.R;
import com.starrtc.demo.demo.MLOC;
import com.starrtc.demo.demo.service.FloatWindowsService;
import com.starrtc.demo.serverAPI.InterfaceUrls;
import com.starrtc.starrtcsdk.api.XHClient;

public class SetupServerHostActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_server_host);
        findViewById(R.id.title_left_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.title_left_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView)findViewById(R.id.title_text)).setText("系统设置");

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String work_server = ((EditText)findViewById(R.id.work_server)).getText().toString().trim();
                if(!TextUtils.isEmpty(work_server)){
                    MLOC.saveWorkServer(work_server);
                }
                String user_id = ((EditText)findViewById(R.id.user_id)).getText().toString().trim();
                if(!TextUtils.isEmpty(user_id)){
                    MLOC.saveUserId(user_id);
                }
                String app_id = ((EditText)findViewById(R.id.app_id)).getText().toString().trim();
                if(!TextUtils.isEmpty(app_id)){
                    MLOC.saveAgentId(app_id);
                }
                String login_host = ((EditText)findViewById(R.id.login_host)).getText().toString().trim();
                if(!TextUtils.isEmpty(login_host)){
                    MLOC.saveLoginServerUrl(login_host);
                }
                String im_host = ((EditText)findViewById(R.id.im_host)).getText().toString().trim();
                if(!TextUtils.isEmpty(im_host)){
                    MLOC.saveIMSchduleUrl(im_host);
                }
                String chatroom_host = ((EditText)findViewById(R.id.chatroom_host)).getText().toString().trim();
                if(!TextUtils.isEmpty(chatroom_host)){
                    MLOC.saveChatroomSchduleUrl(chatroom_host);
                }
                String src_host = ((EditText)findViewById(R.id.src_host)).getText().toString().trim();
                if(!TextUtils.isEmpty(src_host)){
                    MLOC.saveSrcSchduleUrl(src_host);
                }
                String vdn_host = ((EditText)findViewById(R.id.vdn_host)).getText().toString().trim();
                if(!TextUtils.isEmpty(vdn_host)){
                    MLOC.saveVdnSchduleUrl(vdn_host);
                }
                String voip_host = ((EditText)findViewById(R.id.voip_host)).getText().toString().trim();
                if(!TextUtils.isEmpty(voip_host)){
                    MLOC.saveVoipServerUrl(voip_host);
                }
                XHClient.getInstance().getLoginManager().logout();
                stopService(new Intent(SetupServerHostActivity.this, FloatWindowsService.class));
                MLOC.hasLogout = true;
                finish();
            }
        });
    }
    @Override
    public void onResume(){
        super.onResume();
        ((EditText)findViewById(R.id.work_server)).setText(InterfaceUrls.BASE_URL);
        ((EditText)findViewById(R.id.user_id)).setText(MLOC.userId);
        ((EditText)findViewById(R.id.app_id)).setText(MLOC.agentId);
        ((EditText)findViewById(R.id.login_host)).setText(MLOC.STAR_LOGIN_URL);
        ((EditText)findViewById(R.id.im_host)).setText(MLOC.IM_SCHEDULE_URL);
        ((EditText)findViewById(R.id.chatroom_host)).setText(MLOC.CHAT_ROOM_SCHEDULE_URL);
        ((EditText)findViewById(R.id.src_host)).setText(MLOC.LIVE_SRC_SCHEDULE_URL);
        ((EditText)findViewById(R.id.vdn_host)).setText(MLOC.LIVE_VDN_SCHEDULE_URL);
        ((EditText)findViewById(R.id.voip_host)).setText(MLOC.VOIP_SERVER_URL);
    }
}
