package com.richfit.barcodesystemproduct.module.home;

import android.content.Context;
import android.text.TextUtils;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.base.BasePresenter;
import com.richfit.barcodesystemproduct.di.scope.ContextLife;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.MenuTreeHelper;
import com.richfit.domain.bean.MenuNode;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import io.reactivex.subscribers.ResourceSubscriber;


/**
 * Created by monday on 2016/11/7.
 */

public class HomePresenterImp extends BasePresenter<HomeContract.View>
        implements HomeContract.Presenter {


    HomeContract.View mView;

    @Override
    protected void onStart() {
        mView = getView();
    }

    @Inject
    public HomePresenterImp(@ContextLife("Activity") Context context) {
        super(context);
    }

    @Override
    public void setupModule() {
        mView = getView();
        Disposable subscriber =
                mRepository.getMenuTreeInfo(Global.LOGIN_ID,Global.ONLINE_MODE)
                        .filter(listMenu -> listMenu != null && listMenu.size() > 0)
                        .map(listMenu -> initMenu(listMenu))
                        .compose(TransformerHelper.io2main())
                        .subscribeWith(new ResourceSubscriber<List<MenuNode>>() {

                            @Override
                            public void onNext(List<MenuNode> menuNodes) {
                                if (mView != null) {
                                    mView.initModulesComplete(menuNodes);
                                }
                            }

                            @Override
                            public void onError(Throwable t) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });
        addSubscriber(subscriber);
    }

    private List<MenuNode> initMenu(List<MenuNode> menuNodes) {

        final String rootId = menuNodes.get(0).getId();
        MenuTreeHelper.convertDatas2Nodes(menuNodes);
        //生成icon
        for (MenuNode sortedNode : menuNodes) {
            if (!sortedNode.getParentId().equals(rootId))
                continue;
            sortedNode.setIcon(createModuleIcon(sortedNode.getFunctionCode()));
        }
        return menuNodes;
    }

    private int createModuleIcon(String moduleCode) {
        if (TextUtils.isEmpty(moduleCode))
            return 0;
        switch (moduleCode) {
            case Global.WZYS:
                return R.mipmap.icon_module1;
            case Global.WZRK:
                return R.mipmap.icon_module2;
            case Global.WZCK:
                return R.mipmap.icon_module3;
            case Global.WZTK:
                return R.mipmap.icon_module4;
            case Global.WZYK:
                return R.mipmap.icon_module5;
            case Global.WZTH:
                return R.mipmap.icon_module6;
            case Global.WZPD:
                return R.mipmap.icon_module7;
            case Global.CWTZ:
                return R.mipmap.icon_module8;
            case Global.XXCX:
                return R.mipmap.icon_module11;
            case Global.DGRK:
                return R.mipmap.icon_module14;
            case Global.DGCK:
                return R.mipmap.icon_module12;
            case Global.SETTING:
                return R.mipmap.icon_module13;
            case Global.DGYK:
                return R.mipmap.icon_module14;
        }
        return 0;
    }
}
