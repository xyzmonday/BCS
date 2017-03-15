package com.richfit.data.repository.local;

import android.support.annotation.NonNull;

import com.richfit.common_lib.exception.NullException;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.data.db.ApprovalDao;
import com.richfit.data.db.CommonDao;
import com.richfit.domain.bean.BizFragmentConfig;
import com.richfit.domain.bean.ImageEntity;
import com.richfit.domain.bean.InvEntity;
import com.richfit.domain.bean.InventoryEntity;
import com.richfit.domain.bean.MaterialEntity;
import com.richfit.domain.bean.ReferenceEntity;
import com.richfit.domain.bean.ResultEntity;
import com.richfit.domain.bean.RowConfig;
import com.richfit.domain.bean.SimpleEntity;
import com.richfit.domain.bean.UserEntity;
import com.richfit.domain.bean.WorkEntity;
import com.richfit.domain.repository.ILocalRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.exceptions.Exceptions;

/**
 * Created by monday on 2016/12/29.
 */

public class LocalRepositoryImp implements ILocalRepository {

    private CommonDao mCommonDao;
    private ApprovalDao mApprovalDao;


    @Inject
    public LocalRepositoryImp(CommonDao commonDao, ApprovalDao approvalDao) {
        this.mCommonDao = commonDao;
        this.mApprovalDao = approvalDao;
    }

    @Override
    public Flowable<ReferenceEntity> getReference(@NonNull String refNum, @NonNull String refType,
                                                  @NonNull String bizType, @NonNull String moveType,
                                                  @NonNull String refLineId,@NonNull String userId) {
        return null;
    }

    @Override
    public Flowable<String> deleteCollectionData(String refNum, String transId, String refCodeId, String refType, String bizType, String userId, String companyCode) {
        return null;
    }

    @Override
    public Flowable<ReferenceEntity> getTransferInfo(String recordNum, String refCodeId, String bizType, String refType, String userId, String workId, String invId, String recWorkId, String recInvId) {
        return null;
    }

    @Override
    public Flowable<ReferenceEntity> getTransferInfoSingle(String refCodeId, String refType, String bizType, String refLineId, String workId, String invId, String recWorkId, String recInvId, String materialNum,
                                                           String batchFlag, String location,String refDoc,int refDocItem, String userId) {
        return null;
    }

    @Override
    public Flowable<String> deleteCollectionDataSingle(String lineDeleteFlag, String transId, String transLineId, String locationId, String refType, String bizType, String refLineId, String userId, int position, String companyCode) {
        return null;
    }

    @Override
    public Flowable<String> uploadCollectionDataSingle(ResultEntity result) {
        return null;
    }

    @Override
    public Flowable<ReferenceEntity> getCheckInfo(String userId, String bizType, String checkLevel, String checkSpecial, String storageNum, String workId, String invId, String checkNum) {
        return null;
    }

    @Override
    public Flowable<String> deleteCheckData(String storageNum, String workId, String invId,String checkId,
                                            String userId,String bizType) {
        return null;
    }

    @Override
    public Flowable<List<InventoryEntity>> getCheckTransferInfoSingle(String checkId,String materialId, String materialNum, String location,String bizType) {
        return null;
    }

    @Override
    public Flowable<ReferenceEntity> getCheckTransferInfo(String checkId, String materialNum, String location, String isPageQuery, int pageNum, int pageSize,String bizType) {
        return null;
    }

    @Override
    public Flowable<String> deleteCheckDataSingle(String checkId, String checkLineId, String userId,String bizType) {
        return null;
    }

    @Override
    public Flowable<MaterialEntity> getMaterialInfo(String queryType, String materialNum) {
        return null;
    }

    @Override
    public Flowable<String> transferCheckData(String checkId,String userId,String bizType) {
        return null;
    }

    static abstract class SimpleSubscriber<T> implements FlowableOnSubscribe<T> {
        @Override
        public void subscribe(FlowableEmitter<T> emitter) throws Exception {
            try {
                T data = execute();
                if (data == null) {
                    emitter.onError(new NullException("未获取到数据"));
                }
                emitter.onNext(data);
                emitter.onComplete();
            } catch (Throwable e) {
                Exceptions.throwIfFatal(e);
                emitter.onError(e);
                return;
            }
        }

        abstract T execute() throws Throwable;
    }


    @Override
    public Flowable<ArrayList<String>> readUserInfo(String userName, String password) {
        return Flowable.create(new SimpleSubscriber<ArrayList<String>>() {
            @Override
            ArrayList<String> execute() throws Throwable {
                return mCommonDao.readUserInfo();
            }
        }, BackpressureStrategy.BUFFER);
    }

    @Override
    public void saveUserInfo(UserEntity userEntity) {
        mCommonDao.saveUserInfo(userEntity);
    }

    @Override
    public void saveExtraConfigInfo(List<RowConfig> configs) {
        mCommonDao.saveExtraConfigInfo(configs);
    }

    @Override
    public Flowable<ArrayList<RowConfig>> readExtraConfigInfo(String companyId, String bizType, String refType, String configType) {
        return Flowable.create(emitter -> {
            try {
                ArrayList<RowConfig> rowConfigs = mCommonDao.readExtraConfigInfo(companyId, bizType, refType, configType);
                emitter.onNext(rowConfigs);
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        }, BackpressureStrategy.LATEST);
    }

    @Override
    public Flowable<Map<String, Object>> readExtraDataSourceByDictionary(@NonNull String propertyCode,
                                                                         @NonNull String dictionaryCode) {
        return Flowable.create(new SimpleSubscriber<Map<String, Object>>() {
            @Override
            Map<String, Object> execute() throws Throwable {
                Map<String, Object> map = new HashMap<>();
                Map<String, String> datas = mCommonDao.readExtraDataSourceByDictionary(dictionaryCode);
                map.put(UiUtil.MD5(propertyCode), datas);
                return map;
            }
        }, BackpressureStrategy.LATEST);
    }

    @Override
    public String getLoadBasicDataTaskDate(@NonNull String queryType) {
        return mCommonDao.getQueryDate(queryType);
    }

    @Override
    public void saveLoadBasicDataTaskDate(@NonNull String queryType, @NonNull String queryDate) {
        mCommonDao.saveQueryData(queryType, queryDate);
    }

    @Override
    public Flowable<Integer> saveBasicData(List<Map<String, Object>> maps) {
        return Flowable.create(emitter -> {
            try {
                int flag = mCommonDao.insertBaseData(maps);
                if (flag < 0) {
                    emitter.onError(new Throwable("下载基础数据到本地失败"));
                } else {
                    emitter.onNext(flag);
                    emitter.onComplete();
                }
            } catch (Exception e) {
                e.printStackTrace();
                emitter.onError(e);
            }
        }, BackpressureStrategy.LATEST);
    }

    @Override
    public void updateExtraConfigTable(Map<String, Set<String>> map) {
        mCommonDao.updateExtraConfigTable(map);
    }

    @Override
    public Flowable<ArrayList<InvEntity>> getInvsByWorkId(String workId, int flag) {

        return Flowable.create(new SimpleSubscriber<ArrayList<InvEntity>>() {
            @Override
            ArrayList<InvEntity> execute() throws Throwable {
                return mCommonDao.getInvByWorkId(workId, flag);
            }
        }, BackpressureStrategy.LATEST);
    }

    @Override
    public Flowable<ArrayList<WorkEntity>> getWorks(int flag) {
        return Flowable.create(new SimpleSubscriber<ArrayList<WorkEntity>>() {
            @Override
            ArrayList<WorkEntity> execute() throws Throwable {
                return mCommonDao.getWorks(flag);
            }
        }, BackpressureStrategy.LATEST);
    }

    @Override
    public Flowable<Boolean> checkWareHouseNum(String sendWorkId, String sendInvCode, String recWorkId,
                                               String recInvCode, int flag) {
        return Flowable.create(new FlowableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(FlowableEmitter<Boolean> emitter) throws Exception {
                if (mCommonDao.checkWareHouseNum(sendWorkId, sendInvCode, recWorkId, recInvCode, flag)) {
                    emitter.onNext(true);
                    emitter.onComplete();
                } else {
                    emitter.onError(new Throwable("您选择的发出库位与接收库位不隶属于同一个ERP系统仓库号"));
                }
            }
        }, BackpressureStrategy.LATEST);
    }

    @Override
    public Flowable<ArrayList<SimpleEntity>> getSupplierList(String workCode, String keyWord, int defaultItemNum, int flag) {
        return Flowable.create(emitter -> {
            try {
                final ArrayList<SimpleEntity> list = mCommonDao.getSupplierList(workCode, keyWord, defaultItemNum, flag);
                if (list == null || list.size() == 0) {
                    emitter.onError(new Throwable("未获取到该基础数据,请检查是否您选择的工厂是否正确或者是否在设置界面同步过该基础数据"));
                } else {
                    emitter.onNext(list);
                    emitter.onComplete();
                }
            } catch (Exception e) {
                emitter.onError(e);
            }
        }, BackpressureStrategy.LATEST);
    }

    @Override
    public Flowable<ArrayList<SimpleEntity>> getCostCenterList(String workCode, String keyWord, int defaultItemNum, int flag) {
        return Flowable.create(emitter -> {
            try {
                final ArrayList<SimpleEntity> list = mCommonDao.getCostCenterList(workCode, keyWord, defaultItemNum, flag);
                if (list == null || list.size() == 0) {
                    emitter.onError(new Throwable("未获取到该基础数据,请检查是否您选择的工厂是否正确或者是否在设置界面同步过该基础数据"));
                } else {
                    emitter.onNext(list);
                    emitter.onComplete();
                }
            } catch (Exception e) {
                emitter.onError(e);
            }
        }, BackpressureStrategy.LATEST);
    }

    @Override
    public Flowable<ArrayList<SimpleEntity>> getProjectNumList(String workCode, String keyWord, int defaultItemNum, int flag) {
        return Flowable.create(emitter -> {
            try {
                final ArrayList<SimpleEntity> list = mCommonDao.getProjectNumList(workCode, keyWord, defaultItemNum, flag);
                if (list == null || list.size() == 0) {
                    emitter.onError(new Throwable("未获取到项目编号数据,请检查工厂是否合适或者是否已经下载了供应基础数据。" +
                            "如果您还未下载，请到设置界面下载该基础数据"));
                } else {
                    emitter.onNext(list);
                    emitter.onComplete();
                }
            } catch (Exception e) {
                emitter.onError(e);
            }
        }, BackpressureStrategy.LATEST);
    }

    @Override
    public Flowable<Boolean> saveBizFragmentConfig(ArrayList<BizFragmentConfig> bizFragmentConfigs) {
        return Flowable.create(new SimpleSubscriber<Boolean>() {
            @Override
            Boolean execute() throws Throwable {
                return mCommonDao.saveBizFragmentConfig(bizFragmentConfigs);
            }
        }, BackpressureStrategy.LATEST);
    }

    @Override
    public Flowable<ArrayList<BizFragmentConfig>> readBizFragmentConfig(String bizType, String refType, int fragmentType) {
        return Flowable.create(new SimpleSubscriber<ArrayList<BizFragmentConfig>>() {
            @Override
            ArrayList<BizFragmentConfig> execute() throws Throwable {
                return mCommonDao.readBizFragmentConfig(bizType, refType, fragmentType);
            }
        }, BackpressureStrategy.LATEST);
    }

    @Override
    public void deleteInspectionImages(String refNum, String refCodeId, boolean isLocal) {
//        return Flowable.create(emitter -> {
//            try {
//
//                emitter.onNext("删除本地缓存图片成功");
//                emitter.onComplete();
//            } catch (Exception e) {
//                e.printStackTrace();
//                emitter.onError(e);
//            }
//        }, BackpressureStrategy.LATEST);
        mApprovalDao.deleteInspectionImages(refNum, isLocal);
    }

    @Override
    public Flowable<String> deleteInspectionImagesSingle(String refNum, String refLineNum, String refLineId, boolean isLocal) {
        return Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> emitter) throws Exception {
                try {
                    mApprovalDao.deleteInspectionImagesSingle(refNum, refLineNum, refLineId, isLocal);
                    emitter.onNext("删除本地缓存图片成功");
                    emitter.onComplete();
                } catch (Exception e) {
                    e.printStackTrace();
                    emitter.onError(e);
                }

            }
        }, BackpressureStrategy.LATEST);
    }

    @Override
    public Flowable<String> deleteTakedImages(ArrayList<ImageEntity> images, boolean isLocal) {
        return Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> emitter) throws Exception {
                try {
                    mApprovalDao.deleteTakedImages(images, isLocal);
                    emitter.onNext("删除本地缓存图片成功");
                    emitter.onComplete();
                } catch (Exception e) {
                    e.printStackTrace();
                    emitter.onError(e);
                }
            }
        }, BackpressureStrategy.LATEST);
    }

    @Override
    public void saveTakedImages(ArrayList<ImageEntity> images, String refNum, String refLineId,
                                int takePhotoType, String imageDir, boolean isLocal) {
        mApprovalDao.saveTakedImages(images, refNum, refLineId, takePhotoType, imageDir, isLocal);
    }

    @Override
    public ArrayList<ImageEntity> readImagesByRefNum(String refNum, boolean isLocal) {
        return mApprovalDao.readImagesByRefNum(refNum, isLocal);
    }

    @Override
    public Flowable<String> getStorageNum(String workId, String workCode, String invId, String invCode) {
        return Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> e) throws Exception {
                String storageNum = mCommonDao.getStorageNum(workId, workCode, invId, invCode);
                e.onNext(storageNum);
                e.onComplete();
            }
        }, BackpressureStrategy.LATEST);
    }

    @Override
    public Flowable<ArrayList<String>> getStorageNumList(int flag) {
        return Flowable.create(emitter -> {
            try {
                final ArrayList<String> list = mCommonDao.getStorageNumList(flag);
                if (list == null || list.size() <= 1) {
                    emitter.onError(new Throwable("未查询到仓库列表"));
                } else {
                    emitter.onNext(list);
                    emitter.onComplete();
                }
            } catch (Exception e) {
                emitter.onError(e);
            }
        }, BackpressureStrategy.LATEST);
    }
}
