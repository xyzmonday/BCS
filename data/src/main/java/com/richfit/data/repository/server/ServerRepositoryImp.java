package com.richfit.data.repository.server;

import android.support.annotation.NonNull;

import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.CommonUtil;
import com.richfit.common_lib.utils.JsonUtil;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.data.net.api.IRequestApi;
import com.richfit.domain.bean.BizFragmentConfig;
import com.richfit.domain.bean.InventoryEntity;
import com.richfit.domain.bean.LoadBasicDataWrapper;
import com.richfit.domain.bean.LoadDataTask;
import com.richfit.domain.bean.MaterialEntity;
import com.richfit.domain.bean.MenuNode;
import com.richfit.domain.bean.RefNumEntity;
import com.richfit.domain.bean.ReferenceEntity;
import com.richfit.domain.bean.ResultEntity;
import com.richfit.domain.bean.RowConfig;
import com.richfit.domain.bean.UpdateEntity;
import com.richfit.domain.bean.UserEntity;
import com.richfit.domain.repository.IServerRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Flowable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 服务器数据仓库实现类
 * Created by monday on 2016/12/29.
 */

public class ServerRepositoryImp implements IServerRepository {

    IRequestApi mRequestApi;

    HashMap<String, Object> mRequestParam;

    @Inject
    public ServerRepositoryImp(IRequestApi requestApi) {
        this.mRequestApi = requestApi;
        this.mRequestParam = new HashMap<>();
    }

    @Override
    public Flowable<UserEntity> Login(String userName, String password) {
        mRequestParam.clear();
        mRequestParam.put("loginId", userName.toUpperCase());
        mRequestParam.put("password", password.toUpperCase());
        return mRequestApi.login(JsonUtil.map2Json(mRequestParam)).compose(TransformerHelper.handleResponse());
    }

    @Override
    public Flowable<ArrayList<MenuNode>> getMenuTreeInfo(String loginId, int offLine) {
        mRequestParam.clear();
        mRequestParam.put("loginId", loginId);
        mRequestParam.put("offLine", offLine);
        return mRequestApi.getMenuTreeInfo(JsonUtil.map2Json(mRequestParam))
                .compose(TransformerHelper.handleResponse());
    }

    @Override
    public Flowable<ArrayList<RowConfig>> loadExtraConfig(String companyId) {
        mRequestParam.put("companyId", companyId);
        return mRequestApi.loadExtraConfig(JsonUtil.map2Json(mRequestParam))
                .compose(TransformerHelper.handleResponse());
    }

    @Override
    public Flowable<ArrayList<BizFragmentConfig>> loadBizFragmentConfig(String companyId, int mode) {
        return null;
    }

    @Override
    public Flowable<String> syncDate() {
        return mRequestApi.syncDate()
                .compose(TransformerHelper.MapTransformer);
    }

    @Override
    public Flowable<String> getMappingInfo() {
        return mRequestApi.getMappingInfo()
                .compose(TransformerHelper.MapTransformer);
    }

    /**
     * 从服务器获取单据数据
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

        mRequestParam.clear();
        mRequestParam.put("recordNum", refNum.trim().toUpperCase());
        mRequestParam.put("refType", refType);
        mRequestParam.put("moveType", moveType);
        mRequestParam.put("businessType", bizType);
        mRequestParam.put("userId", userId);

        return mRequestApi.getReference(JsonUtil.map2Json(mRequestParam))
                .compose(TransformerHelper.handleResponse());
    }

    /**
     * 抬头界面删除整单缓存
     *
     * @param refNum：单据号
     * @param transId：缓存id(在验收的中可能是inspectionId)
     * @param refCodeId
     * @param bizType:业务类型
     * @param userId
     * @return
     */
    @Override
    public Flowable<String> deleteCollectionData(String refNum, String transId, String refCodeId,
                                                 String refType, String bizType, String userId,
                                                 String companyCode) {
        mRequestParam.clear();
        mRequestParam.put("refNum", refNum);
        mRequestParam.put("transId", transId);
        mRequestParam.put("refType", refType);
        mRequestParam.put("refCodeId", refCodeId);
        mRequestParam.put("businessType", bizType);
        mRequestParam.put("userId", userId);

        return mRequestApi.deleteCollectionData(JsonUtil.map2Json(mRequestParam))
                .compose(TransformerHelper.MapTransformer);
    }


    /**
     * 数据明细界面获取整单缓存
     *
     * @param recordNum：单据号
     * @param refCodeId：单据id
     * @param bizType：业务类型
     * @param refType：单据类型
     * @param userId：用户id
     * @param workId：发出工厂id
     * @param invId:发出库存地点id
     * @param recWorkId：接收工厂id
     * @param recInvId：接搜库存地点id
     * @return
     */
    @Override
    public Flowable<ReferenceEntity> getTransferInfo(String recordNum, String refCodeId, String bizType, String refType, String userId,
                                                     String workId, String invId, String recWorkId, String recInvId) {
        mRequestParam.clear();
        mRequestParam.put("refCodeId", refCodeId);
        mRequestParam.put("businessType", bizType);
        mRequestParam.put("refType", refType);
        mRequestParam.put("userId", userId);
        mRequestParam.put("workId", workId);
        mRequestParam.put("invId", invId);
        mRequestParam.put("recordNum", recordNum);
        mRequestParam.put("recWorkId", recWorkId);
        mRequestParam.put("recInvId", recInvId);

        return mRequestApi.getCacheTransfereInfo(JsonUtil.map2Json(mRequestParam))
                .compose(TransformerHelper.handleResponse());

    }

    /**
     * 数据采集界面获取缓存
     *
     * @param refCodeId：单据id
     * @param refType：单据类型
     * @param bizType：业务类型
     * @param refLineId：单据行id
     * @param workId:工厂id
     * @param invId:库存地点id
     * @param materialNum:物资编码
     * @param batchFlag:批次
     * @param location：仓位
     * @return
     */
    @Override
    public Flowable<ReferenceEntity> getTransferInfoSingle(String refCodeId, String refType, String bizType,
                                                           String refLineId, String workId, String invId,
                                                           String recWorkId, String recInvId,
                                                           String materialNum, String batchFlag,
                                                           String location, String userId) {
        mRequestParam.clear();
        mRequestParam.put("refCodeId", refCodeId);
        mRequestParam.put("businessType", bizType);
        mRequestParam.put("refType", refType);
        mRequestParam.put("refLineId", refLineId);
        mRequestParam.put("workId", workId);
        mRequestParam.put("invId", invId);
        mRequestParam.put("recWorkId", recWorkId);
        mRequestParam.put("recInvId", recInvId);
        mRequestParam.put("materialNum", materialNum);
        mRequestParam.put("batchFlag", batchFlag);
        mRequestParam.put("location", location);
        mRequestParam.put("userId", userId);

        return mRequestApi.getTransferInfoSingle(JsonUtil.map2Json(mRequestParam))
                .compose(TransformerHelper.handleResponse());
    }

    /**
     * 删除单个子节点
     *
     * @param lineDeleteFlag：是否删除整行(true/false)
     * @param transId：缓存抬头id
     * @param transLineId：缓存行明细id
     * @param locationId：缓存仓位id
     * @param businessType：业务类型
     * @param position：仓位
     */
    @Override
    public Flowable<String> deleteCollectionDataSingle(String lineDeleteFlag, String transId, String transLineId,
                                                       String locationId, String refType, String businessType, String refLineId,
                                                       String userId, int position, String companyCode) {
        mRequestParam.clear();
        mRequestParam.put("lineDeleteFlag", lineDeleteFlag);
        mRequestParam.put("transId", transId);
        mRequestParam.put("transLineId", transLineId);
        mRequestParam.put("locationId", locationId);
        mRequestParam.put("refType", refType);
        mRequestParam.put("businessType", businessType);
        mRequestParam.put("refLineId", refLineId);
        mRequestParam.put("userId", userId);

        return mRequestApi.deleteCollectionDataSingle(JsonUtil.map2Json(mRequestParam))
                .compose(TransformerHelper.MapTransformer);

    }

    /**
     * 删除整个盘点单
     *
     * @param checkId：单据抬头id
     * @param userId：用户id
     * @return
     */
    @Override
    public Flowable<String> deleteCheckData(String storageNum, String workId, String invId,
                                            String checkId, String userId) {
        mRequestParam.clear();
        mRequestParam.put("storageNum",storageNum);
        mRequestParam.put("workId",workId);
        mRequestParam.put("invId",invId);
        mRequestParam.put("checkId", checkId);
        mRequestParam.put("userId", userId);
        return mRequestApi.deleteCheckData(JsonUtil.map2Json(mRequestParam))
                .compose(TransformerHelper.MapTransformer);
    }

    /**
     * 数据采集界面获取单条缓存数据
     *
     * @param checkId:盘点单id
     * @param location：仓位
     * @return
     */
    @Override
    public Flowable<List<InventoryEntity>> getCheckTransferInfoSingle(String checkId, String materialId,String materialNum, String location) {
        mRequestParam.clear();
        mRequestParam.put("checkId", checkId);
        mRequestParam.put("materialNum", materialNum);
        mRequestParam.put("materialId", materialId);
        mRequestParam.put("location", location);

        return mRequestApi.getCheckTransferInfoSingle(JsonUtil.map2Json(mRequestParam))
                .compose(TransformerHelper.handleResponse());
    }

    /**
     * 数据明细界面获取整单缓存
     *
     * @param checkId：盘点id
     * @param materialNum：物料编码
     * @param location：仓位
     * @param queryPage：是否分页查询
     * @param pageNum：页码
     * @param pageSize：每页多少行
     * @return
     */
    @Override
    public Flowable<ReferenceEntity> getCheckTransferInfo(String checkId, String materialNum, String location, String queryPage, int pageNum, int pageSize) {
        mRequestParam.clear();
        mRequestParam.put("checkId", checkId);
        mRequestParam.put("materialNum", materialNum);
        mRequestParam.put("location", location);
        mRequestParam.put("queryPage", queryPage);
        mRequestParam.put("pageNum", pageNum);
        mRequestParam.put("pageSize", pageSize);
        return mRequestApi.getCheckTransferInfo(JsonUtil.map2Json(mRequestParam))
                .compose(TransformerHelper.handleResponse());
    }

    /**
     * 数据明细界面删除单条缓存
     *
     * @param checkId：抬头id
     * @param checkLineId：行id
     * @param userId：用户id
     * @return
     */
    @Override
    public Flowable<String> deleteCheckDataSingle(String checkId, String checkLineId, String userId) {
        mRequestParam.clear();
        mRequestParam.put("checkId", checkId);
        mRequestParam.put("checkLineId", checkLineId);
        mRequestParam.put("userId", userId);
        return mRequestApi.deleteCheckDataSingle(JsonUtil.map2Json(mRequestParam))
                .compose(TransformerHelper.MapTransformer);
    }

    @Override
    public Flowable<ReferenceEntity> getCheckInfo(String userId, String bizType, String checkLevel,
                                                  String checkSpecial, String storageNum, String workId,
                                                  String invId,String checkNum) {
        mRequestParam.clear();
        mRequestParam.put("userId", userId);
        mRequestParam.put("businessType", bizType);
        mRequestParam.put("checkLevel", checkLevel);
        mRequestParam.put("checkSpecial", checkSpecial);
        mRequestParam.put("storageNum", storageNum);
        mRequestParam.put("workId", workId);
        mRequestParam.put("invId", invId);
        mRequestParam.put("checkNum",checkNum);
        return mRequestApi.getCheckInfo(JsonUtil.map2Json(mRequestParam))
                .compose(TransformerHelper.handleResponse());
    }

    /**
     * 下载基础数据的总条数
     *
     * @param requestParam:需要下载的基础数据的参数
     * @return
     */
    @Override
    public Flowable<LoadBasicDataWrapper> preparePageLoad(final LoadBasicDataWrapper requestParam) {
        final boolean isByPage = requestParam.isByPage;
        final String queryType = requestParam.queryType;

        if (!isByPage) {
            return Flowable.just(requestParam);
        }
        mRequestParam.clear();
        mRequestParam.put("queryType", queryType);
        mRequestParam.put("requestDate", "");
        mRequestParam.put("queryPage", "queryPage");
        mRequestParam.put("startNum", String.valueOf(0));
        mRequestParam.put("endNum", String.valueOf(0));
        return mRequestApi.preparePageLoad(JsonUtil.map2Json(mRequestParam))
                .compose(TransformerHelper.MapTransformer)
                .flatMap(totalCount -> wrapperTotalCount(totalCount, isByPage, queryType));

    }

    private Flowable<LoadBasicDataWrapper> wrapperTotalCount(String totalCount, boolean isByPage, String queryType) {
        LoadBasicDataWrapper item = new LoadBasicDataWrapper();
        item.totalCount = UiUtil.convertToInt(totalCount, 0);
        item.isByPage = isByPage;
        item.queryType = queryType;
        return Flowable.just(item);
    }

    /**
     * 从服务器下载基础数据
     *
     * @param task：本次基础数据的下载任务
     * @return
     */
    @Override
    public Flowable<List<Map<String, Object>>> loadBasicData(@NonNull LoadDataTask task) {
        mRequestParam.clear();
        mRequestParam.put("queryType", task.queryType);
        mRequestParam.put("pageNum", task.pageNum);
        mRequestParam.put("pageSize", task.pageSize);
        return mRequestApi.loadBasicData(JsonUtil.map2Json(mRequestParam))
                .compose(TransformerHelper.handleResponse());
    }

    @Override
    public Flowable<String> uploadCollectionDataSingle(final ResultEntity result) {
        return mRequestApi.uploadCollectionDataSingle(JsonUtil.object2Json(result))
                .compose(TransformerHelper.MapTransformer);
    }

    /**
     * 数据明细界面过账
     *
     * @param refCodeId:单据id
     * @param transId：缓存抬头id
     * @param bizType：业务类型
     * @param voucherDate:过账日期
     * @param userId:用户id
     * @return
     */
    @Override
    public Flowable<String> uploadCollectionData(String refCodeId, String transId, String bizType,
                                                 String refType, int inspectionType, String voucherDate, String remark, String userId) {
        mRequestParam.clear();
        mRequestParam.put("transId", transId);
        mRequestParam.put("businessType", bizType);
        mRequestParam.put("refType", refType);
        mRequestParam.put("voucherDate", voucherDate);
        mRequestParam.put("refCodeId", refCodeId);
        mRequestParam.put("inspectionType", inspectionType);
        mRequestParam.put("remark", remark);
        mRequestParam.put("userId", userId);
        return mRequestApi.uploadCollectionData(JsonUtil.map2Json(mRequestParam))
                .compose(TransformerHelper.MapTransformer);
    }

    @Override
    public Flowable<String> transferCollectionData(ResultEntity result) {
        return mRequestApi.transferCollectionData2(JsonUtil.object2Json(result))
                .compose(TransformerHelper.ListTransformer);
    }

    @Override
    public Flowable<String> transferCollectionData(String transId, String bizType, String refType, String userId,
                                                   String voucherDate, Map<String, Object> flagMap, Map<String, Object> extraHeaderMap) {
        mRequestParam.clear();
        mRequestParam.put("transId", transId);
        mRequestParam.put("businessType", bizType);
        mRequestParam.put("refType", refType);
        mRequestParam.put("voucherDate", voucherDate);
        mRequestParam.put("userId", userId);
        mRequestParam.put("mapExHead", extraHeaderMap);
        CommonUtil.putAll(mRequestParam, flagMap);

        return mRequestApi.transferCollectionData(JsonUtil.map2Json(mRequestParam))
                .compose(TransformerHelper.ListTransformer);
    }


    @Override
    public Flowable<List<RefNumEntity>> getReserveNumList(String beginDate, String endDate, String loginId, String refType) {
        mRequestParam.clear();
        mRequestParam.put("beginDate", beginDate);
        mRequestParam.put("endDate", endDate);
        mRequestParam.put("loginId", loginId);
        mRequestParam.put("refType", refType);
        return mRequestApi.getReserveNumList(JsonUtil.map2Json(mRequestParam))
                .compose(TransformerHelper.handleResponse());
    }

    @Override
    public Flowable<String> uploadInspectionImage(ResultEntity result) {

        File file = new File(result.imagePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("fileName", file.getName(), requestFile);

        String requestParam = JsonUtil.object2Json(Arrays.asList(result));
        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), requestParam);

        return mRequestApi.uploadInspectionImage(body, description)
                .compose(TransformerHelper.MapTransformer);

    }

    /**
     * 数据采集界面保存单条盘点数据
     *
     * @param result
     * @return
     */
    @Override
    public Flowable<String> uploadCheckDataSingle(ResultEntity result) {
        return mRequestApi.uploadCheckDataSingle(JsonUtil.object2Json(result))
                .compose(TransformerHelper.MapTransformer);
    }

    @Override
    public Flowable<List<InventoryEntity>> getInventoryInfo(String queryType, String workId, String invId,
                                                            String workCode, String invCode, String storageNum,
                                                            String materialNum, String materialId, String materialGroup,
                                                            String materialDesc, String batchFlag, String location,
                                                            String specialInvFlag, String specialInvNum, String invType) {
        mRequestParam.clear();
        mRequestParam.put("queryType", queryType);
        mRequestParam.put("workId", workId);
        mRequestParam.put("invId", invId);
        mRequestParam.put("workCode", workCode);
        mRequestParam.put("invCode", invCode);
        mRequestParam.put("storageNum", storageNum);
        mRequestParam.put("materialNum", materialNum);
        mRequestParam.put("materialId", materialId);
        mRequestParam.put("materialGroup", materialGroup);
        mRequestParam.put("materialDesc", materialDesc);
        mRequestParam.put("batchFlag", CommonUtil.toUpperCase(batchFlag));
        mRequestParam.put("location", CommonUtil.toUpperCase(location));
        mRequestParam.put("invType", invType);
        mRequestParam.put("specialInvFlag", specialInvFlag);
        mRequestParam.put("specialInvNum", specialInvNum);
        return mRequestApi.getInventoryInfo(JsonUtil.map2Json(mRequestParam))
                .compose(TransformerHelper.handleResponse());
    }

    @Override
    public Flowable<String> getLocationInfo(String queryType, String workId, String invId, String location) {
        mRequestParam.clear();
        mRequestParam.put("queryType", queryType);
        mRequestParam.put("workId", workId);
        mRequestParam.put("invId", invId);
        mRequestParam.put("location", location);
        return mRequestApi.getLocation(JsonUtil.map2Json(mRequestParam))
                .compose(TransformerHelper.MapTransformer);
    }

    /**
     * 获取最新Apk的版本信息
     *
     * @return
     */
    @Override
    public Flowable<UpdateEntity> getAppVersion() {
        return mRequestApi.getAppVersion()
                .compose(TransformerHelper.handleResponse());
    }

    /**
     * 从服务器获取单据数据
     *
     * @param refNum：单号
     * @param refType:单据类型
     * @param bizType：业务类型
     * @param moveType:移动类型
     * @param userId：用户loginId
     * @return
     */
    @Override
    public Flowable<ReferenceEntity> loadRefDataFromServer(@NonNull String refNum, @NonNull String refType,
                                                           @NonNull String bizType, @NonNull String moveType,
                                                           @NonNull String userId) {
        mRequestParam.clear();
        mRequestParam.put("recordNum", refNum.trim().toUpperCase());
        mRequestParam.put("refType", refType);
        mRequestParam.put("moveType", moveType);
        mRequestParam.put("businessType", bizType);
        mRequestParam.put("userId", userId);

        return mRequestApi.getReference(JsonUtil.map2Json(mRequestParam))
                .compose(TransformerHelper.handleResponse());
    }

    @Override
    public Flowable<String> changeLoginInfo(String userId, String newPassword) {
        mRequestParam.clear();
        mRequestParam.put("userId", userId);
        mRequestParam.put("password", newPassword);
        return mRequestApi.changeLoginInfo(JsonUtil.map2Json(mRequestParam))
                .compose(TransformerHelper.MapTransformer);
    }

    @Override
    public Flowable<String> uploadInspectionDataOffline(ReferenceEntity refData) {
        return mRequestApi.uploadInspectionDataOffline(JsonUtil.object2Json(refData))
                .compose(TransformerHelper.MapTransformer);
    }

    @Override
    public Flowable<MaterialEntity> getMaterialInfo(String queryType, String materialNum) {
        mRequestParam.clear();
        mRequestParam.put("queryType", queryType);
        mRequestParam.put("materialNum", materialNum);
        return mRequestApi.getMaterialInfo(JsonUtil.map2Json(mRequestParam))
                .compose(TransformerHelper.handleResponse());
    }

    @Override
    public Flowable<String> transferCheckData(String checkId) {
        mRequestParam.clear();
        mRequestParam.put("checkId", checkId);
        return mRequestApi.transferCheckData(JsonUtil.map2Json(mRequestParam))
                .compose(TransformerHelper.MapTransformer);
    }


}
