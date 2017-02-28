package com.richfit.barcodesystemproduct.module_locationadjust.collect;

import com.richfit.common_lib.IInterface.IPresenter;
import com.richfit.domain.bean.ResultEntity;

/**
 * Created by monday on 2017/2/7.
 */

public interface ILACollectPresenter extends IPresenter<ILACollectView> {

    void getMaterialInfo(String queryType, String materialNum);

    void getInventoryInfo(String queryType, String workId, String invId,
                          String workCode, String invCode, String storageNum,
                          String materialNum, String materialId, String materialGroup,
                          String materialDesc, String batchFlag,
                          String location,String specialInvFlag,String specialInvNum, String invType);

    /**
     * 保存本次采集的数据
     *
     * @param result:用户采集的数据(json格式)
     */
    void uploadCollectionDataSingle(ResultEntity result);



}
