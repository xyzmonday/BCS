package com.richfit.barcodesystemproduct.crash;

import java.io.File;

/**
 * 异常接口
 * Created by monday on 2016/4/8.
 */
public interface CrashListener {
    /**
     * 将崩溃日志传到目标层。比如服务器。
     * @param logFile
     */
    void sendLogFileToTarget(Thread thread, Throwable ex,File logFile);

    /**
     * 响应系统崩溃，关闭app
     * @param thread
     * @param ex
     */
    void closeApp(Thread thread, Throwable ex);
}
