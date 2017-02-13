package com.richfit.barcodesystemproduct.adapter.itemdelegate;

import com.richfit.barcodesystemproduct.R;
import com.richfit.common_lib.baseadapterrv.base.ItemViewDelegate;
import com.richfit.common_lib.baseadapterrv.base.ViewHolder;
import com.richfit.common_lib.utils.Global;
import com.richfit.domain.bean.RefDetailEntity;

/**
 * 有参考移库，明细界面父节点
 * Created by monday on 2017/2/10.
 */

public class MSParentHeaderItemDelegate implements ItemViewDelegate<RefDetailEntity>{

    @Override
    public int getItemViewLayoutId() {
        return R.layout.base_ms_detail_parent_header;
    }

    @Override
    public boolean isForViewType(RefDetailEntity item, int position) {
        return Global.PARENT_NODE_HEADER_TYPE == item.getViewType();
    }

    @Override
    public void convert(ViewHolder holder, RefDetailEntity item, int position) {
        holder.setText(R.id.rowNum,(position + 1) + "");
        holder.setText(R.id.refLineNum,item.lineNum);
        holder.setText(R.id.materialNum,item.materialNum);
        holder.setText(R.id.materialDesc,item.materialDesc);
        holder.setText(R.id.materialGroup,item.materialGroup);
        //应发数量
        holder.setText(R.id.actQuantity,item.actQuantity);
        //累计数量
        holder.setText(R.id.totalQuantity,item.totalQuantity);
        //发出库位
        holder.setText(R.id.sendInv,item.invCode);
    }
}
