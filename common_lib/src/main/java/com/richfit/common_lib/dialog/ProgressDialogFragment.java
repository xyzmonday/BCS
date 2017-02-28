package com.richfit.common_lib.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.richfit.common_lib.R;
import com.richfit.common_lib.utils.AppCompat;
import com.richfit.common_lib.widget.NumberCircleProgressBar;

/**
 * Created by monday on 2017/2/24.
 */

public class ProgressDialogFragment extends DialogFragment {

    private TextView mTvProgress;
    private NumberCircleProgressBar mProgressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(AppCompat.getDrawable(getContext(), android.R.color.transparent));
        View view = inflater.inflate(R.layout.dialog_progress, container, false);
        mTvProgress = (TextView) view.findViewById(R.id.tv_progress);
        mProgressBar = (NumberCircleProgressBar) view.findViewById(R.id.progress);
        return view;
    }

    public void setMaxProgress(int maxProgress) {
        if (mProgressBar != null) {
            mProgressBar.setMax(maxProgress);
        }
    }

    public void setProgress(float progress) {
        if (mProgressBar != null) {
            mProgressBar.setProgress((int) progress);
        }
    }
}
