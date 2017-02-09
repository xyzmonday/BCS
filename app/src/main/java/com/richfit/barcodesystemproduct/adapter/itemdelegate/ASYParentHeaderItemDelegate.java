package com.richfit.barcodesystemproduct.adapter.itemdelegate;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.adapter.baseitemdelegate.BaseASParentHeaderItemDelegate;
import com.richfit.common_lib.baseadapterrv.base.ViewHolder;
import com.richfit.domain.bean.RefDetailEntity;

/**
 * Created by monday on 2016/11/16.
 */

public class ASYParentHeaderItemDelegate extends BaseASParentHeaderItemDelegate {

    @Override
    public void convert(ViewHolder holder, RefDetailEntity data, int position) {
        super.convert(holder, data, position);
        holder.setVisible(R.id.y_n_location,false);
        holder.setVisible(R.id.specailInventoryFlag,false);
    }
}
