package com.richfit.barcodesystemproduct.crash;

import java.io.File;

/**
 * 收集程序Crash的日志信息，将该信息保存到手机的sd卡
 * 或者上传到服务器当中，以便后续的处理和分析
 * Created by monday on 2016/4/8.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    /**
     * 异常处理回到接口
     */
    private CrashListener mListener;
    /**
     * 保存异常日期到sd本地的文件
     */
    private File mLogFile;

    /**
     * 单例模式
     */
    private static CrashHandler instance;

    private CrashHandler() {
    }

    //采用DCL
    public static CrashHandler getInstance() {
        if (instance == null) {
            synchronized (CrashHandler.class) {
                if (instance == null) {
                    instance = new CrashHandler();
                }
            }
        }
        return instance;
    }


    /**
     * 初始化相关的参数
     */
    public void init(File logFile, CrashListener listener) {
        AssertUtil.assertNotNull("logFileName", logFile);
        AssertUtil.assertNotNull("crashListener", listener);
        this.mLogFile = logFile;
        this.mListener = listener;
        //系统自己处理崩溃
        Thread.setDefaultUncaughtExceptionHandler(this);
    }


    /**
     * 程序崩溃后的的异常捕获
     *
     * @param thread
     * @param ex
     */
    @Override
    public void uncaughtException(final Thread thread, final Throwable ex) {
        try {
            //保存到本地
            CrashLogUtil.writeCrashLog(mLogFile, ex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //打印出当前调用栈信息
        ex.printStackTrace();
        //保存到服务器或者邮件
        if (mListener != null) {
            mListener.closeApp(thread, ex);
//            mListener.sendLogFileToTarget(thread, ex, mLogFile);
        }

    }
}
