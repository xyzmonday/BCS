package com.richfit.barcodesystemproduct.module_check.qinghai_cn.collect;

import com.richfit.barcodesystemproduct.base.BaseView;
import com.richfit.domain.bean.InventoryEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by monday on 2017/3/3.
 */

public interface ICNCollectView extends BaseView {



    void getCheckTransferInfoSingle(String materialNum,String location);

    void loadInventorySuccess(List<InventoryEntity> list);

    void setupRefLineAdapter(ArrayList<String> refLines);
    void loadInventoryComplete();

    void loadInventoryFail(String message);

    void bindCommonCollectUI();

    void saveCollectedDataSuccess();

    void saveCollectedDataFail(String message);
}
