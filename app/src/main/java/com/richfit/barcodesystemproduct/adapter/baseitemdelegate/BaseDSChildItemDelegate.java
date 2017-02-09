package com.richfit.barcodesystemproduct.adapter.baseitemdelegate;

import com.richfit.barcodesystemproduct.R;
import com.richfit.common_lib.baseadapterrv.base.ItemViewDelegate;
import com.richfit.common_lib.baseadapterrv.base.ViewHolder;
import com.richfit.common_lib.utils.Global;
import com.richfit.domain.bean.RefDetailEntity;

/**
 * Created by monday on 2016/11/20.
 */

public class BaseDSChildItemDelegate implements ItemViewDelegate<RefDetailEntity> {
    @Override
    public int getItemViewLayoutId() {
        return R.layout.base_ds_detail_child_item;
    }

    @Override
    public boolean isForViewType(RefDetailEntity item, int position) {
        return item.getViewType() == Global.CHILD_NODE_ITEM_TYPE;
    }

    @Override
    public void convert(ViewHolder holder, RefDetailEntity item, int position) {
        holder.setText(R.id.location,item.location);
        holder.setText(R.id.batchFlag,item.batchFlag);
        holder.setText(R.id.quantity,item.quantity);
        holder.setText(R.id.supplierNum,item.supplierCode);
    }
}
