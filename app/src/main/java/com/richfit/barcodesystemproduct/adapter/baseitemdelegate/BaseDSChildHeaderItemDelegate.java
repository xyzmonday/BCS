package com.richfit.barcodesystemproduct.adapter.baseitemdelegate;

import com.richfit.barcodesystemproduct.R;
import com.richfit.common_lib.baseadapterrv.base.ItemViewDelegate;
import com.richfit.common_lib.baseadapterrv.base.ViewHolder;
import com.richfit.common_lib.utils.Global;
import com.richfit.domain.bean.RefDetailEntity;

/**
 * Created by monday on 2016/11/20.
 */

public class BaseDSChildHeaderItemDelegate implements ItemViewDelegate<RefDetailEntity> {
    @Override
    public int getItemViewLayoutId() {
        return R.layout.base_ds_detail_child_header;
    }

    @Override
    public boolean isForViewType(RefDetailEntity item, int position) {
        return Global.CHILD_NODE_HEADER_TYPE == item.getViewType();
    }

    @Override
    public void convert(ViewHolder holder, RefDetailEntity refDetailEntity, int position) {

    }
}
