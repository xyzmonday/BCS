package com.richfit.barcodesystemproduct.adapter.baseitemdelegate;

import com.richfit.barcodesystemproduct.R;
import com.richfit.common_lib.baseadapterrv.base.ItemViewDelegate;
import com.richfit.common_lib.baseadapterrv.base.ViewHolder;
import com.richfit.common_lib.utils.Global;
import com.richfit.domain.bean.RefDetailEntity;


/**
 * 带参考物资入库父节点的ItemDelegate
 * Created by monday on 2016/11/15.
 */

public class BaseASParentHeaderItemDelegate implements ItemViewDelegate<RefDetailEntity> {

    @Override
    public int getItemViewLayoutId() {
        return R.layout.base_as_detail_parent_header;
    }

    @Override
    public boolean isForViewType(RefDetailEntity item, int position) {
        return item.getViewType() == Global.PARENT_NODE_HEADER_TYPE;
    }

    @Override
    public void convert(ViewHolder holder, RefDetailEntity data, int position) {
        holder.setText(R.id.rowNum,(position + 1) + "");
        holder.setText(R.id.lineNum,data.lineNum);
        holder.setText(R.id.materialNum,data.materialNum);
        holder.setText(R.id.materialDesc,data.materialDesc);
        holder.setText(R.id.materialGroup,data.materialGroup);
        //应收数量
        holder.setText(R.id.actQuantity,data.actQuantity);
        //累计实收数量
        holder.setText(R.id.totalQuantity,data.totalQuantity);
        //工厂
        holder.setText(R.id.work,data.workCode);
        //库存地点
        holder.setText(R.id.inv,data.invCode);
    }

}
