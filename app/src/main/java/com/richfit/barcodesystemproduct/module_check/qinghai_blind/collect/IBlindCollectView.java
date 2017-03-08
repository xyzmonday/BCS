package com.richfit.barcodesystemproduct.module_check.qinghai_blind.collect;

import com.richfit.barcodesystemproduct.base.BaseView;
import com.richfit.domain.bean.MaterialEntity;

/**
 * Created by monday on 2017/3/3.
 */

public interface IBlindCollectView extends BaseView {

    void getCheckTransferInfoSingle(String materialNum, String location);

    void loadMaterialInfoSuccess(MaterialEntity data);
    void loadMaterialInfoFail(String message);

    void saveCollectedDataSuccess();

    void saveCollectedDataFail(String message);
}
