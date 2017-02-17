package com.richfit.barcodesystemproduct.module_approval.qingyang_ao.collect;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.adapter.BottomMenuAdapter;
import com.richfit.barcodesystemproduct.camera.TakephotoActivity;
import com.richfit.common_lib.utils.Global;
import com.richfit.domain.bean.BottomMenuEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 庆阳验收清单验收的数据采集模块，用户扫描物料后，仅仅提供拍照功能。
 * Created by monday on 2017/1/16.
 */

public class QingYangAO_1CollectFragment extends QingYangAOCollectFragment {


    @Override
    public void initDataLazily() {
        if (mRefData == null) {
            showMessage("请先在抬头界面获取单据数据");
            return;
        }
        if(TextUtils.isEmpty(mRefData.recordNum)) {
            showMessage("请先在抬头界面获取单据数据");
            return;
        }
        if (TextUtils.isEmpty(mRefData.refType)) {
            showMessage("请先在抬头界面获取单据数据");
            return;
        }
        if (TextUtils.isEmpty(mRefData.voucherDate)) {
            showMessage("请先在抬头界面选择过账日期");
            return;
        }
        if (mSubFunEntity.headerConfigs != null && !checkExtraData(mSubFunEntity.headerConfigs, mRefData.mapExt)) {
            showMessage("请在抬头界面输入额外必输字段信息");
            return;
        }
        setEnanble(etMaterialNum,tvActQuantity,spInv,spRefLine);
    }

    private void setEnanble(View ...views) {
        for (View view : views) {
            view.setEnabled(false);
        }
    }

    @Override
    public void showOperationMenuOnCollection(final String companyCode) {
        View rootView = LayoutInflater.from(mActivity).inflate(R.layout.menu_bottom, null);
        GridView menu = (GridView) rootView.findViewById(R.id.gridview);
        final List<BottomMenuEntity> menus = provideDefaultBottomMenu();
        BottomMenuAdapter adapter = new BottomMenuAdapter(mActivity, R.layout.item_bottom_menu, menus);
        menu.setAdapter(adapter);

        final Dialog dialog = new Dialog(mActivity, R.style.MaterialDialogSheet);
        dialog.setContentView(rootView);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();

        menu.setOnItemClickListener((adapterView, view, position, id) -> {
            toTakePhoto(menus.get(position).menuName,menus.get(position).takePhotoType);
            dialog.dismiss();
        });
    }

    @Override
    public List<BottomMenuEntity> provideDefaultBottomMenu() {
        List<BottomMenuEntity> menus = new ArrayList<>();
        BottomMenuEntity menu = new BottomMenuEntity();
        menu.menuName = "质量证明文件";
        menu.menuImageRes = R.mipmap.icon_take_photo1;
        menu.takePhotoType = 1;
        menus.add(menu);

        menu = new BottomMenuEntity();
        menu.menuName = "技术附件";
        menu.menuImageRes = R.mipmap.icon_take_photo2;
        menu.takePhotoType = 2;
        menus.add(menu);

        menu = new BottomMenuEntity();
        menu.menuName = "外观照片";
        menu.menuImageRes = R.mipmap.icon_take_photo3;
        menu.takePhotoType = 3;
        menus.add(menu);

        menu = new BottomMenuEntity();
        menu.menuName = "其他";
        menu.menuImageRes = R.mipmap.icon_take_photo4;
        menu.takePhotoType = 4;
        menus.add(menu);
        return menus;
    }

    @Override
    protected void toTakePhoto(String menuName, int takePhotoType) {
        if (!etMaterialNum.isEnabled()) {
            showMessage("请先获取单据数据");
            return;
        }
        Intent intent = new Intent(mActivity, TakephotoActivity.class);
        Bundle bundle = new Bundle();
        //入库的子菜单的名称
        bundle.putString(Global.EXTRA_TITLE_KEY, "物资验收拍照-" + menuName);
        //拍照类型
        bundle.putInt(Global.EXTRA_TAKE_PHOTO_TYPE, takePhotoType);
        //单据号
        bundle.putString(Global.EXTRA_REF_NUM_KEY, mRefData.recordNum);
        bundle.putString(Global.EXTRA_BIZ_TYPE_KEY, mBizType);
        bundle.putString(Global.EXTRA_REF_TYPE_KEY, mRefType);
        //在线离线模式
        bundle.putBoolean(Global.EXTRA_IS_LOCAL_KEY, false);
        intent.putExtras(bundle);
        mActivity.startActivity(intent);
    }

}
