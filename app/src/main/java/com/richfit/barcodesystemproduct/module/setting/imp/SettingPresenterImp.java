package com.richfit.barcodesystemproduct.module.setting.imp;

import android.content.Context;
import android.text.TextUtils;

import com.richfit.barcodesystemproduct.base.BasePresenter;
import com.richfit.barcodesystemproduct.di.ContextLife;
import com.richfit.barcodesystemproduct.module.setting.ISettingPresenter;
import com.richfit.barcodesystemproduct.module.setting.ISettingView;
import com.richfit.common_lib.rxutils.RetryWhenNetworkException;
import com.richfit.common_lib.rxutils.RxSubscriber;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.L;
import com.richfit.domain.bean.LoadBasicDataWrapper;
import com.richfit.domain.bean.LoadDataTask;
import com.richfit.domain.bean.UpdateEntity;

import java.util.ArrayList;
import java.util.LinkedList;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.ResourceObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.ResourceSubscriber;
import zlc.season.rxdownload2.RxDownload;
import zlc.season.rxdownload2.entity.DownloadStatus;

/**
 * Created by monday on 2016/11/29.
 */

public class SettingPresenterImp extends BasePresenter<ISettingView>
        implements ISettingPresenter {

    private ISettingView mView;
    private RxDownload mRxDownload;
    private int mTaskId = 0;
    private Disposable mUpdateDisposable;

    @Inject
    public SettingPresenterImp(@ContextLife("Activity") Context context) {
        super(context);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mRxDownload = RxDownload.getInstance()
                .maxThread(3)
                .maxRetryCount(3)
                .context(mContext)    // 自动安装需要Context
                .autoInstall(true); //下载完成自动安装
        mTaskId = 0;
    }

    @Override
    public void loadAndSaveBasicData(ArrayList<LoadBasicDataWrapper> requestParam) {
        mView = getView();

        ResourceSubscriber<Integer> subscriber = Flowable.fromIterable(requestParam)
                .concatMap(param -> mRepository.preparePageLoad(param))
                .concatMap(param -> Flowable.fromIterable(addTask(param.queryType, param.totalCount, param.isByPage)))
                .concatMap(task -> mRepository.loadBasicData(task))
                .map(sourceMap -> mRepository.saveBasicData(sourceMap))
                .retryWhen(new RetryWhenNetworkException(3, 2000))
//                .subscribeOn(Schedulers.trampoline())
//                .observeOn(AndroidSchedulers.mainThread())
                .compose(TransformerHelper.io2main())
                .subscribeWith(new ResourceSubscriber<Integer>() {

                    @Override
                    protected void onStart() {
                        super.onStart();
                        if (mView != null) {
                            mView.onStartLoadBasicData(mTaskId);
                        }
                    }

                    @Override
                    public void onNext(Integer integer) {
                        L.e("percent = " + integer + "; mTaskId = " + mTaskId);
                        if (mView != null && mTaskId != 0) {
//                            float percent = (integer.intValue() * 100.0F) / (mTaskId * 1.0F);
                            mView.loadBasicDataProgress(integer.intValue());
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        mTaskId = 0;
                        if (mView != null) {
                            mView.loadBasicDataFail(t.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {
                        mTaskId = 0;
                        if (mView != null) {
                            mView.loadBasicDataSuccess();
                        }
                    }
                });
        addSubscriber(subscriber);
    }

    @Override
    public void getAppVersion() {
        mView = getView();
        mRepository.getAppVersion()
                .compose(TransformerHelper.io2main())
                .subscribeWith(new RxSubscriber<UpdateEntity>(mContext, "正在检查更新...") {
                    @Override
                    public void _onNext(UpdateEntity updateEntity) {
                        if (mView != null) {
                            mView.checkAppVersion(updateEntity);
                        }
                    }

                    @Override
                    public void _onNetWorkConnectError(String message) {

                    }

                    @Override
                    public void _onCommonError(String message) {
                        if (mView != null) {
                            mView.getUpdateInfoFail(message);
                        }
                    }

                    @Override
                    public void _onServerError(String code, String message) {
                        if (mView != null) {
                            mView.getUpdateInfoFail(message);
                        }
                    }

                    @Override
                    public void _onComplete() {

                    }
                });
    }

    @Override
    public void loadLatestApp(String url, String saveName, String savePath) {
        mView = getView();
        if (TextUtils.isEmpty(url) && mView != null) {
            mView.loadLatestAppFail("下载地址为空");
            return;
        }

        if (TextUtils.isEmpty(saveName) && mView != null) {
            mView.loadLatestAppFail("未获取到保存文件的名称");
            return;
        }
        mUpdateDisposable = mRxDownload.download(url, saveName, savePath)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new ResourceObserver<DownloadStatus>() {

                    @Override
                    protected void onStart() {
                        super.onStart();
                        if (mView != null) {
                            mView.prepareLoadApp();
                        }
                    }

                    @Override
                    public void onNext(DownloadStatus status) {
                        if (mView != null) {
                            mView.showLoadProgress(status);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.loadLatestAppFail(e.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (mView != null) {
                            mView.loadComplete();
                        }
                    }
                });
    }

    @Override
    public void pauseLoadApp() {
        if (mUpdateDisposable != null && !mUpdateDisposable.isDisposed()) {
            mUpdateDisposable.dispose();
        }
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mUpdateDisposable != null && !mUpdateDisposable.isDisposed()) {
            mUpdateDisposable.dispose();
        }
    }

    /**
     * 生成下载任务。按照分页下载，每一页数据下载就是一个任务
     *
     * @param queryType
     * @param totalCount
     */
    private LinkedList<LoadDataTask> addTask(String queryType, int totalCount, boolean isByPage) {

        LinkedList<LoadDataTask> tasks = new LinkedList<>();
        if (!isByPage) {
            tasks.addLast(new LoadDataTask(++mTaskId, queryType, null, 0, 0, false, true));
        }

        if (totalCount == 0)
            return tasks;
        int count = totalCount / Global.MAX_PATCH_LENGTH;
        int residual = totalCount % Global.MAX_PATCH_LENGTH;
        int ptr = 0;
        if (count == 0) {
            // 说明数据长度小于PATCH_MAX_LENGTH，直接写入即可
            tasks.addLast(new LoadDataTask(++mTaskId, queryType, "getPage", 1
                    , totalCount, totalCount, 1, true, true));
        } else if (count > 0) {
            for (; ptr < count; ptr++) {
                tasks.addLast(new LoadDataTask(++mTaskId, queryType, "getPage",
                        Global.MAX_PATCH_LENGTH * ptr + 1, Global.MAX_PATCH_LENGTH * (ptr + 1)
                        , Global.MAX_PATCH_LENGTH, ptr + 1, true, ptr == 0 ? true : false));
            }
            if (residual > 0) {
                // 说明还有剩余的数据
                tasks.addLast(new LoadDataTask(++mTaskId, queryType, "getPage",
                        Global.MAX_PATCH_LENGTH * ptr + 1, Global.MAX_PATCH_LENGTH * ptr + residual
                        , Global.MAX_PATCH_LENGTH, ptr + 1, true, false));
            }
        }
        return tasks;
    }
}
