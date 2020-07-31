package com.starrtc.demo.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import androidx.annotation.ColorInt;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import com.starrtc.demo.R;

public class CircularCoverView extends View {

    private int leftTopRadians = 30;        //leftTopRadians
    private int leftBottomRadians = 30;     //leftBottomRadians
    private int rightTopRadians = 30;       //rightTopRadians
    private int rightBottomRadians = 30;    //rightBottomRadians
    private int border = 0;

    private int coverColor = 0xffeaeaea;    //color of cover.

    public CircularCoverView(Context context) {
        this(context, null, 0);
    }

    public CircularCoverView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularCoverView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircularCoverView);
        leftTopRadians = typedArray.getDimensionPixelSize(R.styleable.CircularCoverView_left_top_radius, leftTopRadians);
        leftBottomRadians = typedArray.getDimensionPixelSize(R.styleable.CircularCoverView_left_bottom_radius, leftBottomRadians);
        rightTopRadians = typedArray.getDimensionPixelSize(R.styleable.CircularCoverView_right_top_radius, rightTopRadians);
        rightBottomRadians = typedArray.getDimensionPixelSize(R.styleable.CircularCoverView_right_bottom_radius, rightBottomRadians);
        coverColor = typedArray.getColor(R.styleable.CircularCoverView_cover_color, coverColor);
    }

    /**
     * set radians of cover.
     */
    public void setRadians(int leftTopRadians, int rightTopRadians, int leftBottomRadians, int rightBottomRadians,int border) {
        this.leftTopRadians = leftTopRadians;
        this.rightTopRadians = rightTopRadians;
        this.leftBottomRadians = leftBottomRadians;
        this.rightBottomRadians = rightBottomRadians;
        this.border = border;
    }

    /**
     * set color of cover.
     *
     * @param coverColor cover's color
     */
    public void setCoverColor(@ColorInt int coverColor) {
        this.coverColor = coverColor;
    }

    /**
     * create a sector-bitmap as the dst.
     *
     * @param w width of bitmap
     * @param h height of bitmap
     * @return bitmap
     */
    private Bitmap drawSector(int w, int h) {
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(0xFFFFCC44);//notice:cannot set transparent color here.otherwise cannot clip at final.

        c.drawArc(new RectF(border, border, leftTopRadians * 2+border, leftTopRadians * 2+border), 180, 90, true, p);
        c.drawArc(new RectF(border, getHeight() - leftBottomRadians * 2-border, leftBottomRadians * 2+border, getHeight()-border), 90, 90, true, p);
        c.drawArc(new RectF(getWidth() - rightTopRadians * 2-border, border, getWidth()-border, rightTopRadians * 2+border), 270, 90, true, p);
        c.drawArc(new RectF(getWidth() - rightBottomRadians * 2-border, getHeight() - rightBottomRadians * 2-border, getWidth()-border, getHeight()-border), 0, 90, true, p);
        return bm;
    }

    /**
     * create a rect-bitmap as the src.
     *
     * @param w width of bitmap
     * @param h height of bitmap
     * @return bitmap
     */
    private Bitmap drawRect(int w, int h) {
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(coverColor);

        c.drawRect(new RectF(border, border, leftTopRadians+border, leftTopRadians+border), p);
        c.drawRect(new RectF(border, getHeight() - leftBottomRadians-border, leftBottomRadians+border, getHeight()-border), p);
        c.drawRect(new RectF(getWidth() - rightTopRadians-border, border, getWidth()-border, rightTopRadians+border), p);
        c.drawRect(new RectF(getWidth() - rightBottomRadians-border, getHeight() - rightBottomRadians-border, getWidth()-border, getHeight()-border), p);

        c.drawRect(new RectF(0, 0, getWidth(), border), p);
        c.drawRect(new RectF(0, 0, border, getHeight()), p);
        c.drawRect(new RectF(getWidth()-border, 0, getWidth(), getHeight()), p);
        c.drawRect(new RectF(0, getHeight()-border, getWidth(), getHeight()), p);

        return bm;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            Paint paint = new Paint();
            paint.setFilterBitmap(false);
            paint.setStyle(Paint.Style.FILL);

            //create a canvas layer to show the mix-result
            @SuppressLint("WrongConstant") int sc = canvas.saveLayer(0, 0, getWidth(), getHeight(), null);
            //draw sector-dst-bitmap at first.
            canvas.drawBitmap(drawSector(getWidth(), getHeight()), 0, 0, paint);
            //set Xfermode of paint.
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
            //then draw rect-src-bitmap
            canvas.drawBitmap(drawRect(getWidth(), getHeight()), 0, 0, paint);
            paint.setXfermode(null);
            //restore the canvas
            canvas.restoreToCount(sc);
        }catch (Error e){

        }
    }
}
