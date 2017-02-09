package com.richfit.barcodesystemproduct.adapter;

import android.content.Context;

import com.richfit.common_lib.baseadapterlv.CommonAdapter;
import com.richfit.common_lib.baseadapterlv.ViewHolder;
import com.richfit.domain.bean.WorkEntity;

import java.util.List;

/**
 * Created by monday on 2016/11/16.
 */

public class WorkAdapter extends CommonAdapter<WorkEntity> {

    public WorkAdapter(Context context, int layoutId, List<WorkEntity> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, WorkEntity item, int position) {
        holder.setText(android.R.id.text1, position == 0 ? item.workName :
                item.workName + "_" + item.workCode);
    }
}
