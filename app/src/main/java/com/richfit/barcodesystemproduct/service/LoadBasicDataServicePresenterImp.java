package com.richfit.barcodesystemproduct.service;

import android.content.Context;

import com.richfit.barcodesystemproduct.base.BasePresenter;
import com.richfit.barcodesystemproduct.di.ContextLife;
import com.richfit.common_lib.rxutils.RetryWhenNetworkException;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.L;
import com.richfit.domain.bean.LoadBasicDataWrapper;
import com.richfit.domain.bean.LoadDataTask;

import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
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
     * 下载基础数据的入口
     *
     * @param requestParam
     */
    @Override
    public void loadAndSaveBasicData(ArrayList<LoadBasicDataWrapper> requestParam) {
        mView = getView();
        ResourceSubscriber<Integer> subscriber = Flowable.fromIterable(requestParam)
                .concatMap(param -> mRepository.preparePageLoad(param))
                .concatMap(param -> Flowable.fromIterable(addTask(param.queryType, param.totalCount, param.isByPage)))
                .concatMap(task -> mRepository.loadBasicData(task))
                .onErrorResumeNext(new Function<Throwable, Publisher<? extends List<Map<String, Object>>>>() {
                    @Override
                    public Publisher<? extends List<Map<String, Object>>> apply(Throwable throwable) throws Exception {
                        final List<Map<String,Object>> list = new ArrayList<>();
                        final Map<String, Object> tmp = new HashMap<>();
                        tmp.put("queryType","error");
                        list.add(tmp);
                        return Flowable.just(list);
                    }
                })
                .map(sourceMap -> mRepository.saveBasicData(sourceMap))
                .retryWhen(new RetryWhenNetworkException(3, 2000))
                .compose(TransformerHelper.io2main())
                .subscribeWith(new ResourceSubscriber<Integer>() {
                    @Override
                    public void onNext(Integer integer) {
                        L.d("percent = " + integer + "; thread name = " + Thread.currentThread().getName());
                    }

                    @Override
                    public void onError(Throwable t) {
                        L.d("error = " + t.getMessage());
                        if (mView != null) {
                            mRxManager.post(Global.LOAD_BASIC_DATA_COMPLETE, true);
                            mView.loadBasicDataComplete();
                            mTaskId = 0;
                        }
                    }

                    @Override
                    public void onComplete() {
                        L.e("基础数据下载结束");
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
            tasks.addLast(new LoadDataTask(++mTaskId, queryType, "getPage", 1
                    , totalCount, ptr,Global.MAX_PATCH_LENGTH,true, true));
        } else if (count > 0) {
            for (; ptr < count; ptr++) {
                tasks.addLast(new LoadDataTask(++mTaskId, queryType, "getPage",
                        Global.MAX_PATCH_LENGTH * ptr + 1, Global.MAX_PATCH_LENGTH * (ptr + 1),
                        ptr,Global.MAX_PATCH_LENGTH,true, ptr == 0 ? true : false));
            }
            if (residual > 0) {
                // 说明还有剩余的数据
                tasks.addLast(new LoadDataTask(++mTaskId, queryType, "getPage",
                        Global.MAX_PATCH_LENGTH * ptr + 1, Global.MAX_PATCH_LENGTH * ptr + residual,
                        ptr,Global.MAX_PATCH_LENGTH,true, false));
            }
        }
        return tasks;
    }

}
