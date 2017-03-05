package com.richfit.common_lib.basetreerv;

import android.content.Context;
import android.view.LayoutInflater;

import com.richfit.common_lib.baseadapterrv.base.ItemViewDelegate;
import com.richfit.common_lib.baseadapterrv.base.ViewHolder;
import com.richfit.common_lib.utils.Global;
import com.richfit.domain.bean.RowConfig;
import com.richfit.domain.bean.TreeNode;

import java.util.List;

/**
 * Created by monday on 2016/11/16.
 */

public abstract class CommonTreeAdapter<T extends TreeNode> extends MultiItemTypeTreeAdapter<T> {

    protected int mLayoutId;
    protected LayoutInflater mInflater;

    public CommonTreeAdapter(final Context context, final int layoutId,
                             final List<T> allNodes,
                             final List<RowConfig> parentNodeConfigs,
                             final List<RowConfig> childNodeConfigs,
                             final String companyCode) {
        super(context, allNodes, parentNodeConfigs, childNodeConfigs,companyCode);
        mInflater = LayoutInflater.from(context);
        mLayoutId = layoutId;

        addItemViewDelegate(Global.PARENT_NODE_ITEM_TYPE,new ItemViewDelegate<T>() {
            @Override
            public int getItemViewLayoutId() {
                return mLayoutId;
            }

            @Override
            public boolean isForViewType(T item, int position) {
                return true;
            }

            @Override
            public void convert(ViewHolder holder, T item, int position) {
                CommonTreeAdapter.this.convert(holder, item, position);
            }
        });


    }

    protected abstract void convert(ViewHolder holder, T item, int position);
}
