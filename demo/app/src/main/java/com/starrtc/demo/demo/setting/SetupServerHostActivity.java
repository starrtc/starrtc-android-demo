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
import com.starrtc.demo.demo.service.KeepLiveService;
import com.starrtc.demo.utils.AEvent;
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
        ((TextView)findViewById(R.id.title_text)).setText("服务器配置");

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_id = ((EditText)findViewById(R.id.user_id)).getText().toString().trim();
                if(!TextUtils.isEmpty(user_id)){
                    MLOC.saveUserId(user_id);
                }
                String voip_server = ((EditText)findViewById(R.id.voip_server)).getText().toString().trim();
                if(!TextUtils.isEmpty(voip_server)){
                    MLOC.saveVoipServerUrl(voip_server);
                }
                String im_server = ((EditText)findViewById(R.id.im_server)).getText().toString().trim();
                if(!TextUtils.isEmpty(im_server)){
                    MLOC.saveImServerUrl(im_server);
                }
                String chatroom_server = ((EditText)findViewById(R.id.chatroom_server)).getText().toString().trim();
                if(!TextUtils.isEmpty(chatroom_server)){
                    MLOC.saveChatroomServerUrl(chatroom_server);
                }
                String src_server = ((EditText)findViewById(R.id.src_server)).getText().toString().trim();
                if(!TextUtils.isEmpty(src_server)){
                    MLOC.saveSrcServerUrl(src_server);
                }
                String vdn_server = ((EditText)findViewById(R.id.vdn_server)).getText().toString().trim();
                if(!TextUtils.isEmpty(vdn_server)){
                    MLOC.saveVdnServerUrl(vdn_server);
                }
                String proxy_server = ((EditText)findViewById(R.id.proxy_server)).getText().toString().trim();
                if(!TextUtils.isEmpty(proxy_server)){
                    MLOC.saveProxyServerUrl(proxy_server);
                }

                String imGroupListUrl = ((EditText)findViewById(R.id.im_group_list)).getText().toString().trim();
                if(!TextUtils.isEmpty(imGroupListUrl)){
                    MLOC.saveImGroupListUrl(imGroupListUrl);
                }
                String imGroupInfoUrl = ((EditText)findViewById(R.id.im_group_info)).getText().toString().trim();
                if(!TextUtils.isEmpty(imGroupInfoUrl)){
                    MLOC.saveImGroupInfoUrl(imGroupInfoUrl);
                }
                String listSaveUrl = ((EditText)findViewById(R.id.list_save)).getText().toString().trim();
                if(!TextUtils.isEmpty(listSaveUrl)){
                    MLOC.saveListSaveUrl(listSaveUrl);
                }
                String listDeleteUrl = ((EditText)findViewById(R.id.list_delete)).getText().toString().trim();
                if(!TextUtils.isEmpty(listDeleteUrl)){
                    MLOC.saveListDeleteUrl(listDeleteUrl);
                }
                String listQueryUrl = ((EditText)findViewById(R.id.list_query)).getText().toString().trim();
                if(!TextUtils.isEmpty(listQueryUrl)){
                    MLOC.saveListQueryUrl(listQueryUrl);
                }

                XHClient.getInstance().getLoginManager().logout();
                stopService(new Intent(SetupServerHostActivity.this, KeepLiveService.class));
                stopService(new Intent(SetupServerHostActivity.this, FloatWindowsService.class));
                startService(new Intent(SetupServerHostActivity.this, KeepLiveService.class));
                finish();
            }
        });

    }
    @Override
    public void onResume(){
        super.onResume();
        ((EditText)findViewById(R.id.user_id)).setText(MLOC.userId);
        ((EditText)findViewById(R.id.voip_server)).setText(MLOC.VOIP_SERVER_URL);
        ((EditText)findViewById(R.id.im_server)).setText(MLOC.IM_SERVER_URL);
        ((EditText)findViewById(R.id.chatroom_server)).setText(MLOC.CHATROOM_SERVER_URL);
        ((EditText)findViewById(R.id.src_server)).setText(MLOC.LIVE_SRC_SERVER_URL);
        ((EditText)findViewById(R.id.vdn_server)).setText(MLOC.LIVE_VDN_SERVER_URL);
        ((EditText)findViewById(R.id.proxy_server)).setText(MLOC.LIVE_PROXY_SERVER_URL);

        ((EditText)findViewById(R.id.im_group_list)).setText(MLOC.IM_GROUP_LIST_URL);
        ((EditText)findViewById(R.id.im_group_info)).setText(MLOC.IM_GROUP_INFO_URL);
        ((EditText)findViewById(R.id.list_save)).setText(MLOC.LIST_SAVE_URL);
        ((EditText)findViewById(R.id.list_delete)).setText(MLOC.LIST_DELETE_URL);
        ((EditText)findViewById(R.id.list_query)).setText(MLOC.LIST_QUERY_URL);
    }
}
