package com.richfit.common_lib.utils;

import android.os.SystemClock;

import com.orhanobut.logger.Logger;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class SystemDateTimeUtil {
      
    static final String TAG = "SystemDateTimeUtil";

    private final static int kSystemRootStateUnknow = -1;
    private final static int kSystemRootStateDisable = 0;
    private final static int kSystemRootStateEnable = 1;
    private static int systemRootState = kSystemRootStateUnknow;


    public static void timeSynchronization(String date) {
        ArrayList<String> envlist = new ArrayList<>();
        Map<String, String> env = System.getenv();
        for (String envName : env.keySet()) {
            envlist.add(envName + "=" + env.get(envName));
        }
        String[] envp = (String[]) envlist.toArray(new String[0]);
        String command;
        command = "date -s " + date;
        try {
            Runtime.getRuntime().exec(new String[] { "su", "-c", command }, envp);
        } catch (IOException e) {
            Logger.d("修改系统时间 = " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean isRootSystem() {
        if (systemRootState == kSystemRootStateEnable) {
            return true;
        } else if (systemRootState == kSystemRootStateDisable) {
            return false;
        }
        File f = null;
        final String kSuSearchPaths[] = { "/system/bin/", "/system/xbin/",
                "/system/sbin/", "/sbin/", "/vendor/bin/" };
        try {
            for (int i = 0; i < kSuSearchPaths.length; i++) {
                f = new File(kSuSearchPaths[i] + "su");
                if (f != null && f.exists()) {
                    systemRootState = kSystemRootStateEnable;
                    return true;
                }
            }
        } catch (Exception e) {
        }
        systemRootState = kSystemRootStateDisable;
        return false;
    }

    public static void setDateTime(int year, int month, int day, int hour, int minute) throws IOException, InterruptedException {
  
        requestPermission();  
  
        Calendar c = Calendar.getInstance();
  
        c.set(Calendar.YEAR, year);  
        c.set(Calendar.MONTH, month-1);  
        c.set(Calendar.DAY_OF_MONTH, day);  
        c.set(Calendar.HOUR_OF_DAY, hour);  
        c.set(Calendar.MINUTE, minute);  
          
          
        long when = c.getTimeInMillis();  
  
        if (when / 1000 < Integer.MAX_VALUE) {  
            SystemClock.setCurrentTimeMillis(when);
        }  
  
        long now = Calendar.getInstance().getTimeInMillis();  
        //Log.d(TAG, "set tm="+when + ", now tm="+now);  
  
        if(now - when > 1000)  
            throw new IOException("failed to set Date.");   
          
    }

    /**
     * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
     *
     * @return 应用程序是/否获取Root权限
     */
    public static boolean upgradeRootPermission(String pkgCodePath) {
        Process process = null;
        DataOutputStream os = null;
        try {
            String cmd="chmod 777 " + pkgCodePath;
            process = Runtime.getRuntime().exec("su"); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }

    public static void setDate(int year, int month, int day) throws IOException, InterruptedException {  
  
        requestPermission();  
  
        Calendar c = Calendar.getInstance();  
  
        c.set(Calendar.YEAR, year);  
        c.set(Calendar.MONTH, month);  
        c.set(Calendar.DAY_OF_MONTH, day);  
        long when = c.getTimeInMillis();  
  
        if (when / 1000 < Integer.MAX_VALUE) {  
            SystemClock.setCurrentTimeMillis(when);  
        }  
  
        long now = Calendar.getInstance().getTimeInMillis();  
        //Log.d(TAG, "set tm="+when + ", now tm="+now);  
  
        if(now - when > 1000)  
            throw new IOException("failed to set Date.");  
    }  
  
    public static void setTime(int hour, int minute) throws IOException, InterruptedException {  
          
        requestPermission();  
  
        Calendar c = Calendar.getInstance();  
  
        c.set(Calendar.HOUR_OF_DAY, hour);  
        c.set(Calendar.MINUTE, minute);  
        long when = c.getTimeInMillis();  
  
        if (when / 1000 < Integer.MAX_VALUE) {  
            SystemClock.setCurrentTimeMillis(when);  
        }  
  
        long now = Calendar.getInstance().getTimeInMillis();  
        //Log.d(TAG, "set tm="+when + ", now tm="+now);  
  
        if(now - when > 1000)  
            throw new IOException("failed to set Time.");  
    }  
      
    static void requestPermission() throws InterruptedException, IOException {  
        createSuProcess("chmod 666 /dev/alarm").waitFor();  
    }  
      
    static Process createSuProcess() throws IOException  {  
        File rootUser = new File("/system/xbin/ru");
        if(rootUser.exists()) {
            return Runtime.getRuntime().exec(rootUser.getAbsolutePath());  
        } else {  
            return Runtime.getRuntime().exec("su");  
        }  
    }  
      
    static Process createSuProcess(String cmd) throws IOException {  
  
        DataOutputStream os = null;
        Process process = createSuProcess();  
  
        try {  
            os = new DataOutputStream(process.getOutputStream());  
            os.writeBytes(cmd + "\n");  
            os.writeBytes("exit $?\n");  
        } finally {  
            if(os != null) {  
                try {  
                    os.close();  
                } catch (IOException e) {  
                }  
            }  
        }  
  
        return process;  
    }  
}