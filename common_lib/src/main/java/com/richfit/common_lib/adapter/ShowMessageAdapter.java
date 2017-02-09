package com.richfit.common_lib.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;

import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.richfit.common_lib.R;
import com.richfit.common_lib.baseadapterrv.CommonAdapter;
import com.richfit.common_lib.baseadapterrv.base.ViewHolder;

import java.util.List;

/**
 * Created by monday on 2016/10/20.
 */

public class ShowMessageAdapter extends CommonAdapter<String> {

    private final SparseBooleanArray mCollapsedStatus;

    public ShowMessageAdapter(Context context, int layoutId, List<String> datas) {
        super(context, layoutId, datas);
        mCollapsedStatus = new SparseBooleanArray();
    }


    @Override
    protected void convert(ViewHolder holder, String data, int position) {
        ExpandableTextView tv = holder.getView(R.id.item_time_line_txt);
        tv.setText(data, mCollapsedStatus, position);
    }
}
