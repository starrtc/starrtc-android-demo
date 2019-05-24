package com.starrtc.demo.demo.setting;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.starrtc.demo.R;
import com.starrtc.demo.demo.BaseActivity;
import com.starrtc.demo.demo.MLOC;
import com.starrtc.demo.demo.p2p.VoipP2PDemoActivity;
import com.starrtc.demo.demo.service.FloatWindowsService;
import com.starrtc.demo.demo.test.LoopTestActivity;
import com.starrtc.demo.demo.thirdstream.RtspTestListActivity;
import com.starrtc.demo.utils.AEvent;
import com.starrtc.starrtcsdk.api.XHClient;
import com.starrtc.starrtcsdk.api.XHCustomConfig;
import com.starrtc.starrtcsdk.api.XHConstants;
import com.starrtc.starrtcsdk.api.XHSDKHelper;

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

        findViewById(R.id.btn_server_set).setOnClickListener(this);
        findViewById(R.id.btn_test_loop).setOnClickListener(this);
        findViewById(R.id.btn_test_rtsp).setOnClickListener(this);
        findViewById(R.id.btn_test_p2p).setOnClickListener(this);
        findViewById(R.id.no_audio_switch).setOnClickListener(this);
        findViewById(R.id.no_video_switch).setOnClickListener(this);
        findViewById(R.id.btn_video_size).setOnClickListener(this);
        findViewById(R.id.btn_video_config_big).setOnClickListener(this);
        findViewById(R.id.btn_video_config_small).setOnClickListener(this);
        findViewById(R.id.btn_video_codec_type).setOnClickListener(this);
        findViewById(R.id.btn_audio_codec_type).setOnClickListener(this);
        findViewById(R.id.btn_audio_source).setOnClickListener(this);
        findViewById(R.id.btn_audio_stream_type).setOnClickListener(this);
        findViewById(R.id.opengl_switch).setOnClickListener(this);
        findViewById(R.id.opensl_switch).setOnClickListener(this);
        findViewById(R.id.dy_bt_fp_switch).setOnClickListener(this);
        findViewById(R.id.voip_p2p_switch).setOnClickListener(this);
        findViewById(R.id.audio_process_switch).setOnClickListener(this);
        findViewById(R.id.audio_process_qulity_switch).setOnClickListener(this);
        findViewById(R.id.log_switch).setOnClickListener(this);
        findViewById(R.id.hard_encode_switch).setOnClickListener(this);
        findViewById(R.id.btn_about).setOnClickListener(this);
        findViewById(R.id.btn_logout).setOnClickListener(this);
        findViewById(R.id.btn_uploadlogs).setOnClickListener(this);


    }
    @Override
    public void onResume(){
        super.onResume();
        if(MLOC.hasLogout){
            finish();
            MLOC.hasLogout = true;
            return;
        }
        findViewById(R.id.opengl_switch).setSelected(XHCustomConfig.getInstance().getOpenGLESEnable());
        findViewById(R.id.log_switch).setSelected(FloatWindowsService.runing);
        findViewById(R.id.hard_encode_switch).setSelected(XHCustomConfig.getInstance().getHardwareEnable());
        ((TextView)findViewById(R.id.video_size_text)).setText("("+ XHCustomConfig.getInstance().getVideoSizeName() +")");
        findViewById(R.id.opensl_switch).setSelected(XHCustomConfig.getInstance().getOpenSLESEnable());
        findViewById(R.id.dy_bt_fp_switch).setSelected(XHCustomConfig.getInstance().getDynamicBitrateAndFpsEnable());
        findViewById(R.id.voip_p2p_switch).setSelected(XHCustomConfig.getInstance().getVoipP2PEnable());
        findViewById(R.id.audio_process_switch).setSelected(XHCustomConfig.getInstance().getAudioProcessEnable());
        findViewById(R.id.audio_process_qulity_switch).setSelected(XHCustomConfig.getInstance().getAECConfigQulity() ==
                XHConstants.XHAudioAECQulityEnum.AUDIO_AEC_LOW_QULITY?true:false);
        ((TextView)findViewById(R.id.video_config_big_text)).setText("("+ XHCustomConfig.getInstance().getBigVideoFPS() +"/"+XHCustomConfig.getInstance().getBigVideoBitrate()+")");
        ((TextView)findViewById(R.id.video_config_small_text)).setText("("+ XHCustomConfig.getInstance().getSmallVideoFPS() +"/"+XHCustomConfig.getInstance().getSmallVideoBitrate()+")");
        findViewById(R.id.no_audio_switch).setSelected(!XHCustomConfig.getInstance().getAudioEnable());
        findViewById(R.id.no_video_switch).setSelected(!XHCustomConfig.getInstance().getVideoEnable());
        ((TextView)findViewById(R.id.video_codec_type_text)).setText(XHCustomConfig.getInstance().getVideoCodecTypeName());
        ((TextView)findViewById(R.id.audio_codec_type_text)).setText(XHCustomConfig.getInstance().getAudioCodecTypeName());
        ((TextView)findViewById(R.id.audio_source)).setText(XHCustomConfig.getInstance().getAudioSourceName());
        ((TextView)findViewById(R.id.audio_stream_type)).setText(XHCustomConfig.getInstance().getAudioStreamTypeName());


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
            case R.id.btn_server_set:
                startActivity(new Intent(this,SetupServerHostActivity.class));
                break;
            case R.id.btn_test_loop:
                startActivity(new Intent(this,LoopTestActivity.class));
                break;
            case R.id.btn_test_rtsp:
                startActivity(new Intent(this,RtspTestListActivity.class));
                break;
            case R.id.btn_test_p2p:
                startActivity(new Intent(this,VoipP2PDemoActivity.class));
                break;
            case R.id.no_audio_switch:
                XHCustomConfig.getInstance().setDefConfigAudioEnable(XHCustomConfig.getInstance().getAudioEnable() ?false:true);
                findViewById(R.id.no_audio_switch).setSelected(!XHCustomConfig.getInstance().getAudioEnable());
                break;
            case R.id.no_video_switch:
                XHCustomConfig.getInstance().setDefConfigVideoEnable(XHCustomConfig.getInstance().getVideoEnable() ?false:true);
                findViewById(R.id.no_video_switch).setSelected(!XHCustomConfig.getInstance().getVideoEnable());
                break;
            case R.id.btn_video_config_big:
                showAddDialog(true);
                break;
            case R.id.btn_video_config_small:
                showAddDialog(false);
                break;
            case R.id.btn_video_codec_type:{
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setItems(XHConstants.XHVideoCodecConfigEnumName, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        XHConstants.XHVideoCodecConfigEnum selected = XHCustomConfig.getInstance().getVideoCodecType();
                        for (XHConstants.XHVideoCodecConfigEnum e : XHConstants.XHVideoCodecConfigEnum.values()) {
                            if(i==e.ordinal()) {
                                selected = e;
                            }
                        }
                        XHCustomConfig.getInstance().setDefConfigVideoCodecType(selected);
                        onResume();
                    }
                });
                builder.setCancelable(true);
                AlertDialog dialog=builder.create();
                dialog.show();
                break;
            }
            case R.id.btn_audio_codec_type:{
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setItems(XHConstants.XHAudioCodecConfigEnumName, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        XHConstants.XHAudioCodecConfigEnum selected = XHCustomConfig.getInstance().getAudioCodecType();
                        for (XHConstants.XHAudioCodecConfigEnum e : XHConstants.XHAudioCodecConfigEnum.values()) {
                            if(i==e.ordinal()) {
                                selected = e;
                            }
                        }
                        XHCustomConfig.getInstance().setDefConfigAudioCodecType(selected);
                        onResume();
                    }
                });
                builder.setCancelable(true);
                AlertDialog dialog=builder.create();
                dialog.show();
                break;
            }
            case R.id.btn_audio_source:{
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setItems(XHConstants.XHAudioSourceEnumName, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for (XHConstants.XHAudioSourceEnum e : XHConstants.XHAudioSourceEnum.values()) {
                            if(i==e.ordinal()) {
                                XHCustomConfig.getInstance().setDefConfigAudioSource(e);
                                onResume();
                                return;
                            }
                        }
                    }
                });
                builder.setCancelable(true);
                AlertDialog dialog=builder.create();
                dialog.show();
                break;
            }
            case R.id.btn_audio_stream_type:{
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setItems(XHConstants.XHAudioStreamTypeEnumName, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for (XHConstants.XHAudioStreamTypeEnum e : XHConstants.XHAudioStreamTypeEnum.values()) {
                            if(i==e.ordinal()) {
                                XHCustomConfig.getInstance().setDefConfigAudioStreamType(e);
                                onResume();
                                return;
                            }
                        }
                    }
                });
                builder.setCancelable(true);
                AlertDialog dialog=builder.create();
                dialog.show();
                break;
            }
            case R.id.btn_video_size:{
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setItems(XHConstants.XHCropTypeEnumName, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        XHConstants.XHCropTypeEnum selected = XHCustomConfig.getInstance().getVideoSize();
                        for (XHConstants.XHCropTypeEnum e : XHConstants.XHCropTypeEnum.values()) {
                            if(i==e.ordinal()) {
                                selected = e;
                            }
                        }
                        if(XHCustomConfig.getInstance().setDefConfigVideoSize(selected)){
                            MLOC.d("Setting","Setting selected "+ selected.toString());
                            ((TextView)findViewById(R.id.video_size_text)).setText("("+ XHCustomConfig.getInstance().getVideoSizeName() +")");
                        }else{
                            MLOC.showMsg(SettingActivity.this,"设备无法支持所选配置");
                        }
                    }
                });
                builder.setCancelable(true);
                AlertDialog dialog=builder.create();
                dialog.show();
                break;
            }

            case R.id.opengl_switch:
                XHCustomConfig.getInstance().setDefConfigOpenGLESEnable(XHCustomConfig.getInstance().getOpenGLESEnable() ?false:true);
                findViewById(R.id.opengl_switch).setSelected(XHCustomConfig.getInstance().getOpenGLESEnable());
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
                if(XHCustomConfig.getInstance().setHardwareEnable(XHCustomConfig.getInstance().getHardwareEnable()?false:true)){
                    findViewById(R.id.hard_encode_switch).setSelected(XHCustomConfig.getInstance().getHardwareEnable());
                }else{
                    MLOC.showMsg(SettingActivity.this,"设置失败");
                }
                break;
            case R.id.btn_about:
                startActivity(new Intent(this,AboutActivity.class));
                break;
            case R.id.btn_uploadlogs:
                XHCustomConfig.getInstance().uploadLogs();
                MLOC.showMsg(this,"日志已上传");
                break;
            case R.id.btn_logout:
                XHClient.getInstance().getLoginManager().logout();
                AEvent.notifyListener(AEvent.AEVENT_LOGOUT,true,null);
                stopService(new Intent(SettingActivity.this, FloatWindowsService.class));
                MLOC.hasLogout = true;
                finish();
                break;
            case R.id.opensl_switch:
                XHCustomConfig.getInstance().setDefConfigOpenSLESEnable(XHCustomConfig.getInstance().getOpenSLESEnable() ?false:true);
                findViewById(R.id.opensl_switch).setSelected(XHCustomConfig.getInstance().getOpenSLESEnable());
                break;
            case R.id.dy_bt_fp_switch:
                XHCustomConfig.getInstance().setDefConfigDynamicBitrateAndFpsEnable(XHCustomConfig.getInstance().getDynamicBitrateAndFpsEnable() ?false:true);
                findViewById(R.id.dy_bt_fp_switch).setSelected(XHCustomConfig.getInstance().getDynamicBitrateAndFpsEnable());
                break;
            case R.id.voip_p2p_switch:
                XHCustomConfig.getInstance().setDefConfigVoipP2PEnable(XHCustomConfig.getInstance().getVoipP2PEnable() ?false:true);
                findViewById(R.id.voip_p2p_switch).setSelected(XHCustomConfig.getInstance().getVoipP2PEnable());
                break;
            case R.id.audio_process_switch:
                XHCustomConfig.getInstance().setDefConfigAudioProcessEnable(XHCustomConfig.getInstance().getAudioProcessEnable() ?false:true);
                findViewById(R.id.audio_process_switch).setSelected(XHCustomConfig.getInstance().getAudioProcessEnable());
                break;
            case R.id.audio_process_qulity_switch:
                XHCustomConfig.getInstance().setDefConfigAECConfigQulity(
                        XHCustomConfig.getInstance().getAECConfigQulity() ==
                                XHConstants.XHAudioAECQulityEnum.AUDIO_AEC_HIGH_QULITY
                                ?XHConstants.XHAudioAECQulityEnum.AUDIO_AEC_LOW_QULITY
                                :XHConstants.XHAudioAECQulityEnum.AUDIO_AEC_HIGH_QULITY);
                findViewById(R.id.audio_process_qulity_switch).setSelected(XHCustomConfig.getInstance().getAECConfigQulity() ==
                        XHConstants.XHAudioAECQulityEnum.AUDIO_AEC_LOW_QULITY?true:false);
                break;
        }
    }


    private void showAddDialog(final Boolean isbig){
        final Dialog dialog = new Dialog(this,R.style.dialog_popup);
        dialog.setContentView(R.layout.dialog_video_config_setting);
        Window win = dialog.getWindow();
        win.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        win.setGravity(Gravity.CENTER);
        dialog.setCanceledOnTouchOutside(true);

        final TextView fpsTxt = (TextView) dialog.findViewById(R.id.fps_txt);
        final SeekBar fpsSeekBar = (SeekBar) dialog.findViewById(R.id.fps_seekbar);
        final TextView bitrateTxt = (TextView) dialog.findViewById(R.id.bitrate_txt);
        final SeekBar bitrateSeekBar = (SeekBar) dialog.findViewById(R.id.bitrate_seekbar);

        if(isbig){
            fpsSeekBar.setMax(20);
            fpsSeekBar.setProgress(XHCustomConfig.getInstance().getBigVideoFPS());
            fpsTxt.setText("帧率:"+XHCustomConfig.getInstance().getBigVideoFPS());
            bitrateSeekBar.setMax(2000);
            bitrateSeekBar.setProgress(XHCustomConfig.getInstance().getBigVideoBitrate());
            bitrateTxt.setText("码率:"+XHCustomConfig.getInstance().getBigVideoBitrate());
        }else{
            fpsSeekBar.setMax(10);
            fpsSeekBar.setProgress(XHCustomConfig.getInstance().getSmallVideoFPS());
            fpsTxt.setText("帧率:"+XHCustomConfig.getInstance().getSmallVideoFPS());
            bitrateSeekBar.setMax(200);
            bitrateSeekBar.setProgress(XHCustomConfig.getInstance().getSmallVideoBitrate());
            bitrateTxt.setText("码率:"+XHCustomConfig.getInstance().getSmallVideoBitrate());
        }

        fpsSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                fpsTxt.setText("帧率:"+progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        bitrateSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                bitrateTxt.setText("码率:"+progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        dialog.findViewById(R.id.btn_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isbig){
                    XHCustomConfig.getInstance().setDefConfigBigVideoConfig(fpsSeekBar.getProgress(),bitrateSeekBar.getProgress());
                    ((TextView)findViewById(R.id.video_config_big_text)).setText("("+ XHCustomConfig.getInstance().getBigVideoFPS() +"/"+XHCustomConfig.getInstance().getBigVideoBitrate()+")");
                }else{
                    XHCustomConfig.getInstance().setDefConfigSmallVideoConfig(fpsSeekBar.getProgress(),bitrateSeekBar.getProgress());
                    ((TextView)findViewById(R.id.video_config_small_text)).setText("("+ XHCustomConfig.getInstance().getSmallVideoFPS() +"/"+XHCustomConfig.getInstance().getSmallVideoBitrate()+")");
                }
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.btn_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}
