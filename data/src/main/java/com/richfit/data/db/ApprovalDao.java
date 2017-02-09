package com.richfit.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.L;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.domain.bean.ImageEntity;
import com.richfit.domain.bean.RefDetailEntity;
import com.richfit.domain.bean.ReferenceEntity;
import com.richfit.domain.bean.ResultEntity;
import com.richfit.domain.bean.RowConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import static com.richfit.common_lib.utils.Global.HEADER_CONFIG_TYPE;


/**
 * 验收模块的Dao层
 * Created by monday on 2016/12/13.
 */

public class ApprovalDao extends BaseDao {

    @Inject
    public ApprovalDao(Context context) {
        super(context);
    }

    /**
     * 删除验收整单的缓存图片
     *
     * @param refNum
     * @param isLocal
     * @return
     */
    public void deleteInspectionImages(String refNum, boolean isLocal) {
        SQLiteDatabase db = mSqliteHelper.getReadableDatabase();
        db.delete("MTL_IMAGES", "ref_num = ? and local_flag = ?", new String[]{refNum, isLocal ? "Y" : "N"});
        db.close();
    }

    /**
     * 删除整行图片
     *
     * @param refNum：参考单据
     * @param refLineId：明细行id
     * @param refLineNum：行号
     * @param isLocal：是否是离线模式
     * @return
     */
    public void deleteInspectionImagesSingle(String refNum, String refLineNum, String refLineId, boolean isLocal) {

        if (TextUtils.isEmpty(refLineId) || TextUtils.isEmpty(refLineNum)) {
            return;
        }
        SQLiteDatabase db = null;
        try {
            db = mSqliteHelper.getReadableDatabase();
            db.delete("MTL_IMAGES", "ref_line_id = ? and local_flag = ?", new String[]{refLineId, isLocal ? "Y" : "N"});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * 在拍照界面，用户选择需要删除的图片集合，然后进行删除。
     *
     * @param images：需要删除的图片集合
     * @return
     */
    public void deleteTakedImages(ArrayList<ImageEntity> images, boolean isLocal) {
        SQLiteDatabase db = mSqliteHelper.getReadableDatabase();
        for (ImageEntity image : images) {
            if (!image.isSelected)
                continue;
            db.delete("MTL_IMAGES", "image_name = ? and local_flag = ?", new String[]{image.imageName,
                    isLocal ? "Y" : "N"});
        }
        db.close();
    }


    /**
     * 保存拍照获取的照片信息
     *
     * @param images：照片数据源
     * @param refNum：单据号
     * @param refLineId：行id
     * @param takePhotoType：拍照类型
     * @param imageDir：sd卡缓存的照片目录
     * @param isLocal：离线或者在线模式
     */
    public void saveTakedImages(ArrayList<ImageEntity> images, String refNum, String refLineId,
                                int takePhotoType, String imageDir, boolean isLocal) {
        SQLiteDatabase db = mSqliteHelper.getWritableDatabase();
        //第一步删除这一行所有的图片
//        if (!TextUtils.isEmpty(refLineId)) {
//            db.delete("MTL_IMAGES", "ref_num = ? and ref_line_id = ? and local_flag = ?",
//                    new String[]{refNum, refLineId, isLocal ? "Y" : "N"});
//        } else {
//            //整单拍照
//            db.delete("MTL_IMAGES", "ref_num = ? and local_flag = ?",
//                    new String[]{refNum, isLocal ? "Y" : "N"});
//        }
        //第二步插入图片
        for (ImageEntity image : images) {
            db.delete("MTL_IMAGES", "ref_num = ? and local_flag = ? and image_name = ?",
                    new String[]{refNum, isLocal ? "Y" : "N", image.imageName});
            ContentValues cv = new ContentValues();
            String id = UiUtil.getUUID();
            image.id = id;
            cv.put("id", id);
            cv.put("ref_line_id", refLineId);
            cv.put("ref_num", refNum);
            cv.put("image_dir", imageDir);
            cv.put("image_name", image.imageName);
            cv.put("created_by", Global.USER_ID);
            cv.put("local_flag", isLocal ? "Y" : "N");
            cv.put("take_photo_type", takePhotoType);
            cv.put("biz_type", image.bizType);
            cv.put("ref_type", image.refType);
            L.e("bizTye = " + image.bizType + "; refType = " + image.refType);
            cv.put("creation_date", UiUtil.transferLongToDate("yyyyMMddHHmmss", image.lastModifiedTime));
            db.insert("MTL_IMAGES", null, cv);
        }
        db.close();
    }

    /**
     * 读取整单缓存图片
     *
     * @param refNum
     * @param isLocal
     */
    public ArrayList<ImageEntity> readImagesByRefNum(String refNum, boolean isLocal) {
        ArrayList<ImageEntity> images = new ArrayList<>();
        SQLiteDatabase db = mSqliteHelper.getReadableDatabase();
        Cursor cursor;
        cursor = db.rawQuery("select ref_line_id,image_dir,image_name,created_by,creation_date,take_photo_type,biz_type,ref_type from MTL_IMAGES where ref_num = ? and local_flag = ?",
                new String[]{refNum, isLocal ? "Y" : "N"});
        while (cursor.moveToNext()) {
            ImageEntity image = new ImageEntity();
            image.refLineId = cursor.getString(0);
            image.imageDir = cursor.getString(1);
            image.imageName = cursor.getString(2);
            image.createBy = cursor.getString(3);
            image.createDate = cursor.getString(4);
            image.takePhotoType = cursor.getInt(5);
            image.bizType = cursor.getString(6);
            image.refType = cursor.getString(7);
            images.add(image);
        }
        cursor.close();
        db.close();
        return images;
    }

    /**
     * 保存验收清单单据数据到本地
     *
     * @param refData：单据数据源
     */
    public void saveApprovalRefData2Local(ReferenceEntity refData, String refType, String bizType, String moveType) {
        //保存抬头
        SQLiteDatabase db = mSqliteHelper.getWritableDatabase();
        StringBuffer sb = new StringBuffer();
        sb.append("INSERT OR REPLACE INTO MTL_INSPECTION_HEADERS");
        sb.append(" (id,ref_type,biz_type,move_type,record_num,supplier_num,po_num,created_by,voucher_date)");
        sb.append(" VALUES(?,?,?,?,?,?,?,?,?)");

        db.execSQL(sb.toString(), new Object[]{refData.refCodeId, refType, bizType, moveType, refData.recordNum,
                refData.supplierNum, refData.poNum, Global.USER_ID, refData.voucherDate});

        sb.setLength(0);

        //保存抬头的额外字段信息

        saveExtraMap(db, refData.mapExt, refData.refCodeId, "", "", HEADER_CONFIG_TYPE);

        List<RefDetailEntity> details = refData.billDetailList;

        //保存行
        for (RefDetailEntity entity : details) {
            sb.append("INSERT OR REPLACE INTO MTL_INSPECTION_LINES ");
            sb.append("(id,ref_code_id,record_num,line_num,arrival_quantity,");
            sb.append("batch_flag,inspection_date,inspection_result,inv_id,");
            sb.append("inv_code,inv_name,lineInspect_flag,material_id,material_num,");
            sb.append("material_desc,material_group,order_quantity,photo_flag,");
            sb.append("poLine_num,total_quantity,");
            sb.append("unit,work_id,work_code,work_name) ");
            sb.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            db.execSQL(sb.toString(), new Object[]{entity.refLineId, refData.refCodeId, refData.recordNum,
                    entity.lineNum, entity.arrivalQuantity, entity.batchFlag, entity.inspectionDate, entity.inspectionResult,
                    entity.invId, entity.invCode, entity.invName, entity.lineInspectFlag, entity.materialId,
                    entity.materialNum, entity.materialDesc, entity.materialGroup, entity.orderQuantity,
                    entity.photoFlag, entity.poLineNum, entity.totalQuantity, entity.unit, entity.workId,
                    entity.workCode, entity.workName});

            sb.setLength(0);

            //保存行的额外字段
            saveExtraMap(db, entity.mapExt, refData.refCodeId, entity.refLineId, "", Global.COLLECT_CONFIG_TYPE);
        }
        db.close();
    }

    /**
     * 获取验收清单单据数据
     *
     * @param refNum：单据号
     * @param userId：用户id
     * @return
     */
    public ReferenceEntity getReference(String refNum, String userId) {
        SQLiteDatabase db = mSqliteHelper.getReadableDatabase();
        //读取抬头信息
        String refCodeId = "";
        Cursor cursor = db.rawQuery("select id from MTL_INSPECTION_HEADERS where record_num = ? and created_by = ?",
                new String[]{refNum, userId});
        while (cursor.moveToNext()) {
            refCodeId = cursor.getString(0);
        }
        cursor.close();
        ReferenceEntity refData = new ReferenceEntity();
        if (TextUtils.isEmpty(refCodeId)) {
            db.close();
            return refData;
        }

        StringBuffer sb = new StringBuffer();

        //读取抬头单据
        sb.append("select record_num,ref_type,biz_type,move_type,supplier_num,po_num,voucher_date ");
        sb.append(" from MTL_INSPECTION_HEADERS where id = ?");

        Cursor headerCursor = db.rawQuery(sb.toString(), new String[]{refCodeId});

        while (headerCursor.moveToNext()) {
            refData.refCodeId = refCodeId;
            refData.recordNum = headerCursor.getString(0);
            refData.refType = headerCursor.getString(1);
            refData.bizType = headerCursor.getString(2);
            refData.moveType = headerCursor.getString(3);
            refData.supplierNum = headerCursor.getString(4);
            refData.poNum = headerCursor.getString(5);
            refData.voucherDate = headerCursor.getString(6);
        }
        headerCursor.close();
        sb.setLength(0);

        //读取抬头额外信息
        Map<String, Object> headerExtraMap = readExtraMap(db, HEADER_CONFIG_TYPE, refCodeId, "", "");
        refData.mapExt = headerExtraMap;

        //读取行明细信息
        sb.append("select id,record_num,line_num,arrival_quantity,");
        sb.append("batch_flag,inspection_date,inspection_result,inv_id,inv_code,inv_name,");
        sb.append("lineInspect_flag,material_id,material_num,material_desc,material_group,order_quantity,");
        sb.append("photo_flag,poLine_num,");
        sb.append("total_quantity,unit,work_id,work_code,work_name ");
        sb.append("from MTL_INSPECTION_LINES where ref_code_id = ?");

        Cursor detailCursor = db.rawQuery(sb.toString(), new String[]{refCodeId});

        ArrayList<RefDetailEntity> details = new ArrayList<>();
        while (detailCursor.moveToNext()) {
            RefDetailEntity entity = new RefDetailEntity();
            entity.refLineId = detailCursor.getString(0);
            entity.recordNum = detailCursor.getString(1);
            entity.lineNum = detailCursor.getString(2);
            entity.arrivalQuantity = detailCursor.getString(3);
            entity.batchFlag = detailCursor.getString(4);
            entity.inspectionDate = detailCursor.getString(5);
            entity.inspectionResult = detailCursor.getString(6);
            entity.invId = detailCursor.getString(7);
            entity.invCode = detailCursor.getString(8);
            entity.invName = detailCursor.getString(9);
            entity.lineInspectFlag = detailCursor.getString(10);
            entity.materialId = detailCursor.getString(11);
            entity.materialNum = detailCursor.getString(12);
            entity.materialDesc = detailCursor.getString(13);
            entity.materialGroup = detailCursor.getString(14);
            entity.orderQuantity = detailCursor.getString(15);
            entity.photoFlag = detailCursor.getColumnName(16);
            entity.poLineNum = detailCursor.getString(17);
            entity.totalQuantity = detailCursor.getString(18);
            entity.unit = detailCursor.getString(19);
            entity.workId = detailCursor.getString(20);
            entity.workCode = detailCursor.getString(21);
            entity.workName = detailCursor.getString(22);
            //读取行额外信息
            Map<String, Object> extraLineMap = readExtraMap(db, Global.COLLECT_CONFIG_TYPE, refCodeId, entity.refLineId, "");
            entity.mapExt = extraLineMap;

            details.add(entity);
        }
        detailCursor.close();
        db.close();

        refData.billDetailList = details;

        //如果行里面有缓存，那么将抬头的标志位tempFlag="Y"
        for (RefDetailEntity detail : details) {
            if (!TextUtils.isEmpty(detail.totalQuantity) && !"0".equals(detail.totalQuantity)) {
                refData.tempFlag = "Y";
                break;
            }
        }

        return refData;
    }

    /**
     * 读取验收清单明细信息，显示在离线数据下载界面
     *
     * @return
     */
    public ArrayList<ReferenceEntity> readApprovalReference() {
        SQLiteDatabase db = mSqliteHelper.getReadableDatabase();
        //读取抬头信息
        ArrayList<ReferenceEntity> reDatas = new ArrayList<>();

        //读取抬头的所有单据信息
        StringBuffer sb = new StringBuffer();
        sb.append("select id,record_num,ref_type,biz_type,move_type,supplier_num,po_num,voucher_date ");
        sb.append(" from MTL_INSPECTION_HEADERS ");

        Cursor headerCursor = db.rawQuery(sb.toString(), null);

        while (headerCursor.moveToNext()) {
            ReferenceEntity refData = new ReferenceEntity();
            refData.refCodeId = headerCursor.getString(0);
            refData.recordNum = headerCursor.getString(1);
            refData.refType = headerCursor.getString(2);
            refData.bizType = headerCursor.getString(3);
            refData.moveType = headerCursor.getString(4);
            refData.supplierNum = headerCursor.getString(5);
            refData.poNum = headerCursor.getString(6);
            refData.voucherDate = headerCursor.getString(7);

            //读取抬头额外信息
            Map<String, Object> headerExtraMap = readExtraMap(db, HEADER_CONFIG_TYPE, refData.refCodeId, "", "");
            refData.mapExt = headerExtraMap;

            reDatas.add(refData);
        }

        sb.setLength(0);
        headerCursor.close();

        //读取行明细
        for (ReferenceEntity data : reDatas) {

            ArrayList<RefDetailEntity> details = new ArrayList<>();

            //读取行明细信息
            sb.append("select id,record_num,line_num,arrival_quantity,");
            sb.append("batch_flag,inspection_date,inspection_result,inv_id,inv_code,inv_name,");
            sb.append("lineInspect_flag,material_id,material_num,material_desc,material_group,order_quantity,");
            sb.append("photo_flag,poLine_num,");
            sb.append("total_quantity,unit,work_id,work_code,work_name ");
            sb.append("from MTL_INSPECTION_LINES where ref_code_id = ?");

            Cursor detailCursor = db.rawQuery(sb.toString(), new String[]{data.refCodeId});

            while (detailCursor.moveToNext()) {
                RefDetailEntity entity = new RefDetailEntity();
                entity.refLineId = detailCursor.getString(0);
                entity.recordNum = detailCursor.getString(1);
                entity.lineNum = detailCursor.getString(2);
                entity.arrivalQuantity = detailCursor.getString(3);
                entity.batchFlag = detailCursor.getString(4);
                entity.inspectionDate = detailCursor.getString(5);
                entity.inspectionResult = detailCursor.getString(6);
                entity.invId = detailCursor.getString(7);
                entity.invCode = detailCursor.getString(8);
                entity.invName = detailCursor.getString(9);
                entity.lineInspectFlag = detailCursor.getString(10);
                entity.materialId = detailCursor.getString(11);
                entity.materialNum = detailCursor.getString(12);
                entity.materialDesc = detailCursor.getString(13);
                entity.materialGroup = detailCursor.getString(14);
                entity.orderQuantity = detailCursor.getString(15);
                entity.photoFlag = detailCursor.getColumnName(16);
                entity.poLineNum = detailCursor.getString(17);
                entity.totalQuantity = detailCursor.getString(18);
                entity.unit = detailCursor.getString(19);
                entity.workId = detailCursor.getString(20);
                entity.workCode = detailCursor.getString(21);
                entity.workName = detailCursor.getString(22);

                //读取行额外信息
                Map<String, Object> extraLineMap = readExtraMap(db, Global.COLLECT_CONFIG_TYPE, data.refCodeId, entity.refLineId, "");
                entity.mapExt = extraLineMap;

                details.add(entity);
            }
            sb.setLength(0);
            detailCursor.close();
            data.billDetailList = details;
        }


        db.close();
        return reDatas;
    }

    /**
     * 删除整单验收清单数据
     *
     * @param refCodeId：单据抬头id
     * @param userId：用户id
     */
    public void deleteCollectionData(String refType, String recordNum, String refCodeId, String userId,
                                     String companyCode, String bizType) {

        SQLiteDatabase db = mSqliteHelper.getReadableDatabase();

        //1.更新抬头缓存
        ContentValues headerCV = new ContentValues();
//        headerCV.put("inspect_flag", "N");
        headerCV.put("voucher_date", "");
        db.update("MTL_INSPECTION_HEADERS", headerCV, "id = ? and created_by = ?",
                new String[]{refCodeId, userId});

        //更新抬头的额外字段缓存
        //2. 读取扩展字段配置信息
        ArrayList<RowConfig> headerExtraConfigs = readExtraConfigInfo(db, refType, companyCode, bizType, HEADER_CONFIG_TYPE);
        ContentValues headerExtraCV = new ContentValues();
        for (RowConfig config : headerExtraConfigs) {
            if ("Y".equals(config.displayFlag) && config.uiType != "0") {
                headerExtraCV.put(config.propertyCode, "");
            }
        }
        if (headerExtraCV.size() > 0)
            db.update("T_EXTRA_HEADER", headerExtraCV, "id = ? and config_type = ?", new String[]{refCodeId, HEADER_CONFIG_TYPE});

        //第二步更新单据行的数据
        //1. 查询所有的行id
        ArrayList<String> refLineIds = new ArrayList<>();
        Cursor cursor = db.rawQuery("select id from MTL_INSPECTION_LINES where ref_code_id = ?"
                , new String[]{refCodeId});
        while (cursor.moveToNext()) {
            refLineIds.add(cursor.getString(0));
        }

        //2.更新验收行的缓存
        ContentValues lineCV = new ContentValues();
        //累计合格数量
        lineCV.put("total_quantity", "0");
        //验收结果
        lineCV.put("inspection_result", "");
        //是否已经验收
        lineCV.put("lineInspect_flag", "N");
        //非过账的所有验收行项目
        db.update("MTL_INSPECTION_LINES", lineCV, "ref_code_id = ? and lineInspect_flag != ?"
                , new String[]{refCodeId, "Y"});

        //3.删除单据行的额外扩展字段缓存
        ArrayList<RowConfig> extraLineConfigs = readExtraConfigInfo(db, refType, companyCode, bizType, Global.COLLECT_CONFIG_TYPE);
        ContentValues lineExtraCV = new ContentValues();
        for (String refLineId : refLineIds) {
            lineExtraCV.clear();
            for (RowConfig config : extraLineConfigs) {
                if ("Y".equals(config.displayFlag) && config.uiType != "0") {
                    lineExtraCV.put(config.propertyCode, "");
                }
            }
            if (lineExtraCV.size() > 0)
                db.update("T_EXTRA_LINE", lineExtraCV, "id = ? and config_type = ?",
                        new String[]{refLineId, Global.COLLECT_CONFIG_TYPE});
        }

        //删除图片
        db.delete("MTL_IMAGES", "ref_num = ? and local_flag = ?", new String[]{recordNum, "Y"});
        db.close();
    }

    /**
     * 数据采集获取单条缓存
     *
     * @param refCodeId：单据抬头id
     * @param refType：单据类型
     * @param bizType：业务类型
     * @param refLineId：单据行id
     * @param workId：工厂id
     * @param invId：库存地点id
     * @param recWorkId：接收工厂id
     * @param recInvId：接收库存地点id
     * @param materialNum：物料编码
     * @param batchFlag：批次
     * @param location：仓位
     * @param userId：用户id
     * @return
     */
    public ArrayList<RefDetailEntity> getTransferInfoSingle(String refCodeId, String refType, String bizType,
                                                            String refLineId, String workId, String invId,
                                                            String recWorkId, String recInvId, String materialNum,
                                                            String batchFlag, String location, String userId) {

        final ArrayList<RefDetailEntity> details = new ArrayList<>();

        if (TextUtils.isEmpty(refLineId)) {
            return details;
        }

        SQLiteDatabase db = mSqliteHelper.getWritableDatabase();

        //找出没有验收过的缓存行
        Cursor cursor = db.rawQuery("select id,inspection_result,total_quantity from MTL_INSPECTION_LINES where id = ? and lineInspect_flag != ?",
                new String[]{refLineId, "Y"});

        while (cursor.moveToNext()) {
            RefDetailEntity detail = new RefDetailEntity();
            detail.refLineId = cursor.getString(0);
            detail.inspectionResult = cursor.getString(1);
            detail.totalQuantity = cursor.getString(2);
            //读取额外字段
            Map<String, Object> extraLineMap = readTransExtraMap(db, Global.HEADER_CONFIG_TYPE, "", refLineId, "");
            detail.mapExt = extraLineMap;
            details.add(detail);
        }

        cursor.close();
        db.close();
        return details;
    }

    /**
     * 数据采集界面保存单条数据。对于验收来说，固定字段和扩展字段的数据都是
     * 存放在一个表里面。
     *
     * @param result
     */
    public void uploadCollectionDataSingle(ResultEntity result) {
        final String refCodeId = result.refCodeId;
        final String refLineId = result.refLineId;
        if (TextUtils.isEmpty(refCodeId) || TextUtils.isEmpty(refLineId)) {
            return;
        }
        final String inspectionResult = result.inspectionResult;
        final String inspectionPerson = result.inspectionPerson;
        final String quantity = result.quantity;
        final String voucherDate = result.voucherDate;
        final String modifyFlag = result.modifyFlag;
        final Map<String, Object> extraHeaderMap = result.mapExHead;
        final Map<String, Object> extraLineMap = result.mapExLine;

        SQLiteDatabase db = mSqliteHelper.getWritableDatabase();
        //更新抬头
        ContentValues headerCV = new ContentValues();
        //注意这里的inspectFlag标志是表示是否有缓存
        headerCV.put("voucher_date", voucherDate);
        db.update("MTL_INSPECTION_HEADERS", headerCV, "id = ? and created_by = ?",
                new String[]{refCodeId, inspectionPerson});

        //更新抬头的额外字段信息
        saveExtraMap(db, extraHeaderMap, refCodeId, "", "", HEADER_CONFIG_TYPE);
        StringBuffer sb = new StringBuffer();
        if ("N".equals(modifyFlag)) {
            sb.append("update MTL_INSPECTION_LINES set inspection_result = ?, total_quantity = total_quantity + ?")
                    .append(" where id = ? and ref_code_id = ?");
        } else {
            sb.append("update MTL_INSPECTION_LINES set inspection_result = ?, total_quantity = ?")
                    .append(" where id = ? and ref_code_id = ?");
        }
        db.execSQL(sb.toString(), new Object[]{inspectionResult, quantity, refLineId, refCodeId});
        //更新行的额外字段信息
        saveExtraMap(db, extraLineMap, refCodeId, refLineId, "", Global.COLLECT_CONFIG_TYPE);
    }


    /**
     * 删除单条验收明细数据
     *
     * @param refLineId
     */
    protected void deleteCollectionDataSingle(String refLineId, String refType, String companyCode, String bizType) {
        if (TextUtils.isEmpty(refLineId))
            return;
        SQLiteDatabase db = mSqliteHelper.getReadableDatabase();

        //1. 更新明细行的缓存
        ContentValues lineCV = new ContentValues();
        //累计合格数量
        lineCV.put("total_quantity", "0");
        //验收结果
        lineCV.put("inspection_result", "");
        //是否已经验收
        lineCV.put("lineInspect_flag", "N");
        //非过账的所有验收行项目
        db.update("MTL_INSPECTION_LINES", lineCV, "id = ? and lineInspect_flag != ?"
                , new String[]{refLineId, "Y"});

        //2. 更新该明细行的扩展字段
        ArrayList<RowConfig> extraLineConfigs = readExtraConfigInfo(db, refType, companyCode,
                bizType, Global.COLLECT_CONFIG_TYPE);
        ContentValues lineExtraCV = new ContentValues();

        lineExtraCV.clear();
        for (RowConfig config : extraLineConfigs) {
            if ("Y".equals(config.displayFlag) && config.uiType != "0") {
                lineExtraCV.put(config.propertyCode, "");
            }
        }
        if (lineExtraCV.size() > 0)
            db.update("T_EXTRA_LINE", lineExtraCV, "id = ? and config_type = ?",
                    new String[]{refLineId, Global.COLLECT_CONFIG_TYPE});
        db.close();
    }
}
