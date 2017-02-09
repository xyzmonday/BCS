package com.richfit.common_lib.transformer;

import android.support.v4.view.ViewPager;
import android.view.View;

import com.nineoldandroids.view.ViewHelper;

/**
 * Created by monday on 2016/4/3.
 */
public class CubeTransformer implements ViewPager.PageTransformer {
    @Override
    public void transformPage(View page, float position) {
        int pageWidth = page.getWidth();
        int pageHeight = page.getHeight();


        //A页面
        if (position <= 0) {
            //0~-1
            ViewHelper.setPivotX(page, pageWidth);
            ViewHelper.setPivotY(page, pageHeight * 0.5F);
            ViewHelper.setRotationY(page, position * 90.0F);
        } else if (position <= 1) {
            //B页面 1~0
            ViewHelper.setPivotX(page, 0);
            ViewHelper.setPivotY(page, pageHeight * 0.5F);
            ViewHelper.setRotationY(page, position * 90.0F);

        }
    }
}
