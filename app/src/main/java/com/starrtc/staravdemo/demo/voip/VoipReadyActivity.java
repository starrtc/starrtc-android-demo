package com.starrtc.staravdemo.demo.voip;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.starrtc.staravdemo.R;
import com.starrtc.staravdemo.demo.MLOC;

public class VoipReadyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voip_ready);
        ((TextView)findViewById(R.id.userId)).setText("我的ID:"+ MLOC.userId);
        String targetId = MLOC.loadSharedData(getApplicationContext(),"targetId");
        ((TextView)findViewById(R.id.targetId)).setText(targetId);

        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MLOC.d("","click:calling");
                String targetId = ((TextView)findViewById(R.id.targetId)).getText().toString();
                if(TextUtils.isEmpty(targetId)) return;
                MLOC.saveSharedData(getApplicationContext(),"targetId",targetId);
                Intent intent = new Intent(VoipReadyActivity.this,VoipActivity.class);
                intent.putExtra("targetId",targetId);
                intent.putExtra(VoipActivity.ACTION,VoipActivity.CALLING);
                startActivity(intent);
            }
        });
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //initReportUI();

    }

/*
    EditText delayEdit ;
    RadioGroup radioGroup;
    CheckBox nativeSportAec;

    int delay = -1;
    int mic = -1;
    int nsa = 0;

    private void initReportUI(){
        delayEdit = (EditText) findViewById(R.id.delay_input);
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        nativeSportAec = (CheckBox) findViewById(R.id.native_sport_aec);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.config_GEN:
                        mic = 1;
                        break;
                    case R.id.config_CAM:
                        mic = 2;
                        break;
                    case R.id.config_REC:
                        mic = 3;
                        break;
                    case R.id.config_COMM:
                        mic = 4;
                        break;
                }
            }
        });

        nativeSportAec.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    nsa = 1;
                }else{
                    nsa = 0;
                }
            }
        });

        findViewById(R.id.report_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delay = Integer.parseInt(delayEdit.getText().toString());
                if(delay==-1){
                    MLOC.showMsg("请填写延时值");
                }else if(mic==-1){
                    MLOC.showMsg("请选择MIC配置");
                }else{
                    reportDelay();
                }
            }
        });
        getDelay();
    }

    private void getDelay(){
        StarManager.getDelayOnline(new IStarCallback() {
            @Override
            public void callback(boolean reqSuccess, String statusCode, String data) {
                if(reqSuccess){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            delay = StarManager.getAECConfigDelay();
                            delayEdit.setText(delay+"");

                            mic = StarManager.getAECConfigMic();
                            if(mic==1){
                                ((RadioButton)findViewById(R.id.config_GEN)).setChecked(true);
                            }else if(mic==2){
                                ((RadioButton)findViewById(R.id.config_CAM)).setChecked(true);
                            }else if(mic==3){
                                ((RadioButton)findViewById(R.id.config_REC)).setChecked(true);
                            }else if(mic==4){
                                ((RadioButton)findViewById(R.id.config_COMM)).setChecked(true);
                            }

                            nsa = StarManager.getAECNativeSuport();
                            if(nsa==1){
                                nativeSportAec.setChecked(true);
                            }else{
                                nativeSportAec.setChecked(false);
                            }
                        }
                    });
                }
            }
        });
    }

    private void reportDelay(){
        StarManager.updateDelayOnline(delay, mic, nsa, new IStarCallback() {
            @Override
            public void callback(final boolean reqSuccess, String statusCode, String data) {
                if(reqSuccess){
                    StarManager.setAECNativeSuport(nsa);
                    StarManager.setAECConfigMic(mic);
                    StarManager.setAECConfigDelay(delay);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(VoipReadyActivity.this).setCancelable(true)
                                .setTitle("上报"+(reqSuccess?"成功":"失败"))
                                .setMessage("delay = "+delay+"|"
                                        +"micSrc = "+mic+"|"
                                        +"native = "+nsa
                                        )
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {

                                    }
                                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {

                                    }
                                }).show();
                    }
                });
            }
        });
    }*/
}
