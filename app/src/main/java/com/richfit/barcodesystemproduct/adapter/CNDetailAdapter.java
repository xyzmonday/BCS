package com.richfit.barcodesystemproduct.adapter;

import android.content.Context;

import com.richfit.barcodesystemproduct.R;
import com.richfit.common_lib.baseadapterrv.base.ViewHolder;
import com.richfit.common_lib.basetreerv.CommonTreeAdapter;
import com.richfit.domain.bean.InventoryEntity;
import com.richfit.domain.bean.RowConfig;

import java.util.List;
import java.util.Map;

/**
 * Created by monday on 2016/12/6.
 */

public class CNDetailAdapter extends CommonTreeAdapter<InventoryEntity> {


    public CNDetailAdapter(Context context, int layoutId, List<InventoryEntity> allNodes, List<RowConfig> parentNodeConfigs, List<RowConfig> childNodeConfigs, String companyCode) {
        super(context, layoutId, allNodes, parentNodeConfigs, childNodeConfigs, companyCode);
    }

    @Override
    protected void convert(ViewHolder holder, InventoryEntity item, int position) {
        holder.setText(R.id.rowNum, String.valueOf(position + 1))
                .setText(R.id.materialNum, item.materialNum)
                .setText(R.id.materialDesc, item.materialDesc)
                .setText(R.id.materialGroup, item.materialGroup)
                .setText(R.id.checkLocation, item.location)
                .setText(R.id.invQuantity, item.invQuantity)
                .setText(R.id.checkQuantity, item.totalQuantity)
                .setText(R.id.specialInvFlag,item.specialInventoryFlag)
                .setText(R.id.specialInvNum,item.specialInventoryNum)
                .setText(R.id.newFlag,item.newFlag)
                .setText(R.id.checkState, item.isChecked ? "已盘点" : "未盘点");
        if (item.isChecked) {
            holder.setBackgroundRes(R.id.root_id, R.color.green_color_emerald);
        }
    }

    @Override
    public void notifyParentNodeChanged(int childNodePosition, int parentNodePosition) {

    }

    @Override
    public void notifyNodeChanged(int position) {

    }

    @Override
    protected Map<String, Object> provideExtraData(int position) {
        return null;
    }
}
