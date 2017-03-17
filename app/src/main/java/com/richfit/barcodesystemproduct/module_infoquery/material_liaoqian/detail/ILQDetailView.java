package com.richfit.barcodesystemproduct.module_infoquery.material_liaoqian.detail;

import com.richfit.barcodesystemproduct.base.BaseView;
import com.richfit.domain.bean.InventoryEntity;

import java.util.List;

/**
 * Created by monday on 2017/3/16.
 */

public interface ILQDetailView extends BaseView {

    /**
     * 显示库存
     * @param list
     */
    void showInventory(List<InventoryEntity> list);
    void loadInventoryFail(String message);
}
