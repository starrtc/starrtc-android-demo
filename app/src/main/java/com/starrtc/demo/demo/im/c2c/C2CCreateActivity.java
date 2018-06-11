package com.starrtc.demo.demo.im.c2c;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.starrtc.demo.R;
import com.starrtc.demo.demo.BaseActivity;
import com.starrtc.demo.demo.MLOC;
import com.starrtc.demo.utils.StatusBarUtils;

public class C2CCreateActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c2c_create);
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
                    MLOC.showMsg(C2CCreateActivity.this,"id不能为空");
                }else{
                    MLOC.saveC2CUserId(C2CCreateActivity.this,inputId);
                    Intent intent = new Intent(C2CCreateActivity.this,C2CActivity.class);
                    intent.putExtra("targetId",inputId);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
