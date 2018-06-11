package com.starrtc.demo.demo.test;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.starrtc.demo.R;
import com.starrtc.demo.utils.ColorUtils;
import com.starrtc.demo.utils.DensityUtils;
import com.starrtc.demo.utils.StatusBarUtils;

import java.util.ArrayList;
import java.util.Random;

public class LayoutTestActivity extends Activity {

    private RelativeLayout vRootView;
    private TextView vPlayerCount;
    private ArrayList<RelativeLayout> mPlayerList;
    private int borderW = 0;
    private int borderH = 0;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star_rtc_main);
        //StatusBarUtils.with(this).setColor(Color.parseColor("#FF6C00")).init();
//        setContentView(R.layout.activity_layout_test);
//        findViewById(R.id.button_add).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                addPlayer();
//            }
//        });
//        findViewById(R.id.button_delete).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                deletePlayer();
//            }
//        });
//        borderW = DensityUtils.screenWidth(this);
//        borderH = borderW/3*4;
//        mPlayerList = new ArrayList<>();
//        vRootView = (RelativeLayout) findViewById(R.id.root_view);
//        vPlayerCount = (TextView)findViewById(R.id.count_text);
    }

    private void addPlayer(){
        RelativeLayout player = new RelativeLayout(this);
        mPlayerList.add(player);
        vRootView.addView(player);
        player.setBackgroundColor(ColorUtils.randomColor(200,200,200));
        player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLayout(v);
            }
        });
        resetLayout1();
        vPlayerCount.setText(""+mPlayerList.size());
    }

    private void changeLayout(View v){
        if(v == mPlayerList.get(0))return;
        final RelativeLayout clickPlayer = (RelativeLayout) v;
        int clickIndex = 0;
        for (int i=0;i<mPlayerList.size();i++){
            if(mPlayerList.get(i)==clickPlayer){
                clickIndex = i;
                mPlayerList.remove(i);
                break;
            }
        }
        final RelativeLayout mainPlayer = mPlayerList.remove(0);
        mPlayerList.remove(v);
        mPlayerList.add(0, clickPlayer);
        mPlayerList.add(clickIndex,mainPlayer);

        ObjectAnimator ob1 = ObjectAnimator.ofFloat(clickPlayer, "TranslationX",mainPlayer.getX());
        ObjectAnimator ob2 = ObjectAnimator.ofFloat(clickPlayer, "TranslationY",mainPlayer.getY());
        ObjectAnimator ob3 = ObjectAnimator.ofFloat(mainPlayer, "TranslationX",clickPlayer.getX());
        ObjectAnimator ob4 = ObjectAnimator.ofFloat(mainPlayer, "TranslationY",clickPlayer.getY());


        ValueAnimator  va1 = ValueAnimator.ofFloat(clickPlayer.getWidth(),mainPlayer.getWidth());
        va1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                clickPlayer.getLayoutParams().width = ((Float)animation.getAnimatedValue()).intValue();
                clickPlayer.requestLayout();
            }
        });
        ValueAnimator  va2 = ValueAnimator.ofFloat(clickPlayer.getHeight(),mainPlayer.getHeight());
        va2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                clickPlayer.getLayoutParams().height = ((Float)animation.getAnimatedValue()).intValue();
                clickPlayer.requestLayout();
            }
        });
        ValueAnimator  va3 = ValueAnimator.ofFloat(mainPlayer.getWidth(),clickPlayer.getWidth());
        va3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mainPlayer.getLayoutParams().width = ((Float)animation.getAnimatedValue()).intValue();
                mainPlayer.requestLayout();
            }
        });
        ValueAnimator  va4 = ValueAnimator.ofFloat(mainPlayer.getHeight(),clickPlayer.getHeight());
        va4.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mainPlayer.getLayoutParams().height =((Float)animation.getAnimatedValue()).intValue();
                mainPlayer.requestLayout();
            }
        });

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(300);
        animSet.setInterpolator(new LinearInterpolator());
        animSet.playTogether(ob1, ob2, ob3,ob4,va1,va2,va3,va4);
        animSet.start();
    }

    private void deletePlayer(){
        if(mPlayerList.size()!=0){
            RelativeLayout remove = mPlayerList.remove(mPlayerList.size()-1);
            vRootView.removeView(remove);
            resetLayout1();
            vPlayerCount.setText(""+mPlayerList.size());
        }
    }

    private void resetLayout1(){
        switch (mPlayerList.size()){
            case 1:{
                RelativeLayout player = mPlayerList.get(0);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW,borderH);
                player.setLayoutParams(lp);
                player.setY(0);
                player.setX(0);
                break;
            }
            case 2:
            case 3:{
                for(int i = 0;i<mPlayerList.size();i++){
                    if(i==0){
                        RelativeLayout player = mPlayerList.get(i);
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW/3*2,borderH);
                        player.setLayoutParams(lp);
                        player.setY(0);
                        player.setX(0);
                    }else{
                        RelativeLayout player = mPlayerList.get(i);
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW/3,borderH/2);
                        player.setLayoutParams(lp);
                        player.setY((i-1)*borderH/2);
                        player.setX(borderW/3*2);
                    }
                }
                break;
            }
            case 4:{
                for(int i = 0;i<mPlayerList.size();i++){
                    if(i==0){
                        RelativeLayout player = mPlayerList.get(i);
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW,borderH-borderW/3*3/2);
                        player.setLayoutParams(lp);
                        player.setY(0);
                        player.setX(0);
                    }else{
                        RelativeLayout player = mPlayerList.get(i);
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW/3,borderW/3*3/2);
                        player.setLayoutParams(lp);
                        player.setY(borderH-borderW/3*3/2);
                        player.setX((i-1)*borderW/3);
                    }
                }
                break;
            }
            case 5:{
                for(int i = 0;i<mPlayerList.size();i++){
                    if(i==0){
                        RelativeLayout player = mPlayerList.get(i);
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW/3*2,borderH/3*2);
                        player.setLayoutParams(lp);
                        player.setX(0);
                        player.setY(0);
                    }else if(i<3){
                        RelativeLayout player = mPlayerList.get(i);
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW/3,borderH/3);
                        player.setLayoutParams(lp);
                        player.setX((i-1)*borderW/3);
                        player.setY(borderH/3*2);
                    }else {
                        RelativeLayout player = mPlayerList.get(i);
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW/3,borderH/2);
                        player.setLayoutParams(lp);
                        player.setX(borderW/3*2);
                        player.setY((i-3)*borderH/2);
                    }
                }
                break;
            }
            case 6:{
                for(int i = 0;i<mPlayerList.size();i++){
                    if(i==0){
                        RelativeLayout player = mPlayerList.get(i);
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW/3*2,borderH/3*2);
                        player.setLayoutParams(lp);
                        player.setX(0);
                        player.setY(0);
                    }else if(i<3){
                        RelativeLayout player = mPlayerList.get(i);
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW/3,borderH/3);
                        player.setLayoutParams(lp);
                        player.setX((i-1)*borderW/3);
                        player.setY(borderH/3*2);
                    }else {
                        RelativeLayout player = mPlayerList.get(i);
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW/3,borderH/3);
                        player.setLayoutParams(lp);
                        player.setX(borderW/3*2);
                        player.setY((i-3)*borderH/3);
                    }
                }
                break;

            }
            case 7:{
                for(int i = 0;i<mPlayerList.size();i++){
                    if(i == 0){
                        RelativeLayout player = mPlayerList.get(i);
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW-borderW/3,borderH-borderH/4);
                        player.setLayoutParams(lp);
                    }else if(i>0&&i<3){
                        RelativeLayout player = mPlayerList.get(i);
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW/3,borderH/4);
                        player.setLayoutParams(lp);
                        player.setX((i-1)*borderW/3);
                        player.setY(borderH-borderH/4);
                    }else{
                        RelativeLayout player = mPlayerList.get(i);
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW/3,borderH/4);
                        player.setLayoutParams(lp);
                        player.setX(borderW-borderW/3);
                        player.setY((i-3)*borderH/4);
                    }
                }
                break;
            }
        }
    }


}
