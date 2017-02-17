package com.richfit.common_lib.iosdialog_lib.bottomsheet;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.richfit.common_lib.R;
import com.richfit.common_lib.iosdialog_lib.adapter.SuperLvHolder;


/**
 * Created by Administrator on 2016/10/19.
 */

public class BsGvHolder extends SuperLvHolder<BottomSheetBean> {
    public ImageView ivIcon;
    public TextView mTextView;

    public BsGvHolder(Context context) {
        super(context);
        ivIcon = (ImageView) rootView.findViewById(R.id.iv_icon);
        mTextView = (TextView) rootView.findViewById(R.id.tv_msg);
    }

    @Override
    protected int setLayoutRes() {
        return R.layout.item_bottomsheet_gv;
    }

    @Override
    public void assingDatasAndEvents(Context context, BottomSheetBean bean) {
        if (bean.icon <= 0) {
            ivIcon.setVisibility(View.GONE);
        } else {
            ivIcon.setImageResource(bean.icon);
            ivIcon.setVisibility(View.VISIBLE);
        }
        mTextView.setText(bean.text);
    }
}
