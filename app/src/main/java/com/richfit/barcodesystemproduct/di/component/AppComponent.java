package com.richfit.barcodesystemproduct.di.component;

import android.content.Context;

import com.richfit.barcodesystemproduct.di.ContextLife;
import com.richfit.barcodesystemproduct.di.module.AppModule;
import com.richfit.common_lib.rxutils.RxManager;
import com.richfit.common_lib.rxutils.SimpleRxBus;
import com.richfit.data.repository.Repository;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by monday on 2016/10/18.
 */
@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    @ContextLife("Application")
    Context getContext();

    Repository getRepository();

    RxManager getRxManager();

    SimpleRxBus getSimpleRxBus();
}
