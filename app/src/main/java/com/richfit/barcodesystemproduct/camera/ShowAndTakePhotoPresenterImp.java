package com.richfit.barcodesystemproduct.camera;

import android.content.Context;

import com.richfit.barcodesystemproduct.base.BasePresenter;
import com.richfit.barcodesystemproduct.di.scope.ContextLife;
import com.richfit.common_lib.rxutils.RxSubscriber;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.FileUtil;
import com.richfit.common_lib.utils.Global;
import com.richfit.domain.bean.ImageEntity;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subscribers.ResourceSubscriber;


/**
 * Created by monday on 2016/10/9.
 */

public class ShowAndTakePhotoPresenterImp extends BasePresenter<ShowAndTakePhotoContract.View>
        implements ShowAndTakePhotoContract.Presenter {

    ShowAndTakePhotoContract.View mView;

    @Inject
    public ShowAndTakePhotoPresenterImp(@ContextLife("Activity") Context context) {
        super(context);
    }

    @Override
    public void readImagesFromLocal(final String refNum, final String refLineNum, final String refLineId,
                                    final int takePhotoType, final String imageDir,
                                    String bizType, String refType, final boolean isLocal) {
        mView = getView();
        RxSubscriber<ArrayList<ImageEntity>> subscriber = readImages(imageDir, bizType, refType, takePhotoType)
                .doOnNext(images -> mRepository.saveTakedImages(images, refNum, refLineId, takePhotoType,
                        imageDir, isLocal))
                .compose(TransformerHelper.io2main())
                .subscribeWith(new RxSubscriber<ArrayList<ImageEntity>>(mContext, "正在读取图片...") {
                    @Override
                    public void _onNext(ArrayList<ImageEntity> images) {
                        if (mView != null) {
                            mView.showImages(images);
                        }
                    }

                    @Override
                    public void _onNetWorkConnectError(String message) {
                        if (mView != null) {
                            mView.readImagesFail(message);
                        }
                    }

                    @Override
                    public void _onCommonError(String message) {
                        if (mView != null) {
                            mView.readImagesFail(message);
                        }
                    }

                    @Override
                    public void _onServerError(String code, String message) {
                        if (mView != null) {
                            mView.readImagesFail(message);
                        }
                    }

                    @Override
                    public void _onComplete() {

                    }
                });
        addSubscriber(subscriber);

    }

    @Override
    public void deleteImages(ArrayList<ImageEntity> images, String imageDir, boolean isLocal) {
        mView = getView();
        if (images == null || images.size() == 0) {
            if (mView != null) {
                mView.deleteImageFail("请先采集图片");
                return;
            }
        }
        final ArrayList<Integer> deletePositions = new ArrayList<>();
        for (int i = 0, size = images.size(); i < size; i++) {
            if (images.get(i).isSelected) {
                //注意这里的将进行自动装箱
                deletePositions.add(i);
            }
        }

        if (deletePositions.size() == 0 && mView != null) {
            mView.deleteImageFail("请先选择您要删除的图片");
            return;
        }
        ResourceSubscriber<String> subscriber =
                Flowable.concat(deleteSDImage(images, imageDir), deleteLocalImage(images, isLocal))
                        .compose(TransformerHelper.io2main())
                        .subscribeWith(new ResourceSubscriber<String>() {
                            @Override
                            public void onNext(String s) {

                            }

                            @Override
                            public void onError(Throwable t) {
                                if (mView != null) {
                                    mView.deleteImageFail(t.getMessage());
                                }
                            }

                            @Override
                            public void onComplete() {
                                if (mView != null) {
                                    mView.deleteImageSuccess(deletePositions);
                                }
                            }
                        });
        addSubscriber(subscriber);
    }

    /**
     * 从本地sd卡中读取图片
     *
     * @return
     */
    private Flowable<ArrayList<ImageEntity>> readImages(final String cacheImageDir, final String bizType,
                                                        final String refType, final int takePhotoType) {
        return Flowable.create(emitter -> {
            try {
                File cacheDir = new File(cacheImageDir);
                if (cacheDir == null) {
                    emitter.onError(new Throwable("抱歉未获取到照片"));
                }
                ArrayList<ImageEntity> images = new ArrayList<>();
                File[] files = cacheDir.listFiles((file, s) -> s.endsWith(Global.IMAGE_DEFAULT_FORMAT));
                if (files == null || files.length == 0) {
                    emitter.onNext(images);
                    emitter.onComplete();
                    return;
                }
                for (File file : files) {
                    ImageEntity image = new ImageEntity();
                    image.lastModifiedTime = file.lastModified();
                    image.isSelected = false;
                    image.bizType = bizType;
                    image.refType = refType;
                    image.takePhotoType = takePhotoType;
                    image.imageName = file.getName();
                    images.add(image);
                }
                emitter.onNext(images);
                emitter.onComplete();
            } catch (Exception e) {
                e.printStackTrace();
                emitter.onError(e);
            }
        }, BackpressureStrategy.BUFFER);
    }

    /**
     * 删除数据库的图片
     * * @return
     */
    private Flowable<String> deleteLocalImage(ArrayList<ImageEntity> images, boolean isLocal) {
        return Flowable.just(images)
                .filter(imgs -> imgs != null && imgs.size() > 0)
                .flatMap(imgs -> mRepository.deleteTakedImages(imgs, isLocal));
    }

    /**
     * 删除sd卡的图片(删除指定名称的图片)
     */
    private Flowable<String> deleteSDImage(ArrayList<ImageEntity> imgs, String imageDir) {
        return Flowable.fromIterable(imgs)
                .filter(image -> image != null && image.isSelected)
                .map(image -> FileUtil.deleteImage(imageDir, image.imageName) ? "删除图片成功" : "删除图片失败");
    }
}
