package com.richfit.barcodesystemproduct.base;

import android.content.Context;
import android.support.annotation.ArrayRes;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import com.richfit.common_lib.IInterface.IPresenter;
import com.richfit.common_lib.rxutils.RxManager;
import com.richfit.common_lib.rxutils.SimpleRxBus;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.Global;
import com.richfit.data.repository.Repository;
import com.richfit.domain.bean.RefDetailEntity;
import com.richfit.domain.bean.ReferenceEntity;
import com.richfit.domain.bean.RowConfig;
import com.richfit.domain.bean.SimpleEntity;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subscribers.ResourceSubscriber;

/**
 * presenter的基类，实现类IPresenter的方法.
 * 注意在BasePresenter的构造方法中使用了@Inject注解，
 * 所以凡是通过继承BasePresenter实现的IPresenter实例的构造
 * 方法必须传入相关的实例。
 * 在Activity和Fragment中，由于ActivityComponent提供了activity和rxManager，
 * 所以只要将activityComponent或者fragmentComponent注入到相应
 * 的activity和fragment中。
 * Created by monday on 2016/9/30.
 */

public class BasePresenter<T extends BaseView> implements IPresenter<T> {

    /*presenter持有View层的实例引用。为了防止内存泄露采用弱应用的形式保存*/
    private Reference<T> mViewRef;

    protected Context mContext;

    private CompositeDisposable mCompositeDisposable;

    @Inject
    protected Repository mRepository;

    @Inject
    protected RxManager mRxManager;

    @Inject
    protected SimpleRxBus mSimpleRxBus;

    @Inject
    public BasePresenter(Context context) {
        this.mContext = context;
    }

    @Override
    public void attachView(T view) {
        this.mViewRef = new WeakReference<>(view);
        this.mCompositeDisposable = new CompositeDisposable();
        onStart();
    }

    protected void onStart() {

    }

    //解除view与presenter的绑定
    @Override
    public void detachView() {
        if (mViewRef != null) {
            mViewRef.clear();
            mViewRef = null;
        }
        mRxManager.unRegister();
        unSubscribe();
    }

    //获取View
    public T getView() {
        if (mViewRef != null && mViewRef.get() != null) {
            return mViewRef.get();
        }
        return null;
    }

    //判断View是否还与该Presenter绑定
    public boolean isAttached() {
        return mViewRef != null && mViewRef.get() != null;
    }

    /*添加订阅*/
    public void addSubscriber(Disposable disposable) {
        if(disposable != null) {
            mCompositeDisposable.add(disposable);
        }
    }

    /*取消订阅*/
    public void unSubscribe() {
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
        }
    }

    /*获取String资源*/
    protected String getString(@StringRes int res) {
        return mContext.getString(res);
    }

    public List<String> getStringArray(@ArrayRes int id) {
        return Arrays.asList(mContext.getResources().getStringArray(id));
    }

    /**
     * 针对父子节点结构展示的数据，为父节点增加树状结构的信息
     *
     * @param details
     */
    protected void addTreeInfo(final List<RefDetailEntity> details) {
        if (details == null || details.size() == 0)
            return;
        //配置节点信息
        int treeId = 1;
        int pTreeId = 0;//注意id从1开始，pid从0开始是为了保证一级父节点没有父节点
        for (RefDetailEntity detail : details) {
            detail.setViewType(Global.PARENT_NODE_HEADER_TYPE);
            detail.setTreeId(treeId++);
            detail.setpTreeId(pTreeId);
            detail.setHasChild(false);
        }
    }

    /**
     * 为父节点增加节点的信息,以便方便树形显示
     */
    protected ReferenceEntity addTreeInfo(final ReferenceEntity refData) {
        addTreeInfo(refData.billDetailList);
        return refData;
    }

    /**
     * 为子节点增加树形结构的信息（主要是增加子节点的父节点id）
     */
    protected void addTreeInfo(RefDetailEntity parentNode, RefDetailEntity childNode,
                               final ArrayList<RefDetailEntity> collectedList) {
        if (parentNode == null || childNode == null)
            return;
        //处理子节点
        //向确认该父节点是否已经有子节点，如果没有，那么添加一个子节点的头布局
        if (!parentNode.isHasChild()) {
            RefDetailEntity childHeader = new RefDetailEntity();
            childHeader.setViewType(Global.CHILD_NODE_HEADER_TYPE);
            childHeader.setTreeId(--Global.CHILD_NODE_MAX_ID);
            childHeader.setpTreeId(parentNode.getTreeId());
            collectedList.add(childHeader);
            parentNode.setHasChild(true);
        }
        //给子节点赋予节点信息
        childNode.setViewType(Global.CHILD_NODE_ITEM_TYPE);
        childNode.setTreeId(--Global.CHILD_NODE_MAX_ID);
        childNode.setpTreeId(parentNode.getTreeId());
        collectedList.add(childNode);
    }

    /**
     * 获取父节点的位置
     * @param refData:明细数据列表
     *               @param refLineId:需要查询的父节点的id
     */
    protected int getParentNodePosition(ReferenceEntity refData, String refLineId) {
        int pos = -1;
        if (TextUtils.isEmpty(refLineId)) {
            return pos;
        }
        List<RefDetailEntity> list = refData.billDetailList;
        if (list != null && list.size() > 0) {
            for (RefDetailEntity entity : list) {
                pos++;
                if (refLineId.equals(entity.refLineId))
                    return pos;
            }
        }
        return pos;
    }


    @Override
    public void readExtraConfigs(String companyId, String bizType,String refType, String... configTypes) {
        final T view = getView();

        if (view == null)
            return;

        if (TextUtils.isEmpty(companyId) || TextUtils.isEmpty(bizType)) {
            view.readConfigsFail("读取配置信息失败,请检查传入的参数!");
            return;
        }

        if (configTypes.length == 0) {
            view.readConfigsFail("请输入读取配置信息的类型");
            return;
        }

        ResourceSubscriber<List<ArrayList<RowConfig>>> subscriber =
                Flowable.fromArray(configTypes)
                .concatMap(configType -> mRepository.readExtraConfigInfo(companyId, bizType,refType, configType))
                .buffer(configTypes.length)
                .filter(configs -> configs.size() == configTypes.length)
                .compose(TransformerHelper.io2main())
                .subscribeWith(new ResourceSubscriber<List<ArrayList<RowConfig>>>() {
                    @Override
                    public void onNext(List<ArrayList<RowConfig>> configs) {
                        view.readConfigsSuccess(configs);
                    }

                    @Override
                    public void onError(Throwable t) {
                        view.readConfigsFail(t.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        view.readConfigsComplete();
                    }
                });
        addSubscriber(subscriber);
    }

    @Override
    public void readExtraDataSourceDictionary(List<RowConfig> configs) {
        final T view = getView();
        if (view == null)
            return;

        if (configs == null || configs.size() == 0)
            return;

        ResourceSubscriber<Map<String, Object>> subscriber =
                Flowable.fromIterable(configs)
                .filter(config -> !TextUtils.isEmpty(config.dataSource) &&
                        !TextUtils.isEmpty(config.propertyCode))
                .flatMap(config -> mRepository.readExtraDataSourceByDictionary(config.propertyCode, config.dataSource))
                .filter(extraMap -> extraMap != null && extraMap.size() > 0)
                .compose(TransformerHelper.io2main())
                .subscribeWith(new ResourceSubscriber<Map<String, Object>>() {
                    @Override
                    public void onNext(Map<String, Object> extraMap) {
                        view.readExtraDictionarySuccess(extraMap);
                    }

                    @Override
                    public void onError(Throwable t) {
                        view.readExtraDictionaryFail(t.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        view.readExtraDictionaryComplete();
                    }
                });
        addSubscriber(subscriber);
    }


    protected Flowable<RefDetailEntity> getMatchedLineData(String lineId, ReferenceEntity refData) {
        if (TextUtils.isEmpty(lineId))
            return Flowable.error(new Throwable("该行的行Id不存在,请检查该单据是否正确"));
        List<RefDetailEntity> list = refData.billDetailList;
        for (RefDetailEntity entity : list) {
            if (lineId.equals(entity.refLineId))
                return Flowable.just(entity);
        }
        return Flowable.error(new Throwable("未能找到与该行匹配的缓存"));
    }

    protected ArrayList<String> wrapper2Str(ArrayList<SimpleEntity> list) {
        ArrayList<String> strs = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        for (SimpleEntity entity : list) {
            sb.setLength(0);
            sb.append(entity.code).append("_").append(entity.name);
            strs.add(sb.toString());
        }
        return strs;
    }
}
