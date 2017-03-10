package com.richfit.barcodesystemproduct.module_acceptstore.qinghai_ww;

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
import com.richfit.barcodesystemproduct.module_acceptstore.basecollect.BaseASCollectFragment;
import com.richfit.barcodesystemproduct.module_acceptstore.qinghai_ww.imp.QingHaiASWWCollectPresenterImp;
import com.richfit.barcodesystemproduct.module_acceptstore.ww_component.DSWWComponentActivity;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.domain.bean.BottomMenuEntity;

import java.util.ArrayList;
import java.util.List;

import static com.richfit.common_lib.utils.Global.companyCode;

/**
 * Created by monday on 2017/2/20.
 */

public class QingHaiASWWCollectFragment extends BaseASCollectFragment<QingHaiASWWCollectPresenterImp> {

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    public void initEvent() {
        super.initEvent();
        etLocation.setOnRichEditTouchListener((view, location) -> getTransferSingle(getString(etBatchFlag), location));
    }


    @Override
    public boolean checkCollectedDataBeforeSave() {
        final String location = getString(etLocation);
        if (TextUtils.isEmpty(location)) {
            showMessage("请输入上架仓位");
            return false;
        }

        if (location.length() > 10) {
            showMessage("您输入的上架不合理");
            return false;
        }

        return super.checkCollectedDataBeforeSave();
    }


    @Override
    public void showOperationMenuOnCollection(final String companyCode) {
        View rootView = LayoutInflater.from(mActivity).inflate(R.layout.menu_bottom, null);
        GridView menu = (GridView) rootView.findViewById(R.id.gridview);

        BottomMenuAdapter adapter = new BottomMenuAdapter(mActivity, R.layout.item_bottom_menu, provideDefaultBottomMenu());
        menu.setAdapter(adapter);

        final Dialog dialog = new Dialog(mActivity, R.style.MaterialDialogSheet);
        dialog.setContentView(rootView);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();

        menu.setOnItemClickListener((adapterView, view, position, id) -> {
            switch (position) {
                case 0:
                    //1.保存数据
                    saveCollectedData();
                    break;
                case 1:
                    startComponent();
                    break;

            }
            dialog.dismiss();
        });
    }

    /**
     * 启动组件。必须保证用户已经采集过数据。
     */
    private void startComponent() {
        if(TextUtils.isEmpty(mRefData.transId)) {
            Float tvLocQuantityV = UiUtil.convertToFloat(getString(tvLocQuantity), 0.0f);
            if (Float.compare(tvLocQuantityV, 0.0f) <= 0.0f) {
                showMessage("请先保存数据");
                return;
            }
        }

        if (TextUtils.isEmpty(mSelectedRefLineNum)) {
            showMessage("请选获取物料信息");
            return;
        }

        Intent intent = new Intent(mActivity, DSWWComponentActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Global.EXTRA_COMPANY_CODE_KEY, companyCode);
        bundle.putString(Global.EXTRA_MODULE_CODE_KEY, "");
        bundle.putString(Global.EXTRA_BIZ_TYPE_KEY, "19_ZJ");
        bundle.putString(Global.EXTRA_REF_TYPE_KEY, mRefType);
        bundle.putString(Global.EXTRA_CAPTION_KEY, "委外入库-组件");
        bundle.putString(Global.EXTRA_REF_LINE_NUM_KEY, mSelectedRefLineNum);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    protected List<BottomMenuEntity> provideDefaultBottomMenu() {
        ArrayList<BottomMenuEntity> menus = new ArrayList<>();
        BottomMenuEntity menu = new BottomMenuEntity();
        menu.menuName = "保存数据";
        menu.menuImageRes = R.mipmap.icon_transfer;
        menus.add(menu);

        menu = new BottomMenuEntity();
        menu.menuName = "组件";
        menu.menuImageRes = R.mipmap.icon_component;
        menus.add(menu);

        return menus;
    }


    @Override
    protected int getOrgFlag() {
        return 0;
    }
}
