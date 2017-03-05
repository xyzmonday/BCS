package com.richfit.domain.repository;

import android.support.annotation.NonNull;

import com.richfit.domain.bean.BizFragmentConfig;
import com.richfit.domain.bean.InventoryEntity;
import com.richfit.domain.bean.LoadBasicDataWrapper;
import com.richfit.domain.bean.LoadDataTask;
import com.richfit.domain.bean.MenuNode;
import com.richfit.domain.bean.RefNumEntity;
import com.richfit.domain.bean.ReferenceEntity;
import com.richfit.domain.bean.ResultEntity;
import com.richfit.domain.bean.RowConfig;
import com.richfit.domain.bean.UpdateEntity;
import com.richfit.domain.bean.UserEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;

/**
 * 服务端数据仓库接口
 * Created by monday on 2016/12/29.
 */

public interface IServerRepository extends IRepository {

    /**
     * 用户登录
     *
     * @param userName：登录名
     * @param password：登录密码
     */
    Flowable<UserEntity> Login(String userName, String password);

    /**
     * 获取用户权限
     */
    Flowable<ArrayList<MenuNode>> getMenuTreeInfo(String loginId, int offLine);

    /**
     * 下载额外字段的配置信息
     *
     * @param companyId:公司Id
     * @return
     */
    Flowable<ArrayList<RowConfig>> loadExtraConfig(String companyId);

    Flowable<ArrayList<BizFragmentConfig>> loadBizFragmentConfig(String companyId, int mode);

    /**
     * 下载基础数据之前的准备，也就是获取该基础数据的总条数。
     *
     * @param requestParam:下载基础数据请求的参数包装
     */
    Flowable<LoadBasicDataWrapper> preparePageLoad(@NonNull LoadBasicDataWrapper requestParam);

    /**
     * 下载基础数据
     *
     * @param task：本次基础数据的下载任务
     * @return
     */
    Flowable<List<Map<String, Object>>> loadBasicData(@NonNull LoadDataTask task);

    /**
     * 同步服务器的日期
     *
     * @return
     */
    Flowable<String> syncDate();

    /**
     * 检查用户是否已经申请过账户。目的是控制账户和公司的关系
     *
     * @return
     */
    Flowable<String> getMappingInfo();

    /**
     * 数据明细界面，过账功能
     *
     * @param transId：缓存抬头id
     * @param bizType：业务类型
     * @param voucherDate:过账日期
     * @param inspectionType:验收类别(用户在抬头界面选择验收类别)
     * @param remark:过账备注
     * @param userId：登陆用户id
     * @return
     */
    Flowable<String> uploadCollectionData(String refCodeId, String transId, String bizType, String refType,
                                          int inspectionType, String voucherDate,
                                          String remark, String userId);

    Flowable<String> transferCollectionData(ResultEntity result);

    /**
     * 数据上传
     *
     * @param bizType:15
     * @param voucherDate:过账日期
     * @param userId:登陆的用户ID
     * @param transId:缓存抬头id
     * @param extraHeaderMap:抬头界面的额外字段
     * @param flagMap:不同的公司根据需求添加的标志位集合
     * @return
     */
    Flowable<String> transferCollectionData(String transId, String bizType, String refType, String userId, String voucherDate,
                                            Map<String, Object> flagMap, Map<String, Object> extraHeaderMap);

    /**
     * 获取预留单据号列表
     *
     * @param beginDate：搜索起始日期
     * @param endDate：搜索结束日期
     * @param loginId：登陆名
     * @param refType：单据类型
     * @return
     */
    Flowable<List<RefNumEntity>> getReserveNumList(String beginDate, String endDate,
                                                   String loginId, String refType);


    /**
     * 上传验收采集的所有图片
     *
     * @param result
     * @return
     */
    Flowable<String> uploadInspectionImage(ResultEntity result);

    /**
     * 删除盘点结果
     *
     * @param result
     * @return
     */
    Flowable<String> uploadCheckDataSingle(ResultEntity result);

    /**
     * 获取库存
     *
     * @param queryType：01:获取SAP的库存,"02":获取模糊库存信息,"03":获取精确库存信息,"04":获取SAP的库存信息(必须有仓位)
     * @param workId：工厂id
     * @param invId：库存地点id
     * @param materialId:物料id
     * @param materialGroup:物料组
     * @param materialDesc：物料描述
     * @param batchFlag:批次
     * @param location:仓位
     * @param invType:库存类型 0 代管；1:正常
     * @param specialInvFlag:特殊库存标识 N K
     * @param specialInvNum:抬头界面的供应商编号
     * @return
     */
    Flowable<List<InventoryEntity>> getInventoryInfo(String queryType, String workId, String invId,
                                                     String workCode, String invCode, String storageNum,
                                                     String materialNum, String materialId,
                                                     String materialGroup, String materialDesc, String batchFlag,
                                                     String location, String specialInvFlag, String specialInvNum, String invType);

    Flowable<String> getLocationInfo(String queryType, String workId, String invId, String location);

    /**
     * 获取最新的App的版本信息，用于更新
     *
     * @return
     */
    Flowable<UpdateEntity> getAppVersion();

    /**
     * 离线模式，下载单据数据
     *
     * @param refNum：单号
     * @param refType:单据类型
     * @param bizType：业务类型
     * @param moveType:移动类型
     * @param userId：用户loginId
     * @return
     */
    Flowable<ReferenceEntity> loadRefDataFromServer(@NonNull String refNum, @NonNull String refType,
                                                    @NonNull String bizType, @NonNull String moveType,
                                                    @NonNull String userId);

    /**
     * 用户修改密码
     *
     * @param userId：当前用户登陆id
     * @param newPassword：用户设置的新密码
     * @return
     */
    Flowable<String> changeLoginInfo(String userId, String newPassword);

    /**
     * 离线模式上传验收数据
     *
     * @param refData
     * @return
     */
    Flowable<String> uploadInspectionDataOffline(ReferenceEntity refData);


}
