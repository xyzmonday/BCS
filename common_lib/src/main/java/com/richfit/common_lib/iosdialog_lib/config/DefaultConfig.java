package com.richfit.common_lib.iosdialog_lib.config;

import android.support.annotation.ColorRes;

import com.richfit.common_lib.R;


/**
 * Created by Administrator on 2016/10/9.
 */
public class DefaultConfig {

    public static  @ColorRes int iosBtnColor = R.color.light_blue_400;
    public  static @ColorRes int lvItemTxtColor = R.color.blue_grey_900;
    public static  @ColorRes int mdBtnColor = R.color.black_alpha_16;
    public static @ColorRes int titleTxtColor = R.color.black_alpha_144;
    public static @ColorRes int msgTxtColor = R.color.black_alpha_144;
    public static @ColorRes int inputTxtColor = R.color.white_alpha_16;


/* <dimen name="btn_txt_size">14sp</dimen>
    <dimen name="title_txt_size">17sp</dimen>
    <dimen name="msg_txt_size">14sp</dimen>
    <dimen name="item_txt_size">14sp</dimen>*/
    public static int btnTxtSize = 14;// in sp
    public static int titleTxtSize = 17;
    public static int msgTxtSize = 14;
    public static int itemTxtSize = 14;
    public static int inputTxtSize = 14;


    public static CharSequence btnTxt1 = "确定";
    public static CharSequence btnTxt2 = "取消";

    public static CharSequence bottomTxt = "取消";

    public static final int TYPE_MD_LOADING = 1;
    public static final int TYPE_MD_ALERT = 2;
    public static final int TYPE_MD_SINGLE_CHOOSE = 3;
    public static final int TYPE_MD_MULTI_CHOOSE = 4;



    public static final int TYPE_IOS_HORIZONTAL = 5;
    public static final int TYPE_IOS_VERTICAL = 6;
    public static final int TYPE_IOS_BOTTOM = 7;
    public static final int TYPE_IOS_CENTER_LIST = 8;
    public static final int TYPE_IOS_INPUT = 9;

    public static final int TYPE_CUSTOM_VIEW =10;

    public static final int TYPE_BOTTOM_SHEET_CUSTOM =11;

    public static final int TYPE_BOTTOM_SHEET_LIST =12;

    public static final int TYPE_BOTTOM_SHEET_GRID =13;

    public static final int TYPE_LOADING = 14;





}
