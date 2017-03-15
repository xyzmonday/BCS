package com.richfit.barcodesystemproduct.module_acceptstore.ww_component.edit;

import com.richfit.barcodesystemproduct.base.BaseView;
import com.richfit.common_lib.IInterface.IPresenter;
import com.richfit.domain.bean.ResultEntity;

/**
 * Created by monday on 2017/3/13.
 */

public interface QingHaiWWCEditContract {
    interface IQingHaiWWCEditView extends BaseView {
        void saveEditedDataSuccess(String message);
        void saveEditedDataFail(String message);
    }

    interface IQingHaiWWCEditPreseneter extends IPresenter<IQingHaiWWCEditView> {
        /**
         * 保存本次采集的数据
         *
         * @param result:用户采集的数据(json格式)
         */
        void uploadCollectionDataSingle(ResultEntity result);
    }
}
