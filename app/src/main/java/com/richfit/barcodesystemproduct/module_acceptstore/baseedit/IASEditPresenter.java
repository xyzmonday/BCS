package com.richfit.barcodesystemproduct.module_acceptstore.baseedit;


import com.richfit.common_lib.IInterface.IPresenter;
import com.richfit.domain.bean.ResultEntity;

/**
 * Created by monday on 2016/11/19.
 */

public interface IASEditPresenter extends IPresenter<IASEditView> {
    /**
     * 保存本次采集的数据
     * @param result:用户采集的数据(json格式)
     */
    void uploadCollectionDataSingle(ResultEntity result);
}
