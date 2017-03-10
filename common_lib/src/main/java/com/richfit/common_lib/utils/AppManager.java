package com.richfit.common_lib.utils;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import java.util.Stack;

/**
 * 应用程序Activity管理类：用于Activity管理和应用程序退出
 *
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class AppManager {

    private static Stack<AppCompatActivity> activityStack = new Stack<>();

    /**
     * 添加Activity到堆栈
     */
    public static void addActivity(AppCompatActivity activity) {
        activityStack.push(activity);
    }

    public static void removeActivity(AppCompatActivity activity) {
        activityStack.remove(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public static AppCompatActivity currentActivity() {
        return activityStack.lastElement();
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public static void finishCurrentActivity() {
        AppCompatActivity activity = activityStack.pop();
        activity.finish();
    }

    /**
     * 结束指定的Activity
     */
    public static void finishActivity(AppCompatActivity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            if(!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public static void finishActivity(Class<?> cls) {
        for (AppCompatActivity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public static void finishAllActivity() {
        for (AppCompatActivity activity : activityStack) {
            if (activity != null) {
                activity.finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public static void AppExit(Context context) {
        try {
            finishAllActivity();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}