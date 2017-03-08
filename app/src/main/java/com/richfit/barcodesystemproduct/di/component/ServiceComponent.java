package com.richfit.barcodesystemproduct.di.component;

import android.content.Context;

import com.richfit.barcodesystemproduct.di.scope.ContextLife;
import com.richfit.barcodesystemproduct.di.scope.ServiceScope;
import com.richfit.barcodesystemproduct.di.module.ServiceModule;
import com.richfit.barcodesystemproduct.service.LoadBasicDataService;

import dagger.Component;

@ServiceScope
@Component(dependencies = AppComponent.class, modules = {ServiceModule.class})
public interface ServiceComponent {

    @ContextLife("Service")
    Context getServiceContext();

    @ContextLife("Application")
    Context getApplicationContext();

    void inject(LoadBasicDataService service);

}