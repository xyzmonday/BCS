package com.richfit.barcodesystemproduct.module_acceptstore.qingyang_asn.collect;

import com.richfit.barcodesystemproduct.base.BaseView;
import com.richfit.domain.bean.InvEntity;
import com.richfit.domain.bean.ReferenceEntity;

import java.util.List;

/**
 * Created by monday on 2016/11/27.
 */

public interface IASNCollectView extends BaseView {
    /**
     * 显示库存地点
     *
     * @param invs
     */
    void showInvs(List<InvEntity> invs);

    void loadInvsFail(String message);

    /**
     * 输入物料获取缓存后，刷新界面
     * @param refData
     * @param batchFlag
     */
    void onBindCommonUI(ReferenceEntity refData, String batchFlag);
    void loadTransferSingleInfoFail(String message);

    /**
     * 保存单条数据
     */
    void saveCollectedDataSuccess();
    void saveCollectedDataFail(String message);
}
