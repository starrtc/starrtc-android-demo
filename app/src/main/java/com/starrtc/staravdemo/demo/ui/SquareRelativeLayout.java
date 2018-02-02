package com.starrtc.staravdemo.demo.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by zhangjt on 2017/8/29.
 */

public class SquareRelativeLayout extends RelativeLayout {
    public SquareRelativeLayout(Context context) {
        super(context);
    }
    public SquareRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public SquareRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
               //重写此方法后默认调用父类的onMeasure方法,分别将宽度测量空间与高度测量空间传入
    }
}
