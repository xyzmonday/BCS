package com.richfit.data.db;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by Administrator on 2016/1/8.
 */
public class BCSSQLiteHelper extends SQLiteOpenHelper {

    private final static int DB_VERSION = 1;
    private Context mContext;
    private static BCSSQLiteHelper sInstance;
    private final static String DB_NAME = "barcodesystem.db";
    private final static String Create = "create.sql";
    private final static String Drop = "drop.sql";

    /**
     * 注意为了防止内存泄露，context需要闯入ApplicationContext
     * @param context
     * @return
     */
    public static BCSSQLiteHelper getInstance(Context context) {
        if (sInstance == null) {
            synchronized (BCSSQLiteHelper.class) {
                if (sInstance == null)
                    sInstance = new BCSSQLiteHelper(context);
            }
        }
        return sInstance;
    }

    private BCSSQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        executeSQLScript(db, Create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        executeSQLScript(db, Drop);
        onCreate(db);
    }

    //数据库操作
    private void executeSQLScript(SQLiteDatabase database, String dbname) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte buf[] = new byte[1024];
        int len;
        AssetManager assetManager = mContext.getAssets();
        InputStream inputStream = null;

        try {
            inputStream = assetManager.open(dbname);
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
            String[] createScript = outputStream.toString().split(";");
            for (int i = 0; i < createScript.length; i++) {
                String sqlStatement = createScript[i].trim();
                if (!sqlStatement.startsWith("--") && sqlStatement.length() > 0) {
                    database.execSQL(sqlStatement + ";");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
