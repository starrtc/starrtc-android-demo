package com.starrtc.demo.demo.test;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.starrtc.demo.R;
import com.starrtc.demo.demo.BaseActivity;
import com.starrtc.demo.utils.AEvent;
import com.starrtc.demo.utils.IEventListener;
import com.starrtc.starrtcsdk.core.StarRtcCore;
import com.starrtc.starrtcsdk.core.im.callback.IStarCallback;

public class EchoTestActivity extends BaseActivity{

    private EditText traceBox;
    private View clearBtn;
    private View backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_echo_test);
        StarRtcCore.getInstance().voipEchoTest(new IStarCallback() {
            @Override
            public void callback(boolean reqSuccess, String statusCode, final String data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String msgStr = data;
                        if(traceBox!=null){
                            traceBox.append(msgStr+"\n");
                        }
                    }
                });
            }
        });
        traceBox = (EditText) findViewById(R.id.trace_box);
        clearBtn = findViewById(R.id.clear_btn);
        backBtn = findViewById(R.id.back_btn);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                traceBox.setText("");
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });
    }




}
