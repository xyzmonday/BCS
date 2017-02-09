package com.richfit.common_lib.widget;


import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * 侧拉删除控件
 *
 * @author poplar
 */
public class SwipeLayout extends FrameLayout {
    private Status status = Status.Close;
    private OnSwipeLayoutListener swipeLayoutListener;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public OnSwipeLayoutListener getSwipeLayoutListener() {
        return swipeLayoutListener;
    }

    public void setSwipeLayoutListener(OnSwipeLayoutListener swipeLayoutListener) {
        this.swipeLayoutListener = swipeLayoutListener;
    }

    public static enum Status {
        Close, Open, Draging
    }

    public interface OnSwipeLayoutListener {

        void onClose(SwipeLayout mSwipeLayout);

        void onOpen(SwipeLayout mSwipeLayout);

        void onDraging(SwipeLayout mSwipeLayout);

        // 要去关闭
        void onStartClose(SwipeLayout mSwipeLayout);

        // 要去开启
        void onStartOpen(SwipeLayout mSwipeLayout);
    }

    public SwipeLayout(Context context) {
        this(context, null);
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mDragHelper = ViewDragHelper.create(this, 0.5F, mCallback);
    }

    ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {
        // c. 重写监听
        @Override
        public boolean tryCaptureView(View view, int id) {
            return true;
        }

        // 限定移动范围
        public int clampViewPositionHorizontal(View child, int left, int dx) {

            // left
            if (child == mFrontView) {
                if (left < 0) {
                    return 0;
                } else if (left >= mRange) {
                    return mRange;
                }
            } else if (child == mBackView) {
                if (left > 0) {
                    return 0;
                } else if (left < -mRange) {
                    left = -mRange;
                }
            }
            return left;
        }

        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
//            Log.d("yff", "onViewPositionChanged = " + dx);
            // 传递事件
            if (changedView == mFrontView) {
                mBackView.offsetLeftAndRight(dx);
            } else if (changedView == mBackView) {
                mFrontView.offsetLeftAndRight(dx);
            }

            dispatchSwipeEvent();

            // 兼容老版本
            invalidate();

        }

        /**
         * 松开view,自动打开或者关闭布局
         * @param releasedChild
         * @param xvel
         * @param yvel
         */
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
//            Log.d("yff", "xvel = " + xvel);
            //滑动距离超过backView的一半打开
            if (xvel == 0 && mFrontView.getLeft() >= mRange / 2.0f) {
                open();
            } else if (xvel > 0) {
                open();
            } else {
                close();
            }

        }
    };
    private ViewDragHelper mDragHelper;
    private View mBackView;
    private View mFrontView;
    private int mHeight;
    private int mWidth;
    private int mRange;
    private float mStartX;


    /**
     * 事件分发
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float offsetX = ev.getX() - mStartX;
                if (offsetX > 0) {
                    //向右滑动，请求父view不要拦截
                    getParent().requestDisallowInterceptTouchEvent(true);// 用getParent去请求不拦截

                } else {
                    //向左滑动
                    getParent().requestDisallowInterceptTouchEvent(false);// 用getParent去请求拦截
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    // b. 传递触摸事件
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }


    protected void dispatchSwipeEvent() {

        if (swipeLayoutListener != null) {
            swipeLayoutListener.onDraging(this);
        }

        // 记录上一次的状态
        Status preStatus = status;
        // 更新当前状态
        status = updateStatus();
        if (preStatus != status && swipeLayoutListener != null) {
            if (status == Status.Close) {
                swipeLayoutListener.onClose(this);
            } else if (status == Status.Open) {
                swipeLayoutListener.onOpen(this);
            } else if (status == Status.Draging) {
                if (preStatus == Status.Close) {
                    swipeLayoutListener.onStartOpen(this);
                } else if (preStatus == Status.Open) {
                    swipeLayoutListener.onStartClose(this);
                }
            }
        }
    }

    private Status updateStatus() {

        int left = mFrontView.getLeft();
        if (left == 0) {
            return Status.Close;
        } else if (left == -mRange) {
            return Status.Open;
        }
        return Status.Draging;
    }

    public void close() {

        close(true);
    }

    public void close(boolean isSmooth) {
        int finalLeft = 0;
        if (isSmooth) {
            //开始动画
            if (mDragHelper.smoothSlideViewTo(mFrontView, finalLeft, 0)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
            layoutContent(false);
        }
    }

    public void open() {
        open(true);
    }

    public void open(boolean isSmooth) {
        int finalLeft = mRange;
        if (isSmooth) {
            //开始动画
            if (mDragHelper.smoothSlideViewTo(mFrontView, finalLeft, 0)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
            layoutContent(true);
        }


        postDelayed(new Runnable() {
            @Override
            public void run() {
                mDragHelper.smoothSlideViewTo(mFrontView, 0, 0);
//                            mViewDragHelper.settleCapturedViewAt(mOverPoint.x, mOverPoint.y);
                //这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
                ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
            }
        }, 4000);


    }

    @Override
    public void computeScroll() {
        super.computeScroll();

        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        try {
            mDragHelper.processTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // 摆放位置
        layoutContent(false);
    }

    private void layoutContent(boolean isOpen) {
        // 摆放前View
        Rect frontRect = computeFrontViewRect(isOpen);
        mFrontView.layout(frontRect.left, frontRect.top, frontRect.right, frontRect.bottom);
        // 摆放后View
        Rect backRect = computeBackViewViaFront(frontRect);
        mBackView.layout(backRect.left, backRect.top, backRect.right, backRect.bottom);

        // 调整顺序, 把mFrontView前置
        bringChildToFront(mFrontView);
    }

    private Rect computeBackViewViaFront(Rect frontRect) {
        int right = frontRect.left;
        return new Rect(right - mRange, 0, right, mHeight);
    }

    private Rect computeFrontViewRect(boolean isOpen) {
        int left = 0;
        if (isOpen) {
            left = -mRange;
        }
        return new Rect(left, 0, left + mWidth, 0 + mHeight);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // 当xml被填充完毕时调用
        mBackView = getChildAt(0);
        mFrontView = getChildAt(1);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mHeight = mFrontView.getMeasuredHeight();
        mWidth = mFrontView.getMeasuredWidth();

        mRange = mBackView.getMeasuredWidth();

    }

}
