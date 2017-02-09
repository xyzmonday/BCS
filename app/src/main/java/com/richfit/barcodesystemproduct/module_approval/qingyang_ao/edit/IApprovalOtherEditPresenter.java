package com.richfit.barcodesystemproduct.module_approval.qingyang_ao.edit;


import com.richfit.common_lib.IInterface.IPresenter;
import com.richfit.domain.bean.ResultEntity;

/**
 * Created by monday on 2016/11/29.
 */

public interface IApprovalOtherEditPresenter extends IPresenter<IApprovalOtherEditView> {
    /**
     * 上传本次采集验收数据
     * @param result
     */
    void uploadInspectionDataSingle(ResultEntity result);
}
