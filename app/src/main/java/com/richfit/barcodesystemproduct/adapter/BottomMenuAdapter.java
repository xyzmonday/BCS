package com.richfit.barcodesystemproduct.adapter;

import android.content.Context;

import com.richfit.barcodesystemproduct.R;
import com.richfit.common_lib.baseadapterlv.CommonAdapter;
import com.richfit.common_lib.baseadapterlv.ViewHolder;
import com.richfit.domain.bean.BottomMenuEntity;

import java.util.List;


/**
 * 底部公共功能菜单适配器
 */
public class BottomMenuAdapter extends CommonAdapter<BottomMenuEntity> {

    public BottomMenuAdapter(Context context, int layoutId, List<BottomMenuEntity> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder viewHolder, BottomMenuEntity item, int position) {
        viewHolder.setText(R.id.menu_tv,mDatas.get(position).menuName);
        viewHolder.setImageResource(R.id.menu_iv,mDatas.get(position).menuImageRes);
    }
}
