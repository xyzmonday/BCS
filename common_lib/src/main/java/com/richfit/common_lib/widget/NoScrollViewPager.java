package com.richfit.common_lib.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 不能左右划的ViewPager
 * 
 * @author monday。主要原理是利用Android系统的事件分发机制。
 * 1. 首先在Action_Down事件ViewGroup不能够拦截掉事件，也就是ViewPager不处理滑动事件
 * 2. 如果子View没有消费本次事件，那么事件通过冒泡方式传递到ViewPager的时候也不消费该事件；
 * 
 */
public class NoScrollViewPager extends ViewPager {

	public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
	}

	public NoScrollViewPager(Context context) {
		super(context);
	}

	// 表示事件是否拦截, 返回false表示不拦截, 可以让嵌套在内部的viewpager相应左右划的事件
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		return false;
	}

	/**
	 * 重写onTouchEvent事件,什么都不用做
	 */
	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		return false;
	}
}
