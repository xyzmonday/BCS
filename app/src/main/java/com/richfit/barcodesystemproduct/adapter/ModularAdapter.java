package com.richfit.barcodesystemproduct.adapter;

import android.content.Context;

import com.richfit.barcodesystemproduct.R;
import com.richfit.common_lib.baseadapterrv.CommonAdapter;
import com.richfit.common_lib.baseadapterrv.base.ViewHolder;
import com.richfit.domain.bean.MenuNode;

import java.util.List;

/**
 * 功能主页面的适配器
 * Created by monday on 2016/5/25.
 */
public class ModularAdapter extends CommonAdapter<MenuNode> {


    public ModularAdapter(Context context, int layoutId, List<MenuNode> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, MenuNode item, int position) {
        holder.setBackgroundRes(R.id.modular_item_icon,item.getIcon());
        holder.setText(R.id.modular_item_name, item.getCaption());
    }
}
