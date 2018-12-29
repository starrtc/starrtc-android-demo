package com.starrtc.demo.demo.p2p;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.starrtc.demo.R;
import com.starrtc.demo.demo.BaseActivity;
import com.starrtc.demo.demo.MLOC;


public class VoipP2PCreateActivity extends BaseActivity implements View.OnClickListener {
    private TextView vTargetId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voip_p2p_create);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        ((TextView)findViewById(R.id.title_text)).setText("请输入目标终端IP");
        findViewById(R.id.title_left_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.title_left_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        vTargetId = (TextView)findViewById(R.id.targetid_input);
        findViewById(R.id.btn_1).setOnClickListener(this);
        findViewById(R.id.btn_2).setOnClickListener(this);
        findViewById(R.id.btn_3).setOnClickListener(this);
        findViewById(R.id.btn_4).setOnClickListener(this);
        findViewById(R.id.btn_5).setOnClickListener(this);
        findViewById(R.id.btn_6).setOnClickListener(this);
        findViewById(R.id.btn_7).setOnClickListener(this);
        findViewById(R.id.btn_8).setOnClickListener(this);
        findViewById(R.id.btn_9).setOnClickListener(this);
        findViewById(R.id.btn_0).setOnClickListener(this);
        findViewById(R.id.btn_point).setOnClickListener(this);
        findViewById(R.id.btn_clean).setOnClickListener(this);
        findViewById(R.id.btn_call).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_call:
                final String inputId = ((TextView)findViewById(R.id.targetid_input)).getText().toString();
                if(TextUtils.isEmpty(inputId)){
                    MLOC.showMsg(VoipP2PCreateActivity.this,"ip不能为空");
                }else{
                    saveIp(inputId);
                    Intent intent = new Intent(VoipP2PCreateActivity.this,VoipP2PActivity.class);
                    intent.putExtra("targetId",inputId);
                    intent.putExtra(VoipP2PActivity.ACTION,VoipP2PActivity.CALLING);
                    startActivity(intent);
                    VoipP2PCreateActivity.this.finish();
                }
                break;
            case R.id.btn_clean:
                vTargetId.setText("");
                break;
            case R.id.btn_0:
                vTargetId.append("0");
                break;
            case R.id.btn_1:
                vTargetId.append("1");
                break;
            case R.id.btn_2:
                vTargetId.append("2");
                break;
            case R.id.btn_3:
                vTargetId.append("3");
                break;
            case R.id.btn_4:
                vTargetId.append("4");
                break;
            case R.id.btn_5:
                vTargetId.append("5");
                break;
            case R.id.btn_6:
                vTargetId.append("6");
                break;
            case R.id.btn_7:
                vTargetId.append("7");
                break;
            case R.id.btn_8:
                vTargetId.append("8");
                break;
            case R.id.btn_9:
                vTargetId.append("9");
                break;
            case R.id.btn_point:
                vTargetId.append(".");
                break;
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        ((TextView)findViewById(R.id.targetid_input)).setText(loadIP());
    }

    private String loadIP(){
        SharedPreferences prefer = getSharedPreferences("com.starrtc.boins", Context.MODE_PRIVATE);
        String v = prefer.getString("P2P_IP", "");
        return v;
    }
    private void saveIp(String IP){
        SharedPreferences prefer = getSharedPreferences("com.starrtc.boins", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefer.edit();
        editor.putString("P2P_IP", IP);
        editor.commit();
    }
}
