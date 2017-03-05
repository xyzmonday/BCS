package com.richfit.data.repository;

import android.support.annotation.NonNull;

import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.domain.bean.BizFragmentConfig;
import com.richfit.domain.bean.ImageEntity;
import com.richfit.domain.bean.InvEntity;
import com.richfit.domain.bean.InventoryEntity;
import com.richfit.domain.bean.LoadBasicDataWrapper;
import com.richfit.domain.bean.LoadDataTask;
import com.richfit.domain.bean.MaterialEntity;
import com.richfit.domain.bean.MenuNode;
import com.richfit.domain.bean.RefNumEntity;
import com.richfit.domain.bean.ReferenceEntity;
import com.richfit.domain.bean.ResultEntity;
import com.richfit.domain.bean.RowConfig;
import com.richfit.domain.bean.SimpleEntity;
import com.richfit.domain.bean.UpdateEntity;
import com.richfit.domain.bean.UserEntity;
import com.richfit.domain.bean.WorkEntity;
import com.richfit.domain.repository.ILocalRepository;
import com.richfit.domain.repository.IServerRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import io.reactivex.Flowable;

/**
 * 数据仓库，该仓库管理了远程数据，本地数据（数据库，SharePreference），
 * 文件和内存缓存
 * Created by monday on 2016/12/29.
 */

public class Repository implements ILocalRepository, IServerRepository {

    private ILocalRepository mLocalRepository;
    private IServerRepository mServerRepository;
    //在线模式和了离线模式标识
    private boolean isLocal;

    @Inject
    public Repository(IServerRepository serverRepository, ILocalRepository localRepository) {
        this.mServerRepository = serverRepository;
        this.mLocalRepository = localRepository;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    @Override
    public Flowable<UserEntity> Login(String userName, String password) {
        return mServerRepository.Login(userName, password);
    }

    @Override
    public Flowable<ArrayList<MenuNode>> getMenuTreeInfo(String loginId, int offLine) {
        return mServerRepository.getMenuTreeInfo(loginId, offLine);
    }

    @Override
    public Flowable<ArrayList<RowConfig>> loadExtraConfig(String companyId) {
        return mServerRepository.loadExtraConfig(companyId);
    }

    @Override
    public Flowable<ArrayList<BizFragmentConfig>> loadBizFragmentConfig(String companyId, int mode) {
        return null;
    }

    /**
     * 下载基础数据的总条数
     *
     * @param param:需要下载的基础数据的类型
     * @return
     */
    @Override
    public Flowable<LoadBasicDataWrapper> preparePageLoad(@NonNull LoadBasicDataWrapper param) {
        return mServerRepository.preparePageLoad(param);
    }

    /**
     * 下载基础数据。对于增量更新的数据（仓位和物料）那么需要保存当前请求的日期，
     * 对于非增量更新的数据，那么使用当前日期即可。
     *
     * @param task
     * @return
     */
    @Override
    public Flowable<List<Map<String, Object>>> loadBasicData(final LoadDataTask task) {

        final String queryType = task.queryType;
        final String currentDate = UiUtil.getCurrentDate(Global.GLOBAL_DATE_PATTERN_TYPE4);
        //针对增量更新的基础数据，使用上一次请求的日期
        final String queryDate = "CW".equals(queryType) ? getLoadBasicDataTaskDate(queryType) : currentDate;
        //保存查询日期
        task.queryDate = queryDate;
        final Map<String, Object> tmp = new HashMap<>();
        tmp.put("queryType", queryType);
        tmp.put("queryDate", queryDate);
        tmp.put("isFirstPage", task.isFirstPage);
        tmp.put("taskId", task.id);
        return mServerRepository.loadBasicData(task)
                .filter(list -> list != null && list.size() > 0)
                .zipWith(Flowable.just(tmp), (maps, map) -> {
                    maps.add(0, map);
                    return maps;
                })
                .onBackpressureBuffer()
                .doOnNext(map -> {
                    if (!"CW".equals(queryType))
                        return;
                    saveLoadBasicDataTaskDate(queryType, currentDate);
                });
    }

    @Override
    public Flowable<String> syncDate() {
        return mServerRepository.syncDate();
    }

    @Override
    public Flowable<String> getMappingInfo() {
        return mServerRepository.getMappingInfo();
    }


    @Override
    public Flowable<ArrayList<String>> readUserInfo(String userName, String password) {
        return mLocalRepository.readUserInfo(userName, password);
    }

    @Override
    public void saveUserInfo(UserEntity userEntity) {
        mLocalRepository.saveUserInfo(userEntity);
    }

    @Override
    public void saveExtraConfigInfo(List<RowConfig> configs) {
        mLocalRepository.saveExtraConfigInfo(configs);
    }

    @Override
    public Flowable<ArrayList<RowConfig>> readExtraConfigInfo(String companyCode, String bizType, String refType, String configType) {
        return mLocalRepository.readExtraConfigInfo(companyCode, bizType, refType, configType);
    }

    @Override
    public Flowable<Map<String, Object>> readExtraDataSourceByDictionary(@NonNull String propertyCode, @NonNull String dictionaryCode) {
        return mLocalRepository.readExtraDataSourceByDictionary(propertyCode, dictionaryCode);
    }

    /**
     * 获取基础数据下载的日期
     *
     * @param requestType：下载基础数据的请求类型
     * @return
     */
    @Override
    public String getLoadBasicDataTaskDate(@NonNull String requestType) {
        return mLocalRepository.getLoadBasicDataTaskDate(requestType);
    }

    /**
     * 更新额外信息表字段
     *
     * @param map:需要插入的字段（列明），key表示需要插入的表的类型分别是抬头，明细和采集； value表示需要插入的列明的集合
     */
    @Override
    public void updateExtraConfigTable(Map<String, Set<String>> map) {
        mLocalRepository.updateExtraConfigTable(map);
    }


    @Override
    public Flowable<String> uploadCollectionDataSingle(ResultEntity result) {
        return isLocal ? mLocalRepository.uploadCollectionDataSingle(result) :
                mServerRepository.uploadCollectionDataSingle(result);
    }

    @Override
    public Flowable<String> uploadCollectionData(String refCodeId, String transId, String bizType,
                                                 String refType, int inspectionType, String voucherDate,
                                                 String remark, String userId) {
        return mServerRepository.uploadCollectionData(refCodeId, transId, bizType, refType, inspectionType, voucherDate, remark, userId);
    }

    @Override
    public Flowable<String> transferCollectionData(ResultEntity result) {
        return mServerRepository.transferCollectionData(result);
    }

    @Override
    public Flowable<String> transferCollectionData(String transId, String bizType, String refType, String userId, String voucherDate,
                                                   Map<String, Object> flagMap, Map<String, Object> extraHeaderMap) {
        return mServerRepository.transferCollectionData(transId, bizType, refType, userId, voucherDate, flagMap, extraHeaderMap);
    }

    @Override
    public Flowable<List<RefNumEntity>> getReserveNumList(String beginDate, String endDate, String loginId, String refType) {
        return mServerRepository.getReserveNumList(beginDate, endDate, loginId, refType);
    }

    @Override
    public Flowable<String> uploadInspectionImage(ResultEntity result) {
        return mServerRepository.uploadInspectionImage(result);
    }

    @Override
    public Flowable<String> uploadCheckDataSingle(ResultEntity result) {
        return mServerRepository.uploadCheckDataSingle(result);
    }

    @Override
    public Flowable<List<InventoryEntity>> getInventoryInfo(String queryType, String workId, String invId,
                                                            String workCode, String invCode, String storageNum,
                                                            String materialNum, String materialId, String materialGroup,
                                                            String materialDesc, String batchFlag,
                                                            String location, String specialInvFlag, String specialInvNum, String invType) {
        return mServerRepository.getInventoryInfo(queryType, workId, invId, workCode, invCode, storageNum, materialNum, materialId, materialGroup,
                materialDesc, batchFlag, location, specialInvFlag, specialInvNum, invType);
    }

    @Override
    public Flowable<String> getLocationInfo(String queryType, String workId, String invId, String location) {
        return mServerRepository.getLocationInfo(queryType, workId, invId, location);
    }

    @Override
    public Flowable<UpdateEntity> getAppVersion() {
        return mServerRepository.getAppVersion();
    }

    /**
     * 获取单据数据，如果是离线则从本地数据库中获取
     *
     * @param refNum：单号
     * @param refType:单据类型
     * @param bizType：业务类型
     * @param moveType:移动类型
     * @param userId：用户loginId
     * @return
     */
    @Override
    public Flowable<ReferenceEntity> getReference(@NonNull String refNum, @NonNull String refType,
                                                  @NonNull String bizType, @NonNull String moveType, @NonNull String userId) {
        return isLocal ? mLocalRepository.getReference(refNum, refType, bizType, moveType, userId) :
                mServerRepository.getReference(refNum, refType, bizType, moveType, userId);
    }

    /**
     * 抬头界面删除整单缓存数据
     *
     * @param refNum：单据号
     * @param transId：缓存id
     * @param refCodeId:单据抬头id
     * @param refType:单据类型
     * @param bizType:业务类型
     * @param userId：用户id
     * @return
     */
    @Override
    public Flowable<String> deleteCollectionData(String refNum, String transId, String refCodeId,
                                                 String refType, String bizType, String userId,
                                                 String companyCode) {
        return isLocal ? mLocalRepository.deleteCollectionData(refNum, transId, refCodeId,
                refType, bizType, userId, companyCode) : mServerRepository.deleteCollectionData(refNum, transId, refCodeId,
                refType, bizType, userId, companyCode);
    }

    /**
     * 获取整单缓存
     *
     * @param refCodeId：单据id
     * @param bizType:业务类型
     * @param refType：单据类型
     * @return
     */
    @Override
    public Flowable<ReferenceEntity> getTransferInfo(String recordNum, String refCodeId, String bizType, String refType, String userId,
                                                     String workId, String invId, String recWorkId, String recInvId) {
        return isLocal ? mLocalRepository.getTransferInfo(recordNum, refCodeId, bizType, refType, userId,
                workId, invId, recWorkId, recInvId) :
                mServerRepository.getTransferInfo(recordNum, refCodeId, bizType, refType, userId,
                        workId, invId, recWorkId, recInvId);
    }

    @Override
    public Flowable<ReferenceEntity> getTransferInfoSingle(String refCodeId, String refType, String bizType, String refLineId,
                                                           String workId, String invId, String recWorkId, String recInvId,
                                                           String materialNum, String batchFlag, String location, String userId) {
        return isLocal ? mLocalRepository.getTransferInfoSingle(refCodeId, refType, bizType, refLineId, workId, invId, recWorkId, recInvId, materialNum, batchFlag, location, userId)
                : mServerRepository.getTransferInfoSingle(refCodeId, refType, bizType, refLineId, workId, invId, recWorkId, recInvId, materialNum, batchFlag, location, userId);
    }


    @Override
    public Flowable<String> deleteCollectionDataSingle(String lineDeleteFlag, String transId, String transLineId,
                                                       String locationId, String refType, String bizType, String refLineId, String userId,
                                                       int position, String companyCode) {
        return isLocal ? mLocalRepository.deleteCollectionDataSingle(lineDeleteFlag, transId,
                transLineId, locationId, refType, bizType, refLineId, userId, position, companyCode) :
                mServerRepository.deleteCollectionDataSingle(lineDeleteFlag, transId,
                        transLineId, locationId, refType, bizType, refLineId, userId, position, companyCode);
    }

    @Override
    public Flowable<ReferenceEntity> getCheckInfo(String userId, String bizType, String checkLevel, String checkSpecial,
                                                  String storageNum, String workId, String invId, String checkNum) {
        return isLocal ? mLocalRepository.getCheckInfo(userId, bizType, checkLevel, checkSpecial, storageNum, workId, invId, checkNum) :
                mServerRepository.getCheckInfo(userId, bizType, checkLevel, checkSpecial, storageNum, workId, invId, checkNum);
    }

    @Override
    public Flowable<String> deleteCheckData(String storageNum, String workId, String invId, String checkId, String userId) {
        return isLocal ? mLocalRepository.deleteCheckData(storageNum, workId, invId, checkId, userId) :
                mServerRepository.deleteCheckData(storageNum, workId, invId, checkId, userId);
    }

    @Override
    public Flowable<List<InventoryEntity>> getCheckTransferInfoSingle(String checkId, String materialId, String materialNum, String location) {
        return isLocal ? mLocalRepository.getCheckTransferInfoSingle(checkId, materialId, materialNum, location)
                : mServerRepository.getCheckTransferInfoSingle(checkId, materialId, materialNum, location);
    }

    @Override
    public Flowable<ReferenceEntity> getCheckTransferInfo(String checkId, String materialNum, String location, String isPageQuery, int pageNum, int pageSize) {
        return isLocal ? mLocalRepository.getCheckTransferInfo(checkId, materialNum, location, isPageQuery, pageNum, pageSize)
                : mServerRepository.getCheckTransferInfo(checkId, materialNum, location, isPageQuery, pageNum, pageSize);
    }

    @Override
    public Flowable<String> deleteCheckDataSingle(String checkId, String checkLineId, String userId) {
        return isLocal ? mLocalRepository.deleteCheckDataSingle(checkId, checkLineId, userId)
                : mServerRepository.deleteCheckDataSingle(checkId, checkLineId, userId);
    }

    /**
     * 保存基础数据的下载日期
     *
     * @param requestType
     * @param requestDate
     */
    @Override
    public void saveLoadBasicDataTaskDate(@NonNull String requestType, @NonNull String requestDate) {
        mLocalRepository.saveLoadBasicDataTaskDate(requestType, requestDate);
    }

    /**
     * 保存基础数据
     *
     * @param maps:基础数据源
     */
    @Override
    public Flowable<Integer> saveBasicData(List<Map<String, Object>> maps) {
        return mLocalRepository.saveBasicData(maps);
    }

    @Override
    public Flowable<ArrayList<InvEntity>> getInvsByWorkId(String workId, int flag) {
        return mLocalRepository.getInvsByWorkId(workId, flag);
    }

    @Override
    public Flowable<ArrayList<WorkEntity>> getWorks(int flag) {
        return mLocalRepository.getWorks(flag);
    }

    @Override
    public Flowable<Boolean> checkWareHouseNum(String sendWorkId, String sendInvCode, String recWorkId,
                                               String recInvCode, int flag) {
        return mLocalRepository.checkWareHouseNum(sendWorkId, sendInvCode, recWorkId, recInvCode, flag);
    }

    @Override
    public Flowable<ArrayList<SimpleEntity>> getSupplierList(String workCode, String keyWord, int defaultItemNum, int flag) {
        return mLocalRepository.getSupplierList(workCode, keyWord, defaultItemNum, flag);
    }

    @Override
    public Flowable<ArrayList<SimpleEntity>> getCostCenterList(String workCode, String keyWord, int defaultItemNum, int flag) {
        return mLocalRepository.getCostCenterList(workCode, keyWord, defaultItemNum, flag);
    }

    @Override
    public Flowable<ArrayList<SimpleEntity>> getProjectNumList(String workCode, String keyWord, int defaultItemNum, int flag) {
        return mLocalRepository.getProjectNumList(workCode, keyWord, defaultItemNum, flag);
    }

    @Override
    public Flowable<Boolean> saveBizFragmentConfig(ArrayList<BizFragmentConfig> bizFragmentConfigs) {
        return mLocalRepository.saveBizFragmentConfig(bizFragmentConfigs);
    }

    @Override
    public Flowable<ArrayList<BizFragmentConfig>> readBizFragmentConfig(String bizType, String refType, int fragmentType) {
        return mLocalRepository.readBizFragmentConfig(bizType, refType, fragmentType);
    }

    @Override
    public Flowable<String> deleteInspectionImages(String refNum, String refCodeId, boolean isLocal) {
        return mLocalRepository.deleteInspectionImages(refNum, refCodeId, isLocal);
    }

    @Override
    public Flowable<String> deleteInspectionImagesSingle(String refNum, String refLineNum, String refLineId, boolean isLocal) {
        return mLocalRepository.deleteInspectionImagesSingle(refNum, refLineNum, refLineId, isLocal);
    }

    @Override
    public Flowable<String> deleteTakedImages(ArrayList<ImageEntity> images, boolean isLocal) {
        return mLocalRepository.deleteTakedImages(images, isLocal);
    }

    @Override
    public void saveTakedImages(ArrayList<ImageEntity> images, String refNum, String refLineId,
                                int takePhotoType, String imageDir, boolean isLocal) {
        mLocalRepository.saveTakedImages(images, refNum, refLineId, takePhotoType, imageDir, isLocal);
    }

    @Override
    public ArrayList<ImageEntity> readImagesByRefNum(String refNum, boolean isLocal) {
        return mLocalRepository.readImagesByRefNum(refNum, isLocal);
    }

    @Override
    public Flowable<String> getStorageNum(String workId, String workCode, String invId, String invCode) {
        return mLocalRepository.getStorageNum(workId, workCode, invId, invCode);
    }

    @Override
    public Flowable<ArrayList<String>> getStorageNumList(int flag) {
        return mLocalRepository.getStorageNumList(flag);
    }


    @Override
    public Flowable<String> changeLoginInfo(String userId, String newPassword) {
        return mServerRepository.changeLoginInfo(userId, newPassword);
    }

    @Override
    public Flowable<String> uploadInspectionDataOffline(ReferenceEntity refData) {
        return mServerRepository.uploadInspectionDataOffline(refData);
    }

    @Override
    public Flowable<MaterialEntity> getMaterialInfo(String queryType, String materialNum) {
        return isLocal ? mLocalRepository.getMaterialInfo(queryType, materialNum) : mServerRepository.getMaterialInfo(queryType, materialNum);
    }

    @Override
    public Flowable<String> transferCheckData(String checkId) {
        return isLocal ? mLocalRepository.transferCheckData(checkId) : mServerRepository.transferCheckData(checkId);
    }

    @Override
    public Flowable<ReferenceEntity> loadRefDataFromServer(@NonNull String refNum, @NonNull String refType, @NonNull String bizType, @NonNull String moveType, @NonNull String userId) {
        return mServerRepository.getReference(refNum, refType, bizType, moveType, userId);
    }

}
