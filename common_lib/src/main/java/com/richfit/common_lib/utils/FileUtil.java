package com.richfit.common_lib.utils;

/**
 * Created by monday on 2016/1/26.
 */

import android.content.Context;
import android.os.Environment;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;


public class FileUtil {

    private static final String INSPECTION_IMAGE_CACHE_ROOT = "inspectionCacheImage";
    private static final String LOCAL_INSPECTION_IMAGE_CACHE_ROOT = "inspectionCacheLocalImage";
    private static final String APK_CACHE_ROOT = "BarcodeSystemApk";

    /**
     * 获取缓存目录路径
     * ！注意：若不存在，则返回null
     *
     * @param context
     * @return 返回:/storage/sdcard0/Android/data/you_packageName/cache/
     */
    public static File getStorageCacheDir(Context context) {
        if (!isAvailable()) {
            return getDataCacheDir(context);
        }

        File path = Environment.getExternalStorageDirectory();
        if (path == null) {
            return getDataCacheDir(context);
        }

        return new File(path.getAbsolutePath() + File.separator + "Android" + File.separator + "data" + File.separator
                + context.getPackageName() + File.separator + "cache" + File.separator);
    }

    static File getDataCacheDir(Context context) {
        return context.getCacheDir();
    }


    /**
     * 获取apk缓存的路径
     */
    public static String getApkCacheDir(Context context) {
        File cacheDir = null;
        StringBuilder sb = new StringBuilder();
        if (isAvailable()) {
            //外部存储
            sb.append(context.getExternalCacheDir().getAbsolutePath());//sdcard/android/data/appname/cache
            sb.append(File.separator);
            sb.append(APK_CACHE_ROOT);
        } else {
            //内部存储
            sb.append(context.getCacheDir().getAbsolutePath());//data/data/appname/cache
            sb.append(File.separator);
            sb.append(APK_CACHE_ROOT);
        }
        cacheDir = new File(sb.toString());
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        return sb.toString();
    }

    /**
     * 获取图片缓存目录。图片的缓存路径是sd/refNum/refLineNum/type/
     * 所以获取该缓存目录的目的是获取该行特定拍照类型的所有缓存的图片。
     *
     * @param refNum:参考单号
     * @param refLineNum:参考单号的行号
     * @param takPhotoType:拍照类型
     * @return
     */
    public static File getImageCacheDir(Context context, final String refNum, final String refLineNum, final int takPhotoType,
                                        final boolean isLocal) {
        File cacheDir = null;
        StringBuilder sb = new StringBuilder();
        if (isAvailable()) {
            sb.append(context.getExternalCacheDir().getAbsolutePath());//sdcard/android/data/appname/cache
            sb.append(File.separator);
            sb.append(isLocal ? LOCAL_INSPECTION_IMAGE_CACHE_ROOT : INSPECTION_IMAGE_CACHE_ROOT);
        } else {
            sb.append(context.getCacheDir().getAbsolutePath());//data/data/appname/cache
            sb.append(File.separator);
            sb.append(isLocal ? LOCAL_INSPECTION_IMAGE_CACHE_ROOT : INSPECTION_IMAGE_CACHE_ROOT);
        }
        sb.append(File.separator);
        sb.append(refNum);
        sb.append(File.separator);
        sb.append(refLineNum);
        sb.append(File.separator);
        sb.append(String.valueOf(takPhotoType));
        cacheDir = new File(sb.toString());
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        return cacheDir;
    }

    /**
     * 获取该行的所有缓存图片的路径。
     * 用于数据明细删除某一行的所有图片
     *
     * @param refNum
     * @param refLineNum
     * @return
     */
    public static File getImageCacheDir(Context context, final String refNum, final String refLineNum,
                                        final boolean isLocal) {
        File cacheDir = null;
        StringBuilder sb = new StringBuilder();
        if (isAvailable()) {
            sb.append(context.getExternalCacheDir().getAbsolutePath());//sdcard/android/data/appname/cache
            sb.append(File.separator);
            sb.append(isLocal ? LOCAL_INSPECTION_IMAGE_CACHE_ROOT : INSPECTION_IMAGE_CACHE_ROOT);
        } else {
            sb.append(context.getCacheDir().getAbsolutePath());//data/data/appname/cache
            sb.append(File.separator);
            sb.append(isLocal ? LOCAL_INSPECTION_IMAGE_CACHE_ROOT : INSPECTION_IMAGE_CACHE_ROOT);
        }
        sb.append(File.separator);
        sb.append(refNum);
        sb.append(File.separator);
        sb.append(refLineNum);
        cacheDir = new File(sb.toString());
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        return cacheDir;
    }

    public static File getImageCacheDir(Context context, String refNum, int takePhotoType,
                                        final boolean isLocal) {
        File cacheDir = null;
        StringBuilder sb = new StringBuilder();
        if (isAvailable()) {
            sb.append(context.getExternalCacheDir().getAbsolutePath());//sdcard/android/data/appname/cache
            sb.append(File.separator);
            sb.append(isLocal ? LOCAL_INSPECTION_IMAGE_CACHE_ROOT : INSPECTION_IMAGE_CACHE_ROOT);
        } else {
            sb.append(context.getCacheDir().getAbsolutePath());//data/data/appname/cache
            sb.append(File.separator);
            sb.append(isLocal ? LOCAL_INSPECTION_IMAGE_CACHE_ROOT : INSPECTION_IMAGE_CACHE_ROOT);
        }
        sb.append(File.separator);
        sb.append(refNum);
        sb.append(File.separator);
        sb.append(String.valueOf(takePhotoType));
        cacheDir = new File(sb.toString());
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        return cacheDir;
    }

    /**
     * 删除整单缓存图片
     *
     * @param refNum：单据号
     * @return
     */
    public static File getImageCacheDir(Context context, final String refNum, final boolean isLocal) {
        File cacheDir = null;
        StringBuilder sb = new StringBuilder();
        if (isAvailable()) {
            sb.append(context.getExternalCacheDir().getAbsolutePath());//sdcard/android/data/appname/cache
            sb.append(File.separator);
            sb.append(isLocal ? LOCAL_INSPECTION_IMAGE_CACHE_ROOT : INSPECTION_IMAGE_CACHE_ROOT);
        } else {
            sb.append(context.getCacheDir().getAbsolutePath());//data/data/appname/cache
            sb.append(File.separator);
            sb.append(isLocal ? LOCAL_INSPECTION_IMAGE_CACHE_ROOT : INSPECTION_IMAGE_CACHE_ROOT);
        }
        sb.append(File.separator);
        sb.append(refNum);
        sb.append(File.separator);
        cacheDir = new File(sb.toString());
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        return cacheDir;
    }

    /**
     * 判断SD是否存在
     */
    public static boolean isAvailable() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ||
                !Environment.isExternalStorageRemovable()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除某目录下的图片
     *
     * @param imageDir
     * @param fileName
     * @return
     */
    public static boolean deleteImage(String imageDir, String fileName) {
        boolean isSuccess = false;
        try {
            File file = new File(imageDir + File.separator + fileName);
            if (!file.exists()) {
                return false;
            }
            isSuccess = file.delete();
        } catch (Exception e) {
            e.printStackTrace();
            return isSuccess;
        }
        return isSuccess;
    }


    /**
     * 删除空目录
     *
     * @param dir 将要删除的目录路径
     */
    public static void doDeleteEmptyDir(String dir) {
        boolean success = (new File(dir)).delete();
        if (success) {
            System.out.println("删除空目录成功: " + dir);
        } else {
            System.out.println("删除空目录失败" + dir);
        }
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dir 将要删除的文件目录
     * @return 返回true说明删除成功。
     */
    public static boolean deleteDir(File dir) {
        try {
            if (dir.isDirectory()) {
                String[] children = dir.list();
                //递归删除目录中的子目录下
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }
            // 目录此时为空，可以删除
            return dir.delete();
        } catch (Exception e) {
            e.printStackTrace();
            L.e("删除SD的图片 = " + e.getMessage());
        }
        return false;
    }

    public static void close(Closeable close) {
        if (close != null) {
            try {
                closeThrowException(close);
            } catch (IOException ignored) {
            }
        }
    }

    public static void closeThrowException(Closeable close) throws IOException {
        if (close != null) {
            close.close();
        }
    }

}