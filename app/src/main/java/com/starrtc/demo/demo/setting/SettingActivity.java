package com.starrtc.demo.demo.setting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.starrtc.demo.R;
import com.starrtc.demo.demo.BaseActivity;
import com.starrtc.demo.demo.MLOC;
import com.starrtc.demo.demo.test.EchoTestActivity;
import com.starrtc.starrtcsdk.api.XHClient;
import com.starrtc.starrtcsdk.core.StarRtcCore;
import com.starrtc.starrtcsdk.api.XHConstants;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

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
        findViewById(R.id.btn_video_rotation).setOnClickListener(this);
        findViewById(R.id.opengl_switch).setOnClickListener(this);
        findViewById(R.id.hard_encode_switch).setOnClickListener(this);
        findViewById(R.id.btn_about).setOnClickListener(this);
        findViewById(R.id.btn_logout).setOnClickListener(this);
    }
    @Override
    public void onResume(){
        super.onResume();

        findViewById(R.id.opengl_switch).setSelected(StarRtcCore.openGLESEnable);
        findViewById(R.id.hard_encode_switch).setSelected(StarRtcCore.hardEncode);
        ((TextView)findViewById(R.id.video_size_text)).setText("("+ StarRtcCore.videoConfig_videoSize +")");
        ((TextView)findViewById(R.id.video_rotation_text)).setText("("+ StarRtcCore.defaultVideoRotation +")");
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
            case R.id.btn_video_rotation:{
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setItems(new String[]{"0", "90", "180", "270"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int selectRotation = i*90;
                        XHClient.getInstance().setDefConfigCameraRotation(selectRotation);
                        if(StarRtcCore.setVideoSizeConfig(StarRtcCore.cropTypeEnum)){
                            MLOC.d("Setting","Setting rotation "+ i*90);
                            ((TextView)findViewById(R.id.video_rotation_text)).setText("("+ selectRotation +")");
                        }else{
                            MLOC.showMsg(SettingActivity.this,"配置无法修改");
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
                MLOC.hasLogout = true;
                finish();
                break;
        }
    }
}
