package com.richfit.barcodesystemproduct.module_locationadjust.header;

import com.richfit.barcodesystemproduct.base.BaseView;
import com.richfit.domain.bean.InvEntity;
import com.richfit.domain.bean.WorkEntity;

import java.util.List;

/**
 * Created by monday on 2017/2/7.
 */

public interface ILAHeaderView extends BaseView{

    void showWorks(List<WorkEntity> works);
    void loadWorksFail(String message);

    void showInvs(List<InvEntity> invs);
    void loadInvsFail(String message);

    void getStorageNumSuccess(String storageNum);
    void getStorageNumFail(String message);
    void clearAllUI();

}
