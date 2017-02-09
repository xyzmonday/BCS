package com.richfit.barcodesystemproduct.adapter;

import android.content.Context;
import android.graphics.Color;
import android.widget.ImageView;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.camera.NativeImageLoader;
import com.richfit.common_lib.baseadapterrv.CommonAdapter;
import com.richfit.common_lib.baseadapterrv.base.ViewHolder;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.domain.bean.ImageEntity;

import java.io.File;
import java.util.List;

/**
 * Created by monday on 2016/11/26.
 */

public class ShowPhotosAdapter extends CommonAdapter<ImageEntity> {

    private String mImageDir;
    private NativeImageLoader mImageLoader;

    public ShowPhotosAdapter(Context context, int layoutId, List<ImageEntity> datas, String imageDir) {
        super(context, layoutId, datas);
        this.mImageDir = imageDir;
        this.mImageLoader = NativeImageLoader.getInstance();
    }

    @Override
    protected void convert(ViewHolder holder, ImageEntity data, int position) {

        final ImageView image = holder.getView(R.id.image);
        mImageLoader.loadImage(image, mImageDir + File.separator + data.imageName);

        holder.setText(R.id.name, "上次修改时间:" + UiUtil.transferLongToDate("yyyy-MM-dd HH:mm:ss", data.lastModifiedTime));
        image.setColorFilter(null);

        /*已经选择过的图片，显示出选择过的效果*/
        if (data.isSelected) {
            holder.setImageResource(R.id.image_button, R.mipmap.icon_pictures_selected);
            image.setColorFilter(Color.parseColor("#77000000"));
        } else {
            holder.setImageResource(R.id.image_button, R.mipmap.icon_picture_unselected);
            image.setColorFilter(null);
        }
    }
}
