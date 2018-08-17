package com.starrtc.demo.demo.setting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.starrtc.demo.R;
import com.starrtc.demo.demo.BaseActivity;
import com.starrtc.demo.demo.MLOC;
import com.starrtc.demo.demo.service.FloatWindowsService;
import com.starrtc.demo.demo.test.EchoTestActivity;
import com.starrtc.starrtcsdk.api.XHClient;
import com.starrtc.starrtcsdk.core.StarRtcCore;
import com.starrtc.starrtcsdk.api.XHConstants;

public class SettingActivity extends BaseActivity implements View.OnClickListener {
    /***
     * 请求悬浮窗权限
     * */
    public static final int REQUEST_WINDOW_GRANT = 201;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        findViewById(R.id.title_left_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.title_left_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView)findViewById(R.id.title_text)).setText("设置");

        findViewById(R.id.btn_test_speed).setOnClickListener(this);
        findViewById(R.id.btn_video_size).setOnClickListener(this);
        findViewById(R.id.opengl_switch).setOnClickListener(this);
        findViewById(R.id.log_switch).setOnClickListener(this);
        findViewById(R.id.hard_encode_switch).setOnClickListener(this);
        findViewById(R.id.btn_about).setOnClickListener(this);
        findViewById(R.id.btn_logout).setOnClickListener(this);
    }
    @Override
    public void onResume(){
        super.onResume();

        findViewById(R.id.opengl_switch).setSelected(StarRtcCore.openGLESEnable);
        findViewById(R.id.log_switch).setSelected(FloatWindowsService.runing);
        findViewById(R.id.hard_encode_switch).setSelected(StarRtcCore.hardEncode);
        ((TextView)findViewById(R.id.video_size_text)).setText("("+ StarRtcCore.videoConfig_videoSize +")");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_WINDOW_GRANT:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(SettingActivity.this)) {
                        Toast.makeText(SettingActivity.this, "没有打开悬浮权限~，", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_main_logout:
                finish();
                break;
            case R.id.btn_test_speed:
                startActivity(new Intent(this,EchoTestActivity.class));
                break;
            case R.id.btn_video_size:{
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setItems(XHConstants.XHCropTypeEnumName, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        XHConstants.XHCropTypeEnum selected = StarRtcCore.cropTypeEnum;
                        for (XHConstants.XHCropTypeEnum e : XHConstants.XHCropTypeEnum.values()) {
                            if(i==e.ordinal()) {
                                selected = e;
                            }
                        }
                        if(StarRtcCore.setVideoSizeConfig(selected)){
                            MLOC.d("Setting","Setting selected "+ selected.toString());
                            ((TextView)findViewById(R.id.video_size_text)).setText("("+ StarRtcCore.videoConfig_videoSize +")");
                        }else{
                            MLOC.showMsg(SettingActivity.this,"固定配置无法修改");
                        }
                    }
                });
                builder.setCancelable(true);
                AlertDialog dialog=builder.create();
                dialog.show();
                break;
            }

            case R.id.opengl_switch:
                StarRtcCore.getInstance().setOpenGLESEnable(StarRtcCore.openGLESEnable ?false:true);
                findViewById(R.id.opengl_switch).setSelected(StarRtcCore.openGLESEnable);
                break;
            case R.id.log_switch:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // 动态申请悬浮窗权限
                    if (!Settings.canDrawOverlays(SettingActivity.this)) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, REQUEST_WINDOW_GRANT);
                        return;
                    }
                }

                if(FloatWindowsService.runing){
                    findViewById(R.id.log_switch).setSelected(false);
                    stopService(new Intent(SettingActivity.this, FloatWindowsService.class));
                }else{
                    findViewById(R.id.log_switch).setSelected(true);
                    startService(new Intent(SettingActivity.this, FloatWindowsService.class));
                }
                break;
            case R.id.hard_encode_switch:
                if(StarRtcCore.setHardEncodeEnable(StarRtcCore.hardEncode?false:true)){
                    findViewById(R.id.hard_encode_switch).setSelected(StarRtcCore.hardEncode);
                }else{
                    MLOC.showMsg(SettingActivity.this,"设置失败");
                }
                break;
            case R.id.btn_about:
                startActivity(new Intent(this,AboutActivity.class));
                break;
            case R.id.btn_logout:
                XHClient.getInstance().getLoginManager().logout();
                stopService(new Intent(SettingActivity.this, FloatWindowsService.class));
                MLOC.hasLogout = true;
                finish();
                break;
        }
    }
}
