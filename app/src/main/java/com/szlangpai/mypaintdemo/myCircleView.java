package com.szlangpai.mypaintdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;

/******************************************************************************
 * Copyright (C), 2006-2020, SZLangpai Co., Ltd.
 * ****************************************************************************
 * Version       : Initial Draft
 * Author        : FengQ
 * Created       : 2017/6/17
 * Description   :
 *****************************************************************************/
public class myCircleView extends View {

    private static final String TAG = "myCircleView";

    private Paint mPaint;
    private RectF mRect;
    private Paint mArcRunPaint;
    private Paint mDefaultArcPaint;
    private Paint mProgressPaint;

    /**
     * 默认画笔颜色
     */
    private int mDottedDefaultColor = 0xFF8D99A1;
    /**
     * 默认圆形底颜色
     */
    private int mBackGroundColor = 0xFFFFFFFF;
    /**
     * 默认文字颜色
     */
    private int mProgressTextDefaultColor = 0xFF5B5B5B;
    /**
     * 默认刻度颜色
     */
    private int mDottedRunColor = 0xFFFFFFFF;
    /**
     * 默认文本大小
     */
    private int mProgressTextSize = 50;
    /**
     * 默认刻度大小
     */
    private float mCurrentTickSize = 15.0f;
    /**
     * 默认边缘距离
     */
    private int mLineDistance = 10;
    private int mCircleWidth;
    private int mCircleHeight;

    private int mProgressMax = 60;
    private int mProgress = 0;
    private String mProgressDesc = "0.00";

    public myCircleView(Context context) {
        this(context, null);
    }

    public myCircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public myCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        intiAttributes(context, attrs);
        initView();
    }

    public void intiAttributes(Context context, AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ArcProgressBar);
        mDottedDefaultColor = a.getColor(R.styleable.ArcProgressBar_dottedDefaultColor, mDottedDefaultColor);
        mDottedRunColor = a.getColor(R.styleable.ArcProgressBar_dottedRunColor, mDottedRunColor);
        mProgressTextDefaultColor = a.getColor(R.styleable.ArcProgressBar_progressTextColor, mProgressTextDefaultColor);
        mBackGroundColor = a.getColor(R.styleable.ArcProgressBar_backgroundColor, mBackGroundColor);
        mCurrentTickSize = a.getInteger(R.styleable.ArcProgressBar_tickSize, (int)mCurrentTickSize);
        mLineDistance = a.getInteger(R.styleable.ArcProgressBar_lineDistance, mLineDistance);
        mProgressTextSize = a.getInteger(R.styleable.ArcProgressBar_progressTextSize, mProgressTextSize);
        mProgressDesc = a.getString(R.styleable.ArcProgressBar_progressDesc);

        a.recycle();
    }

    public void initView() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mDottedDefaultColor);

        mDefaultArcPaint = new Paint();
        mDefaultArcPaint.setAntiAlias(true);
        mDefaultArcPaint.setStyle(Paint.Style.STROKE);
        mDefaultArcPaint.setStrokeWidth(mCurrentTickSize * 2);
        mDefaultArcPaint.setColor(mDottedDefaultColor);

        mArcRunPaint = new Paint();
        mArcRunPaint.setAntiAlias(true);
        mArcRunPaint.setStyle(Paint.Style.STROKE);
        mArcRunPaint.setStrokeWidth(mCurrentTickSize * 2);
        mArcRunPaint.setColor(mDottedDefaultColor);

        mProgressPaint = new Paint();
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setColor(mProgressTextDefaultColor);
        mProgressPaint.setTextSize(dp2px(mProgressTextSize));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width[] = getScreenWH();
        mCircleWidth = width[0] / 2;
        mCircleHeight = width[1] / 2;
        setMeasuredDimension(mCircleWidth, mCircleWidth);
        float padding = mCurrentTickSize/2;
//        int trackSize = dp2px(mDefaultSize);
        Log.i(TAG, "onMeasure: " + padding + " " + (mCircleWidth - padding));
//        mRect = new RectF(trackSize, trackSize, mCircleWidth - trackSize, mCircleWidth - trackSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.i(TAG, "w: " + w + "h: " + h + "oldw: " + oldw + "oldh: " + oldh);

        int trackSize = dp2px(mLineDistance);
        mRect = new RectF(trackSize, trackSize, mCircleWidth - trackSize, mCircleWidth - trackSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBackGround(canvas);
        drawEmptyArc(canvas);
        drawRunArc(canvas);
        drawRunText(canvas);
    }

    public void resetDraw() {
        mProgress = 0;
        mBackGroundColor = 0xFFFFFFFF;
        mProgressTextDefaultColor = 0xFF5B5B5B;
        invalidate();
    }

    public void onReset() {
        mProgress = 0;
        invalidate();
    }

    public void setMaxProgress(int maxProgress) {
        mProgressMax = maxProgress;
    }

    public void setProgress(int progress) {
        mProgress = progress;
        postInvalidate();
    }

    public void setDottedDefaultColor(int color) {
        mDottedDefaultColor = color;
    }

    public void setBackGroundColor(int color) {
        mBackGroundColor = color;
    }

    public void setProgressTextDefaultColor(int color) {
        mProgressTextDefaultColor = color;
    }

    public void setDottedRunColor(int color) {
        mDottedRunColor = color;
    }

    private void drawBackGround(Canvas canvas) {
        mPaint.setColor(mBackGroundColor);
        Log.i(TAG, "drawBackGround: " + mCircleWidth + " " + mCircleHeight);
        canvas.drawCircle(mCircleWidth / 2, mCircleWidth / 2, mCircleWidth / 2, mPaint);
    }

    private void drawEmptyArc(Canvas canvas) {
        for (int i = 0; i < 60; i++) {
            canvas.drawArc(mRect, (-90 + i * 6), 3, false, mDefaultArcPaint);
        }
    }

    private void drawRunArc(Canvas canvas) {
        mArcRunPaint.setColor(mDottedRunColor);
        for (int i = 0; i < (60 * mProgress) / mProgressMax; i++) {
            canvas.drawArc(mRect, (-90 + i * 6), 3, false, mArcRunPaint);
        }
    }

    private void drawRunText(Canvas canvas) {
        mProgressPaint.setColor(mProgressTextDefaultColor);
        int m = mProgress / 60;
        int s = mProgress % 60;
        mProgressDesc = String.format("%d:%02d", m, s);
        canvas.drawText(mProgressDesc, mCircleWidth / 2 - mProgressPaint.measureText(mProgressDesc) / 2, mCircleWidth / 2 - (mProgressPaint.descent() + mProgressPaint.ascent()) / 2, mProgressPaint);
    }

    private int[] getScreenWH() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int[] wh = {displayMetrics.widthPixels, displayMetrics.heightPixels};
        return wh;
    }

    /**
     * dp 2 px
     * @param dpVal
     */
    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }
}
