package com.richfit.barcodesystemproduct.module.main;

/**
 * Created by monday on 2016/11/10.
 */

public class MainActivity extends BaseMainActivity<MainPresenterImp> {
    @Override
    public void initInjector() {
        mActivityComponent.inject(this);
    }
}
