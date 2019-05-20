package com.starrtc.demo.demo.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.starrtc.demo.utils.AEvent;
import com.starrtc.starrtcsdk.api.XHClient;

public class SetupServerHostActivity extends Activity {
    String[] arr = {"私有部署","公有云"};
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
        ((TextView)findViewById(R.id.title_text)).setText("服务器配置");

        findViewById(R.id.sdk_type).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(SetupServerHostActivity.this);
                builder.setItems(arr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==1){
                            //公有云
                            MLOC.saveServerType(MLOC.SERVER_TYPE_PUBLIC);
                            findViewById(R.id.more_box).setVisibility(View.VISIBLE);
                            findViewById(R.id.singel_box).setVisibility(View.GONE);
                            ((TextView)findViewById(R.id.sdk_type_text)).setText(arr[1]);
                        }else{
                            //私有部署
                            MLOC.saveServerType(MLOC.SERVER_TYPE_CUSTOM);
                            findViewById(R.id.more_box).setVisibility(View.GONE);
                            findViewById(R.id.singel_box).setVisibility(View.VISIBLE);
                            ((TextView)findViewById(R.id.sdk_type_text)).setText(arr[0]);
                        }
                    }
                });
                builder.setCancelable(true);
                AlertDialog dialog=builder.create();
                dialog.show();
            }
        });

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
                String im_host = ((EditText)findViewById(R.id.im_schedule)).getText().toString().trim();
                if(!TextUtils.isEmpty(im_host)){
                    MLOC.saveIMSchduleUrl(im_host);
                }
                String chatroom_host = ((EditText)findViewById(R.id.chatroom_schedule)).getText().toString().trim();
                if(!TextUtils.isEmpty(chatroom_host)){
                    MLOC.saveChatroomSchduleUrl(chatroom_host);
                }
                String src_host = ((EditText)findViewById(R.id.src_schedule)).getText().toString().trim();
                if(!TextUtils.isEmpty(src_host)){
                    MLOC.saveSrcSchduleUrl(src_host);
                }
                String vdn_host = ((EditText)findViewById(R.id.vdn_schedule)).getText().toString().trim();
                if(!TextUtils.isEmpty(vdn_host)){
                    MLOC.saveVdnSchduleUrl(vdn_host);
                }
                String proxy_host = ((EditText)findViewById(R.id.proxy_schedule)).getText().toString().trim();
                if(!TextUtils.isEmpty(proxy_host)){
                    MLOC.saveProxySchduleUrl(proxy_host);
                }
                String voip_host = ((EditText)findViewById(R.id.voip_schedule)).getText().toString().trim();
                if(!TextUtils.isEmpty(voip_host)){
                    MLOC.saveVoipSchduleUrl(voip_host);
                }
                String voip_server = ((EditText)findViewById(R.id.voip_server)).getText().toString().trim();
                if(!TextUtils.isEmpty(voip_server)){
                    MLOC.saveVoipServerUrl(voip_server);
                }
                String im_server = ((EditText)findViewById(R.id.im_server)).getText().toString().trim();
                if(!TextUtils.isEmpty(im_host)){
                    MLOC.saveImServerUrl(im_server);
                }
                String chatroom_server = ((EditText)findViewById(R.id.chatroom_server)).getText().toString().trim();
                if(!TextUtils.isEmpty(chatroom_host)){
                    MLOC.saveChatroomServerUrl(chatroom_server);
                }
                String src_server = ((EditText)findViewById(R.id.src_server)).getText().toString().trim();
                if(!TextUtils.isEmpty(src_host)){
                    MLOC.saveSrcServerUrl(src_server);
                }
                String vdn_server = ((EditText)findViewById(R.id.vdn_server)).getText().toString().trim();
                if(!TextUtils.isEmpty(vdn_host)){
                    MLOC.saveVdnServerUrl(vdn_server);
                }
                String proxy_server = ((EditText)findViewById(R.id.proxy_server)).getText().toString().trim();
                if(!TextUtils.isEmpty(proxy_server)){
                    MLOC.saveProxyServerUrl(proxy_server);
                }

                XHClient.getInstance().getLoginManager().logout();
                AEvent.notifyListener(AEvent.AEVENT_LOGOUT,true,null);
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
        ((EditText)findViewById(R.id.im_schedule)).setText(MLOC.IM_SCHEDULE_URL);
        ((EditText)findViewById(R.id.chatroom_schedule)).setText(MLOC.CHAT_ROOM_SCHEDULE_URL);
        ((EditText)findViewById(R.id.src_schedule)).setText(MLOC.LIVE_SRC_SCHEDULE_URL);
        ((EditText)findViewById(R.id.vdn_schedule)).setText(MLOC.LIVE_VDN_SCHEDULE_URL);
        ((EditText)findViewById(R.id.proxy_schedule)).setText(MLOC.LIVE_PROXY_SCHEDULE_URL);
        ((EditText)findViewById(R.id.voip_schedule)).setText(MLOC.VOIP_SCHEDULE_URL);
        ((EditText)findViewById(R.id.voip_server)).setText(MLOC.VOIP_SERVER_URL);
        ((EditText)findViewById(R.id.im_server)).setText(MLOC.IM_SERVER_URL);
        ((EditText)findViewById(R.id.chatroom_server)).setText(MLOC.CHATROOM_SERVER_URL);
        ((EditText)findViewById(R.id.src_server)).setText(MLOC.LIVE_SRC_SERVER_URL);
        ((EditText)findViewById(R.id.vdn_server)).setText(MLOC.LIVE_VDN_SERVER_URL);
        ((EditText)findViewById(R.id.proxy_server)).setText(MLOC.LIVE_PROXY_SERVER_URL);

        if(MLOC.SERVER_TYPE.equals(MLOC.SERVER_TYPE_PUBLIC)){
            findViewById(R.id.more_box).setVisibility(View.VISIBLE);
            findViewById(R.id.singel_box).setVisibility(View.GONE);
            ((TextView)findViewById(R.id.sdk_type_text)).setText(arr[1]);
        }else{
            findViewById(R.id.more_box).setVisibility(View.GONE);
            findViewById(R.id.singel_box).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.sdk_type_text)).setText(arr[0]);
        }
    }
}
