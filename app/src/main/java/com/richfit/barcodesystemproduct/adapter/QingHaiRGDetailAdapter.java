package com.richfit.barcodesystemproduct.adapter;

import android.content.Context;

import com.richfit.barcodesystemproduct.R;
import com.richfit.common_lib.baseadapterrv.base.ViewHolder;
import com.richfit.common_lib.utils.Global;
import com.richfit.domain.bean.RefDetailEntity;
import com.richfit.domain.bean.RowConfig;

import java.util.List;

/**
 * Created by monday on 2017/3/16.
 */

public class QingHaiRGDetailAdapter extends DSYDetailAdapter {

    public QingHaiRGDetailAdapter(Context context, List<RefDetailEntity> allNodes, List<RowConfig> parentNodeConfigs, List<RowConfig> childNodeConfigs, String companyCode) {
        super(context, allNodes, parentNodeConfigs, childNodeConfigs, companyCode);
    }


    @Override
    public void onViewHolderBinded(ViewHolder holder, int position) {
        if(holder.getItemViewType() == Global.CHILD_NODE_HEADER_TYPE) {
            holder.setText(R.id.quantity,"实退数量");
        }
    }
}
