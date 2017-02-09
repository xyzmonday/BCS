package com.richfit.common_lib.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.jakewharton.rxbinding.view.RxView;
import com.richfit.common_lib.R;
import com.richfit.common_lib.utils.AppCompat;

import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by monday on 2016/11/11.
 */

public class NetConnectErrorDialogFragment extends DialogFragment {

    private static final String LOADING_DIALOG_FRAGMENT_TAG = "loading_dialog_fragment_tag";
    private static final String RETRY_ACTION = "retry_action";
    private View mView;


    public static NetConnectErrorDialogFragment newInstance(String message,String action) {
        Bundle bundle = new Bundle();
        bundle.putString(LOADING_DIALOG_FRAGMENT_TAG,message);
        bundle.putString(RETRY_ACTION,action);
        NetConnectErrorDialogFragment fragment = new NetConnectErrorDialogFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static NetConnectErrorDialogFragment newInstance(String action) {
        NetConnectErrorDialogFragment fragment = new NetConnectErrorDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(RETRY_ACTION,action);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(AppCompat.getDrawable(getContext(),android.R.color.transparent));
        mView = inflater.inflate(R.layout.layout_netconnnet_error_dialog, container, false);
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        setOnClickListener();
    }

    private void setOnClickListener() {
        RxView.clicks(mView)
                .debounce(500, TimeUnit.MILLISECONDS,AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(a->{
                    Bundle bundle = getArguments();
                    if(mListener != null && bundle != null) {
                        String action = bundle.getString(RETRY_ACTION);
                        mListener.retry(action);
                    }
                });

    }

    private INetworkConnectListener mListener;

    public void setINetworkConnectListener(INetworkConnectListener listener) {
        this.mListener = listener;
    }

    public interface INetworkConnectListener {
        void retry(String action);
    }

}
