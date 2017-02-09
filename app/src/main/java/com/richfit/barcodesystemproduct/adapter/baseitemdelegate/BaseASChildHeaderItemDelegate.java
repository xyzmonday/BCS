package com.richfit.barcodesystemproduct.adapter.baseitemdelegate;

import com.richfit.barcodesystemproduct.R;
import com.richfit.common_lib.baseadapterrv.base.ItemViewDelegate;
import com.richfit.common_lib.baseadapterrv.base.ViewHolder;
import com.richfit.common_lib.utils.Global;
import com.richfit.domain.bean.RefDetailEntity;

/**
 * 带参考物资入库子节点的ItemDelegate
 * Created by monday on 2016/11/16.
 */

public class BaseASChildHeaderItemDelegate implements ItemViewDelegate<RefDetailEntity> {

    @Override
    public int getItemViewLayoutId() {
        return R.layout.base_as_detail_child_header;
    }

    @Override
    public boolean isForViewType(RefDetailEntity item, int position) {
        return item.getViewType() == Global.CHILD_NODE_HEADER_TYPE;
    }

    @Override
    public void convert(ViewHolder holder, RefDetailEntity item, int position) {

    }
}
