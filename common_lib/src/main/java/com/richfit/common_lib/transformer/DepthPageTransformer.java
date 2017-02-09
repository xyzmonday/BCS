package com.richfit.common_lib.transformer;

import android.support.v4.view.ViewPager;
import android.view.View;

public class DepthPageTransformer implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.75f;  
  
    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();  
  
        if (position < -1) { // [-Infinity,-1)  
            // This page is way off-screen to the left.  
            view.setAlpha(0);  
  
        } else if (position <= 0) { // [-1,0]  A页面（0~-1）
            // Use the default slide transition when moving to the left page  
            view.setAlpha(1);  
            view.setTranslationX(0);  
            view.setScaleX(1);  
            view.setScaleY(1);  
  
        } else if (position <= 1) { // (0,1]   B页面（1-0）
            // Fade the page out.  
            view.setAlpha(1 - position);  //(0~1)
  
            // Counteract the default slide transition  
            view.setTranslationX(pageWidth * -position);//(-1~0)。刚开始B的坐标是（pageWidth，0）
  
            // Scale the page down (between MIN_SCALE and 1)  
            float scaleFactor = MIN_SCALE  
                    + (1 - MIN_SCALE) * (1 - Math.abs(position));  //(0.75~1)
            view.setScaleX(scaleFactor);  
            view.setScaleY(scaleFactor);  
  
        } else { // (1,+Infinity]  
            // This page is way off-screen to the right.  
            view.setAlpha(0);  
        }  
    }  
}  