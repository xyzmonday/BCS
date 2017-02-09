package com.richfit.barcodesystemproduct.module_delivery.baseedit;


import com.richfit.barcodesystemproduct.base.BaseView;
import com.richfit.domain.bean.InventoryEntity;
import com.richfit.domain.bean.RefDetailEntity;

import java.util.List;

/**
 * Created by monday on 2016/11/21.
 */

public interface IDSEditView extends BaseView {
    /**
     * 显示库存
     * @param list
     */
    void showInventory(List<InventoryEntity> list);
    void loadInventoryFail(String message);

    /**
     * 获取缓存成功
     * @param cache
     * @param batchFlag
     * @param location
     */
    void onBindCache(RefDetailEntity cache, String batchFlag, String location);
    void loadCacheFail(String message);

    /**
     * 保存修改的数据
     */
    void saveCollectedDataSuccess(String message);
    void saveCollectedDataFail(String message);

}
