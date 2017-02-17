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
 * Created by monday on 2016/11/22.
 */

public class QingYangNMS301DetailAdapter extends CommonTreeAdapter<RefDetailEntity> {


    public QingYangNMS301DetailAdapter(Context context, int layoutId, List<RefDetailEntity> allNodes,
                                       List<RowConfig> parentNodeConfigs, List<RowConfig> childNodeConfigs,
                                       String companyCode) {
        super(context, layoutId, allNodes, parentNodeConfigs, childNodeConfigs, companyCode);
    }

    @Override
    protected void convert(ViewHolder holder, RefDetailEntity item, int position) {
        holder.setText(R.id.rowNum, String.valueOf(position + 1));
        holder.setText(materialNum, item.materialNum);
        holder.setText(R.id.materialDesc, item.materialDesc);
        holder.setText(R.id.materialGroup, item.materialGroup);

        //发出库位
        holder.setText(R.id.sendInv, item.invCode);
        //发出仓位
        holder.setText(R.id.sendLoc, item.location);
        //发出批次
        holder.setText(R.id.sendBatchFlag, item.batchFlag);
        //移库数量
        holder.setText(R.id.quantity, item.quantity);
        //接收仓位
        holder.setText(R.id.recLoc, item.recLocation);
        //接收批次
        holder.setText(R.id.recBatchFlag, item.recBatchFlag);
    }

    @Override
    public void notifyParentNodeChanged(int childNodePosition, int parentNodePosition) {

    }

    @Override
    public void notifyNodeChanged(int position) {

    }

    @Override
    protected Map<String, Object> provideExtraData(int position) {
        return mVisibleNodes.get(position).mapExt;
    }

    /**
     * 获取发出仓位和接收仓位列表
     *
     * @param position:需要修改的子节点的位置
     * @param flag:0表示获取发出仓位,1:表示获取接收仓位
     * @return
     */
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


}
