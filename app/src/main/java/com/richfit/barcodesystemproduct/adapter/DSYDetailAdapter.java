package com.richfit.barcodesystemproduct.adapter;

import android.content.Context;
import android.text.TextUtils;

import com.richfit.barcodesystemproduct.adapter.itemdelegate.DSYChildHeaderItemDelegate;
import com.richfit.barcodesystemproduct.adapter.itemdelegate.DSYChildItemDelegate;
import com.richfit.barcodesystemproduct.adapter.itemdelegate.DSYParentHeaderItemDelegate;
import com.richfit.common_lib.basetreerv.MultiItemTypeTreeAdapter;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.L;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.domain.bean.RefDetailEntity;
import com.richfit.domain.bean.RowConfig;

import java.util.List;
import java.util.Map;

/**
 * 有参考物资(有参考)标志出库
 * Created by monday on 2016/11/16.
 */

public class DSYDetailAdapter extends MultiItemTypeTreeAdapter<RefDetailEntity> {

    public DSYDetailAdapter(Context context, List<RefDetailEntity> allNodes,
                            List<RowConfig> parentNodeConfigs,
                            List<RowConfig> childNodeConfigs,
                            String companyCode) {
        super(context, allNodes, parentNodeConfigs, childNodeConfigs, companyCode);
        addItemViewDelegate(Global.PARENT_NODE_HEADER_TYPE, new DSYParentHeaderItemDelegate());
        addItemViewDelegate(Global.CHILD_NODE_HEADER_TYPE, new DSYChildHeaderItemDelegate());
        addItemViewDelegate(Global.CHILD_NODE_ITEM_TYPE, new DSYChildItemDelegate());
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

    }

    /**
     * 判断是否可以过账
     *
     * @return
     */
    public boolean isTransferValide() {
        if (mVisibleNodes != null) {
            for (RefDetailEntity item : mVisibleNodes) {
                //如果累计数量为空或者等于0，那么认为该明细没有采集过数据(注意必须仅检查父节点的totalQuantity)
                if (item.isRoot() && (TextUtils.isEmpty(item.totalQuantity) || "0".equals(item.totalQuantity))) {
                    L.e("totalQuantity = " + item.totalQuantity);
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    protected Map<String, Object> provideExtraData(int position) {
        return null;
    }

}
