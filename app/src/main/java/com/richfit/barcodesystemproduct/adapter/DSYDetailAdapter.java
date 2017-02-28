package com.richfit.barcodesystemproduct.adapter;

import android.content.Context;

import com.richfit.barcodesystemproduct.adapter.itemdelegate.DSYChildHeaderItemDelegate;
import com.richfit.barcodesystemproduct.adapter.itemdelegate.DSYChildItemDelegate;
import com.richfit.barcodesystemproduct.adapter.itemdelegate.DSYParentHeaderItemDelegate;
import com.richfit.common_lib.basetreerv.MultiItemTypeTreeAdapter;
import com.richfit.common_lib.utils.Global;
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
        super(context, allNodes, parentNodeConfigs, childNodeConfigs,companyCode);
        addItemViewDelegate(Global.PARENT_NODE_HEADER_TYPE,new DSYParentHeaderItemDelegate());
        addItemViewDelegate(Global.CHILD_NODE_HEADER_TYPE,new DSYChildHeaderItemDelegate());
        addItemViewDelegate(Global.CHILD_NODE_ITEM_TYPE,new DSYChildItemDelegate());
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
