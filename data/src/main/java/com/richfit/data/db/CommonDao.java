package com.richfit.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

import com.richfit.common_lib.utils.CommonUtil;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.L;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.domain.bean.BizFragmentConfig;
import com.richfit.domain.bean.InvEntity;
import com.richfit.domain.bean.RowConfig;
import com.richfit.domain.bean.SupplierEntity;
import com.richfit.domain.bean.UserEntity;
import com.richfit.domain.bean.WorkEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

/**
 * 基础数据Dao层。包括了数据采集需要的基础数据，额外字段配置信息，
 * 用户信息等
 * Created by monday on 2016/11/8.
 */

public class CommonDao extends BaseDao {

    @Inject
    public CommonDao(Context context) {
        super(context);
    }


    private SQLiteDatabase getReadableDB() {
        return mSqliteHelper.getReadableDatabase();
    }

    private SQLiteDatabase getWritableDB() {
        return mSqliteHelper.getWritableDatabase();
    }

    public void updatExtraConfigTable(Map<String, Set<String>> map) {
        //尝试更新T_EXTRA_HEADER表
        updateExtraHeader(map.get(Global.HEADER_CONFIG_TYPE), "T_EXTRA_HEADER");
        updateExtraHeader(map.get(Global.COLLECT_CONFIG_TYPE), "T_EXTRA_LINE");
        updateExtraHeader(map.get(Global.LOCATION_CONFIG_TYPE), "T_EXTRA_CW");
        updateExtraHeader(map.get(Global.HEADER_CONFIG_TYPE), "T_TRANSACTION_EXTRA_HEADER");
        updateExtraHeader(map.get(Global.COLLECT_CONFIG_TYPE), "T_TRANSACTION_EXTRA_LINE");
        updateExtraHeader(map.get(Global.LOCATION_CONFIG_TYPE), "T_TRANSACTION_EXTRA_CW");
    }

    private void updateExtraHeader(Set<String> targetColumns, String tableName) {
        if (targetColumns == null || targetColumns.size() == 0)
            return;
        SQLiteDatabase db = mSqliteHelper.getReadableDatabase();

        StringBuffer sb = new StringBuffer();
        sb.append("SELECT count(*) FROM sqlite_master WHERE type=");
        sb.append("\"table\"");
        sb.append(" AND name = ");
        sb.append("\"");
        sb.append(tableName);
        sb.append(("\""));
        Cursor cursor = db.rawQuery(sb.toString(), null);
        int count = -1;
        while (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        sb.setLength(0);

        if (count > 0) {
            Set<String> columns = getTableInfo(db, tableName);
            //需要插入的列减去已经存在的列
            targetColumns.removeAll(columns);
            if (targetColumns.size() == 0)
                return;
            if (targetColumns.size() > 0) {
                //开始插入列
                for (String column : targetColumns) {
                    sb.append("ALTER TABLE ")
                            .append(tableName)
                            .append(" ADD COLUMN ")
                            .append(column)
                            .append(" TEXT");
                    db.execSQL(sb.toString());
                    sb.setLength(0);
                }
            }
        }
        db.close();
    }

    /**
     * 保存用户的基本信息
     *
     * @param userEntity：用户信息实体类
     */
    public void saveUserInfo(UserEntity userEntity) {
        if (userEntity == null || TextUtils.isEmpty(userEntity.loginId))
            return;
        SQLiteDatabase db = mSqliteHelper.getReadableDatabase();
        //获取当前的时间
        final long lastLoginDate = System.currentTimeMillis();
        StringBuffer sb = new StringBuffer();
        sb.append("INSERT OR REPLACE INTO T_USER(login_id,auth_orgs,user_id,last_login_date,user_name) ");
        sb.append(" values(?,?,?,?,?)");
        db.execSQL(sb.toString(), new Object[]{userEntity.loginId, userEntity.authOrgs,
                userEntity.userId, lastLoginDate, userEntity.userName});
        sb.setLength(0);
        db.close();
    }

    /**
     * 读取一周之内登陆的用户信息
     *
     * @return
     */
    public ArrayList<String> readUserInfo() {
        ArrayList<String> userList = new ArrayList<>();
        final long endLoginDate = System.currentTimeMillis();
        //计算一个星期之内登陆的用户
        final long startLoginDate = endLoginDate - 7 * 24 * 60 * 60 * 1000;
        SQLiteDatabase db = mSqliteHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT login_id FROM T_USER WHERE LAST_LOGIN_DATE BETWEEN ? AND ?  ",
                new String[]{String.valueOf(startLoginDate), String.valueOf(endLoginDate)});

        while (cursor.moveToNext()) {
            userList.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return userList;
    }

    /**
     * 保存配置信息
     *
     * @param configs：配置信息列表
     */
    public void saveExtraConfigInfo(List<RowConfig> configs) {
        if (configs == null || configs.size() == 0)
            return;
        SQLiteDatabase db = mSqliteHelper.getReadableDatabase();
        //先删除数据
        db.delete("T_CONFIG", null, null);
        StringBuffer sb = new StringBuffer();
        for (RowConfig config : configs) {
            sb.append("INSERT INTO T_CONFIG(id,property_name,");
            sb.append("property_code,display_flag,input_flag,company_code,");
            sb.append("company_name,module_code,module_name,biz_type,");
            sb.append("ref_code,ref_name,config_type,ui_type,col_num,col_name,data_source)");
            sb.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            db.execSQL(sb.toString(), new Object[]{config.id, config.propertyName,
                    config.propertyCode, config.displayFlag, config.inputFlag, config.companyCode,
                    config.companyName, config.moduleCode, config.moduleName, config.bizType, config.refCode, config.refName, config.configType, config.uiType,
                    config.colNum, config.colName, config.dataSource});
            sb.setLength(0);
        }
        db.close();
    }

    /**
     * 读取配置文件
     *
     * @param companyCode：地区公司编码
     * @param bizType：子模块编码
     * @param configType：配置类型
     * @return
     */
    public ArrayList<RowConfig> readExtraConfigInfo(String companyCode, String bizType, String refType,
                                                    String configType) {
        ArrayList<RowConfig> configs = new ArrayList<>();

        if (TextUtils.isEmpty(bizType) || TextUtils.isEmpty(companyCode)) {
            return configs;
        }

        SQLiteDatabase db = mSqliteHelper.getReadableDatabase();
        configs = readExtraConfigInfo(db, refType, companyCode, bizType, configType);
        db.close();
        return configs;
    }


    /**
     * 获取额外控件对应的字典数据
     *
     * @param dictionaryCode：需要查询的字典编码
     * @return
     */
    public Map<String, String> readExtraDataSourceByDictionary(String dictionaryCode) {
        HashMap<String, String> map = new HashMap<>();
        if (TextUtils.isEmpty(dictionaryCode))
            return map;
        SQLiteDatabase db = mSqliteHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select val,name from T_EXTRA_DATA_SOURCE where code = ? order by sort asc",
                new String[]{dictionaryCode});

        while (cursor.moveToNext()) {
            map.put(cursor.getString(0), cursor.getString(1));
        }
        cursor.close();
        db.close();
        return map;
    }

    public boolean saveBizFragmentConfig(ArrayList<BizFragmentConfig> bizFragmentConfigs) {
        if (bizFragmentConfigs == null || bizFragmentConfigs.size() == 0) {
            return false;
        }
        SQLiteDatabase db = mSqliteHelper.getReadableDatabase();
        db.delete("T_FRAGMENT_CONFIGS", null, null);
        ContentValues cv = new ContentValues();
        for (BizFragmentConfig bizFragmentConfig : bizFragmentConfigs) {
            cv.clear();
            cv.put("id", bizFragmentConfig.id);
            cv.put("fragment_tag", bizFragmentConfig.fragmentTag);
            cv.put("fragment_type", bizFragmentConfig.fragmentType);
            cv.put("biz_type", bizFragmentConfig.bizType);
            cv.put("ref_type", bizFragmentConfig.refType);
            cv.put("tab_title", bizFragmentConfig.tabTitle);
            cv.put("class_name", bizFragmentConfig.className);
            db.insert("T_FRAGMENT_CONFIGS", null, cv);
        }
        db.close();
        return true;
    }

    /**
     * 获取上次请求基础数据的日期
     *
     * @param queryType:查询基础数据类型，注意只有仓位和物料采用了增量跟新时 我们需要保存上一次用户的请求日期，其他的基础数据只给定
     *                                             当前的日期即可。
     * @return
     */
    public String getQueryDate(String queryType) {
        SQLiteDatabase db = mSqliteHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("select query_date from REQUEST_DATE where query_type = ?",
                new String[]{queryType});

        String date = "";
        while (cursor.moveToNext()) {
            date = cursor.getString(0);
        }
        if (TextUtils.isEmpty(date)) {
            date = "0001/01/01";
        } else if (!TextUtils.isEmpty(date) && "0001/01/01".equals(date)) {
            date = "0001/01/01";
        } else {
            //返回当前的时间
            date = UiUtil.getCurrentDate("yyyy/MM/dd");
        }
        cursor.close();
        db.close();
        return date;
    }

    /**
     * 设置当前基础数据更新的日期
     *
     * @param queryType:查询基础数据类型
     * @param queryDate：查询日期
     */
    public void saveQueryData(String queryType, String queryDate) {
        SQLiteDatabase db = mSqliteHelper.getWritableDatabase();
        db.delete("REQUEST_DATE", "query_type = ?", new String[]{queryType});
        ContentValues cv = new ContentValues();
        cv.put("query_type", queryType);
        cv.put("query_date", queryDate);
        db.insert("REQUEST_DATE", null, cv);
        db.close();
    }

    /**
     * 将服务器获取的基础数据保存到本地
     *
     * @param maps:服务器获取的基本数据源
     * @return
     */
    public int insertBaseData(List<Map<String, Object>> maps) {

        //获取基本参数的map
        Map<String, Object> basicParamMap = maps.get(0);

        final String queryType = (String) basicParamMap.get("queryType");

        if ("error".equals(queryType)) {
            L.e("基础数据下载出错");
            return -1;
        }

        final String queryDate = (String) basicParamMap.get("queryDate");
        final boolean isFirstPage = (boolean) basicParamMap.get("isFirstPage");
        final int taskId = (int) basicParamMap.get("taskId");
        int tableIndex = -1;
        if ("CW".equals(queryType)) {
            tableIndex = 0;
        } else if ("ZZ".equals(queryType)) {
            tableIndex = 1;
        } else if ("LZ".equals(queryType)) {
            tableIndex = 2;
        } else if ("CZ".equals(queryType)) {
            tableIndex = 3;
        } else if ("XJD".equals(queryType)) {
            tableIndex = 4;
        } else if ("GYS".equals(queryType)) {
            tableIndex = 5;
        } else if ("SD".equals(queryType)) {
            tableIndex = 6;
        } else if ("ZZ2".equals(queryType)) {
            tableIndex = 7;
        }
        boolean isCWFirst = "0001/01/01".equalsIgnoreCase(queryDate);
        insertData(maps, tableIndex, isFirstPage, isCWFirst);
        return taskId;
    }

    /**
     * 数据保存控制
     *
     * @param source：需要爆粗你的数据源
     * @param tableIndex：数据要保存的本地数据表的索引，由开发者提前定义
     * @param isFirstPage：是否是第一页。对于非增量更新的数据源的分页数据请求，第一页时需要先删除所有的数据
     * @param isCWFirst：对于增量请求的仓位数据，如果是首次请求服务器(业绩说说本次请求获取的不是增量数据)需要删除之前所有的数据
     */
    private void insertData(List<Map<String, Object>> source, int tableIndex, boolean isFirstPage, boolean isCWFirst) {
        if (tableIndex < 0)
            return;
        if (source == null || source.size() == 0)
            return;
        SQLiteDatabase db = mSqliteHelper.getWritableDatabase();
        String tableName;
        StringBuilder sql = new StringBuilder();
        switch (tableIndex) {
            case 0:
                /*表示第一次请求数据，那么直接插入.
                * 注意CW数据的特殊性在于，它根据日期返回增量数据。所以第一次是全新的数据，
                * 以后的增量数量使用INSERT OR REPLACE */
                tableName = "BASE_LOCATION";
                if (isCWFirst) {
                    db.execSQL("delete from " + tableName);
                }
                sql.append("INSERT INTO ")
                        .append(tableName)
                        .append(" (id,storage_num,location,sap_update_date)")
                        .append(" VALUES (?,?,?,?)");

                break;
            case 1:
                tableName = "P_AUTH_ORG";
                db.execSQL("delete from " + tableName);
                sql.append("INSERT INTO ")
                        .append(tableName)
                        .append(" (org_id,org_code,org_name,org_level,parent_id,storage_code) ")
                        .append("VALUES (?,?,?,?,?,?)");
                break;
            case 7:
                tableName = "P_AUTH_ORG2";
                db.execSQL("delete from " + tableName);
                sql.append("INSERT INTO ")
                        .append(tableName)
                        .append(" (org_id,org_code,org_name,org_level,parent_id,storage_code) ")
                        .append("VALUES (?,?,?,?,?,?)");
                break;
            case 2:
                tableName = "BASE_WAREHOUSE_GROUP";
                db.execSQL("delete from " + tableName);
                sql.append("INSERT INTO ")
                        .append(tableName)
                        .append(" (id,group_code,group_desc) VALUES (?,?,?)");
                break;

            case 3:
                tableName = "BASE_COST_CENTER";
                if (isFirstPage) {
                    db.execSQL("delete from " + tableName);
                }
                //注意对于成本中心和供应商，由于是分页加载数据，所有仅仅是在第一页加载的时候需要删除数据。
                sql.append("INSERT INTO ")
                        .append(tableName)
                        .append(" (id,org_id,cost_center,cost_center_desc,creation_date,last_update_date)")
                        .append(" VALUES (?,?,?,?,?,?)");

                break;
            case 4:
                tableName = "BASE_INSPECTION_PLACE";
                db.execSQL("delete from " + tableName);
                sql.append("INSERT INTO ")
                        .append(tableName)
                        .append(" (id,code,name)")
                        .append(" VALUES (?,?,?)");
                break;
            case 5:
                tableName = "BASE_SUPPLIER";
                if (isFirstPage) {
                    db.execSQL("delete from " + tableName);
                }
                sql.append("INSERT INTO ")
                        .append(tableName)
                        .append(" (id,org_id,supplier_code,supplier_desc,creation_date,last_update_date)")
                        .append(" VALUES (?,?,?,?,?,?)");
                break;
            case 6:
                tableName = "T_EXTRA_DATA_SOURCE";
                db.execSQL("delete from " + tableName);
                sql.append("INSERT INTO ")
                        .append(tableName)
                        .append(" (id,code,name,sort,val)")
                        .append(" VALUES (?,?,?,?,?)");
                break;

        }
        db.close();
//        int length = source.size() - 1;
//        //批量插入算法
//        int count = length / Global.MAX_PATCH_LENGTH;
//        int residual = length % Global.MAX_PATCH_LENGTH;
//        int ptr = 0;
//        if (count == 0) {
//            // 说明数据长度小于PATCH_MAX_LENGTH，直接写入即可
//            patchUpdateBaseData(source, residual, ptr, sql.toString(), tableIndex, isCWFirst);
//        } else if (count > 0) {
//            for (; ptr < count; ptr++) {
//                patchUpdateBaseData(source, Global.MAX_PATCH_LENGTH, ptr, sql.toString(), tableIndex, isCWFirst);
//            }
//            if (residual > 0) {
//                // 说明还有剩余的数据
//                patchUpdateBaseData(source, residual, ptr, sql.toString(), tableIndex, isCWFirst);
//            }
//        }
        patchUpdateBaseData(source, 1, source.size(), 0, sql.toString(), tableIndex, isCWFirst);
    }

    /**
     * 批量更新基础数据
     *
     * @param source:数据源
     * @param start:本次批量保存的下边界
     * @param end：本次批量保存的上边界
     * @param ptr：本次保存的页码
     * @param sql：本次保存需要执行的sql语句
     * @param tableIndex：同上
     * @param isCWFirst：同上
     */
    private void patchUpdateBaseData(List<Map<String, Object>> source, int start, int end, int ptr, String sql,
                                     int tableIndex, boolean isCWFirst) {
        SQLiteDatabase db = mSqliteHelper.getReadableDatabase();
        db.beginTransaction();
        try {
            SQLiteStatement stmt = db.compileStatement(sql);
            Map<String, Object> item;
            //绑定数据
            switch (tableIndex) {
                case 0:
                    //插入数据
                    if (!isCWFirst) {
                        for (int i = start; i < end; i++) {
                            item = source.get(ptr * Global.MAX_PATCH_LENGTH + i);
                            db.execSQL("INSERT OR REPLACE INTO BASE_LOCATION(id,location,storage_num,sap_update_date) " +
                                    "values(?,?,?,?)", new Object[]{item.get(Global.id_Key), item.get(Global.code_Key),
                                    item.get(Global.storageNum_Key), item.get(Global.sapUpdateDate_Key)});
                        }
                    } else {
                        //仓位
                        for (int i = start; i < end; i++) {
                            item = source.get(ptr * Global.MAX_PATCH_LENGTH + i);
                            stmt.bindString(1, item.get(Global.id_Key).toString());
                            stmt.bindString(2, item.get(Global.storageNum_Key).toString());
                            stmt.bindString(3, item.get(Global.code_Key).toString());
                            stmt.bindString(4, item.get(Global.sapUpdateDate_Key).toString());
                            stmt.execute();
                            stmt.clearBindings();
                        }
                    }
                    break;
                case 7:
                case 1:
                    //组织机构
                    for (int i = start; i < end; i++) {
                        item = source.get(ptr * Global.MAX_PATCH_LENGTH + i);
                        stmt.bindString(1, item.get(Global.id_Key).toString());
                        stmt.bindString(2, item.get(Global.code_Key).toString());
                        stmt.bindString(3, item.get(Global.name_Key).toString());
                        stmt.bindString(4, item.get(Global.orgLevel_Key).toString());
                        stmt.bindString(5, item.get(Global.parentId_Key).toString());
                        stmt.bindString(6, CommonUtil.Obj2String(item.get(Global.storageNum_Key)));
                        stmt.execute();
                        stmt.clearBindings();
                    }
                    break;
                case 2:
                    //料组
                    for (int i = start; i < end; i++) {
                        item = source.get(ptr * Global.MAX_PATCH_LENGTH + i);
                        stmt.bindString(1, item.get(Global.id_Key).toString());
                        stmt.bindString(2, item.get(Global.code_Key).toString());
                        stmt.bindString(3, item.get(Global.name_Key).toString());
                        stmt.execute();
                        stmt.clearBindings();
                    }
                    break;

                case 3:
                    //成本中心
                    for (int i = start; i < end; i++) {
                        item = source.get(ptr * Global.MAX_PATCH_LENGTH + i);
                        stmt.bindString(1, item.get(Global.id_Key).toString());
                        stmt.bindString(2, item.get(Global.parentId_Key).toString());
                        stmt.bindString(3, item.get(Global.code_Key).toString());
                        stmt.bindString(4, item.get(Global.name_Key).toString());
                        stmt.bindString(5, item.get(Global.sapCreationDate_Key).toString());
                        stmt.bindString(6, item.get(Global.sapUpdateDate_Key).toString());
                        stmt.execute();
                        stmt.clearBindings();
                    }
                    break;
                case 4:
                    //巡检点
                    for (int i = start; i < end; i++) {
                        item = source.get(ptr * Global.MAX_PATCH_LENGTH + i);
                        stmt.bindString(1, item.get(Global.id_Key).toString());
                        stmt.bindString(2, item.get(Global.code_Key).toString());
                        stmt.bindString(3, item.get(Global.name_Key).toString());
                        stmt.execute();
                        stmt.clearBindings();
                    }
                    break;
                case 5:
                    //供应商
                    for (int i = start; i < end; i++) {
                        item = source.get(ptr * Global.MAX_PATCH_LENGTH + i);
                        stmt.bindString(1, item.get(Global.id_Key).toString());
                        stmt.bindString(2, item.get(Global.parentId_Key).toString());
                        stmt.bindString(3, item.get(Global.code_Key).toString());
                        stmt.bindString(4, item.get(Global.name_Key).toString());
                        stmt.execute();
                        stmt.clearBindings();
                    }
                    break;
                case 6:
                    //额外字段字典表
                    for (int i = start; i < end; i++) {
                        item = source.get(ptr * Global.MAX_PATCH_LENGTH + i);
                        stmt.bindString(1, item.get(Global.id_Key).toString());
                        stmt.bindString(2, item.get(Global.code_Key).toString());
                        stmt.bindString(3, item.get(Global.name_Key).toString());
                        stmt.bindString(4, item.get(Global.sort_key).toString());
                        stmt.bindString(5, item.get(Global.value_key).toString());
                        stmt.execute();
                        stmt.clearBindings();
                    }
                    break;

            }
        } catch (Exception e) {
            L.d("插入基础数据报错 = " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
            source.clear();
            System.gc();
        }
    }

    /**
     * 根据工厂id查询所有的仓库
     *
     * @param workId:工厂id
     */
    public ArrayList<InvEntity> getInvByWorkId(String workId, int flag) {
        ArrayList<InvEntity> datas = new ArrayList<>();
        if (TextUtils.isEmpty(workId)) {
            return datas;
        }
        SQLiteDatabase db = mSqliteHelper.getWritableDatabase();
        InvEntity tamp = new InvEntity();
        tamp.invName = "请选择";
        datas.add(tamp);

        final String tableName = flag == 0 ? PAuthOrgKey : PAuthOrg2Key;

        Cursor cursor = db.rawQuery("select org_id,org_name,org_code from " + tableName + " where parent_id = ?"
                , new String[]{workId});
        while (cursor.moveToNext()) {
            InvEntity entity = new InvEntity();
            entity.invId = cursor.getString(0);
            entity.invName = cursor.getString(1);
            entity.invCode = cursor.getString(2);
            datas.add(entity);
        }

        cursor.close();
        db.close();
        return datas;
    }

    /**
     * 获取工厂列表
     */
    public ArrayList<WorkEntity> getWorks(int flag) {
        SQLiteDatabase db = mSqliteHelper.getWritableDatabase();
        ArrayList<WorkEntity> works = new ArrayList<>();
        WorkEntity data = new WorkEntity();

        data.workCode = "-1";
        data.workName = "请选择";
        works.add(0, data);

        ArrayList<String> authOrgs = new ArrayList<>();
        if (!TextUtils.isEmpty(Global.authOrg)) {
            authOrgs.addAll(Arrays.asList(Global.authOrg.split("\\|")));
        }

        StringBuffer sb = new StringBuffer();
        final String tableName = flag == 0 ? PAuthOrgKey : PAuthOrg2Key;
        sb.append("select org_id,org_name,org_code from ").append(tableName).append(" where org_level = 2 ");
        if (authOrgs.size() > 0 && flag == 0) {
            sb.append("and org_code IN (");
            for (int i = 0; i < authOrgs.size(); i++) {
                sb.append("'").append(authOrgs.get(i)).append("'").append(i == authOrgs.size() - 1 ? "" : ",");
            }
            sb.append(")");
        }

        Cursor cursor = db.rawQuery(sb.toString(), null);
        while (cursor.moveToNext()) {
            WorkEntity item = new WorkEntity();
            item.workId = cursor.getString(0);
            item.workName = cursor.getString(1);
            item.workCode = cursor.getString(2);
            works.add(item);
        }

        sb.setLength(0);
        cursor.close();
        db.close();
        return works;
    }


    /**
     * 检查发出库位和接收库位是否同属一个ERP仓库号
     *
     * @param sendWorkId:发出工厂id
     * @param sendInvCode：发出库位编码
     * @param recWorkId：接收工厂id
     * @param recInvCode：接收库存地点编码
     * @return
     */
    public boolean checkWareHouseNum(final String sendWorkId, final String sendInvCode,
                                     final String recWorkId, final String recInvCode, int flag) {

        SQLiteDatabase db = getReadableDB();
        String sendStorageNum = getStorageNum(db, sendWorkId, sendInvCode, flag);
        String recStorageNum = getStorageNum(db, recWorkId, recInvCode, flag);

        //如果两个都不为空
        if (!TextUtils.isEmpty(sendStorageNum) &&
                !TextUtils.isEmpty(recStorageNum) &&
                sendStorageNum.equals(recStorageNum)) {
            return true;
        }

        //如果两个都为空
        if (TextUtils.isEmpty(sendStorageNum) &&
                TextUtils.isEmpty(recStorageNum)) {
            return true;
        }
        return false;
    }

    private String getStorageNum(SQLiteDatabase db, String workId, String invCode, int flag) {
        String storageNum = null;
        final String tableName = flag == 0 ? PAuthOrgKey : PAuthOrg2Key;
        Cursor cursor = db.rawQuery("select storage_code from " + tableName + " where parent_id = ? and org_code = ? and org_level = ?",
                new String[]{workId, invCode, "3"});
        while (cursor.moveToNext()) {
            storageNum = cursor.getString(0);
        }
        cursor.close();
        return storageNum;
    }

    /**
     * 获取供应商列表
     *
     * @param workCode：工厂编码
     * @return
     */
    public ArrayList<SupplierEntity> getSupplierList(String workCode, int flag) {
        ArrayList<SupplierEntity> list = new ArrayList<>();
        if (TextUtils.isEmpty(workCode))
            return list;

        SQLiteDatabase db = getReadableDB();

        StringBuffer sb = new StringBuffer();
        final String tableName = flag == 0 ? PAuthOrgKey : PAuthOrg2Key;
        sb.append("select B.org_id , B.supplier_code,B.supplier_desc from ")
                .append(tableName)
                .append("  P,BASE_SUPPLIER B ")
                .append(" where P.parent_id = B.org_id ")
                .append(" and P.org_level = 2 and P.org_code = ?");

        Cursor cursor = db.rawQuery(sb.toString(), new String[]{workCode});
        while (cursor.moveToNext()) {
            SupplierEntity supplierEntity = new SupplierEntity();
            supplierEntity.supplierId = cursor.getString(0);
            supplierEntity.supplierCode = cursor.getString(1);
            supplierEntity.supplierName = cursor.getString(2);
            list.add(supplierEntity);
        }

        db.close();
        return list;
    }

    /**
     * 获得成本中心
     *
     * @param workId：工厂id
     */
    public ArrayList<String> getCostCenterListByWorkId(String workId) {
        ArrayList<String> list = new ArrayList<>();
        if (TextUtils.isEmpty(workId)) {
            return list;
        }
        SQLiteDatabase db = mSqliteHelper.getWritableDatabase();
        String parent_id = "";
        //查询工厂的父节点
        Cursor cursor = db.rawQuery("select parent_id from P_AUTH_ORG where org_id = ?",
                new String[]{workId});
        while (cursor.moveToNext()) {
            parent_id = cursor.getString(0);
        }
        cursor.close();
        if (TextUtils.isEmpty(parent_id)) {
            db.close();
            return null;
        }

        Cursor cursor_cz = db.rawQuery("select cost_center,cost_center_desc from BASE_COST_CENTER where org_id = ?",
                new String[]{parent_id});
        while (cursor_cz.moveToNext()) {
            if (!TextUtils.isEmpty(cursor_cz.getString(1))) {
                list.add(cursor_cz.getString(0) + "_" + cursor_cz.getString(1));
            } else {
                list.add(cursor_cz.getString(0));
            }
        }
        cursor_cz.close();
        db.close();
        return list;
    }

    /**
     * 获取项目编号
     *
     * @param workId:工厂id
     */
    public ArrayList<String> getProjectNumListByWorkId(String workId) {
        ArrayList<String> list = new ArrayList<>();
        if (TextUtils.isEmpty(workId)) {
            return list;
        }

        return list;
    }

    /**
     * 获取网络编号
     *
     * @param workId:工厂id
     */
    public ArrayList<String> getNetNumListByWorkId(String workId) {
        return null;
    }

    /**
     * 获取Fragment页面的配置信息
     *
     * @param bizType:业务类型
     * @param refType:单据类型
     * @param fragmentType:Fragment类型，1~3分别表示抬头，数据明细，数据采集界面，-1表示明细修改
     * @return
     */
    public ArrayList<BizFragmentConfig> readBizFragmentConfig(String bizType, String refType, int fragmentType) {
        ArrayList<BizFragmentConfig> bizFragmentConfigs = new ArrayList<>();

        if (TextUtils.isEmpty(bizType)) {
            return bizFragmentConfigs;
        }
        SQLiteDatabase db = mSqliteHelper.getReadableDatabase();
        Cursor cursor = null;
        if (fragmentType < 0) {
            if (TextUtils.isEmpty(refType)) {
                cursor = db.rawQuery("select * from T_FRAGMENT_CONFIGS where biz_type = ? and fragment_type = -1 order by fragment_type",
                        new String[]{bizType});
            } else {
                cursor = db.rawQuery("select * from T_FRAGMENT_CONFIGS where biz_type = ? and ref_type = ? and fragment_type = -1 order by fragment_type",
                        new String[]{bizType, refType});
            }
        } else {
            if (TextUtils.isEmpty(refType)) {
                cursor = db.rawQuery("select * from T_FRAGMENT_CONFIGS where biz_type = ? and fragment_type >= 0 order by fragment_type ",
                        new String[]{bizType});
            } else {
                cursor = db.rawQuery("select * from T_FRAGMENT_CONFIGS where biz_type = ? and ref_type = ? and fragment_type >= 0 order by fragment_type ",
                        new String[]{bizType, refType});
            }
        }

        if (cursor != null) {
            while (cursor.moveToNext()) {
                BizFragmentConfig config = new BizFragmentConfig();
                config.id = cursor.getString(0);
                config.fragmentTag = cursor.getString(1);
                config.bizType = cursor.getString(2);
                config.refType = cursor.getString(3);
                config.tabTitle = cursor.getString(4);
                config.fragmentType = cursor.getInt(5);
                config.className = cursor.getString(6);
                bizFragmentConfigs.add(config);
            }
            cursor.close();
        }
        db.close();
        return bizFragmentConfigs;
    }

    public String getStorageNum(String workId, String workCode, String invId, String invCode) {
        SQLiteDatabase db = getReadableDB();
        Cursor cursor = db.rawQuery("select storage_code from p_auth_org where parent_id = ? and org_code = ? and org_level = ?",
                new String[]{workId, invCode, "3"});

        String storageNum = "";
        while (cursor.moveToNext()) {
            storageNum = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return storageNum;
    }
}
