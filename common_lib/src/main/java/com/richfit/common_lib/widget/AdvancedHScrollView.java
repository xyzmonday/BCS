package com.richfit.common_lib.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.HorizontalScrollView;
import android.widget.Scroller;

/**
 * 该控件主要解决的问题是当HorizontalView嵌套RecyclerView或者ListView
 * 情况下，让RecyclerView或者ListView垂直滑动顺畅。
 * <p>
 * Created by monday on 2016/10/14.
 */

public class AdvancedHScrollView extends HorizontalScrollView {

    /*记录上次拦截的x坐标*/
    private int mLastInterceptedX = 0;
    private int mLastInterceptedY = 0;
    private int mLastX = 0;
    private int mLastY = 0;
    private VelocityTracker mVelocityTracker;
    private Scroller mScroller;
    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private int mOverscrollDistance;
    private int mOverflingDistance;
    private int mScrollX;
    private int mScrollY;


    public AdvancedHScrollView(Context context) {
        this(context, null);
    }

    public AdvancedHScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdvancedHScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mVelocityTracker = VelocityTracker.obtain();
        mScroller = new Scroller(context);
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        mOverscrollDistance = configuration.getScaledOverscrollDistance();
        mOverflingDistance = configuration.getScaledOverflingDistance();
    }

    /**
     * 事件拦截。
     * 这里的思路是当用户的水平滑动距离大于垂直滑动的距离时HS消费滑动事件
     * 处理。
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean isIntercept = false;
        int x = (int) ev.getX();
        int y = (int) ev.getY();

        int action = ev.getActionMasked();
        int index = ev.getActionIndex();
        int pointerId = ev.getPointerId(index);

        mVelocityTracker.addMovement(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //不能拦截
                mLastInterceptedX = x;
                mLastInterceptedY = y;

                isIntercept = false;
                if (!mScroller.isFinished()) {
                    //如果动画还没有结束
                    mScroller.abortAnimation();
                    isIntercept = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:

                mVelocityTracker.computeCurrentVelocity(1000);
                int dx = x - mLastInterceptedX;
                int dy = y - mLastInterceptedY;
                float velocityX = VelocityTrackerCompat.getXVelocity(mVelocityTracker,
                        pointerId);
//                float velocityY = VelocityTrackerCompat.getYVelocity(mVelocityTracker,
//                        pointerId);
                if (Math.abs(dx) > Math.abs(dy)) {
                    //水平距离大于垂直距离
                    isIntercept = true;

                    //此时需要速度继续判断用户是向删除或者修改还是想滑动
                    if (velocityX >= 0 && velocityX <= 300) {
                        //向右滑动
                        isIntercept = false;
                    }

                } else {
                    //表明用户的意图是垂直滑动
                    isIntercept = false;
                }


                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.clear();
                isIntercept = false;
                break;
        }
        mLastX = x;
        mLastY = y;
        mLastInterceptedX = x;
        mLastInterceptedY = y;
        return isIntercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        mVelocityTracker.addMovement(event);
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = x - mLastX;
                scrollBy(-dx, 0);
                break;
            case MotionEvent.ACTION_UP:
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int initialVelocity = (int) velocityTracker.getXVelocity();

                mScrollX = getScrollX();
                mScrollY = getScrollY();
                if (getChildCount() > 0) {
                    //向右滑动速度大于0
                    if (initialVelocity >= 200) {
                        fling(initialVelocity);
                    } else if (initialVelocity <= -200) {
                        fling(initialVelocity);
                    }
                }
                mVelocityTracker.clear();
                break;
        }
        mLastX = x;
        mLastY = y;
        return true;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void fling(int velocityX) {
        if (getChildCount() > 0) {
            if (velocityX > 0) {
                mScroller.startScroll(mScrollX, mScrollY, -250, 0, 500);
                postInvalidateOnAnimation();
            } else {
                mScroller.startScroll(mScrollX, mScrollY, 250, 0, 500);
                postInvalidateOnAnimation();
            }
        }
    }


    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        recycleVelocityTracker();
        super.onDetachedFromWindow();
    }


    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }
}
