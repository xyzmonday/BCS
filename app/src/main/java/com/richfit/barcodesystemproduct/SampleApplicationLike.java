/*
 * Tencent is pleased to support the open source community by making Tinker available.
 *
 * Copyright (C) 2016 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.richfit.barcodesystemproduct;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.multidex.MultiDex;

import com.github.moduth.blockcanary.BlockCanary;
import com.richfit.barcodesystemproduct.di.component.AppComponent;
import com.richfit.barcodesystemproduct.di.component.DaggerAppComponent;
import com.richfit.barcodesystemproduct.di.module.AppModule;
import com.richfit.barcodesystemproduct.service.InitializeService;
import com.richfit.barcodesystemproduct.tinker.Log.MyLogImp;
import com.richfit.barcodesystemproduct.tinker.util.SampleApplicationContext;
import com.richfit.barcodesystemproduct.tinker.util.TinkerManager;
import com.richfit.common_lib.blockcanary.AppBlockCanaryContext;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.loader.app.DefaultApplicationLike;
import com.tencent.tinker.loader.shareutil.ShareConstants;


@SuppressWarnings("unused")


@DefaultLifeCycle(application = "com.richfit.barcodesystemproduct.BarcodeSystemApplication",
        flags = ShareConstants.TINKER_ENABLE_ALL,
        loadVerifyFlag = false)
public class SampleApplicationLike extends DefaultApplicationLike {

    private static final String TAG = "yff";
    private static SampleApplicationLike app;
    private static AppComponent mAppComponent;
    private static RefWatcher mRefWatcher;

    public SampleApplicationLike(Application application, int tinkerFlags, boolean tinkerLoadVerifyFlag,
                                 long applicationStartElapsedTime, long applicationStartMillisTime, Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);
    }

    /**
     * install multiDex before install tinker
     * so we don't need to put the tinker lib classes in the main dex
     *
     * @param base
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);

        //you must install multiDex whatever tinker is installed!
        MultiDex.install(base);

        SampleApplicationContext.application = getApplication();
        SampleApplicationContext.context = getApplication();
        TinkerManager.setTinkerApplicationLike(this);

        TinkerManager.initFastCrashProtect();
        //should set before tinker is installed
        TinkerManager.setUpgradeRetryEnable(true);

        //optional set logIml, or you can use default debug log
        TinkerInstaller.setLogIml(new MyLogImp());

        //installTinker after load multiDex
        //or you can put com.tencent.tinker.** to main dex
        TinkerManager.installTinker(this);
        Tinker tinker = Tinker.with(getApplication());
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback) {
        getApplication().registerActivityLifecycleCallbacks(callback);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化自己的全局配置
        app = this;
        final Application application = getApplication();
        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(application))
                .build();
        mRefWatcher = LeakCanary.install(application);
        BlockCanary.install(application, new AppBlockCanaryContext()).start();
        InitializeService.start(application);
    }

    public static SampleApplicationLike getAppContext() {
        return app;
    }

    public static AppComponent getAppComponent() {
        return mAppComponent;
    }

    public static RefWatcher getRefWatcher() {
        return mRefWatcher;
    }


}
