package com.richfit.barcodesystemproduct.adapter;

import android.content.Context;

import com.richfit.barcodesystemproduct.R;
import com.richfit.common_lib.baseadapterrv.base.ViewHolder;
import com.richfit.common_lib.basetreerv.CommonTreeAdapter;
import com.richfit.domain.bean.RefDetailEntity;
import com.richfit.domain.bean.RowConfig;

import java.util.List;
import java.util.Map;

/**
 * Created by monday on 2016/11/22.
 */

public class QingYangNMS301DetailAdapter extends CommonTreeAdapter<RefDetailEntity> {


    public QingYangNMS301DetailAdapter(Context context, int layoutId, List<RefDetailEntity> allNodes,
                                       List<RowConfig> parentNodeConfigs, List<RowConfig> childNodeConfigs,
                                       String companyCode) {
        super(context, layoutId, allNodes, parentNodeConfigs, childNodeConfigs,companyCode);
    }

    @Override
    protected void convert(ViewHolder holder, RefDetailEntity item, int position) {
        holder.setText(R.id.rowNum, String.valueOf(position + 1) );
        holder.setText(R.id.materialNum, item.materialNum);
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
}
