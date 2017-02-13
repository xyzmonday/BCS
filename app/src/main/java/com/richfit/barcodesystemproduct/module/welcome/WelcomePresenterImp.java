package com.richfit.barcodesystemproduct.module.welcome;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;
import com.richfit.barcodesystemproduct.base.BasePresenter;
import com.richfit.barcodesystemproduct.di.ContextLife;
import com.richfit.barcodesystemproduct.module.home.HomeActivity;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.LocalFileUtil;
import com.richfit.domain.bean.BizFragmentConfig;
import com.richfit.domain.bean.RowConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.subscribers.ResourceSubscriber;

/**
 * Created by monday on 2016/11/8.
 */

public class WelcomePresenterImp extends BasePresenter<WelcomeContract.View>
        implements WelcomeContract.Presenter {

    WelcomeContract.View mView;

    @Inject
    public WelcomePresenterImp(@ContextLife("Activity") Context context) {
        super(context);
    }

    /**
     * 下载配置文件(包括了扩展字段的配置信息和所有业务的页面配置信息)，并保存到本地数据库
     *
     * @param mode
     */
    @Override
    public void loadConfig(String companyId, int mode) {

        if (TextUtils.isEmpty(companyId)) {
            mView.loadConfigFail("未获取到公司id");
            return;
        }

        mView = getView();
//
//        ResourceSubscriber<List<RowConfig>> subscriber = mRepository.loadExtraConfig(companyId)
//                .doOnNext(configs -> mRepository.saveExtraConfigInfo(configs))
//                .doOnNext(configs -> updateExtraConfigTable(configs))
//                .compose(TransformerHelper.io2main())
//                .subscribeWith(new RxSubscriber<List<RowConfig>>(mContext, "正在初始化条码系统,请稍后...") {
//                    @Override
//                    public void _onNext(List<RowConfig> configs) {
//                    }
//
//                    @Override
//                    public void _onNetWorkConnectError(String message) {
//                        if (mView != null) {
//                            mView.loadConfigFail(message);
//                        }
//                    }
//
//                    @Override
//                    public void _onCommonError(String message) {
//                        if (mView != null) {
//                            mView.loadConfigFail(message);
//                        }
//                    }
//
//                    @Override
//                    public void _onServerError(String code, String message) {
//                        if (mView != null) {
//                            mView.loadConfigFail(message);
//                        }
//                    }
//
//                    @Override
//                    public void _onComplete() {
//                        if (mView != null) {
//                            mView.loadConfigSuccess(mode);
//                        }
//                    }
//                });
//        addSubscriber(subscriber);
        final String jsonPath = "bizConfig_QingYang.json";
//        final String jsonPath = "bizConfig_QingHai.json";
        Flowable.just(jsonPath)
                .map(path -> LocalFileUtil.getStringFormAsset(mContext, path))
                .map(json -> {
                    Gson gson = new Gson();
                    ArrayList<BizFragmentConfig> list=
                            gson.fromJson(json,new TypeToken<ArrayList<BizFragmentConfig>>(){}.getType());
                    return list;
                })
                .flatMap(list -> mRepository.saveBizFragmentConfig(list))
                .compose(TransformerHelper.io2main())
                .subscribe(new ResourceSubscriber<Boolean>() {
                    @Override
                    public void onNext(Boolean aBoolean) {

                    }

                    @Override
                    public void onError(Throwable t) {
                        Logger.d("保存业务配置信息出错 = " + t.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        mView.loadConfigSuccess(mode);
                    }
                });
    }

    @Override
    public void toHome(int mode) {
        switch (mode) {
            case Global.ONLINE_MODE:
                mRepository.setLocal(false);
                HomeActivity.newInstance(mContext);
                break;
            case Global.OFFLINE_MODE:
                mRepository.setLocal(true);
//                LocalHomeActivity.newInstance(mContext);
                break;
        }

    }

    private void updateExtraConfigTable(ArrayList<RowConfig> configs) {
        Map<String, Set<String>> map = new HashMap<>();
        Set<String> headerSet = new HashSet<>();
        Set<String> collectSet = new HashSet<>();
        Set<String> locationSet = new HashSet<>();
        for (RowConfig config : configs) {
            switch (config.configType) {
                case Global.HEADER_CONFIG_TYPE:
                    headerSet.add(config.propertyCode);
                    break;
                case Global.COLLECT_CONFIG_TYPE:
                    collectSet.add(config.propertyCode);
                    break;
                case Global.LOCATION_CONFIG_TYPE:
                    locationSet.add(config.propertyCode);
                    break;
            }
        }
        map.put(Global.HEADER_CONFIG_TYPE, headerSet);
        map.put(Global.COLLECT_CONFIG_TYPE, collectSet);
        map.put(Global.LOCATION_CONFIG_TYPE, locationSet);
        mRepository.updateExtraConfigTable(map);
    }
}
