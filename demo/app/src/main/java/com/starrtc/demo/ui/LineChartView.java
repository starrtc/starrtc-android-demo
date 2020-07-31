package com.starrtc.demo.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class LineChartView extends View {
    public LineChartView(Context context) {
        super(context);
        initPaint();
    }

    public LineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public LineChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LineChartView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initPaint();
    }

    private ArrayList<LineData> srcDatas;
    private Paint paint = new Paint();//依靠此类开始画线

    private void initPaint(){
        paint.setAntiAlias(true);// 抗锯齿
        paint.setDither(true); // 防抖动
        paint.setStyle(Paint.Style.FILL);// 画笔类型 STROKE空心 FILL 实心
        paint.setStrokeJoin(Paint.Join.ROUND);// 画笔接洽点类型 如影响矩形但角的外轮廓,让画的线圆滑
        paint.setStrokeCap(Paint.Cap.ROUND);// 画笔笔刷类型 如影响画笔但始末端
        paint.setStrokeWidth(3);// 设置线宽
    }

    public void refreshData(ArrayList<LineData> datas){
        srcDatas = datas;
        this.postInvalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if(srcDatas==null||srcDatas.size()==0)return;

        ArrayList<LineData> drawData = (ArrayList<LineData>) srcDatas.clone();
        int totalHeight = this.getHeight()-10;
        for(LineData lineData:drawData){
            String lineName = lineData.name;
            int lineColor = lineData.color;
            ArrayList<Float> data = lineData.datas;
            paint.setColor(lineColor);
            if(data.size()<=1)continue;
            float stepX = this.getWidth()/(data.size()-1);
            float maxValue = 1;
            for(int i = 1;i<data.size();i++){
                maxValue = Math.max(data.get(i),data.get(i-1));
            }
            if(maxValue<=1){
                for(int i = 0;i<data.size()-2;i++){
                    canvas.drawLine(stepX*i,totalHeight*(1-data.get(i))+5, stepX*(i+1), totalHeight*(1-data.get(i+1))+5, paint);
                }
            }else{
                for(int i = 0;i<data.size()-2;i++){
                    canvas.drawLine(stepX*i,totalHeight*(1-data.get(i)/maxValue)+5, stepX*(i+1), totalHeight*(1-data.get(i+1)/maxValue)+5, paint);
                }
            }

        }
    }



    public static class LineData{
        public String name = "line";
        public int color = 0xff000000;
        public ArrayList<Float> datas = new ArrayList<>();
    }

}
