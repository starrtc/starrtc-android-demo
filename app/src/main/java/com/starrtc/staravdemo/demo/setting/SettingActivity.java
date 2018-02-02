package com.starrtc.staravdemo.demo.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.starrtc.staravdemo.R;
import com.starrtc.staravdemo.demo.test.EchoTestActivity;
import com.starrtc.starrtcsdk.StarManager;

public class SettingActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        findViewById(R.id.button5).setOnClickListener(this);
        findViewById(R.id.button6).setOnClickListener(this);
        findViewById(R.id.button7).setOnClickListener(this);
        findViewById(R.id.button8).setOnClickListener(this);
        findViewById(R.id.button9).setOnClickListener(this);
        findViewById(R.id.button10).setOnClickListener(this);
        findViewById(R.id.button11).setOnClickListener(this);
        findViewById(R.id.button12).setOnClickListener(this);
    }
    @Override
    public void onResume(){
        super.onResume();
        ((Button)findViewById(R.id.button8)).setText(StarManager.openGLESEnable ?"OPENGL 开":"OPENGL 关");
        ((Button)findViewById(R.id.button11)).setText(StarManager.openSLESEnable ?"OPENSL 开":"OPENSL 关");
        ((Button)findViewById(R.id.button12)).setText("音视频硬编格式("+StarManager.keepWatch_mediaEncodeConfig+")");
        ((Button)findViewById(R.id.button9)).setText(StarManager.ctrlFps?"FPS控制 开":"FPS控制 关");
        ((Button)findViewById(R.id.button10)).setText("编码器选择（"+StarManager.keepWatch_hardEncoderSetting+")");
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button5:
                finish();
                break;
            case R.id.button6:
                startActivity(new Intent(this,EchoTestActivity.class));
                break;
            case R.id.button7:
                startActivity(new Intent(this,VideoSizeSettingActivity.class));
                break;
            case R.id.button8:
                StarManager.openGLESEnable =StarManager.openGLESEnable ?false:true;
                ((Button)findViewById(R.id.button8)).setText(StarManager.openGLESEnable ?"OPENGL 开":"OPENGL 关");
                break;
            case R.id.button9:
                StarManager.ctrlFps=StarManager.ctrlFps?false:true;
                ((Button)findViewById(R.id.button9)).setText(StarManager.ctrlFps?"FPS控制 开":"FPS控制 关");
                break;
            case R.id.button10:
                startActivity(new Intent(this,HardEncodeSettingActivity.class));
                break;
            case R.id.button11:
                StarManager.openSLESEnable =StarManager.openSLESEnable ?false:true;
                ((Button)findViewById(R.id.button11)).setText(StarManager.openSLESEnable ?"OPENSL 开":"OPENSL 关");
                break;
            case R.id.button12:
                startActivity(new Intent(this,MediaEncodeConfigSettingActivity.class));
                break;
        }
    }
}
