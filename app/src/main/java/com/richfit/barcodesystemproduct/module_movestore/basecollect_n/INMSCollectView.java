package com.richfit.barcodesystemproduct.module_movestore.basecollect_n;


import com.richfit.barcodesystemproduct.base.BaseView;
import com.richfit.domain.bean.InvEntity;
import com.richfit.domain.bean.InventoryEntity;
import com.richfit.domain.bean.ReferenceEntity;

import java.util.List;

/**
 * Created by monday on 2016/11/20.
 */

public interface INMSCollectView extends BaseView {
    /**
     * 显示发出库位
     * @param invs
     */
    void showSendInvs(List<InvEntity> invs);
    void loadSendInvsFail(String message);

    /**
     * 显示库存
     * @param list
     */
    void showInventory(List<InventoryEntity> list);
    void loadInventoryFail(String message);

    /**
     * 输入物料获取缓存后，刷新界面
     * @param refData
     * @param batchFlag
     */
    void onBindCommonUI(ReferenceEntity refData, String batchFlag);
    void loadTransferSingleInfoFail(String message);
    void loadTransferSingleInfoComplete();

    /**
     * 保存单条数据
     */
    void saveCollectedDataSuccess();
    void saveCollectedDataFail(String message);

    /**
     * 检查ERP仓库号是否一致
     */
    void checkWareHouseSuccess();
    void checkWareHouseFail(String message);
}
