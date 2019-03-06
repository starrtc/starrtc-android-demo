package com.starrtc.demo.demo.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.starrtc.demo.R;
import com.starrtc.demo.ui.LineChartView;
import com.starrtc.starrtcsdk.core.StarRtcCore;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class FloatWindowsService extends Service {
    public static final String TAG = "FloatWindowsService";
    private WindowManager mWindowManager;
    public static Boolean runing = false;

    @Override
    public void onCreate() {
        super.onCreate();
        createFloatView();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////
    private TextView vLogText;
    private View vCloseBtn;
    private LineChartView vLineChart;
    private ArrayList<LineChartView.LineData> lines = new ArrayList<>();
    private RelativeLayout floatView;
    private RelativeLayout floatViewBall;
    private Timer refreshTimer;
    private TimerTask refreshTimerTask;
    private Handler handler;
    LayoutInflater inflater;
    private WindowManager.LayoutParams params;
    private void createFloatView()
    {
        runing = true;
        //加载需要的XML布局文件
        inflater = LayoutInflater.from(getApplicationContext());

        floatView = (RelativeLayout)inflater.inflate(R.layout.float_view, null, false);
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        params.format = PixelFormat.RGBA_8888;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        params.height = RelativeLayout.LayoutParams.WRAP_CONTENT;

        vLineChart = (LineChartView) floatView.findViewById(R.id.line_chart);
        LineChartView.LineData netLine = new LineChartView.LineData();
        netLine.name = "net";
        netLine.color = 0xFFFF0000;
        netLine.datas = new ArrayList<>();
        lines.add(netLine);
        LineChartView.LineData netLine2 = new LineChartView.LineData();
        netLine2.name = "net";
        netLine2.color = 0xFF0000FF;
        netLine2.datas = new ArrayList<>();
        lines.add(netLine2);
        LineChartView.LineData farNetLine = new LineChartView.LineData();
        farNetLine.name = "net";
        farNetLine.color = 0xFF00FFFF;
        farNetLine.datas = new ArrayList<>();
        lines.add(farNetLine);

        vLogText = (TextView) floatView.findViewById(R.id.log_txt);
        vCloseBtn = floatView.findViewById(R.id.log_close);
        vCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(refreshTimer!=null){
                    refreshTimer.cancel();
                    refreshTimer = null;
                    refreshTimerTask.cancel();
                    refreshTimerTask = null;
                }
                mWindowManager.removeView(floatView);
            }
        });

        floatViewBall = (RelativeLayout)inflater.inflate(R.layout.float_view_ball, null, false);
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        params.format = PixelFormat.RGBA_8888;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        params.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        mWindowManager.addView(floatViewBall, params);
        floatViewBall.findViewById(R.id.btn).setOnTouchListener(floatOnTouchListener);
        floatViewBall.findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindowManager.addView(floatView, params);
                if(refreshTimer!=null){
                    refreshTimer.cancel();
                    refreshTimer = null;
                    refreshTimerTask.cancel();
                    refreshTimerTask = null;
                }
                refreshTimer = new Timer();
                refreshTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        handler =new Handler(Looper.getMainLooper());
                        handler.post(new Runnable(){
                            public void run(){
                                StringBuffer stringBuffer = new StringBuffer();
                                stringBuffer.append("Interval："+ StarRtcCore.currentNetIQInterval+"\n");
                                stringBuffer.append("上传速度："+ StarRtcCore.currentUploadSpeed+" kb/s "+ StarRtcCore.currentUploadSpeed2+" kb/s\n");
                                stringBuffer.append("下载速度："+ StarRtcCore.currentDownloadSpeed+" kb/s\n");
                                stringBuffer.append("---------------------------------\n");
                                if(StarRtcCore.videoTotalBytes>0){
                                    stringBuffer.append("视频丢包率："
                                            +  new BigDecimal((float)StarRtcCore.videoDropBytes/StarRtcCore.videoTotalBytes).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue()*100
                                            +"%("+StarRtcCore.videoDropBytes+"/"+StarRtcCore.videoTotalBytes+")\n");
                                }else{
                                    stringBuffer.append("视频丢包率：0.0%(0/0)\n");
                                }
                                if(StarRtcCore.audioTotalBytes>0){
                                    stringBuffer.append("音频丢包率："
                                            +  new BigDecimal((float)StarRtcCore.audioDropBytes/StarRtcCore.audioTotalBytes).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue()*100
                                            +"%("+StarRtcCore.audioDropBytes+"/"+StarRtcCore.audioTotalBytes+")\n");
                                }else{
                                    stringBuffer.append("音频丢包率：0.0%(0/0)\n");
                                }
                                if(StarRtcCore.realTimeTotalBytes>0){
                                    stringBuffer.append("白板丢包率："
                                            +  new BigDecimal((float)StarRtcCore.realTimeDropBytes/StarRtcCore.realTimeTotalBytes).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue()*100
                                            +"%("+StarRtcCore.realTimeDropBytes+"/"+StarRtcCore.realTimeTotalBytes+")\n");
                                }else{
                                    stringBuffer.append("白板丢包率：0.0%(0/0)\n");
                                }
                                stringBuffer.append("---------------------------------\n");
                                stringBuffer.append("分辨率大："+ StarRtcCore.currentVideoWidth+"x"+StarRtcCore.currentVideoHeight+"\n");
                                stringBuffer.append("码率大："+ StarRtcCore.currentBitRate+" kbps\n");
                                stringBuffer.append("帧率大："+ StarRtcCore.currentFPS+" fps\n");
                                stringBuffer.append("---------------------------------\n");
                                stringBuffer.append("分辨率小："+ StarRtcCore.currentVideoWidthSmall+"x"+StarRtcCore.currentVideoHeightSmall+"\n");
                                stringBuffer.append("码率小："+ StarRtcCore.currentBitRateSmall+" kbps\n");
                                stringBuffer.append("帧率小："+ StarRtcCore.currentFPSSmall+" fps\n");
                                vLogText.setText(stringBuffer.toString());

                                if(lines.get(0).datas.size()>30){
                                    lines.get(0).datas.remove(0);
                                }
                                if(lines.get(1).datas.size()>30){
                                    lines.get(1).datas.remove(0);
                                }
                                lines.get(1).datas.add((float) StarRtcCore.currentUploadSpeed);

                                if(lines.get(2).datas.size()>30){
                                    lines.get(2).datas.remove(0);
                                }
                                lines.get(2).datas.add((float) StarRtcCore.currentUploadSpeed2);
                                vLineChart.refreshData(lines);
                            }
                        });
                    }
                };
                refreshTimer.schedule(refreshTimerTask,10,1000);
            }
        });
    }



    private View.OnTouchListener floatOnTouchListener = new View.OnTouchListener()
    {
        int lastX, lastY;
        int paramX, paramY;
        long startTime = 0;
        long endTime = 0;
        boolean isclick;

        public boolean onTouch(View v, MotionEvent event)
        {
            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    paramX = params.x;
                    paramY = params.y;
                    isclick = false;
                    startTime = System.currentTimeMillis();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int dx = (int) event.getRawX() - lastX;
                    int dy = (int) event.getRawY() - lastY;
                    int nx = paramX + dx;
                    int ny = paramY + dy;
                    params.x = nx;
                    params.y = ny;
                    // 更新悬浮窗位置
                    mWindowManager.updateViewLayout(floatViewBall, params);
                    break;
                case MotionEvent.ACTION_UP:
                    endTime = System.currentTimeMillis();
                    //当从点击到弹起小于半秒的时候,则判断为点击,如果超过则不响应点击事件
                    if ((endTime - startTime) > 0.1 * 1000L) {
                        isclick = true;
                    } else {
                        isclick = false;
                    }
                    break;

            }
            return isclick;
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        runing = false;
        try {
            mWindowManager.removeView(floatView);
        }catch (Exception e){

        }
        try {
            mWindowManager.removeView(floatViewBall);
        }catch (Exception e){

        }


    }


}
