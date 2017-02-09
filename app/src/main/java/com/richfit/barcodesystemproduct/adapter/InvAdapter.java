package com.richfit.barcodesystemproduct.adapter;

import android.content.Context;

import com.richfit.common_lib.baseadapterlv.CommonAdapter;
import com.richfit.common_lib.baseadapterlv.ViewHolder;
import com.richfit.domain.bean.InvEntity;

import java.util.List;

public class InvAdapter extends CommonAdapter<InvEntity> {

    public InvAdapter(Context context, int layoutId, List<InvEntity> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder viewHolder, InvEntity item, int position) {
        viewHolder.setText(android.R.id.text1, position == 0 ? item.invName :
                item.invName + "_" + item.invCode);
    }
}