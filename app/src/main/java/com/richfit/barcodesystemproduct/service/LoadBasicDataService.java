package com.richfit.barcodesystemproduct.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.richfit.barcodesystemproduct.SampleApplicationLike;
import com.richfit.barcodesystemproduct.di.component.DaggerServiceComponent;
import com.richfit.barcodesystemproduct.di.module.ServiceModule;
import com.richfit.domain.bean.LoadBasicDataWrapper;
import com.richfit.domain.bean.RowConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by monday on 2016/11/16.
 */

public class LoadBasicDataService extends Service implements ILoadBasicDataServiceView {

    private static final String LOAD_BAISC_DATA_SERVICE_TAG = "load_baisc_data_service_tag";

    @Inject
    LoadBasicDataServicePresenterImp mPresenter;

    public static void startService(Context context) {
        Intent intent = new Intent(context, LoadBasicDataService.class);
        intent.setAction(LOAD_BAISC_DATA_SERVICE_TAG);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DaggerServiceComponent.builder()
                .serviceModule(new ServiceModule(this))
                .appComponent(SampleApplicationLike.getAppComponent())
                .build()
                .inject(this);
        mPresenter.attachView(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ArrayList<LoadBasicDataWrapper> requestParam = new ArrayList<>();
        LoadBasicDataWrapper task = new LoadBasicDataWrapper();
        task.isByPage = false;
        task.queryType = "ZZ";
        requestParam.add(task);

        task = new LoadBasicDataWrapper();
        task.isByPage = false;
        task.queryType = "ZZ2";
        requestParam.add(task);

        //获取扩展字段的字典
        task = new LoadBasicDataWrapper();
        task.isByPage = false;
        task.queryType = "SD";
        requestParam.add(task);

        mPresenter.loadAndSaveBasicData(requestParam);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public void onDestroy() {
        mPresenter.detachView();
        mPresenter.unSubscribe();
        super.onDestroy();
    }

    @Override
    public void loadBasicDataComplete() {
       stopSelf();
    }

    @Override
    public void networkConnectError(String retryAction) {

    }

    @Override
    public void readConfigsSuccess(List<ArrayList<RowConfig>> configs) {

    }

    @Override
    public void readConfigsFail(String message) {

    }

    @Override
    public void readConfigsComplete() {

    }

    @Override
    public void readExtraDictionarySuccess(Map<String, Object> datas) {

    }

    @Override
    public void readExtraDictionaryFail(String message) {

    }

    @Override
    public void readExtraDictionaryComplete() {

    }
}
