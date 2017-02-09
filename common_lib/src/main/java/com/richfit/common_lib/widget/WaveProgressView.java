package com.richfit.common_lib.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

/**
 * 波浪进度控件
 */
public class WaveProgressView extends View {
    private int mWidth;
    private int mHeight;

    private Bitmap mBackgroundBitmap;

    private Path mPath;
    private Paint mPathPaint;

    private float mWaveHeight = 20f;
    private float mWaveHalfWidth = 100f;
    private String mWaveColor = "#5be4ef";
    private int mWaveSpeed = 30;

    private Paint mTextPaint;
    private String mCurrentText = "";
    private String mTextColor = "#FFFFFF";
    private int mTextSize = 41;

    private int mMaxProgress = 100;
    private int mCurrentProgress = 0;
    private float mCurY;

    private float mDistance = 0;
    private int mRefreshGap = 10;

    private boolean mAllowProgressInBothDirections = false;

    private static final int INVALIDATE = 0X777;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case INVALIDATE:
                    invalidate();
                    sendEmptyMessageDelayed(INVALIDATE, mRefreshGap);
                    break;
            }
        }
    };


    public WaveProgressView(Context context) {
        this(context, null, 0);
    }

    public WaveProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        Init();
    }

    public void setCurrent(int currentProgress, String currentText) {
        this.mCurrentProgress = currentProgress;
        this.mCurrentText = currentText;
    }

    public void setMaxProgress(int maxProgress) {
        this.mMaxProgress = maxProgress;
    }


    public void setText(String mTextColor, int mTextSize) {
        this.mTextColor = mTextColor;
        this.mTextSize = mTextSize;
    }

    public void setWave(float mWaveHight, float mWaveWidth) {
        this.mWaveHeight = mWaveHight;
        this.mWaveHalfWidth = mWaveWidth / 2;
    }


    public void setWaveColor(String mWaveColor) {
        this.mWaveColor = mWaveColor;
    }

    public void setWaveSpeed(int mWaveSpeed) {
        this.mWaveSpeed = mWaveSpeed;
    }

    public void allowProgressInBothDirections(boolean allow) {
        this.mAllowProgressInBothDirections = allow;
    }

    private void Init() {

        if (null == getBackground()) {
            throw new IllegalArgumentException(String.format("background is null."));
        } else {
            mBackgroundBitmap = getBitmapFromDrawable(getBackground());
        }

        mPath = new Path();
        mPathPaint = new Paint();
        mPathPaint.setAntiAlias(true);
        mPathPaint.setStyle(Paint.Style.FILL);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        handler.sendEmptyMessageDelayed(INVALIDATE, 100);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mCurY = mHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mBackgroundBitmap != null) {
            canvas.drawBitmap(createImage(), 0, 0, null);
        }
    }

    private Bitmap createImage() {
        mPathPaint.setColor(Color.parseColor(mWaveColor));
        mTextPaint.setColor(Color.parseColor(mTextColor));
        mTextPaint.setTextSize(mTextSize);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap finalBmp = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(finalBmp);

        float CurMidY = mHeight * (mMaxProgress - mCurrentProgress) / mMaxProgress;

        if (mAllowProgressInBothDirections || mCurY > CurMidY) {
            mCurY = mCurY - (mCurY - CurMidY) / 10;
        }
        mPath.reset();
        mPath.moveTo(0 - mDistance, mCurY);

        int waveNum = mWidth / ((int) mWaveHalfWidth * 4) + 1;
        int multiplier = 0;

        for (int i = 0; i < waveNum; i++) {
            mPath.quadTo(mWaveHalfWidth * (multiplier + 1) - mDistance, mCurY - mWaveHeight, mWaveHalfWidth * (multiplier + 2) - mDistance, mCurY);
            mPath.quadTo(mWaveHalfWidth * (multiplier + 3) - mDistance, mCurY + mWaveHeight, mWaveHalfWidth * (multiplier + 4) - mDistance, mCurY);
            multiplier += 4;
        }
        mDistance += mWaveHalfWidth / mWaveSpeed;
        mDistance = mDistance % (mWaveHalfWidth * 4);

        mPath.lineTo(mWidth, mHeight);
        mPath.lineTo(0, mHeight);
        mPath.close();
        canvas.drawPath(mPath, mPathPaint);

        int min = Math.min(mWidth, mHeight);
        mBackgroundBitmap = Bitmap.createScaledBitmap(mBackgroundBitmap, min, min, false);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));

        canvas.drawBitmap(mBackgroundBitmap, 0, 0, paint);

        canvas.drawText(mCurrentText, mWidth / 2, mHeight / 2, mTextPaint);
        return finalBmp;
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {

        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }
}