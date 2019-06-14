package com.starrtc.demo.demo.miniclass;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.starrtc.demo.R;
import com.starrtc.demo.demo.BaseActivity;
import com.starrtc.demo.demo.MLOC;
import com.starrtc.starrtcsdk.api.XHConstants;

public class MiniClassCreateActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mini_class_create);
        ((TextView)findViewById(R.id.title_text)).setText("创建小班课");
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
                XHConstants.XHLiveType type = XHConstants.XHLiveType.XHLiveTypeGlobalPublic;

                if(TextUtils.isEmpty(inputId)){
                    MLOC.showMsg(MiniClassCreateActivity.this,"id不能为空");
                }else{
                    Intent intent = new Intent(MiniClassCreateActivity.this, MiniClassActivity.class);
                    intent.putExtra(MiniClassActivity.CLASS_NAME,inputId);
                    intent.putExtra(MiniClassActivity.CLASS_CREATOR,MLOC.userId);
                    intent.putExtra(MiniClassActivity.CLASS_TYPE,type);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
