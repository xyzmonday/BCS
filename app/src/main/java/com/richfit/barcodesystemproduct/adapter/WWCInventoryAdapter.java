package com.richfit.barcodesystemproduct.adapter;

import android.content.Context;

import com.richfit.common_lib.baseadapterlv.CommonAdapter;
import com.richfit.common_lib.baseadapterlv.ViewHolder;
import com.richfit.domain.bean.InventoryEntity;

import java.util.List;

/**
 * 青海委外入库组件批次的适配器
 * Created by monday on 2017/3/17.
 */

public class WWCInventoryAdapter extends CommonAdapter<InventoryEntity>{
    public WWCInventoryAdapter(Context context, int layoutId, List<InventoryEntity> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, InventoryEntity item, int position) {
        holder.setText(android.R.id.text1,item.batchFlag);
    }
}
