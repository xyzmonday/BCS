package com.richfit.common_lib.utils;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.richfit.common_lib.dialog.LoadingDialogFragment;


public class LoadingLayoutHelper {

    private static final String LOADING_DIALOG_FRAGMENT_TAG = "loading_dialog_fragment_tag";

    private static LoadingDialogFragment mDialogFragment;

    /**
     * 显示加载对话框
     *
     * @param context 上下文
     * @param msg     对话框显示内容
     */
    public static void showDialogForLoading(Context context, String msg) {
        AppCompatActivity activity = (AppCompatActivity) context;
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        mDialogFragment = (LoadingDialogFragment) fragmentManager.findFragmentByTag(LOADING_DIALOG_FRAGMENT_TAG);
        if (mDialogFragment == null) {
            mDialogFragment = LoadingDialogFragment.newInstance(msg);
        }
        if (!mDialogFragment.isAdded())
            mDialogFragment.show(fragmentManager, LOADING_DIALOG_FRAGMENT_TAG);
    }

    public static void showDialogForLoading(Context context) {
        AppCompatActivity activity = (AppCompatActivity) context;
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        LoadingDialogFragment dialogFragment = (LoadingDialogFragment) fragmentManager.findFragmentByTag(LOADING_DIALOG_FRAGMENT_TAG);
        if (dialogFragment == null) {
            dialogFragment = new LoadingDialogFragment();
        }
        dialogFragment.show(fragmentManager, LOADING_DIALOG_FRAGMENT_TAG);
    }

    /**
     * 关闭加载对话框
     */
    public static void cancelDialogForLoading() {
        if (mDialogFragment != null) {
            mDialogFragment.dismiss();
        }
    }
}