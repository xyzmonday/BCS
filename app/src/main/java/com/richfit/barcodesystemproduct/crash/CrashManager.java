package com.richfit.barcodesystemproduct.crash;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;

/**
 * Created by monday on 2016/4/8.
 */
public class CrashManager {

    public static final String FILE_NAME = "bcs_crash";
    /**
     * log文件的后缀名
     */
    public static final String FILE_NAME_SUFFIX = ".txt";

    private String mLogFileName;


    private CrashManager() {
    }


    private static CrashManager instance;

    public static CrashManager getInstance() {
        if (instance == null) {
            synchronized (CrashManager.class) {
                if (instance == null) {
                    instance = new CrashManager();
                }
            }
        }
        return instance;
    }


    public void init(Context context,BaseCrashReport crashReport) {
        //初始化CrashHandler
        if (TextUtils.isEmpty(mLogFileName)) {
            mLogFileName = getDefaultLogFileName();
        }
        File logFile = getCrashLogFile(context,mLogFileName);
        CrashHandler.getInstance().init(logFile, crashReport);
    }

    /**
     * 如果用户没有设置本地崩溃日志的文件名，那么
     * 通过该方法获取一个日志文件名
     * @return
     */
    public String getDefaultLogFileName() {
        String time = CrashLogUtil.getCurrentTime("yyyy-MM-dd HHmmss");
        return FILE_NAME + "_" + time + FILE_NAME_SUFFIX;
    }

    /**
     * 获取SD卡的崩溃日志文件
     * @param context
     * @param logFileName
     * @return
     */
    public File getCrashLogFile(Context context,String logFileName) {
        return CrashLogUtil.getCrashLogFile(context,logFileName);
    }
}
