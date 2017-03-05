package com.richfit.barcodesystemproduct.module_approval.qinghai_ao.edit;

import com.richfit.common_lib.IInterface.IPresenter;
import com.richfit.domain.bean.ResultEntity;

/**
 * Created by monday on 2017/3/1.
 */

public interface IQingHaiAOEditPresenter extends IPresenter<IQingHaiAOEditView>{


    void getInvsByWorkId(String workId,int flag);

    /**
     * 上传本次采集验收数据
     * @param result
     */
    void uploadInspectionDataSingle(ResultEntity result);
}
