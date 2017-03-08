package com.richfit.barcodesystemproduct.di.module;

import android.app.Activity;
import android.content.Context;

import com.richfit.barcodesystemproduct.di.scope.ActivityScope;
import com.richfit.barcodesystemproduct.di.scope.ContextLife;

import dagger.Module;
import dagger.Provides;

/**
 * Created by monday on 2016/10/18.
 */
@Module
public class ActivityModule {

    private Activity mActivity;

    public ActivityModule(Activity activity) {
        mActivity = activity;
    }

    @Provides
    @ActivityScope
    @ContextLife("Activity")
    public Context provideContext() {
        return mActivity;
    }

    @Provides
    @ActivityScope
    public Activity provideActivity() {
        return mActivity;
    }
}
