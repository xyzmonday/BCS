package com.richfit.common_lib.iosdialog_lib;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatDialog;
import android.view.View;

import com.richfit.common_lib.iosdialog_lib.config.ConfigBean;
import com.richfit.common_lib.iosdialog_lib.interfaces.MyDialogListener;
import com.richfit.common_lib.iosdialog_lib.interfaces.MyItemDialogListener;

import java.util.List;

/**
 * Created by Administrator on 2016/5/4 0004.
 */
public class StyledDialog {

    private static DialogInterface mLoadingDialog;//缓存加载中的dialog,便于以后可以不需要对象就让它消失

    public static void setLoadingObj(DialogInterface loading) {
        dismiss(mLoadingDialog);
        mLoadingDialog = loading;
    }


    /**
     * 一键让loading消失.
     */
    public static void dismissLoading() {
        if (mLoadingDialog != null) {
            dismiss(mLoadingDialog);
            mLoadingDialog = null;
        }
    }

    public static void dismiss(DialogInterface... dialogs) {
        if (dialogs != null && dialogs.length > 0) {
            for (DialogInterface dialog : dialogs) {
                if (dialog instanceof Dialog) {
                    Dialog dialog1 = (Dialog) dialog;
                    if (dialog1.isShowing()) {
                        dialog1.dismiss();
                    }
                } else if (dialog instanceof AppCompatDialog) {
                    AppCompatDialog dialog2 = (AppCompatDialog) dialog;
                    if (dialog2.isShowing()) {
                        dialog2.dismiss();
                    }
                }
            }

        }
    }

    public static ConfigBean buildLoading(Context context, CharSequence msg) {
        return DialogAssigner.getInstance().assignLoading(context, msg, true, false);
    }

    public static ConfigBean buildLoading(Context context) {
        return DialogAssigner.getInstance().assignLoading(context, "加载中...", true, false);
    }

    public static ConfigBean buildMdLoading(Context context) {
        return DialogAssigner.getInstance().assignMdLoading(context, "加载中...", true, false);
    }

    public static ConfigBean buildMdLoading(Context context, CharSequence msg) {
        return DialogAssigner.getInstance().assignMdLoading(context, msg, true, false);
    }


    public static ConfigBean buildMdAlert(Context context, CharSequence title, CharSequence msg, MyDialogListener listener) {
        return DialogAssigner.getInstance().assignMdAlert(context, title, msg, listener);
    }


    public static ConfigBean buildMdSingleChoose(Context context, CharSequence title, int defaultChosen, CharSequence[] words, MyItemDialogListener listener) {
        return DialogAssigner.getInstance().assignMdSingleChoose(context, title, defaultChosen, words, listener);
    }


    public static ConfigBean buildMdMultiChoose(Context context, CharSequence title, CharSequence[] words, boolean[] checkedItems, MyDialogListener btnListener) {
        return DialogAssigner.getInstance().assignMdMultiChoose(context, title, words, checkedItems, btnListener);
    }


    public static ConfigBean buildIosAlert(Context context, CharSequence title, CharSequence msg, MyDialogListener listener) {
        return DialogAssigner.getInstance().assignIosAlert(null, title, msg, listener);
    }


    public static ConfigBean buildIosAlertVertical(Context context, CharSequence title, CharSequence msg, MyDialogListener listener) {
        return DialogAssigner.getInstance().assignIosAlertVertical(context, title, msg, listener);
    }


    public static ConfigBean buildIosSingleChoose(Context context, List<? extends CharSequence> words, MyItemDialogListener listener) {
        return DialogAssigner.getInstance().assignIosSingleChoose(context, words, listener);
    }


    public static ConfigBean buildBottomItemDialog(Context context, List<? extends CharSequence> words, CharSequence bottomTxt, MyItemDialogListener listener) {
        return DialogAssigner.getInstance().assignBottomItemDialog(context, words, bottomTxt, listener);
    }


    public static ConfigBean buildNormalInput(Context context, CharSequence title, CharSequence hint1, CharSequence hint2, CharSequence firstTxt, CharSequence secondTxt, MyDialogListener listener) {
        return DialogAssigner.getInstance().assignNormalInput(context, title, hint1, hint2, firstTxt, secondTxt, listener);
    }

    public static ConfigBean buildCustom(Context context, View contentView, int gravity) {
        return DialogAssigner.getInstance().assignCustom(context, contentView, gravity);
    }

    public static ConfigBean buildCustomBottomSheet(Context context, View contentView) {
        return DialogAssigner.getInstance().assignCustomBottomSheet(context, contentView);
    }

    public static ConfigBean buildBottomSheetLv(Context context,CharSequence title, List datas, CharSequence bottomTxt, MyItemDialogListener listener) {
        return DialogAssigner.getInstance().assignBottomSheetLv(context, title, datas, bottomTxt, listener);
    }

    public static ConfigBean buildBottomSheetGv(Context context,CharSequence title, List datas, CharSequence bottomTxt, int columnsNum, MyItemDialogListener listener) {
        return DialogAssigner.getInstance().assignBottomSheetGv(context, title, datas, bottomTxt, columnsNum, listener);
    }

}
