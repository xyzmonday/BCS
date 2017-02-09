package com.richfit.barcodesystemproduct.camera;


import com.richfit.barcodesystemproduct.base.BaseView;
import com.richfit.common_lib.IInterface.IPresenter;
import com.richfit.domain.bean.ImageEntity;

import java.util.ArrayList;

/**
 * Created by monday on 2016/10/9.
 */

public interface ShowAndTakePhotoContract {
    interface View extends BaseView {
        void showImages(ArrayList<ImageEntity> images);

        //未获取到图片，或者图片缓存的目录为空
        void readImagesFail(String message);

        void deleteImageSuccess(ArrayList<Integer> deletePositions);

        void deleteImageFail(String message);
    }

    interface Presenter extends IPresenter<View> {
        void readImagesFromLocal(String refNum, String refLineNum, String refLineId, int takePhotoType, String imageDir,
                                 String bizType, String refType, boolean isLocal);

        void deleteImages(ArrayList<ImageEntity> images, String imageDir, boolean isLocal);
    }


}
