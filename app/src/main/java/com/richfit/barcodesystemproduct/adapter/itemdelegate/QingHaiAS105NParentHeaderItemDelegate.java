package com.richfit.barcodesystemproduct.adapter.itemdelegate;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.adapter.baseitemdelegate.BaseASParentHeaderItemDelegate;
import com.richfit.common_lib.baseadapterrv.base.ViewHolder;
import com.richfit.domain.bean.RefDetailEntity;

/**
 * 青海非必检父节点
 * Created by monday on 2017/3/15.
 */

public class QingHaiAS105NParentHeaderItemDelegate extends BaseASParentHeaderItemDelegate {

    @Override
    public void convert(ViewHolder holder, RefDetailEntity data, int position) {
        holder.setText(R.id.rowNum, (position + 1) + "");
        holder.setText(R.id.lineNum, data.lineNum);
        holder.setText(R.id.refLineNum,data.lineNum105);
        holder.setVisible(R.id.refLineNum,true);
        holder.setText(R.id.materialNum, data.materialNum);
        holder.setText(R.id.materialDesc, data.materialDesc);
        holder.setText(R.id.materialGroup, data.materialGroup);
        holder.setText(R.id.materialUnit, data.unit);
        //应收数量
        holder.setText(R.id.actQuantity, data.actQuantity);
        //累计实收数量
        holder.setText(R.id.totalQuantity, data.totalQuantity);
        //工厂
        holder.setText(R.id.work, data.workCode);
        //库存地点
        holder.setText(R.id.inv, data.invCode);
    }
}
