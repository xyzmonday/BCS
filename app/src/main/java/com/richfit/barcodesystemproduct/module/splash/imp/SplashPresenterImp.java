package com.richfit.barcodesystemproduct.module.splash.imp;

import android.content.Context;

import com.richfit.barcodesystemproduct.base.BasePresenter;
import com.richfit.barcodesystemproduct.di.scope.ContextLife;
import com.richfit.barcodesystemproduct.module.splash.ISplashPresenter;
import com.richfit.barcodesystemproduct.module.splash.ISplashView;
import com.richfit.common_lib.rxutils.RetryWhenNetworkException;
import com.richfit.common_lib.rxutils.RxSubscriber;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.Global;
import com.richfit.domain.bean.LoadBasicDataWrapper;
import com.richfit.domain.bean.LoadDataTask;

import java.util.ArrayList;
import java.util.LinkedList;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.subscribers.ResourceSubscriber;

/**
 * Created by monday on 2016/12/2.
 */

public class SplashPresenterImp extends BasePresenter<ISplashView>
        implements ISplashPresenter {

    ISplashView mView;
    private int mTaskId = 0;

    @Inject
    public SplashPresenterImp(@ContextLife("Activity") Context context) {
        super(context);
    }

    @Override
    public void onStart() {
        mTaskId = 0;
    }

    @Override
    public void syncDate() {
        mView = getView();
        addSubscriber(mRepository.syncDate()
                .compose(TransformerHelper.io2main())
                .subscribeWith(new ResourceSubscriber<String>() {
                    @Override
                    public void onNext(String s) {
                        if (mView != null) {
                            mView.syncDateSuccess(s);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        if (mView != null) {
                            mView.syncDateFail(t.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (mView != null) {
                            mView.syncDateComplete();
                        }
                    }
                }));
    }

    @Override
    public void register() {
        mView = getView();
        ResourceSubscriber<String> subscriber =
                mRepository.getMappingInfo()
                        .compose(TransformerHelper.io2main())
                        .subscribeWith(new RxSubscriber<String>(mContext, "正在初始化系统...") {
                            @Override
                            public void _onNext(String s) {

                            }

                            @Override
                            public void _onNetWorkConnectError(String message) {
                                if (mView != null) {
                                    mView.networkConnectError(Global.RETRY_REGISTER_ACTION);
                                }
                            }

                            @Override
                            public void _onCommonError(String message) {
                                if (mView != null) {
                                    mView.unRegister(message);
                                }
                            }

                            @Override
                            public void _onServerError(String code, String message) {
                                if (mView != null) {
                                    mView.unRegister(message);
                                }
                            }

                            @Override
                            public void _onComplete() {
                                if (mView != null) {
                                    mView.registered();
                                }
                            }
                        });
        addSubscriber(subscriber);
    }

    /**
     * 下载基础数据的入口。
     * 注意这里由于系统必须去下载两种类型的ZZ的基础数据，
     * 然而二级单位的ZZ基础数据又比较特别(仅仅针对某些地区公司存在)，
     * 所以需要拦击错误，不管有没有都必须执行完所有的任务。
     *
     * @param requestParam
     */
    @Override
    public void loadAndSaveBasicData(ArrayList<LoadBasicDataWrapper> requestParam) {
        mView = getView();
        ResourceSubscriber<Integer> subscriber = Flowable.fromIterable(requestParam)
                .concatMap(param -> mRepository.preparePageLoad(param))
                .concatMap(param -> Flowable.fromIterable(addTask(param.queryType, param.totalCount, param.isByPage)))
                .concatMapDelayError(task -> mRepository.loadBasicData(task))
//                .onErrorResumeNext(throwable -> {
//                    final List<Map<String,Object>> list = new ArrayList<>();
//                    final Map<String, Object> tmp = new HashMap<>();
//                    tmp.put("queryType","error");
//                    list.add(tmp);
//                    return Flowable.just(list);
//                })
                .flatMap(sourceMap -> mRepository.saveBasicData(sourceMap))
//                .onErrorResumeNext(throwable -> {
//                    return Flowable.just(-1);
//                })
                .retryWhen(new RetryWhenNetworkException(3, 2000))
                .compose(TransformerHelper.io2main())
                .subscribeWith(new ResourceSubscriber<Integer>() {
                    @Override
                    public void onNext(Integer integer) {
                    }

                    @Override
                    public void onError(Throwable t) {
                        if (mView != null) {
                            mTaskId = 0;
                            mView.toLogin();
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (mView != null) {
                            mTaskId = 0;
                            mView.toLogin();
                        }
                    }
                });
        addSubscriber(subscriber);
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
            tasks.addLast(new LoadDataTask(++mTaskId, queryType, "queryPage", 1
                    , totalCount, ptr, Global.MAX_PATCH_LENGTH, true, true));
        } else if (count > 0) {
            for (; ptr < count; ptr++) {
                tasks.addLast(new LoadDataTask(++mTaskId, queryType, "queryPage",
                        Global.MAX_PATCH_LENGTH * ptr + 1, Global.MAX_PATCH_LENGTH * (ptr + 1),
                        ptr, Global.MAX_PATCH_LENGTH, true, ptr == 0 ? true : false));
            }
            if (residual > 0) {
                // 说明还有剩余的数据
                tasks.addLast(new LoadDataTask(++mTaskId, queryType, "queryPage",
                        Global.MAX_PATCH_LENGTH * ptr + 1, Global.MAX_PATCH_LENGTH * ptr + residual,
                        ptr, Global.MAX_PATCH_LENGTH, true, false));
            }
        }
        return tasks;
    }
}
