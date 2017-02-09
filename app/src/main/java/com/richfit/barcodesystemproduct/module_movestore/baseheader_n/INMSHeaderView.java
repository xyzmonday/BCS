package com.richfit.barcodesystemproduct.module_movestore.baseheader_n;


import com.richfit.barcodesystemproduct.base.BaseView;
import com.richfit.domain.bean.InvEntity;
import com.richfit.domain.bean.WorkEntity;

import java.util.List;

/**
 * 无参考移库抬头view标准
 * Created by monday on 2016/11/20.
 */

public interface INMSHeaderView extends BaseView {
    void showWorks(List<WorkEntity> works);
    void loadWorksFail(String message);

    void showSendInvs(List<InvEntity> invs);
    void loadSendInvsFail(String message);

    void showRecInvs(List<InvEntity> invs);
    void loadRecInvsFail(String message);

    void deleteCacheSuccess(String message);
    void deleteCacheFail(String message);

    /**
     * 数据上传成功后，清除抬头控件的信息
     */
    void clearAllUI();
}
