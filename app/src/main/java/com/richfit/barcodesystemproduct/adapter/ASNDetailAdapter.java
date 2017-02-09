package com.richfit.barcodesystemproduct.adapter;

import android.content.Context;

import com.richfit.barcodesystemproduct.R;
import com.richfit.common_lib.baseadapterrv.base.ViewHolder;
import com.richfit.common_lib.basetreerv.CommonTreeAdapter;
import com.richfit.domain.bean.RefDetailEntity;
import com.richfit.domain.bean.RowConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by monday on 2016/11/27.
 */

public class ASNDetailAdapter extends CommonTreeAdapter<RefDetailEntity> {

    public ASNDetailAdapter(Context context, int layoutId, List<RefDetailEntity> allNodes, List<RowConfig> parentNodeConfigs, List<RowConfig> childNodeConfigs, String companyCode) {
        super(context, layoutId, allNodes, parentNodeConfigs, childNodeConfigs, companyCode);
    }

    @Override
    protected void convert(ViewHolder holder, RefDetailEntity item, int position) {
        holder.setVisible(R.id.batchFlag,false);
        holder.setVisible(R.id.location,false);
        holder.setText(R.id.rowNum, String.valueOf(position + 1))
                .setText(R.id.materialNum, item.materialNum)
                .setText(R.id.materialDesc, item.materialDesc)
                .setText(R.id.materialGroup, item.materialGroup)
                //批次
//                .setText(R.id.batchFlag, item.batchFlag)
                //库存地点
                .setText(R.id.inv, item.invCode)
                //上架仓位
//                .setText(R.id.location, item.location)
                //入库数量
                .setText(R.id.quantity, item.quantity);
    }

    public ArrayList<String> getSendLocations(String materialNum,String invId,int position) {
        ArrayList<String> locations = new ArrayList<>();
        for (int i = 0; i < mVisibleNodes.size(); i++) {
            if (i == position)
                continue;
            RefDetailEntity node = mVisibleNodes.get(i);
            if (!node.materialNum.equals(materialNum) && !node.invId.equals(invId)) {
                locations.add(node.location);
            }
        }
        return locations;
    }

    public ArrayList<String> getRecLocations(String materialNum, String invId, int position) {
        ArrayList<String> locations = new ArrayList<>();
        for (int i = 0; i < mVisibleNodes.size(); i++) {
            if (i == position)
                continue;
            RefDetailEntity node = mVisibleNodes.get(i);
            if (node.materialNum.equals(materialNum) && node.invId.equals(invId)) {
                locations.add(node.recLocation);
            }
        }
        return locations;
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
