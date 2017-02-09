package com.richfit.barcodesystemproduct.module_movestore.baseedit_n;

import com.richfit.barcodesystemproduct.base.BaseView;
import com.richfit.domain.bean.InventoryEntity;
import com.richfit.domain.bean.ReferenceEntity;

import java.util.List;

/**
 * Created by monday on 2016/11/22.
 */

public interface INMSEditView extends BaseView {
    /**
     * 输入物料获取缓存后，刷新界面
     *
     * @param refData
     * @param batchFlag
     */
    void onBindCommonUI(ReferenceEntity refData, String batchFlag);

    void loadTransferSingleInfoFail(String message);

    void loadTransferSingleInfoComplete();

    /**
     * 显示库存
     *
     * @param list
     */
    void showInventory(List<InventoryEntity> list);

    void loadInventoryFail(String message);

    /**
     * 保存单条数据
     */
    void saveCollectedDataSuccess();

    void saveCollectedDataFail(String message);
}
