package com.starrtc.demo.utils;

import android.view.View;

/**
 * Created by zhangjt on 2018/3/23.
 */

public class ViewWrapper {
    private View mTarget;

    public ViewWrapper(View target) {
        mTarget = target;
    }

    public int getWidth() {
        return mTarget.getLayoutParams().width;
    }

    public void setWidth(int width) {
        mTarget.getLayoutParams().width = width;
        mTarget.requestLayout();
    }

    public int getHeight() {
        return mTarget.getLayoutParams().height;
    }

    public void setHeight(int height) {
        mTarget.getLayoutParams().height = height;
        mTarget.requestLayout();
    }
}
