package com.richfit.barcodesystemproduct.adapter;

import android.content.Context;
import android.text.TextUtils;

import com.richfit.barcodesystemproduct.R;
import com.richfit.common_lib.baseadapterrv.base.ViewHolder;
import com.richfit.common_lib.basetreerv.CommonTreeAdapter;
import com.richfit.domain.bean.RefDetailEntity;
import com.richfit.domain.bean.RowConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by monday on 2017/2/27.
 */

public class DSNDetailAdapter extends CommonTreeAdapter<RefDetailEntity> {


    public DSNDetailAdapter(Context context, int layoutId, List<RefDetailEntity> allNodes, List<RowConfig> parentNodeConfigs, List<RowConfig> childNodeConfigs, String companyCode) {
        super(context, layoutId, allNodes, parentNodeConfigs, childNodeConfigs, companyCode);
    }

    @Override
    protected void convert(ViewHolder holder, RefDetailEntity item, int position) {
        holder.setText(R.id.rowNum, String.valueOf(position + 1))
                .setText(R.id.materialNum, item.materialNum)
                .setText(R.id.materialDesc, item.materialDesc)
                .setText(R.id.materialGroup, item.materialGroup)
                .setText(R.id.batchFlag, item.batchFlag)
                .setText(R.id.inv, item.invCode)
                .setText(R.id.location, item.location)
                .setText(R.id.quantity, item.totalQuantity);
    }

    public ArrayList<String> getLocations(int position, int flag) {
        ArrayList<String> locations = new ArrayList<>();
        for (int i = 0; i < mVisibleNodes.size(); i++) {
            RefDetailEntity node = mVisibleNodes.get(i);
            if (i == position || TextUtils.isEmpty(node.location) || TextUtils.isEmpty(node.recLocation)) {
                continue;
            }
            locations.add(0 == flag ? node.location : node.recLocation);
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
