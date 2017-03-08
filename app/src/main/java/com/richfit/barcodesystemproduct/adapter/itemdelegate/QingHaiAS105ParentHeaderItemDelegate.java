package com.richfit.barcodesystemproduct.adapter.itemdelegate;

import com.richfit.barcodesystemproduct.R;
import com.richfit.common_lib.baseadapterrv.base.ItemViewDelegate;
import com.richfit.common_lib.baseadapterrv.base.ViewHolder;
import com.richfit.common_lib.utils.Global;
import com.richfit.domain.bean.RefDetailEntity;

/**
 * Created by monday on 2016/11/16.
 */

public class QingHaiAS105ParentHeaderItemDelegate implements ItemViewDelegate<RefDetailEntity> {

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_qinghai_as105_detail_header;
    }

    @Override
    public boolean isForViewType(RefDetailEntity item, int position) {
        return item.getViewType() == Global.PARENT_NODE_HEADER_TYPE;
    }

    @Override
    public void convert(ViewHolder holder, RefDetailEntity data, int position) {
        holder.setText(R.id.rowNum, (position + 1) + "")
                .setText(R.id.lineNum, data.lineNum)
                .setText(R.id.insLot, data.insLot)
                .setText(R.id.materialNum, data.materialNum)
                .setText(R.id.materialDesc, data.materialDesc)
                .setText(R.id.materialGroup, data.materialGroup)
                .setText(R.id.materialUnit, data.unit)
                //应收数量
                .setText(R.id.actQuantity, data.actQuantity)
                //累计实收数量
                .setText(R.id.totalQuantity, data.totalQuantity)
                //工厂
                .setText(R.id.work, data.workCode)
                //库存地点
                .setText(R.id.inv, data.invCode)
                .setText(R.id.refDoc, data.refDoc)
                .setText(R.id.refDocItem, String.valueOf(data.refDocItem))
                //退货交货数量
                .setText(R.id.returnDeliveryQuantity, data.returnQuantity)
                .setText(R.id.projectText, data.projectText)
                //移动原因说明
                .setText(R.id.moveCauseDesc, data.moveCauseDesc);
    }
}
