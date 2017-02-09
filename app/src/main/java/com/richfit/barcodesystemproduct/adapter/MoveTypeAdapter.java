package com.richfit.barcodesystemproduct.adapter;

import android.content.Context;

import com.richfit.common_lib.baseadapterlv.CommonAdapter;
import com.richfit.common_lib.baseadapterlv.ViewHolder;

import java.util.List;

/**
 * Created by monday on 2016/11/16.
 */

public class MoveTypeAdapter extends CommonAdapter<String>{

    public MoveTypeAdapter(Context context, int layoutId, List<String> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, String item, int position) {
        holder.setText(android.R.id.text1, item);
    }
}
