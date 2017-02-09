package com.richfit.data.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.richfit.common_lib.utils.Global;
import com.richfit.domain.bean.RowConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by monday on 2016/11/8.
 */

public abstract class BaseDao {

    protected final static String PAuthOrgKey = "P_AUTH_ORG";
    protected final static String PAuthOrg2Key = "P_AUTH_ORG2";


    protected BCSSQLiteHelper mSqliteHelper;

    public BaseDao(Context context) {
        this.mSqliteHelper = BCSSQLiteHelper.getInstance(context);
    }


    /**
     * 保存额外字段信息。如果服务器返回的扩展字段超出了对应的表的列，那么先过滤掉它们。
     * 这里仅仅是防止服务的错误导致app崩溃做的检查。
     *
     * @param db
     * @param extraMap：扩展字段数据集合
     * @param refCodeId：单据抬头id
     * @param refLineId：单据行id
     * @param locationId：仓位id
     * @param configType：配置类型
     */
    protected void saveExtraMap(SQLiteDatabase db, Map<String, Object> extraMap,
                                String refCodeId, String refLineId, String locationId, String configType) {
        //如果没有额外字段
        if (extraMap == null || extraMap.size() == 0)
            return;

        StringBuffer sb = new StringBuffer();
        Set<String> keys = extraMap.keySet();
        //插入抬头的额外字段
        switch (configType) {
            case Global.HEADER_CONFIG_TYPE:
                if (!TextUtils.isEmpty(refCodeId)) {
                    Set<String> extraHeadKeys = getTableInfo(db, "T_EXTRA_HEADER");
                    //如果Set集合包含参数c指定的集合对象中的所有内容
                    if (extraHeadKeys.containsAll(keys)) {
                        sb.append("INSERT OR REPLACE INTO T_EXTRA_HEADER ");
                        sb.append("(id,config_type");
                        sb.append(createSql(keys));
                        db.execSQL(sb.toString(), createInsertObjs(extraMap, refCodeId, configType));
                    }
                }
                break;
            case Global.COLLECT_CONFIG_TYPE:
                if (!TextUtils.isEmpty(refLineId)) {
                    Set<String> extraHeadKeys = getTableInfo(db, "T_EXTRA_LINE");
                    //如果Set集合包含参数c指定的集合对象中的所有内容
                    if (extraHeadKeys.containsAll(keys)) {
                        sb.append("INSERT OR REPLACE INTO T_EXTRA_LINE ");
                        sb.append("(id,ref_code_id,config_type");
                        sb.append(createSql(keys));
                        db.execSQL(sb.toString(), createInsertObjs(extraMap, refLineId, refCodeId, configType));
                    }
                }
                break;
            case Global.LOCATION_CONFIG_TYPE:
                if (!TextUtils.isEmpty(locationId)) {
                    Set<String> extraHeadKeys = getTableInfo(db, "T_EXTRA_CW");
                    //如果Set集合包含参数c指定的集合对象中的所有内容
                    if (extraHeadKeys.containsAll(keys)) {
                        //保存第三级的额外字段数据
                        sb.append("INSERT OR REPLACE INTO T_EXTRA_CW ");
                        sb.append("(id,ref_line_id,config_type");
                        sb.append(createSql(keys));
                        db.execSQL(sb.toString(), createInsertObjs(extraMap, locationId, refLineId, configType));
                    }
                    break;
                }
                sb.setLength(0);
        }
    }

    private String createSql(Set<String> keys) {
        StringBuffer sb = new StringBuffer();
        for (String key : keys) {
            sb.append(",").append(key);
        }
        sb.append(") ");
        sb.append("VALUES(?,?,?");
        for (int i = 0, size = keys.size(); i < size; i++) {
            sb.append(",").append("?");
        }
        sb.append(")");
        return sb.toString();
    }

    private Object[] createInsertObjs(Map<String, Object> extraMap, String... fixElements) {
        ArrayList<Object> list = new ArrayList<>();
        list.addAll(Arrays.asList(fixElements));
        Iterator<Map.Entry<String, Object>> iterator = extraMap.entrySet().iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next().getValue());
        }
        return list.toArray();
    }

    /**
     * 保存缓存的额外的字段信息
     *
     * @param db：数据库
     * @param extraMap：需要缓存的额外字段数据源
     * @param configType：保存的额外字段类型
     * @param transId：单据抬头缓存表的主键id
     * @param transLineId：单据行明细的缓存表的主键id
     * @param transLocationId：仓位缓存表的主键id
     */
    protected void saveTransExtraMap(SQLiteDatabase db, Map<String, Object> extraMap, String configType,
                                     String transId, String transLineId, String transLocationId) {
        if (extraMap == null || extraMap.size() == 0)
            return;
        StringBuffer sb = new StringBuffer();
        Set<String> keys = extraMap.keySet();
        switch (configType) {
            case Global.HEADER_CONFIG_TYPE:
                if (!TextUtils.isEmpty(transId)) {
                    Set<String> extraHeadKeys = getTableInfo(db, "T_EXTRA_HEADER");
                    //如果Set集合包含参数c指定的集合对象中的所有内容
                    if (extraHeadKeys.containsAll(keys)) {
                        sb.append("INSERT OR REPLACE INTO T_EXTRA_HEADER ");
                        sb.append("(id,config_type");
                        sb.append(createSql(keys));
                        db.execSQL(sb.toString(), createInsertObjs(extraMap, transId, configType));
                    }
                }
                break;
            case Global.COLLECT_CONFIG_TYPE:
                if (!TextUtils.isEmpty(transLineId)) {
                    Set<String> extraHeadKeys = getTableInfo(db, "T_EXTRA_LINE");
                    //如果Set集合包含参数c指定的集合对象中的所有内容
                    if (extraHeadKeys.containsAll(keys)) {
                        sb.append("INSERT OR REPLACE INTO T_EXTRA_LINE ");
                        sb.append("(id,ref_code_id,config_type");
                        sb.append(createSql(keys));
                        db.execSQL(sb.toString(), createInsertObjs(extraMap, transLineId, transId, configType));
                    }
                }
                break;
            case Global.LOCATION_CONFIG_TYPE:
                if (!TextUtils.isEmpty(transLocationId)) {
                    Set<String> extraHeadKeys = getTableInfo(db, "T_EXTRA_CW");
                    //如果Set集合包含参数c指定的集合对象中的所有内容
                    if (extraHeadKeys.containsAll(keys)) {
                        //保存第三级的额外字段数据
                        sb.append("INSERT OR REPLACE INTO T_EXTRA_CW ");
                        sb.append("(id,ref_line_id,config_type");
                        sb.append(createSql(keys));
                        db.execSQL(sb.toString(), createInsertObjs(extraMap, transLocationId, transLineId, configType));
                    }
                    break;
                }
                sb.setLength(0);
        }
    }

    /**
     * 读取额外字段信息。这里表中某一行的所有列的数据。注意由于id和config_type也同时
     * 被读入了map中，所以配置信息不应该有以id和config_type作为propertyCode的扩展
     * 字段。
     *
     * @param db
     * @param refCodeId：抬头id
     * @param refLineId：行id
     * @param LocationId：仓位id
     * @return
     */
    protected Map<String, Object> readExtraMap(SQLiteDatabase db, String configType, String refCodeId,
                                               String refLineId, String LocationId) {
        Map<String, Object> extraMap = new HashMap<>();
        Cursor cursor = null;
        switch (configType) {
            case Global.HEADER_CONFIG_TYPE:
                if (!TextUtils.isEmpty(refCodeId)) {
                    //读取抬头额外信息
                    cursor = db.rawQuery("SELECT * FROM T_EXTRA_HEADER WHERE id = ?", new String[]{refCodeId});
                }
                break;
            case Global.COLLECT_CONFIG_TYPE:
                if (!TextUtils.isEmpty(refLineId)) {
                    cursor = db.rawQuery("SELECT * FROM T_EXTRA_LINE WHERE id = ?", new String[]{refLineId});
                }
                break;
            case Global.LOCATION_CONFIG_TYPE:
                if (!TextUtils.isEmpty(LocationId)) {
                    cursor = db.rawQuery("SELECT * FROM T_EXTRA_CW WHERE id = ?", new String[]{LocationId});
                }
                break;
        }
        if (cursor == null)
            return extraMap;

        while (cursor.moveToNext()) {
            String[] columnNames = cursor.getColumnNames();
            for (String columnName : columnNames) {
                Object obj = cursor.getString(cursor.getColumnIndex(columnName));
                if (obj != null) {
                    extraMap.put(columnName, obj);
                }
            }
        }
        cursor.close();
        return extraMap;
    }

    /**
     * 读取已经缓存的额外字段
     *
     * @param db：数据库
     * @param configType：额外字段配置类型
     * @param transId：单据抬头缓存表的主键id
     * @param transLineId：单据行明细的缓存表的主键id
     * @param transLocationId：仓位缓存表的主键id
     * @return
     */
    protected Map<String, Object> readTransExtraMap(SQLiteDatabase db, String configType, String transId,
                                                    String transLineId, String transLocationId) {
        Map<String, Object> extraMap = new HashMap<>();
        Cursor cursor = null;
        switch (configType) {
            case Global.HEADER_CONFIG_TYPE:
                if (!TextUtils.isEmpty(transId)) {
                    //读取抬头额外信息
                    cursor = db.rawQuery("SELECT * FROM T_TRANSACTION_EXTRA_HEADER WHERE id = ?"
                            , new String[]{transId});
                }
                break;
            case Global.COLLECT_CONFIG_TYPE:
                if (!TextUtils.isEmpty(transLineId)) {
                    cursor = db.rawQuery("SELECT * FROM T_TRANSACTION_EXTRA_LINE WHERE id = ?"
                            , new String[]{transLineId});
                }
                break;
            case Global.LOCATION_CONFIG_TYPE:
                if (!TextUtils.isEmpty(transLocationId)) {
                    cursor = db.rawQuery("SELECT * FROM T_TRANSACTION_EXTRA_CW WHERE id = ?"
                            , new String[]{transLineId});
                }
                break;
        }
        if (cursor == null)
            return extraMap;

        while (cursor.moveToNext()) {
            String[] columnNames = cursor.getColumnNames();
            for (String columnName : columnNames) {
                Object obj = cursor.getString(cursor.getColumnIndex(columnName));
                if (obj != null) {
                    extraMap.put(columnName, obj);
                }
            }
        }

        cursor.close();
        return extraMap;
    }

    /**
     * 读取扩展字段的配置信息
     *
     * @param companyCode：公司编码
     * @param refType：单据类型
     * @param bizType：子功能模块编码
     * @param configType：配置类型
     * @return
     */
    protected synchronized ArrayList<RowConfig> readExtraConfigInfo(SQLiteDatabase db, String refType, String companyCode,
                                                                    String bizType, String configType) {
        ArrayList<RowConfig> configs = new ArrayList<>();

        if (TextUtils.isEmpty(companyCode) || TextUtils.isEmpty(bizType)) {
            return configs;
        }

        StringBuffer sb = new StringBuffer();
        sb.append("select id,property_name,property_code,");
        sb.append("display_flag,input_flag,company_code,");
        sb.append("company_name,module_code,module_name,");
        sb.append("biz_type,ref_code,");
        sb.append("ref_name,config_type,ui_type,col_num,col_name,data_source ");
        sb.append(" from T_CONFIG ");
        Cursor cursor;
        if (!TextUtils.isEmpty(refType)) {
            sb.append("where company_code = ? and biz_type = ? and ref_code = ? and config_type = ?");
            cursor = db.rawQuery(sb.toString(), new String[]{companyCode, bizType, refType, configType});
        } else {
            sb.append("where company_code = ? and  module_code = ? and biz_type = ? and config_type = ?");
            cursor = db.rawQuery(sb.toString(), new String[]{companyCode, bizType, configType});
        }


        while (cursor.moveToNext()) {
            RowConfig config = new RowConfig();
            config.id = cursor.getString(0);
            config.propertyName = cursor.getString(1);
            config.propertyCode = cursor.getString(2);
            config.displayFlag = cursor.getString(3);
            config.inputFlag = cursor.getString(4);
            config.companyCode = cursor.getString(5);
            config.companyName = cursor.getString(6);
            config.moduleCode = cursor.getString(7);
            config.moduleName = cursor.getString(8);
            config.bizType = cursor.getString(9);
            config.refCode = cursor.getString(10);
            config.refName = cursor.getString(11);
            config.configType = cursor.getString(12);
            config.uiType = cursor.getString(13);
            config.colNum = cursor.getString(14);
            config.colName = cursor.getString(15);
            config.dataSource = cursor.getString(16);

            configs.add(config);
        }
        sb.setLength(0);
        cursor.close();
        return configs;
    }

    /**
     * 获取表的列信息
     *
     * @param db
     * @param tableName：目标表
     * @return
     */
    protected Set<String> getTableInfo(SQLiteDatabase db, String tableName) {
        Set<String> columns = new HashSet<>();
        StringBuffer sb = new StringBuffer();
        sb.append("PRAGMA table_info");
        sb.append("([");
        sb.append(tableName);
        sb.append("])");
        Cursor columnCursor = db.rawQuery(sb.toString(), null);
        while (columnCursor.moveToNext()) {
            columns.add(columnCursor.getString(columnCursor.getColumnIndex("name")));
        }
        columnCursor.close();
        sb.setLength(0);
        return columns;
    }


}
