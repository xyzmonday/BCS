/*
 * Copyright (c) 2016 咖枯 <kaku201313@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.richfit.common_lib.scrollbehavior;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

import com.richfit.common_lib.R;


public class ScrollingFABBehavior extends FloatingActionButton.Behavior {

    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    private boolean mIsAnimatingOut = false;

    private int toolbarHeight;

    public ScrollingFABBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.toolbarHeight = getToolbarHeight(context);
    }

//    @Override
//    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
//        return dependency instanceof Toolbar || super.layoutDependsOn(parent, child, dependency);
//    }
//
//    @Override
//    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
//        boolean returnValue = super.onDependentViewChanged(parent, child, dependency);
//        if (dependency instanceof Toolbar) {
//            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
//            int fabBottomMargin = lp.bottomMargin;
//            int distanceToScroll = child.getHeight() + fabBottomMargin;
//            float ratio = dependency.getY() / (float) toolbarHeight;
//            ratio = Math.max(0.0F,ratio);
//            ratio = Math.min(ratio,1.0F);
//            L.e("dependency.getY() = " + dependency.getY() + "; child.getHeight() = " + child.getHeight() +
//            "; toolbarHeight = " + toolbarHeight + "; ratio = " + ratio);
//            child.setTranslationY(-distanceToScroll * ratio);
//        }
//        return returnValue;
//    }

    @Override
    public boolean onStartNestedScroll(final CoordinatorLayout coordinatorLayout, final FloatingActionButton child,
                                       final View directTargetChild, final View target, final int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
                || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onNestedScroll(final CoordinatorLayout coordinatorLayout, final FloatingActionButton child,
                               final View target, final int dxConsumed, final int dyConsumed,
                               final int dxUnconsumed, final int dyUnconsumed) {
//        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
//        Log.d("yff","dyConsumed = " + dyConsumed + "; dyUnconsumed = " + dyUnconsumed);
//        if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
//            child.hide();
//        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
//            child.show();
//        }
    }

//
//    @Override
//    public void onNestedScroll(final CoordinatorLayout coordinatorLayout, final FloatingActionButton child,
//                               final View target, final int dxConsumed, final int dyConsumed,
//                               final int dxUnconsumed, final int dyUnconsumed) {
//        L.e("dyConsumed = " + dyConsumed + ";dyUnconsumed = " + dyUnconsumed);
////        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
//        if (dyUnconsumed > 0 && !this.mIsAnimatingOut && child.getVisibility() == View.VISIBLE) {
//            // User scrolled down and the FAB is currently visible -> hide the FAB
//            animateOut(child);
//        } else if (dyUnconsumed < 0 && child.getVisibility() != View.VISIBLE) {
//            // User scrolled up and the FAB is currently not visible -> show the FAB
//            animateIn(child);
//        }
//    }

    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }

    // Same animation that FloatingActionButton.Behavior uses to hide the FAB when the AppBarLayout exits
    private void animateOut(final FloatingActionButton button) {
        if (Build.VERSION.SDK_INT >= 14) {
            ViewCompat.animate(button).translationY(button.getHeight() + getMarginBottom(button)).setInterpolator(INTERPOLATOR).withLayer()
                    .setListener(new ViewPropertyAnimatorListener() {
                        public void onAnimationStart(View view) {
                            ScrollingFABBehavior.this.mIsAnimatingOut = true;
                        }

                        public void onAnimationCancel(View view) {
                            ScrollingFABBehavior.this.mIsAnimatingOut = false;
                        }

                        public void onAnimationEnd(View view) {
                            ScrollingFABBehavior.this.mIsAnimatingOut = false;
                            view.setVisibility(View.GONE);
                        }
                    }).start();
        } else {

        }
    }

    // Same animation that FloatingActionButton.Behavior uses to show the FAB when the AppBarLayout enters
    private void animateIn(FloatingActionButton button) {
        button.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= 14) {
            ViewCompat.animate(button).translationY(0)
                    .setInterpolator(INTERPOLATOR).withLayer().setListener(null)
                    .start();
        } else {

        }
    }

    private int getMarginBottom(View v) {
        int marginBottom = 0;
        final ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            marginBottom = ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin;
        }
        return marginBottom;
    }
}