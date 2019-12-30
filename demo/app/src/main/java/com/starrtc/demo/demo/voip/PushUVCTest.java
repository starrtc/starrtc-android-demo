package com.starrtc.demo.demo.voip;

import android.annotation.SuppressLint;

import com.starrtc.starrtcsdk.core.pusher.XHCustomRecorder;

import java.util.Timer;
import java.util.TimerTask;

public class PushUVCTest {

    private XHCustomRecorder recorder;
    private int fps = 20;
    private byte[] frameData;

    public PushUVCTest(XHCustomRecorder recorder){
        this.recorder = recorder;
    }

    public void startRecoder(){
        //启动XHCustomRecorder
        recorder.start();

        //初始化Camera，持续采集视频，把采集到的数据传给sdk
        uploadTask();
    }

    public void stopRecoder(){
        //停止XHCustomRecorder
        recorder.stop();

        //停止上传任务
        if(uploadTimer!=null){
            uploadTimer.cancel();
            uploadTimer = null;
        }

        //销毁Camera
    }

    private Timer uploadTimer;
    private void uploadTask(){
        if(uploadTimer!=null){
            uploadTimer.cancel();
            uploadTimer = null;
            uploadTimer = new Timer();
        }else{
            uploadTimer = new Timer();
        }
        uploadTimer.schedule(new TimerTask() {
            @SuppressLint({"NewApi", "LocalSuppress"})
            @Override
            public void run() {
                uploadVideoData();
            }
        },0,1000/fps);
    }

    @SuppressLint({"NewApi", "LocalSuppress"})
    private void uploadVideoData(){
        recorder.fillData_NV21(frameData);
    }
}
