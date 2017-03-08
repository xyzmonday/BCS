package com.richfit.barcodesystemproduct.di.module;

import android.app.Application;
import android.content.Context;

import com.richfit.barcodesystemproduct.di.scope.ContextLife;
import com.richfit.common_lib.rxutils.RxManager;
import com.richfit.common_lib.rxutils.SimpleRxBus;
import com.richfit.data.repository.Repository;
import com.richfit.domain.repository.ILocalRepository;
import com.richfit.domain.repository.IServerRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by monday on 2016/10/18.
 */

@Module(includes = {LocalApiModule.class, ServerApiModule.class})
public class AppModule {

    private Application mApp;

    public AppModule(Application app) {
        this.mApp = app;
    }

    @Provides
    @Singleton
    @ContextLife("Application")
    public Context provideContext() {
        return mApp.getApplicationContext();
    }


    @Provides
    @Singleton
    public RxManager provideRxManager() {
        return RxManager.getInstance();
    }

    @Provides
    @Singleton
    public SimpleRxBus provideSimpleRxBus() {
        return SimpleRxBus.getInstance();
    }

    @Provides
    @Singleton
    public Repository provideRepository(IServerRepository serverDataApi, ILocalRepository localDataApi) {
        return new Repository(serverDataApi, localDataApi);
    }
}
