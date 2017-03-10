package com.richfit.barcodesystemproduct.crash;

import com.richfit.barcodesystemproduct.SampleApplicationLike;
import com.richfit.data.repository.Repository;

import javax.inject.Inject;

/**
 * Created by monday on 2016/4/8.
 * 异常处理基类
 */
public abstract class BaseCrashReport implements CrashListener {

    @Inject
    protected Repository mRepository;

    /**
     * 系统默认的异常处理（默认情况下，系统会终止当前的异常程序）
     */
    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;

    public BaseCrashReport() {
        SampleApplicationLike.getAppComponent().inject(this);
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    /**
     * 关闭app
     * @param thread
     * @param ex
     */
    @Override
    public void closeApp(Thread thread, Throwable ex) {
        mDefaultCrashHandler.uncaughtException(thread, ex);
    }
}
