package com.richfit.common_lib.iosdialog_lib.interfaces;

import android.content.Context;
import android.view.View;

import com.richfit.common_lib.iosdialog_lib.config.ConfigBean;

import java.util.List;

/**
 * Created by Administrator on 2016/10/9.
 */
public interface Assignable {

    ConfigBean assignMdLoading(Context context, CharSequence msg, boolean cancleable, boolean outsideTouchable);

    ConfigBean assignMdAlert(Context context, CharSequence title, CharSequence msg, final MyDialogListener listener);

    ConfigBean assignMdSingleChoose(Context context, CharSequence title, final int defaultChosen, final CharSequence[] words,
                                    final MyItemDialogListener listener);

    ConfigBean assignMdMultiChoose(Context context, CharSequence title, final CharSequence[] words, final boolean[] checkedItems,
                                   final MyDialogListener btnListener);

    ConfigBean assignIosAlert(Context activity, CharSequence title, CharSequence msg, final MyDialogListener listener);

    ConfigBean assignIosAlertVertical(Context activity, CharSequence title, CharSequence msg, final MyDialogListener listener);

    ConfigBean assignIosSingleChoose(Context context, List<? extends CharSequence> words, final MyItemDialogListener listener);

    ConfigBean assignBottomItemDialog(Context context, List<? extends CharSequence> words, CharSequence bottomTxt, final MyItemDialogListener listener);


    ConfigBean assignNormalInput(Context context, CharSequence title, CharSequence hint1, CharSequence hint2,
                                 CharSequence firstTxt, CharSequence secondTxt, final MyDialogListener listener);

    ConfigBean assignCustom(Context context, View contentView, int gravity);

    ConfigBean assignCustomBottomSheet(Context context, View contentView);


    ConfigBean assignLoading(Context context, CharSequence msg, boolean cancleable, boolean outsideTouchable);


    ConfigBean assignBottomSheetLv(Context context, CharSequence title, List datas, CharSequence bottomTxt, MyItemDialogListener listener);

    ConfigBean assignBottomSheetGv(Context context, CharSequence title, List datas, CharSequence bottomTxt, int columnsNum, MyItemDialogListener listener);


}
