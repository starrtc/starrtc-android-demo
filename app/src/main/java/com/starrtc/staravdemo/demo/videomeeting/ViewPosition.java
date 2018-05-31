package com.starrtc.staravdemo.demo.videomeeting;

import android.widget.RelativeLayout;

import com.starrtc.starrtcsdk.core.player.StarPlayer;

/**
 * Created by zhangjt on 2017/10/26.
 */

public class ViewPosition {
    private RelativeLayout parentView;
    private StarPlayer videoPlayer;
    private String userId;
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

    public int getUpId() {
        return upId;
    }

    public void setUpId(int upId) {
        this.upId = upId;
    }
}
