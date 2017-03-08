package com.richfit.barcodesystemproduct.di.module;

import android.app.Service;
import android.content.Context;

import com.richfit.barcodesystemproduct.di.scope.ContextLife;
import com.richfit.barcodesystemproduct.di.scope.ServiceScope;

import dagger.Module;
import dagger.Provides;

@Module
public class ServiceModule {
    private Service mService;

    public ServiceModule(Service service) {
        mService = service;
    }

    @Provides
    @ServiceScope
    @ContextLife("Service")
    public Context provideContext() {
        return mService;
    }

}