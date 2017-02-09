package com.richfit.barcodesystemproduct.adapter;

import android.content.Context;

import com.richfit.barcodesystemproduct.R;
import com.richfit.common_lib.baseadapterlv.CommonAdapter;
import com.richfit.common_lib.baseadapterlv.ViewHolder;

import java.util.List;


/**
 * 底部公共功能菜单适配器
 */
public class BottomMenuAdapter extends CommonAdapter<String> {
    private int[] mImages;

    public BottomMenuAdapter(Context context, int layoutId, List<String> datas,int[] images) {
        super(context, layoutId, datas);
        this.mImages = images;
    }

    @Override
    protected void convert(ViewHolder viewHolder, String item, int position) {
        viewHolder.setText(R.id.menu_tv,mDatas.get(position));
        viewHolder.setImageResource(R.id.menu_iv,mImages[position % mImages.length]);
    }
}
