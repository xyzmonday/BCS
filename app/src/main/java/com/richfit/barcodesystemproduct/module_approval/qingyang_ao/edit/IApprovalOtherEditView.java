package com.richfit.barcodesystemproduct.module_approval.qingyang_ao.edit;


import com.richfit.barcodesystemproduct.base.BaseView;

/**
 * Created by monday on 2016/11/29.
 */

public interface IApprovalOtherEditView extends BaseView {

    /**
     * 保存单条采集验收数据
     */
    void saveCollectedDataSuccess();
    void saveCollectedDataFail(String message);
}
