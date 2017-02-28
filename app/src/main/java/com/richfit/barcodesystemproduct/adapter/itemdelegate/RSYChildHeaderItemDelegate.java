package com.richfit.barcodesystemproduct.adapter.itemdelegate;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.adapter.baseitemdelegate.BaseASChildHeaderItemDelegate;
import com.richfit.common_lib.baseadapterrv.base.ViewHolder;
import com.richfit.domain.bean.RefDetailEntity;

/**
 * Created by monday on 2017/2/27.
 */

public class RSYChildHeaderItemDelegate  extends BaseASChildHeaderItemDelegate{

    @Override
    public void convert(ViewHolder holder, RefDetailEntity item, int position) {

        holder.setText(R.id.quantity,"应退数量");
    }
}
