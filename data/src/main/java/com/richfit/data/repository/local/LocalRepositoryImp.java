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
import com.richfit.domain.bean.ReferenceEntity;
import com.richfit.domain.bean.ResultEntity;
import com.richfit.domain.bean.RowConfig;
import com.richfit.domain.bean.SupplierEntity;
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
    public Flowable<ReferenceEntity> getReference(@NonNull String refNum, @NonNull String refType, @NonNull String bizType, @NonNull String moveType, @NonNull String userId) {
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
    public Flowable<ReferenceEntity> getTransferInfoSingle(String refCodeId, String refType, String bizType, String refLineId, String workId, String invId, String recWorkId, String recInvId, String materialNum, String batchFlag, String location, String userId) {
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
    public Flowable<ReferenceEntity> getCheckInfo(String checkNum) {
        return null;
    }

    @Override
    public Flowable<String> deleteCheckData(String checkId, String userId) {
        return null;
    }

    @Override
    public Flowable<List<InventoryEntity>> getCheckTransferInfoSingle(String checkId, String materialNum, String location) {
        return null;
    }

    @Override
    public Flowable<ReferenceEntity> getCheckTransferInfo(String checkId, String materialNum, String location, String isPageQuery, int pageNum, int pageSize) {
        return null;
    }

    @Override
    public Flowable<String> deleteCheckDataSingle(String checkId, String checkLineId, String userId) {
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
    public Flowable<ArrayList<RowConfig>> readExtraConfigInfo(String companyCode, String bizType, String refType, String configType) {
        return Flowable.create(new SimpleSubscriber<ArrayList<RowConfig>>() {
            @Override
            ArrayList<RowConfig> execute() throws Throwable {
                return mCommonDao.readExtraConfigInfo(companyCode, bizType, refType, configType);
            }
        }, BackpressureStrategy.LATEST);
    }

    @Override
    public Flowable<Map<String, Object>> readExtraDataSourceByDictionary(@NonNull String propertyCode, @NonNull String dictionaryCode) {
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
    public int saveBasicData(List<Map<String, Object>> maps) {
        return mCommonDao.insertBaseData(maps);
    }

    @Override
    public void updateExtraConfigTable(Map<String, Set<String>> map) {
        mCommonDao.updatExtraConfigTable(map);
    }

    @Override
    public Flowable<ArrayList<InvEntity>> getInvsByWorkId(String workId,int flag) {

        return Flowable.create(new SimpleSubscriber<ArrayList<InvEntity>>() {
            @Override
            ArrayList<InvEntity> execute() throws Throwable {
                return mCommonDao.getInvByWorkId(workId,flag);
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
                                               String recInvCode,int flag) {
        return Flowable.create(new FlowableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(FlowableEmitter<Boolean> emitter) throws Exception {
                if(mCommonDao.checkWareHouseNum(sendWorkId,sendInvCode,recWorkId,recInvCode,flag)) {
                    emitter.onNext(true);
                    emitter.onComplete();
                }else {
                    emitter.onError(new Throwable("您选择的发出库位与接收库位不隶属于同一个ERP系统仓库号"));
                }
            }
        },BackpressureStrategy.LATEST);
    }

    @Override
    public Flowable<ArrayList<SupplierEntity>> getSupplierList(String workCode,int flag) {
        return Flowable.create(new SimpleSubscriber<ArrayList<SupplierEntity>>() {
            @Override
            ArrayList<SupplierEntity> execute() throws Throwable {
                return mCommonDao.getSupplierList(workCode,flag);
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
    public Flowable<String> deleteInspectionImages(String refNum, String refCodeId, boolean isLocal) {
        return Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> emitter) throws Exception {
                try {
                    mApprovalDao.deleteInspectionImages(refNum, isLocal);
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
        return Flowable.create(new SimpleSubscriber<String>() {
            @Override
            String execute() throws Throwable {
                return mCommonDao.getStorageNum(workId, workCode, invId, invCode);
            }
        }, BackpressureStrategy.LATEST);
    }
}