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
 * 青海委外组件适配器
 * Created by monday on 2017/3/10.
 */

public class QingHaiWWCAdapter extends CommonTreeAdapter<RefDetailEntity> {

    public QingHaiWWCAdapter(Context context, int layoutId, List<RefDetailEntity> allNodes, List<RowConfig> parentNodeConfigs, List<RowConfig> childNodeConfigs, String companyCode) {
        super(context, layoutId, allNodes, parentNodeConfigs, childNodeConfigs, companyCode);
    }

    @Override
    protected void convert(ViewHolder holder, RefDetailEntity item, int position) {
        holder.setText(R.id.rowNum, String.valueOf(position + 1))
                .setText(R.id.refLineNum, String.valueOf(item.refDocItem))
                .setText(R.id.materialNum, item.materialNum)
                .setText(R.id.materialDesc, item.materialDesc)
                .setText(R.id.batchFlag, item.batchFlag)
                .setText(R.id.totalUsedQuantity, item.totalQuantity);

    }

    @Override
    public void notifyParentNodeChanged(int childNodePosition, int parentNodePosition) {

    }

    /**
     * 明细删除成功后清空累计消耗数量
     *
     * @param position
     */
    @Override
    public void notifyNodeChanged(int position) {
        RefDetailEntity deleteNode = mVisibleNodes.get(position);
        deleteNode.totalQuantity = "";
        notifyItemChanged(position);
    }

    @Override
    protected Map<String, Object> provideExtraData(int position) {
        return null;
    }
}
