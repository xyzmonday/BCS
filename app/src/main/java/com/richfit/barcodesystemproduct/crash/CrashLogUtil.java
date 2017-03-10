package com.richfit.barcodesystemproduct.crash;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import com.richfit.barcodesystemproduct.SampleApplicationLike;
import com.richfit.common_lib.utils.L;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.richfit.barcodesystemproduct.tinker.util.SampleApplicationContext.context;

/**
 * Created by monday on 2016/4/8.
 */
public class CrashLogUtil {

    /**
     * 异常信息的更目录
     */
    private static final String ROOT_DIR = "crash";

    /**
     * 获取异常信息保存文件
     */
    public static File getCrashLogFile(Context context, String fileName) {
        String cacheDir;
        StringBuilder sb = new StringBuilder();
        if (context == null) {
            try {
                cacheDir = "storage/emulated/0/Android/data/tinker.sample.android/cache/crash";
                File file = new File(cacheDir, fileName);
                if (!file.exists()) {
                    file.mkdirs();
                }
                return file;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        if (isAvailable()) {
            cacheDir = context.getExternalCacheDir().getAbsolutePath();//sdcard/android/data/appname/cache
        } else {
            cacheDir = context.getCacheDir().getAbsolutePath();
        }
        sb.append(cacheDir);
        sb.append(File.separator);
        sb.append(ROOT_DIR);

        File dir = new File(sb.toString());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File crashFile = new File(dir, fileName);
        if (!crashFile.exists()) {
            try {
                crashFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        L.e("崩溃日志的路径 = " + crashFile.getAbsolutePath());
        return crashFile;
    }

    public static File getCrashLogFileDir() {
        String cacheDir;
        StringBuilder sb = new StringBuilder();
        if (context == null) {
            try {
                cacheDir = "storage/emulated/0/Android/data/tinker.sample.android/cache/crash";
                File file = new File(cacheDir);
                if (!file.exists()) {
                    file.mkdirs();
                }
                return file;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        if (isAvailable()) {
            cacheDir = context.getExternalCacheDir().getAbsolutePath();//sdcard/android/data/appname/cache
        } else {
            cacheDir = context.getCacheDir().getAbsolutePath();
        }
        sb.append(cacheDir);
        sb.append(File.separator);
        sb.append(ROOT_DIR);

        File dir = new File(sb.toString());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        L.e("崩溃日志的缓存目录 = " + dir.getAbsolutePath());
        return dir;
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     * If a deletion fails, the method stops attempting to
     * delete and returns "false".
     */
    public static boolean deleteCashLogDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteCashLogDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    /**
     * 判断SD是否存在
     */
    public static boolean isAvailable() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || !Environment.isExternalStorageRemovable()) {
            return true;
        } else {
            return false;
        }
    }


    public static void writeCrashLog(File crashFile, Throwable ex) {
        if (crashFile == null) {
            L.d("SD不可用，不能够保存异常信息日志");
            return;
        }
        if (crashFile == null) {
            L.d("日志文件为空");
            return;
        }
        if (!crashFile.isFile()) {
            L.d("为找到日志文件");
            return;
        }
        //保存崩溃信息
        FileWriter fw = null;
        BufferedWriter bw = null;
        PrintWriter pw = null;
        String time = CrashLogUtil.getCurrentTime("yyyy-MM-dd HHmmss");
        String message = ex.getMessage();
        synchronized (crashFile) {
            try {
                fw = new FileWriter(crashFile);
                bw = new BufferedWriter(fw);
                //导出手机信息
                pw = new PrintWriter(fw);
                dumpPhoneInfo(pw);
                bw.append(time).append(" ").append("E").append('/').append(" ")
                        .append(message).append('\n');
                bw.flush();
                //保存异常的栈信息
                ex.printStackTrace(pw);
                pw.flush();
                fw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeIO(fw);
                closeIO(bw);
                closeIO(pw);
            }
        }
    }

    /**
     * 导出手机的基本信息
     *
     * @param pw
     */
    private static void dumpPhoneInfo(PrintWriter pw) {
        //应用的版本名称和版本号
        PackageManager packageManager = SampleApplicationLike.getAppContext().getApplication().getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), packageManager.GET_ACTIVITIES);
            pw.print("App Version: ");
            pw.print(packageInfo.versionName);
            pw.print("_");
            pw.println(packageInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //android版本号
        pw.print("OS Version: ");
        pw.print(Build.VERSION.RELEASE);
        pw.print("_");
        pw.println(Build.VERSION.SDK_INT);

        //手机制造商
        pw.print("Vendor: ");
        pw.println(Build.MANUFACTURER);

        //手机型号
        pw.print("Model: ");
        pw.println(Build.MODEL);

        //cpu架构
        pw.print("CPU ABI: ");
        pw.println(Build.CPU_ABI);

    }

    public static void closeIO(Closeable close) {
        if (close != null) {
            try {
                close.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getCurrentTime(String format) {
        long current = System.currentTimeMillis();
        return new SimpleDateFormat(format).format(new Date(current));
    }
}
