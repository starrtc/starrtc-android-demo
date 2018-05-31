package com.starrtc.staravdemo.demo.test;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.starrtc.staravdemo.R;
import com.starrtc.staravdemo.utils.AEvent;
import com.starrtc.staravdemo.utils.IEventListener;
import com.starrtc.starrtcsdk.core.StarRtcCore;

public class EchoTestActivity extends Activity implements IEventListener {

    private EditText traceBox;
    private View clearBtn;
    private View backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_echo_test);
        StarRtcCore.getInstance().voipEchoTest();
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

    @Override
    public void onStart(){
        super.onStart();
        AEvent.addListener(AEvent.AEVENT_ECHO_FIN,this);
    }

    @Override
    public void onStop(){
        AEvent.removeListener(AEvent.AEVENT_ECHO_FIN,this);
        super.onStop();

    }


    @Override
    public void dispatchEvent(String aEventID, boolean success, Object eventObj) {
        Message msg2 = new Message();
        msg2.what = 0;
        Bundle b2 = new Bundle();
        b2.putString("data",(String)eventObj);
        msg2.setData(b2);
        uiHandler.sendMessage(msg2);
    }

    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                   String msgStr = msg.getData().getString("data");
                    if(traceBox!=null){
                        traceBox.append(msgStr+"\n");
                    }
                    break;
            }
        }
    };

}
