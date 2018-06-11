package com.starrtc.staravdemo.demo.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.starrtc.staravdemo.R;
import com.starrtc.staravdemo.demo.BaseActivity;
import com.starrtc.staravdemo.demo.MLOC;
import com.starrtc.staravdemo.demo.test.EchoTestActivity;
import com.starrtc.starrtcsdk.core.StarRtcCore;
import com.starrtc.starrtcsdk.api.XHConstants;
import com.starrtc.starrtcsdk.core.utils.StarLog;

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
        findViewById(R.id.opengl_switch).setOnClickListener(this);
        findViewById(R.id.hard_encode_switch).setOnClickListener(this);
        findViewById(R.id.opensl_switch).setOnClickListener(this);
        findViewById(R.id.btn_about).setOnClickListener(this);
    }
    @Override
    public void onResume(){
        super.onResume();

        findViewById(R.id.opengl_switch).setSelected(StarRtcCore.openGLESEnable);
        findViewById(R.id.opensl_switch).setSelected(StarRtcCore.openSLESEnable);
        findViewById(R.id.hard_encode_switch).setSelected(StarRtcCore.hardEncode);
        ((TextView)findViewById(R.id.video_size_text)).setText("("+ StarRtcCore.videoConfig_videoSize +")");
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
                        StarLog.d("Setting","Setting selected "+ selected.toString());
                        StarRtcCore.setVideoSizeConfig(selected);
                        ((TextView)findViewById(R.id.video_size_text)).setText("("+ StarRtcCore.videoConfig_videoSize +")");
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
            case R.id.opensl_switch:
                StarRtcCore.getInstance().setOpenSLESEnable(StarRtcCore.openSLESEnable ?false:true);
                findViewById(R.id.opensl_switch).setSelected(StarRtcCore.openSLESEnable);
                break;
            case R.id.btn_about:
                startActivity(new Intent(this,AboutActivity.class));
                break;
        }
    }
}
