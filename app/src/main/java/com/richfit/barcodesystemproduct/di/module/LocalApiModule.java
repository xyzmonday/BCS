package com.richfit.barcodesystemproduct.di.module;

import android.content.Context;
import com.richfit.barcodesystemproduct.di.scope.ContextLife;
import com.richfit.data.db.ApprovalDao;
import com.richfit.data.db.CommonDao;
import com.richfit.data.repository.local.LocalRepositoryImp;
import com.richfit.domain.repository.ILocalRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by monday on 2016/11/9.
 */
@Module
public class LocalApiModule {

    @Provides
    @Singleton
    public CommonDao provideBCSDao(@ContextLife("Application") Context context) {
        return new CommonDao(context);
    }


    @Provides
    @Singleton
    public ApprovalDao provideApprovalDao(@ContextLife("Application") Context context) {
        return new ApprovalDao(context);
    }

    @Provides
    @Singleton
    public ILocalRepository provideLocalDataApi(CommonDao commonDao,ApprovalDao approvalDao) {
        return new LocalRepositoryImp(commonDao,approvalDao);
    }
}
