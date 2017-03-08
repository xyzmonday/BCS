package com.richfit.barcodesystemproduct.module_delivery.qingyang_dsy;

import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.barcodesystemproduct.module_delivery.basedetail.BaseDSDetailFragment;
import com.richfit.barcodesystemproduct.module_delivery.basedetail.imp.DSDetailPresenterImp;
import com.richfit.common_lib.utils.Global;

/**
 * Created by monday on 2017/1/17.
 */

public class QingYangDSYDetailFragment extends BaseDSDetailFragment<DSDetailPresenterImp>{

    @Override
    protected String getSubFunName() {
        return "入库无参考";
    }

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }


    @Override
    public void submitBarcodeSystemSuccess() {
        showMessage("过账成功");
    }

    @Override
    public void submitBarcodeSystemFail(String message) {
        showMessage("过账失败;" + message);
    }

    @Override
    public void submitSAPSuccess() {
        setRefreshing(false, "数据上传成功");
        showSuccessDialog(mInspectionNum);
        if(mAdapter != null) {
            mAdapter.removeAllVisibleNodes();
        }
        mPresenter.showHeadFragmentByPosition(BaseFragment.HEADER_FRAGMENT_INDEX);
    }

    @Override
    public void submitSAPFail(String[] messages) {
        showErrorDialog(messages);
    }

    @Override
    public void retry(String retryAction) {
        switch (retryAction) {
            case Global.RETRY_TRANSFER_DATA_ACTION:
                submit2BarcodeSystem(mBottomMenus.get(0).transToSapFlag);
                break;
            case Global.RETRY_UPLOAD_DATA_ACTION:
                submit2SAP(mBottomMenus.get(1).transToSapFlag);
                break;
        }
        super.retry(retryAction);
    }
}
