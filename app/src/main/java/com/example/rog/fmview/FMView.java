package com.example.rog.fmview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 项目名称：FM
 * 类描述：FM刻度尺
 * 创建人：niechaoqun
 * 创建时间：2016/7/11 19:16
 * 修改人：niechaoqun
 * 修改时间：2016/7/11 19:16
 * 修改备注：
 */
public class FMView extends View {
    private float mMin;//最小刻度
    private float mMax;//最大刻度
    private float mGap;//单位刻度
    private int mScaleCount;//总刻度数

    private int mMargin;//单位刻度间距

    private int mIndex;//指针默认刻度

    private Paint mPaint;

    private boolean mDisabled = false;//预留

    public FMView(Context context) {
        super(context);
        init();

    }

    public FMView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.FMView, 0, 0);

        try {
            mMin = a.getFloat(R.styleable.FMView_fm_min, 0);
            mMax = a.getFloat(R.styleable.FMView_fm_max, 0);
            mGap = a.getFloat(R.styleable.FMView_fm_gap, 0);
        }finally {
            a.recycle();
        }

        mScaleCount = (int) ((mMax - mMin) / mGap);//mark,精度问题可能出错
        mIndex = mScaleCount / 2;
        init();
    }

    private void init() {

        //初始化Paint
        mPaint = new Paint();
        // 抗锯齿
        mPaint.setAntiAlias(true);
        // 设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
        mPaint.setDither(true);
        // 空心
        mPaint.setStyle(Paint.Style.STROKE);
        // 文字居中
        mPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mMargin = getWidth() / mScaleCount;

        mPaint.setTextSize(mMargin * 3);
        mPaint.setColor(Color.GRAY);
        mPaint.setStrokeWidth(0);

        //画刻度尺的底线
        int rectHeight = mMargin * 20;
        canvas.drawLine(0, rectHeight, mMargin * mScaleCount, rectHeight, mPaint);

        //画刻度线
        for (int i = 0; i <= mScaleCount; i++) {
            if (((mMin + i * mGap) / mGap) % 10 == 0) {
                canvas.drawLine(i * mMargin, rectHeight, i * mMargin, rectHeight - 8 * mMargin, mPaint);
                canvas.drawText(String.valueOf(mMin + mGap * i), i * mMargin, rectHeight - 12 * mMargin, mPaint);
            } else {
                canvas.drawLine(i * mMargin, rectHeight, i * mMargin, rectHeight - 3 * mMargin, mPaint);
            }
        }

        //画指针
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(3);
        canvas.drawLine(mIndex * mMargin, rectHeight + mMargin, mIndex * mMargin, rectHeight - 9 * mMargin, mPaint);

        //画阴影效果
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLUE);
        mPaint.setAlpha(100);
        canvas.drawRect(0, rectHeight - 8 * mMargin, mIndex * mMargin, rectHeight, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int position = (int) event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                mIndex = position / mMargin;
                if (mIndex < 0) {
                    mIndex = 0;
                }
                if (mIndex > mScaleCount) {
                    mIndex = mScaleCount;
                }
                postInvalidate();
                return true;
        }
        return false;
    }

    public float getScaleValue() {
        return mMin + mIndex * mGap;
    }

    public void setScaleValue(float scaleValue) {
        mIndex = Math.round((scaleValue - mMin) / mGap);
        postInvalidate();
    }


}