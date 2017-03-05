package com.richfit.barcodesystemproduct.module.welcome;


import com.richfit.barcodesystemproduct.base.BaseView;
import com.richfit.common_lib.IInterface.IPresenter;

/**
 * Created by monday on 2016/11/8.
 */

public interface WelcomeContract {

    interface View extends BaseView {

        void loadExtraConfigSuccess();
        void loadExtraConfigFail(String message);

        void loadFragmentConfigSuccess();
        void loadFragmentConfigFail(String message);
    }

    interface Presenter extends IPresenter<View> {
        //下载扩展字段的配置文件
        void loadExtraConfig(String companyId);

        /**
         * 下载Fragment页面的配置文件
         * @param companyId:公司id,该字段用于从服务器下载的唯一标识
         * @param configFileName:从本地获取配置的文件名称，等服务端的接口完成后该字段无效
         */
        void loadFragmentConfig(String companyId,String configFileName);
        //跳转到Home页面
        void toHome(int mode);
    }
}
