package com.richfit.barcodesystemproduct.module_approval.qinghai_ao.edit;

import com.richfit.barcodesystemproduct.base.BaseView;
import com.richfit.domain.bean.InvEntity;

import java.util.List;

/**
 * Created by monday on 2017/3/1.
 */

public interface IQingHaiAOEditView extends BaseView{
    /**
     * 保存单条采集验收数据
     */
    void saveCollectedDataSuccess();
    void saveCollectedDataFail(String message);

    void showInvs(List<InvEntity> invs);
    void loadInvsFail(String message);
}
