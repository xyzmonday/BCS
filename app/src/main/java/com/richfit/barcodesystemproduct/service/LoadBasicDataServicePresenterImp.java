package com.richfit.barcodesystemproduct.service;

import android.content.Context;

import com.richfit.barcodesystemproduct.base.BasePresenter;
import com.richfit.barcodesystemproduct.di.scope.ContextLife;
import com.richfit.common_lib.rxutils.RetryWhenNetworkException;
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
 * Created by monday on 2016/11/16.
 */

public class LoadBasicDataServicePresenterImp extends BasePresenter<ILoadBasicDataServiceView>
        implements ILoadBasicDataServicePresenter {

    ILoadBasicDataServiceView mView;

    private int mTaskId = 0;

    @Inject
    public LoadBasicDataServicePresenterImp(@ContextLife("Service") Context context) {
        super(context);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mTaskId = 0;
    }

    /**
     * 下载基础数据的入口。
     *  注意这里由于系统必须去下载两种类型的ZZ的基础数据，
     *  然而二级单位的ZZ基础数据又比较特别(仅仅针对某些地区公司存在)，
     *  所以需要拦击错误，不管有没有都必须执行完所有的任务。
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
                            mRxManager.post(Global.LOAD_BASIC_DATA_COMPLETE, true);
                            mView.loadBasicDataComplete();
                            mTaskId = 0;
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (mView != null) {
                            mRxManager.post(Global.LOAD_BASIC_DATA_COMPLETE, true);
                            mView.loadBasicDataComplete();
                            mTaskId = 0;
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
                    , totalCount, ptr,Global.MAX_PATCH_LENGTH,true, true));
        } else if (count > 0) {
            for (; ptr < count; ptr++) {
                tasks.addLast(new LoadDataTask(++mTaskId, queryType, "queryPage",
                        Global.MAX_PATCH_LENGTH * ptr + 1, Global.MAX_PATCH_LENGTH * (ptr + 1),
                        ptr,Global.MAX_PATCH_LENGTH,true, ptr == 0 ? true : false));
            }
            if (residual > 0) {
                // 说明还有剩余的数据
                tasks.addLast(new LoadDataTask(++mTaskId, queryType, "queryPage",
                        Global.MAX_PATCH_LENGTH * ptr + 1, Global.MAX_PATCH_LENGTH * ptr + residual,
                        ptr,Global.MAX_PATCH_LENGTH,true, false));
            }
        }
        return tasks;
    }

}
