package com.richfit.barcodesystemproduct.adapter;

import android.content.Context;
import android.text.TextUtils;

import com.richfit.barcodesystemproduct.R;
import com.richfit.common_lib.baseadapterrv.base.ViewHolder;
import com.richfit.common_lib.basetreerv.CommonTreeAdapter;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.domain.bean.RefDetailEntity;
import com.richfit.domain.bean.RowConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.richfit.barcodesystemproduct.R.id.materialNum;

/**
 * Created by monday on 2017/2/17.
 */

public class QingHaiAS103DetailAdapter extends CommonTreeAdapter<RefDetailEntity> {


    public QingHaiAS103DetailAdapter(Context context, int layoutId, List<RefDetailEntity> allNodes, List<RowConfig> parentNodeConfigs, List<RowConfig> childNodeConfigs, String companyCode) {
        super(context, layoutId, allNodes, parentNodeConfigs, childNodeConfigs, companyCode);
    }

    @Override
    protected void convert(ViewHolder holder, RefDetailEntity item, int position) {
        holder.setText(R.id.rowNum, String.valueOf(position + 1))
                .setText(R.id.lineNum, item.lineNum)
                .setText(materialNum, item.materialNum)
                .setText(R.id.materialDesc, item.materialDesc)
                .setText(R.id.materialGroup, item.materialGroup)
                .setText(R.id.actQuantity, item.actQuantity)
                .setText(R.id.totalQuantity, item.totalQuantity)
                .setText(R.id.work, item.workCode)
                .setText(R.id.inv, item.invCode);
    }

    @Override
    public void notifyParentNodeChanged(int childNodePosition, int parentNodePosition) {
        RefDetailEntity childNode = mVisibleNodes.get(childNodePosition);
        RefDetailEntity parentNode = mVisibleNodes.get(parentNodePosition);
        final float parentTotalQuantityV = UiUtil.convertToFloat(parentNode.totalQuantity, 0.0f);
        final float childTotalQuantityV = UiUtil.convertToFloat(childNode.quantity, 0.0f);
        final String newTotalQuantity = String.valueOf(parentTotalQuantityV - childTotalQuantityV);
        parentNode.totalQuantity = newTotalQuantity;
        notifyItemChanged(parentNodePosition);
    }

    @Override
    public void notifyNodeChanged(int position) {
        if (position >= 0 && position < mVisibleNodes.size()) {
            RefDetailEntity node = mVisibleNodes.get(position);
            node.invCode = "";
            node.invId = "";
            node.invName = "";
            node.totalQuantity = "";
            notifyItemChanged(position);
        }
    }

    @Override
    protected Map<String, Object> provideExtraData(int position) {
        return null;
    }

    public ArrayList<String> getLocations(int position, int flag) {
        ArrayList<String> locations = new ArrayList<>();
        for (int i = 0; i < mVisibleNodes.size(); i++) {
            RefDetailEntity node = mVisibleNodes.get(i);
            if (i == position || TextUtils.isEmpty(node.location) || TextUtils.isEmpty(node.recLocation))
                continue;
            locations.add(flag == 0 ? node.location : node.recLocation);
        }
        return locations;
    }

}
