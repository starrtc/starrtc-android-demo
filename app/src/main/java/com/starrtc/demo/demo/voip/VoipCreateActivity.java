package com.starrtc.demo.demo.voip;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.starrtc.demo.R;
import com.starrtc.demo.demo.BaseActivity;
import com.starrtc.demo.demo.MLOC;

public class VoipCreateActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voip_create);
        ((TextView)findViewById(R.id.title_text)).setText("创建新会话");
        findViewById(R.id.title_left_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.title_left_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.yes_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputId = ((EditText)findViewById(R.id.targetid_input)).getText().toString();
                if(TextUtils.isEmpty(inputId)){
                    MLOC.showMsg(VoipCreateActivity.this,"id不能为空");
                }else{
                    Intent intent = new Intent(VoipCreateActivity.this,VoipActivity.class);
                    intent.putExtra("targetId",inputId);
                    intent.putExtra(VoipActivity.ACTION,VoipActivity.CALLING);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
