package com.richfit.barcodesystemproduct.module_returnstore.qinghai_rsn.edit;

import com.richfit.barcodesystemproduct.base.BaseView;
import com.richfit.domain.bean.ReferenceEntity;

/**
 * Created by monday on 2017/3/2.
 */

public interface IQingHaiRSNEditView extends BaseView{

    /**
     * 输入物料获取缓存后，刷新界面
     *
     * @param refData
     * @param batchFlag
     */
    void onBindCommonUI(ReferenceEntity refData, String batchFlag);

    void loadTransferSingleInfoFail(String message);

    void loadTransferSingeInfoComplete();

    /**
     * 保存单条数据
     */
    void saveCollectedDataSuccess();
    void saveCollectedDataFail(String message);
}
