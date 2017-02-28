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

/**
 * Created by monday on 2016/11/11.
 */

public class LoadingDialogFragment extends DialogFragment {

    private static final String LOADING_DIALOG_FRAGMENT_TAG = "loading_dialog_fragment_tag";

    TextView mTvLoadingMsg;

    public static LoadingDialogFragment newInstance(String message) {
        Bundle bundle = new Bundle();
        bundle.putString(LOADING_DIALOG_FRAGMENT_TAG,message);
        LoadingDialogFragment fragment = new LoadingDialogFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(AppCompat.getDrawable(getContext(),android.R.color.transparent));
        View view = inflater.inflate(R.layout.dialog_loading, container,false);
        mTvLoadingMsg = (TextView) view.findViewById(R.id.tv_msg);
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        String message = getArguments().getString(LOADING_DIALOG_FRAGMENT_TAG);
        mTvLoadingMsg.setText(message);
    }

}
