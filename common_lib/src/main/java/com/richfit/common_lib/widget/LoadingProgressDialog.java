package com.richfit.common_lib.widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.richfit.common_lib.R;

/**
 * 加载提醒对话框
 */
public class LoadingProgressDialog extends ProgressDialog {

    private TextView mTvProgress;

    public LoadingProgressDialog(Context context) {
        super(context);
    }

    public LoadingProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init(getContext());
    }

    private void init(Context context) {
        //设置不可取消，点击其他区域不能取消，实际中可以抽出去封装供外包设置
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        setContentView(R.layout.load_dialog);
        mTvProgress = (TextView) findViewById(R.id.tv_load_progress);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
    }

    @Override
    public void show() {
        super.show();
    }



    public void setProgress(float progress) {
        if(mTvProgress != null) {
            mTvProgress.setText("正在玩命加载(" + String.valueOf(progress) + "%)");
        }
    }
}