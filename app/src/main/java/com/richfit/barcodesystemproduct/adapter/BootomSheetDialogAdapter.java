package com.richfit.barcodesystemproduct.adapter;

import android.content.Context;

import com.richfit.barcodesystemproduct.R;
import com.richfit.common_lib.baseadapterlv.CommonAdapter;
import com.richfit.common_lib.baseadapterlv.ViewHolder;
import com.richfit.domain.bean.MenuNode;

import java.util.List;

/**
 * Created by monday on 2017/1/11.
 */

public class BootomSheetDialogAdapter extends CommonAdapter<MenuNode> {
    private int[] mImages;
    public BootomSheetDialogAdapter(Context context, int layoutId, List<MenuNode> datas,int[] images) {
        super(context, layoutId, datas);
        this.mImages = images;
    }

    @Override
    protected void convert(ViewHolder viewHolder, MenuNode item, int position) {
        viewHolder.setText(R.id.menu_tv,mDatas.get(position).getCaption());
        viewHolder.setImageResource(R.id.menu_iv,mImages[position % mImages.length]);
    }
}
