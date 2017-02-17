package com.richfit.common_lib.iosdialog_lib;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.richfit.common_lib.iosdialog_lib.config.ConfigBean;
import com.richfit.common_lib.iosdialog_lib.config.DefaultConfig;


/**
 * Created by Administrator on 2016/10/9 0009.
 */
public class Tool {

    /**
     * 解决badtoken问题,一劳永逸
     *
     * @param dialog
     */
    public static void showDialog(Dialog dialog) {
        try {
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setMdBtnStytle(ConfigBean bean) {
        Button btnPositive =
                bean.alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button btnNegative =
                bean.alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        Button btnNatural =
                bean.alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        if (btnPositive != null && btnNegative != null) {
            btnPositive.setTextSize(bean.btnTxtSize);
            btnNegative.setTextSize(bean.btnTxtSize);
            btnNatural.setTextSize(bean.btnTxtSize);

            if (bean.btn1Color != 0)
                btnPositive.setTextColor(getColor(null, bean.btn1Color));
            if (bean.btn2Color != 0)
                btnNegative.setTextColor(getColor(null, bean.btn2Color));
            if (bean.btn3Color != 0)
                btnNatural.setTextColor(getColor(null, bean.btn3Color));
        }
    }

    public static ConfigBean newCustomDialog(ConfigBean bean) {
        Dialog dialog = new Dialog(bean.context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bean.dialog = dialog;
        return bean;
    }

    public static ConfigBean setCancelable(ConfigBean bean) {

        if (bean.alertDialog != null) {
            bean.alertDialog.setCancelable(bean.cancelable);
            bean.alertDialog.setCanceledOnTouchOutside(bean.outsideTouchable);
        } else if (bean.dialog != null) {
            bean.dialog.setCancelable(bean.cancelable);
            bean.dialog.setCanceledOnTouchOutside(bean.outsideTouchable);
        }
        return bean;
    }


    public static Dialog buildDialog(Context context, boolean cancleable, boolean outsideTouchable) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(cancleable);
        dialog.setCanceledOnTouchOutside(outsideTouchable);
        return dialog;
    }

    public static void setDialogStyle(ConfigBean bean) {
        if (bean.alertDialog != null) {
            setMdBtnStytle(bean);
        } else {
            setDialogStyle(bean.context, bean.dialog, bean.viewHeight, bean);
        }

    }

    public static void setDialogStyle(Context activity, Dialog dialog, int measuredHeight, ConfigBean bean) {
        if (dialog == null) {
            return;
        }
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//todo keycode to show round corner
        WindowManager.LayoutParams wl = window.getAttributes();
        int width = ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
        int height = (int) (((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight());
        if (bean.type != DefaultConfig.TYPE_LOADING) {
            wl.width = (int) (width * 0.9);  // todo keycode to keep gap
        } else {
            wl.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;  //TODO  一般情况下为wrapcontent,最大值为height*0.9
        if (measuredHeight > height * 0.9) {
            wl.height = (int) (height * 0.9);
        }
        if (activity instanceof Activity) {

        } else {
            wl.type = WindowManager.LayoutParams.TYPE_TOAST;
        }
        dialog.onWindowAttributesChanged(wl);
    }


    public static void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int lpHeight = p.height;
        int lpWidth = p.width;
        int childHeightSpec;
        int childWidthSpec;
        if (lpHeight > 0) {   //如果Height是一个定值，那么我们测量的时候就使用这个定值
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(lpHeight,
                    View.MeasureSpec.EXACTLY);
        } else {  // 否则，我们将mode设置为不指定，size设置为0
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);
        }
        if (lpWidth > 0) {
            childWidthSpec = View.MeasureSpec.makeMeasureSpec(lpHeight,
                    View.MeasureSpec.EXACTLY);
        } else {
            childWidthSpec = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    /**
     * @param root
     * @param id   height为0,weight为1的scrollview包裹的view的id,如果没有,传0或负数即可
     * @return
     */
    public static int mesureHeight(View root, int id) {
        measureView(root);
        int height = root.getMeasuredHeight();
        int heightExtra = 0;
        if (id > 0) {
            View view = root.findViewById(id);
            if (view != null) {
                measureView(view);
                heightExtra = view.getMeasuredHeight();
            }

        }
        return height + heightExtra;
    }

    public static int mesureHeight(View root, View... subViews) {
        measureView(root);
        int height = root.getMeasuredHeight();
        int heightExtra = 0;
        if (subViews != null && subViews.length > 0) {
            for (View view : subViews) {
                measureView(view);
                heightExtra += view.getMeasuredHeight();
            }

        }

        return height + heightExtra;
    }

    public static int getColor(Context context, int colorRes) {
        return context.getResources().getColor(colorRes);

    }

}
