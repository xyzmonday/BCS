package com.richfit.barcodesystemproduct.adapter;

import android.content.Context;

import com.richfit.barcodesystemproduct.adapter.itemdelegate.MSChildHeaderItemDelegate;
import com.richfit.barcodesystemproduct.adapter.itemdelegate.MSChildItemDelegate;
import com.richfit.barcodesystemproduct.adapter.itemdelegate.MSParentHeaderItemDelegate;
import com.richfit.common_lib.basetreerv.MultiItemTypeTreeAdapter;
import com.richfit.common_lib.utils.Global;
import com.richfit.domain.bean.RefDetailEntity;
import com.richfit.domain.bean.RowConfig;

import java.util.List;
import java.util.Map;

/**
 * Created by monday on 2017/2/10.
 */

public class QingHaiUbSto351DetailAdapter extends MultiItemTypeTreeAdapter<RefDetailEntity> {
    public QingHaiUbSto351DetailAdapter(Context context, List<RefDetailEntity> allNodes,
                            List<RowConfig> parentNodeConfigs,
                            List<RowConfig> childNodeConfigs,
                            String companyCode) {
        super(context, allNodes, parentNodeConfigs, childNodeConfigs, companyCode);
        addItemViewDelegate(Global.PARENT_NODE_HEADER_TYPE, new MSParentHeaderItemDelegate());
        addItemViewDelegate(Global.CHILD_NODE_HEADER_TYPE, new MSChildHeaderItemDelegate());
        addItemViewDelegate(Global.CHILD_NODE_ITEM_TYPE, new MSChildItemDelegate());
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
