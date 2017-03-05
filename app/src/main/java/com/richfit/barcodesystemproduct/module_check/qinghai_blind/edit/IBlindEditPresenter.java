package com.richfit.barcodesystemproduct.module_check.qinghai_blind.edit;


import com.richfit.common_lib.IInterface.IPresenter;
import com.richfit.domain.bean.ResultEntity;

/**
 * Created by monday on 2016/12/6.
 */

public interface IBlindEditPresenter extends IPresenter<IBlindEditView> {
    /**
     * 保存本次采集的数据
     *
     * @param result:用户采集的数据(json格式)
     */
    void uploadCheckDataSingle(ResultEntity result);
}
