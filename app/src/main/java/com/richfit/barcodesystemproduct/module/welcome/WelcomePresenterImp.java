package com.richfit.barcodesystemproduct.module.welcome;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.richfit.barcodesystemproduct.base.BasePresenter;
import com.richfit.barcodesystemproduct.di.ContextLife;
import com.richfit.barcodesystemproduct.module.home.HomeActivity;
import com.richfit.common_lib.rxutils.RetryWhenNetworkException;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.LocalFileUtil;
import com.richfit.domain.bean.BizFragmentConfig;
import com.richfit.domain.bean.RowConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.subscribers.ResourceSubscriber;

import static com.richfit.common_lib.utils.Global.companyId;

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
     * @param companyId:公司Id
     */
    @Override
    public void loadExtraConfig(String companyId) {
        mView = getView();
        if (TextUtils.isEmpty(companyId)) {
            mView.loadExtraConfigFail("未获取到公司id");
            return;
        }
        ResourceSubscriber<List<RowConfig>> subscriber = mRepository.loadExtraConfig(companyId)
                .doOnNext(configs -> mRepository.saveExtraConfigInfo(configs))
                .doOnNext(configs -> updateExtraConfigTable(configs))
                .retryWhen(new RetryWhenNetworkException(3,2000))
                .compose(TransformerHelper.io2main())
                .subscribeWith(new ResourceSubscriber<List<RowConfig>>() {
                    @Override
                    public void onNext(List<RowConfig> rowConfigs) {

                    }

                    @Override
                    public void onError(Throwable t) {
                        if(mView != null) {
                            mView.loadExtraConfigFail(t.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {
                        if(mView != null) {
                            mView.loadExtraConfigSuccess();
                        }
                    }
                });
        addSubscriber(subscriber);

    }

    @Override
    public void loadFragmentConfig(String companyI, String configFileName) {
        mView = getView();
        if (TextUtils.isEmpty(configFileName) && mView != null) {
            mView.loadFragmentConfigFail("未找到合适Fragment也只文件名称");
            return;
        }

        if (TextUtils.isEmpty(companyId) && mView != null) {
            mView.loadExtraConfigFail("未获取到公司id");
            return;
        }

        ResourceSubscriber<Boolean> subscriber = Flowable.just(configFileName)
                .map(name -> LocalFileUtil.getStringFormAsset(mContext, name))
                .map(json -> parseJson(json))
                .flatMap(list -> mRepository.saveBizFragmentConfig(list))
                .compose(TransformerHelper.io2main())
                .subscribeWith(new ResourceSubscriber<Boolean>() {
                    @Override
                    public void onNext(Boolean aBoolean) {

                    }

                    @Override
                    public void onError(Throwable t) {
                        if (mView != null) {
                            mView.loadFragmentConfigFail(t.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (mView != null)
                            mView.loadFragmentConfigSuccess();
                    }
                });
        addSubscriber(subscriber);
    }

    private ArrayList<BizFragmentConfig> parseJson(final String json) {
        Gson gson = new Gson();
        ArrayList<BizFragmentConfig> list =
                gson.fromJson(json, new TypeToken<ArrayList<BizFragmentConfig>>() {
                }.getType());
        return list;
    }

    @Override
    public void toHome(int mode) {
        switch (mode) {
            case Global.ONLINE_MODE:
                mRepository.setLocal(false);
                break;
            case Global.OFFLINE_MODE:
                mRepository.setLocal(true);
                break;
        }
        Intent intent = new Intent(mContext, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        AppCompatActivity activity = (AppCompatActivity) mContext;
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
        activity.finish();
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
