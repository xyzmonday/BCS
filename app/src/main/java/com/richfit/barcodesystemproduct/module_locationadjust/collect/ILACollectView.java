package com.richfit.barcodesystemproduct.module_locationadjust.collect;

import com.richfit.barcodesystemproduct.base.BaseView;
import com.richfit.domain.bean.InventoryEntity;
import com.richfit.domain.bean.MaterialEntity;

/**
 * Created by monday on 2017/2/7.
 */

public interface ILACollectView extends BaseView {

    void getMaterialInfoSuccess(MaterialEntity materialEntity);
    void getMaterialInfoFail(String message);

    void getInventorySuccess(InventoryEntity inventoryEntity);
    void getInventoryFail(String message);

    void saveCollectedDataSuccess(String message);
    void saveCollectedDataFail(String message);

}
