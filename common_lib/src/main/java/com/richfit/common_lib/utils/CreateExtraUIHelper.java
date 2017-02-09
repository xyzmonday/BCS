package com.richfit.common_lib.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.richfit.common_lib.R;
import com.richfit.common_lib.widget.RichEditText;
import com.richfit.domain.bean.RowConfig;

/**
 * Created by monday on 2016/5/24.
 */
public class CreateExtraUIHelper {

    private static final int TEXT_VIEW_TYPE = 0;
    private static final int EDIT_TEXT_TYPE = 1;
    private static final int RICH_EDIT_TEXT_TYPE = 2;
    private static final int SPINNER_TYPE = 3;
    private static final int CHECK_BOX_TYPE = 4;
    private static final int RADIO_BOX_TYPE = 5;

    private static int MAX_ID = Integer.MAX_VALUE;

    /**
     * 生成一行的界面
     *
     * @param context
     * @param rowConfig
     * @return
     */
    public static View createRowView(Context context, RowConfig rowConfig) {
        //创建改行的父容器
        LinearLayout extraRootContainer = new LinearLayout(context);
        int padding = getUnitedValue(context, 1, context.getResources().getDimension(R.dimen.collect_row_padding));
        LinearLayout.LayoutParams lm = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        extraRootContainer.setOrientation(LinearLayout.HORIZONTAL);
        extraRootContainer.setLayoutParams(lm);
        extraRootContainer.setPadding(padding, padding, padding, padding);

        //生成第一列的控件
        String tvName = rowConfig.propertyName;
        TextView textView = createLeftTextView(context, extraRootContainer, 1);
        textView.setText(tvName);

        //根据type动态生成第二行控件
        int type = Integer.valueOf(rowConfig.uiType);
        switch (type) {
            case TEXT_VIEW_TYPE:
                createRightTextView(context, extraRootContainer, 2);
                break;
            case EDIT_TEXT_TYPE:
                //普通的EditText,仅仅可以输入
                createRightEditText(context, extraRootContainer, 2);
                break;
            case RICH_EDIT_TEXT_TYPE:
                createRightRichEditText(context, extraRootContainer, 2);
                break;
            case SPINNER_TYPE:
                createRightSpinner(context, extraRootContainer, 2);
                break;
            case CHECK_BOX_TYPE:
                createRightCheckBox(context, extraRootContainer, 2, rowConfig.propertyName);
                break;
            case RADIO_BOX_TYPE:
                createRightRadioBox(context, extraRootContainer, 2, rowConfig.propertyName);
                break;
        }
        return extraRootContainer;
    }


    /**
     * 创建第一列的textView
     *
     * @param context
     * @param mRowRoot
     * @param weight
     * @return
     */
    private static TextView createLeftTextView(Context context, LinearLayout mRowRoot, float weight) {
        TextView tv = new TextView(context);
        addLeftTextViewStyle(context, tv, weight);
        mRowRoot.addView(tv);
        return tv;
    }


    /**
     * 创建第二列的textView
     *
     * @param context
     * @param mRowRoot
     * @param weight
     * @return
     */
    private static TextView createRightTextView(Context context, LinearLayout mRowRoot, float weight) {
        TextView tv = new TextView(context);
        tv.setId(MAX_ID--);
        addRightTextViewStyle(context, tv, weight);
        mRowRoot.addView(tv);
        return tv;
    }


    /**
     * 创建第二列的EditText
     *
     * @param context
     * @param mRowRoot
     * @param weight
     * @return
     */
    private static EditText createRightEditText(Context context, LinearLayout mRowRoot, float weight) {
        EditText et = new EditText(context);
        et.setId(MAX_ID--);
        addRightEditTextItemStyle(context, et, weight);
        mRowRoot.addView(et);
        return et;
    }


    /**
     * 创建第二列的RichEditText
     *
     * @param context
     * @param mRowRoot
     * @param weight
     * @return
     */
    private static EditText createRightRichEditText(Context context, LinearLayout mRowRoot, float weight) {
        RichEditText ret = new RichEditText(context);
        ret.setId(MAX_ID--);
        addRightRichEditTextStyle(context, ret, weight);
        mRowRoot.addView(ret);
        return ret;

    }

    /**
     * 创建第二列的Spinner
     *
     * @param context
     * @param mRowRoot
     * @param weight
     * @return
     */
    private static MaterialSpinner createRightSpinner(Context context, LinearLayout mRowRoot, float weight) {
        MaterialSpinner spinner = new MaterialSpinner(context);
        spinner.setId(MAX_ID--);
        addRightSpinnerStyle(context, spinner, weight);
        mRowRoot.addView(spinner);
        return spinner;
    }

    private static void createRightCheckBox(Context context, LinearLayout root, int weight, String name) {
        CheckBox cb = new CheckBox(context);
        cb.setId(MAX_ID--);
        LinearLayout.LayoutParams lm = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, weight);
        lm.gravity = Gravity.CENTER_HORIZONTAL;
        cb.setText(name);
        cb.setLayoutParams(lm);
        root.addView(cb);
    }


    private static void createRightRadioBox(Context context, LinearLayout root, int weight, String name) {
        RadioButton rb = new RadioButton(context);
        rb.setId(MAX_ID--);
        LinearLayout.LayoutParams lm = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, weight);
        lm.gravity = Gravity.CENTER_HORIZONTAL;
        rb.setText(name);
        rb.setLayoutParams(lm);
        root.addView(rb);
    }


    /**
     * 添加LeftTextViewItemStyle样式
     * <p/>
     * <style name="LeftTextViewItemStyle">
     * <item name="android:layout_width">0dp</item>
     * <item name="android:layout_weight">1</item>
     * <item name="android:layout_height">wrap_content</item>
     * <item name="android:textSize">15sp</item>
     * <item name="android:gravity">center</item>
     * <item name="android:layout_gravity">center_vertical</item>
     * <item name="android:textColor">@color/black</item>
     * </style>
     *
     * @param context
     * @param tv
     * @param weight
     */
    public static void addLeftTextViewStyle(Context context, TextView tv, float weight) {
        LinearLayout.LayoutParams lm = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, weight);
        lm.gravity = Gravity.CENTER;
        tv.setGravity(Gravity.START);
        tv.setTextColor(AppCompat.getColor(R.color.black,context));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        //设置左边距
        int leftMargin = (int) context.getResources().getDimension(R.dimen.textview_left_margin);
        lm.setMargins(leftMargin, 0, 0, 0);
        tv.setLayoutParams(lm);
    }


    /**
     * 添加RightTextViewStyle
     * <style name="RightTextViewItemStyle">
     * <item name="android:layout_width">0dp</item>
     * <item name="android:layout_height">wrap_content</item>
     * <item name="android:background">@drawable/edit_selector</item>
     * <item name="android:layout_weight">2</item>
     * <item name="android:freezesText">true</item>
     * <item name="android:padding">2dp</item>
     * </style>
     *
     * @param context
     * @param tv
     * @param weight
     */
    public static void addRightTextViewStyle(Context context, TextView tv, float weight) {
        LinearLayout.LayoutParams lm = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, weight);
        lm.gravity = Gravity.CENTER;
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setTextColor(AppCompat.getColor(R.color.black,context));
        tv.setBackgroundResource(R.drawable.edit_selector);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        tv.setFreezesText(true);
        int padding = getUnitedValue(context, 0, 2.0f);
        tv.setPadding(padding, padding, padding, padding);
        tv.setLayoutParams(lm);
    }


    /**
     * 添加RightEditTextItemStyle样式
     * <style name="RightEditTextItemStyle">
     * <item name="android:layout_width">0dp</item>
     * <item name="android:layout_height">wrap_content</item>
     * <item name="android:background">@drawable/edit_selector</item>
     * <item name="android:layout_weight">2</item>
     * <item name="android:padding">2dp</item>
     * </style>
     *
     * @param context
     * @param et
     * @param weight
     */
    public static void addRightEditTextItemStyle(Context context, EditText et, float weight) {
        LinearLayout.LayoutParams lm = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, weight);
        lm.gravity = Gravity.CENTER_HORIZONTAL;
        et.setTextColor(AppCompat.getColor(R.color.black,context));
        et.setGravity(Gravity.CENTER);
        et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        int padding = getUnitedValue(context, 1, 2.0f);
        et.setPadding(padding, padding, padding, padding);
        et.setLayoutParams(lm);
        et.setBackgroundResource(R.drawable.edit_selector);
    }

    /**
     * <style name="RightRichEditTextItemStyle">
     * <item name="android:layout_width">0dp</item>
     * <item name="android:layout_height">wrap_content</item>
     * <item name="android:singleLine">true</item>
     * <item name="android:layout_weight">2</item>
     * <item name="android:padding">2dp</item>
     * <item name="android:focusableInTouchMode">true</item>
     * <item name="android:background">@drawable/edit_selector</item>
     * <item name="android:textSize">15dp</item>
     * <item name="android:textColorHint">@color/colorAccent</item>
     * <item name="android:gravity">center_horizontal</item>
     * <item name="android:textColor">@color/black</item>
     * </style>
     *
     * @param context
     * @param et
     * @param weight
     */
    public static void addRightRichEditTextStyle(Context context, RichEditText et, float weight) {
        LinearLayout.LayoutParams lm = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, weight);
        lm.gravity = Gravity.CENTER_HORIZONTAL;
        et.setTextColor(AppCompat.getColor(R.color.black,context));
        et.setGravity(Gravity.CENTER_HORIZONTAL);
        et.setBackgroundResource(R.drawable.edit_selector);
        et.setFocusableInTouchMode(true);
        int padding = getUnitedValue(context, 1, 2.0f);
        et.setPadding(padding, padding, padding, padding);
        et.setHintTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        et.setLayoutParams(lm);
    }


    /**
     * 创建第二列的spinner控件样式
     * android:layout_width="0dp"
     * android:layout_height="wrap_content"
     * android:layout_weight="2"
     * android:freezesText="true"
     * app:ms_arrow_tint="@color/black"
     * app:ms_background_color="@color/lightgray"
     * app:ms_dropdown_height="wrap_content"
     * app:ms_dropdown_max_height="350dp"
     * app:ms_text_color="@color/blue" />
     *
     * @param context
     * @param spinner
     * @param weight
     */
    public static void addRightSpinnerStyle(Context context, MaterialSpinner spinner, float weight) {
        LinearLayout.LayoutParams lm = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, weight);
        spinner.setLayoutParams(lm);
        spinner.setArrowColor(AppCompat.getColor(R.color.black,context));
        spinner.setDropdownHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        spinner.setDropdownMaxHeight(getUnitedValue(context, 1, 350.0F));
        spinner.setTextColor(AppCompat.getColor(R.color.black,context));
    }


    /**
     * @param context
     * @param width
     * @return
     */
    public static View createTableHeaderColumn(Context context, int width) {
        TextView tv = new TextView(context);
        addTableStyle(context, tv, width);
        return tv;
    }


    /**
     * 为明细的item添加额外字段
     *
     * @param context
     * @param tv
     * @param width
     */
    public static void addTableStyle(Context context, TextView tv, int width) {
        LinearLayout.LayoutParams lm = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT);
        tv.setTextColor(AppCompat.getColor(R.color.black,context));
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        lm.gravity = Gravity.CENTER;
        int padding = getUnitedValue(context, 0, 3.0f);
        tv.setPadding(padding, padding, padding, padding);
        tv.setLayoutParams(lm);
    }


    /**
     * 创建tab分割线
     * <style name="Item_seperator_layout">
     * <item name="android:layout_width">1dp</item>
     * <item name="android:layout_height">match_parent</item>
     * <item name="android:background">@color/mediumseagreen</item>
     * </style>
     */
    public static View addHeaderTabSeparator(Context context) {
        View view = new View(context);
        int width = getUnitedValue(context, 1, 1.0f);
        LinearLayout.LayoutParams lm = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.MATCH_PARENT);
        view.setBackgroundColor(AppCompat.getColor(R.color.white,context));
        view.setLayoutParams(lm);
        return view;
    }

    /**
     * 获取dp和sp的值。0代表获取sp的值，1代表获取dp的值
     *
     * @param context
     * @param unit
     * @param targetValue
     * @return
     */
    public static int getUnitedValue(Context context, int unit, float targetValue) {
        int concreteUnit = -1;
        switch (unit) {
            case 0:
                concreteUnit = TypedValue.COMPLEX_UNIT_DIP;
                break;
            case 1:
                concreteUnit = TypedValue.COMPLEX_UNIT_SP;
                break;
        }
        int value = (int) TypedValue.applyDimension(concreteUnit, targetValue, context.getResources().getDisplayMetrics());
        return value;
    }


}
