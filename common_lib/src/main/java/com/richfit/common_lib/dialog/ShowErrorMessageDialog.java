package com.richfit.common_lib.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.richfit.common_lib.R;
import com.richfit.common_lib.adapter.TimeLineAdapter;

import java.util.Arrays;


/**
 * 使用DialogFragment生成错误列表对话框
 * Created by monday on 2016/10/19.
 */

public class ShowErrorMessageDialog extends DialogFragment {

    public static final String MESSAGES_KEY = "messages";
    RecyclerView mRvmessages;


    public static ShowErrorMessageDialog newInstance(String[] messages) {
        Bundle bundle = new Bundle();
        bundle.putStringArray(MESSAGES_KEY, messages);
        ShowErrorMessageDialog fragment = new ShowErrorMessageDialog();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(dm.widthPixels, getDialog().getWindow().getAttributes().height);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_show_visa, container, false);
        mRvmessages = (RecyclerView) view.findViewById(R.id.time_line_recycler);
        //启用窗体的扩展特性。
        getDialog().requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRvmessages.setLayoutManager(lm);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //设置自定义的title  layout
        getDialog().getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.item_show_message_title);

        //初始化适配器
        String[] messages = getArguments().getStringArray(MESSAGES_KEY);

        TimeLineAdapter adapter = new TimeLineAdapter(Arrays.asList(messages));
        mRvmessages.setAdapter(adapter);
    }
}
