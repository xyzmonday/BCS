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

import static com.richfit.barcodesystemproduct.R.id.materialNum;

/**
 * Created by monday on 2017/3/2.
 */

public class QingHaiRSNDetailAdapter extends CommonTreeAdapter<RefDetailEntity> {

    public QingHaiRSNDetailAdapter(Context context, int layoutId, List<RefDetailEntity> allNodes, List<RowConfig> parentNodeConfigs, List<RowConfig> childNodeConfigs, String companyCode) {
        super(context, layoutId, allNodes, parentNodeConfigs, childNodeConfigs, companyCode);
    }

    @Override
    protected void convert(ViewHolder holder, RefDetailEntity item, int position) {

        holder.setText(R.id.rowNum, String.valueOf(position + 1))
                .setText(materialNum, item.materialNum)
                .setText(R.id.materialDesc, item.materialDesc)
                .setText(R.id.materialGroup, item.materialGroup)
                //批次
                .setText(R.id.batchFlag, item.batchFlag)
                //库存地点
                .setText(R.id.inv, item.invCode)
                //上架仓位
                .setText(R.id.location, item.location)
                //实退数量
                .setText(R.id.quantity, item.quantity);
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
