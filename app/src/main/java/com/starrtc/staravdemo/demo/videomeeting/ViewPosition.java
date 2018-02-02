package com.starrtc.staravdemo.demo.videomeeting;

import android.widget.RelativeLayout;

import com.starrtc.starrtcsdk.player.StarPlayer;

/**
 * Created by zhangjt on 2017/10/26.
 */

public class ViewPosition {
    private RelativeLayout parentView;
    private StarPlayer videoPlayer;
    private String userId;
    private int bigW;
    private int bigH;
    private int smallW;
    private int smallH;
    private int upId;
    public RelativeLayout getParentView() {
        return parentView;
    }

    public void setParentView(RelativeLayout parentView) {
        this.parentView = parentView;
    }

    public StarPlayer getVideoPlayer() {
        return videoPlayer;
    }

    public void setVideoPlayer(StarPlayer videoPlayer) {
        this.videoPlayer = videoPlayer;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getBigW() {
        return bigW;
    }

    public void setBigW(int bigW) {
        this.bigW = bigW;
    }

    public int getBigH() {
        return bigH;
    }

    public void setBigH(int bigH) {
        this.bigH = bigH;
    }

    public int getSmallW() {
        return smallW;
    }

    public void setSmallW(int smallW) {
        this.smallW = smallW;
    }

    public int getSmallH() {
        return smallH;
    }

    public void setSmallH(int smallH) {
        this.smallH = smallH;
    }

    public int getUpId() {
        return upId;
    }

    public void setUpId(int upId) {
        this.upId = upId;
    }
}
