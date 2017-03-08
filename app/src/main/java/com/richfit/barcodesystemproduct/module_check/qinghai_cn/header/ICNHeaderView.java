package com.richfit.barcodesystemproduct.module_check.qinghai_cn.header;

import com.richfit.barcodesystemproduct.base.BaseView;
import com.richfit.domain.bean.InvEntity;
import com.richfit.domain.bean.ReferenceEntity;
import com.richfit.domain.bean.WorkEntity;

import java.util.List;

/**
 * Created by monday on 2017/3/3.
 */

public interface ICNHeaderView extends BaseView {
    void showWorks(List<WorkEntity> works);

    void loadWorksFail(String message);

    void showInvs(List<InvEntity> invs);

    void loadInvsFail(String message);

    void showStorageNums(List<String> storageNums);
    void loadStorageNumFail(String message);


    /**
     * 删除缓存成功
     */
    void deleteCacheSuccess();

    /**
     * 删除缓存失败
     *
     * @param message
     */
    void deleteCacheFail(String message);

    /**
     * 为公共控件绑定数据
     */
    void bindCommonHeaderUI();

    /**
     * 读取单据数据
     *
     * @param refData:单据数据
     */
    void getCheckInfoSuccess(ReferenceEntity refData);

    /**
     * 读取单据数据失败
     *
     * @param message
     */
    void getCheckInfoFail(String message);

    void clearAllUIAfterSubmitSuccess();
}
