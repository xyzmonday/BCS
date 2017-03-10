package com.richfit.domain.repository;

import android.support.annotation.NonNull;

import com.richfit.domain.bean.BizFragmentConfig;
import com.richfit.domain.bean.ImageEntity;
import com.richfit.domain.bean.InvEntity;
import com.richfit.domain.bean.RowConfig;
import com.richfit.domain.bean.SimpleEntity;
import com.richfit.domain.bean.UserEntity;
import com.richfit.domain.bean.WorkEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Flowable;

/**
 * 离线数据仓库接口
 * Created by monday on 2016/12/29.
 */

public interface ILocalRepository extends IRepository {

    /**
     * 用户登录
     *
     * @param userName：登录名
     * @param password：登录密码
     */
    Flowable<ArrayList<String>> readUserInfo(String userName, String password);

    /**
     * 保存用户信息
     *
     * @param userEntity:服务器返回的用户信息
     * @return
     */
    void saveUserInfo(UserEntity userEntity);

    /**
     * 保存配置文件
     *
     * @param configs：配置文件
     */
    void saveExtraConfigInfo(List<RowConfig> configs);

    /**
     * 读取配置文件
     */
    Flowable<ArrayList<RowConfig>> readExtraConfigInfo(String companyCode, String bizType,
                                                       String refType, String configType);

    /**
     * 读取额外字段数据源
     *
     * @param propertyCode:额外字段的编码
     * @param dictionaryCode：需要查询的字典编码
     * @return :查询结果列表
     */
    Flowable<Map<String, Object>> readExtraDataSourceByDictionary(@NonNull String propertyCode, @NonNull String dictionaryCode);

    /**
     * 获取本次基础数据下载的下载日期
     *
     * @param queryType：下载基础数据的请求类型
     * @return
     */
    String getLoadBasicDataTaskDate(String queryType);

    /**
     * 保存本次基础数据下载的下载日期
     *
     * @param queryType:下载基础数据的请求类型
     * @param queryDate：本次下载基础数据的日期
     */
    void saveLoadBasicDataTaskDate(String queryType, String queryDate);

    /**
     * 保存基础数据
     *
     * @param maps：数据源
     */
    Flowable<Integer> saveBasicData(List<Map<String, Object>> maps);


    /**
     * 更新额外字段的表字段
     *
     * @param map:需要插入的字段（列明），key表示需要插入的表的类型分别是抬头，明细和采集； value表示需要插入的列明的集合
     */
    void updateExtraConfigTable(Map<String, Set<String>> map);


    /**
     * 通过工厂id获取该工厂下的困地点列表
     */
    Flowable<ArrayList<InvEntity>> getInvsByWorkId(String workId, int flag);

    /**
     * @param flag:0表示从P_Auth_Org获取Erp的组织机构信息;1:表示从 P_Auth_Org2获取二级单位的组织机构信息
     *                                              获取工厂列表
     */
    Flowable<ArrayList<WorkEntity>> getWorks(int flag);

    /**
     * 检查发出工厂和接收工厂的ERP仓库号是否一致
     *
     * @param sendWorkId
     * @param sendInvCode
     * @param recWorkId
     * @param recInvCode
     * @return
     */
    Flowable<Boolean> checkWareHouseNum(final String sendWorkId, final String sendInvCode,
                                        final String recWorkId, final String recInvCode, int flag);

    /**
     * 获取供应商列表
     *
     * @param workCode：工厂编码
     * @param keyWord:用户输入的搜索关键字
     * @param defaultItemNum:初始化时没有关键字那么默认给出数据条数
     * @param flag:0:表示以及组织机构;1:表示二级组织机构
     * @return
     */
    Flowable<ArrayList<SimpleEntity>> getSupplierList(String workCode, String keyWord, int defaultItemNum, int flag);


    Flowable<ArrayList<SimpleEntity>> getCostCenterList(String workCode, String keyWord, int defaultItemNum,int flag);

    /**
     * 获取项目编号
     * @param workCode
     * @param keyWord
     * @param defaultItemNum
     * @param flag
     * @return
     */
    Flowable<ArrayList<SimpleEntity>> getProjectNumList(String workCode, String keyWord, int defaultItemNum,int flag);




    /**
     * 保存所有业务的页面信息。
     *
     * @param bizFragmentConfigs
     * @return
     */
    Flowable<Boolean> saveBizFragmentConfig(ArrayList<BizFragmentConfig> bizFragmentConfigs);

    /**
     * 读取所有的业务页面信息，系统利用该信息，反射生成该页面
     *
     * @param bizType
     * @param refType
     * @return
     */
    Flowable<ArrayList<BizFragmentConfig>> readBizFragmentConfig(String bizType, String refType, int fragmentType);

    /**
     * 验收抬头界面删除该单据的所有缓存图片
     *
     * @param refNum:单据号
     * @param refCodeId：单据id
     * @param isLocal：是否是离线，true：离线，false:在线
     * @return
     */
    void deleteInspectionImages(String refNum, String refCodeId, boolean isLocal);

    /**
     * 验收明细界面删除该行的缓存图片
     *
     * @param refNum：参考单据号
     * @param refLineNum：单据行号
     * @param refLineId：单据行id
     * @param isLocal:true表示删除在线模式下的缓存图片
     * @return
     */
    Flowable<String> deleteInspectionImagesSingle(String refNum, String refLineNum, String refLineId, boolean isLocal);

    /**
     * 拍照界面，用户选择删除图片
     *
     * @param images：将要删除的图片集合
     * @param isLocal：是否是离线模式
     * @return
     */
    Flowable<String> deleteTakedImages(ArrayList<ImageEntity> images, boolean isLocal);


    /**
     * 保存拍照界面的照片
     *
     * @param images：需要保存的照片集合
     * @param refNum：单号
     * @param refLineId：行id
     * @param takePhotoType：拍照类型
     * @param imageDir:缓存目录
     * @param isLocal：是否是离线模式
     * @return
     */
    void saveTakedImages(ArrayList<ImageEntity> images, String refNum,
                         String refLineId, int takePhotoType,
                         String imageDir, boolean isLocal);

    /**
     * 读取该单号的所有的缓存图片
     *
     * @param refNum
     * @param isLocal
     * @return
     */
    ArrayList<ImageEntity> readImagesByRefNum(String refNum, boolean isLocal);

    /**
     * 获取该工厂下的storageNum
     * @param workId
     * @param workCode
     * @param invId
     * @param invCode
     * @return
     */
    Flowable<String> getStorageNum(String workId, String workCode, String invId, String invCode);

    /**
     * 获取用户的仓库号列表
     * @param flag
     * @return
     */
    Flowable<ArrayList<String>> getStorageNumList(int flag);
}
