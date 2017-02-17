package com.richfit.barcodesystemproduct.camera;

import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.adapter.ShowPhotosAdapter;
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.common_lib.animationrv.Animation.animators.FadeInDownAnimator;
import com.richfit.common_lib.animationrv.DividerGridItemDecoration;
import com.richfit.common_lib.baseadapterrv.MultiItemTypeAdapter;
import com.richfit.common_lib.rxutils.TransformerHelper;
import com.richfit.common_lib.utils.FileUtil;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.widget.SmoothImageView;
import com.richfit.domain.bean.ImageEntity;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import io.reactivex.Flowable;
import io.reactivex.subscribers.ResourceSubscriber;

/**
 * 图片展示界面。负责验收和入库的拍照。主要的逻辑是1.当进入拍照界面的时候
 * 读取SD卡的所有图片并将读取的图片展示出来。用户拍照添加一张新的图片后
 * 再次调用读取图片方法进行展示。2.用户可选择图片进行删除。
 * 3.对于图片信息分别缓存到了SD卡和本地数据库，在删除该张单据的所有图片时只需要
 * 单据号，而删除单张图片时需要图片的存储路径和文件名(也就是全路径)。
 * Created by monday on 2016/7/22.
 */
public class ShowAndTakePhotoFragment extends BaseFragment<ShowAndTakePhotoPresenterImp, Object>
        implements ShowAndTakePhotoContract.View, MultiItemTypeAdapter.OnItemClickListener {

    public static final String PHOTO_FRAGMENT_TAG = "photo_fragment_tag";

    @BindView(R.id.recycle_view)
    RecyclerView mRecycleView;

    int mPosition;
    String mRefNum;
    String mRefLineNum;
    String mRefLineId;
    int mTakePhotoType;
    String mImageDir;
    boolean isLocal;
    ShowPhotosAdapter mAdapter;
    ArrayList<ImageEntity> mImages;

    public static ShowAndTakePhotoFragment newInstance(Bundle arguments) {
        ShowAndTakePhotoFragment fragment = new ShowAndTakePhotoFragment();
        if (arguments != null)
            fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    protected int getContentId() {
        return R.layout.fragment_show_take_photo;
    }

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    public void initVariable(Bundle savedInstanceState) {
        super.initVariable(savedInstanceState);
        mImages = new ArrayList<>();
        //在onCreate设置setHasOptionsMenu(true)保证能够onOptionsItemSelected重写有效
        setHasOptionsMenu(true);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mPosition = bundle.getInt(Global.EXTRA_POSITION_KEY);
            //单号
            mRefNum = bundle.getString(Global.EXTRA_REF_NUM_KEY);
            //行号
            mRefLineNum = bundle.getString(Global.EXTRA_REF_LINE_NUM_KEY);
            //行id
            mRefLineId = bundle.getString(Global.EXTRA_REF_LINE_ID_KEY);
            //拍照类型
            mTakePhotoType = bundle.getInt(Global.EXTRA_TAKE_PHOTO_TYPE);
            isLocal = bundle.getBoolean(Global.EXTRA_IS_LOCAL_KEY, false);

            //构建图片的SD卡的缓存路径，该路径是操作操作图片的唯一标识。需要考虑针对整单拍照和针对
            //整单拍照两种业务类型
            if (!TextUtils.isEmpty(mRefNum) && TextUtils.isEmpty(mRefLineNum) && mTakePhotoType >= 0) {
                mImageDir = isLocal ? FileUtil.getImageCacheDir(mActivity.getApplicationContext(),mRefNum, mTakePhotoType,true).getAbsolutePath() :
                        FileUtil.getImageCacheDir(mActivity.getApplicationContext(),mRefNum, mTakePhotoType,false).getAbsolutePath();
                return;
            }

            //该单号+行号所有缓存图片的更目录
            if (!TextUtils.isEmpty(mRefNum) && !TextUtils.isEmpty(mRefLineNum) && mTakePhotoType >= 0) {
                mImageDir = isLocal ? FileUtil.getImageCacheDir(mActivity.getApplicationContext(),mRefNum, mRefLineNum, mTakePhotoType,true).getAbsolutePath() :
                        FileUtil.getImageCacheDir(mActivity.getApplicationContext(),mRefNum, mRefLineNum, mTakePhotoType,false).getAbsolutePath();
                return;
            }
        }
    }

    @Override
    public void initView() {
        GridLayoutManager gm = new GridLayoutManager(mActivity, 2);
        mRecycleView.setLayoutManager(gm);
        mRecycleView.setItemAnimator(new FadeInDownAnimator());
        mRecycleView.addItemDecoration(new DividerGridItemDecoration(mActivity));
    }

    @Override
    public void onStart() {
        super.onStart();
        //注意这里，我们需要从拍照页面返回后，在此执行图片扫描
        if (!TextUtils.isEmpty(mImageDir) && !TextUtils.isEmpty(mRefLineId)) {
            mPresenter.readImagesFromLocal(mRefNum, mRefLineNum, mRefLineId, mTakePhotoType,
                    mImageDir, mBizType, mRefType, isLocal);
        } else if (!TextUtils.isEmpty(mImageDir) && TextUtils.isEmpty(mRefLineId)) {
            mPresenter.readImagesFromLocal(mRefNum, mRefLineNum, mRefLineId, mTakePhotoType,
                    mImageDir, mBizType, mRefType, isLocal);
        }
    }

    @Override
    public void showImages(ArrayList<ImageEntity> images) {
        if (images == null || images.size() == 0) {
            showMessage("您还未拍照!");
            return;
        }
        mImages.clear();
        mImages.addAll(images);
        if (mAdapter == null) {
            mAdapter = new ShowPhotosAdapter(mActivity, R.layout.item_show_take_photo,
                    mImages, mImageDir);
            mRecycleView.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener(ShowAndTakePhotoFragment.this);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void readImagesFail(String message) {
        showMessage(message);
    }

    /**
     * 响应菜单点击监听
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mImages == null || mImages.size() == 0)
            return super.onOptionsItemSelected(item);
        int itemId = item.getItemId();
        switch (itemId) {
            //删除单个图片
            case R.id.id_delete_image:
                mPresenter.deleteImages(mImages, mImageDir, isLocal);
                break;
            //全选
            case R.id.id_select_all_image:
                selectAllImages();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void deleteImageSuccess(ArrayList<Integer> deletePositions) {
        showMessage("删除成功");
        //删除内存的图片，刷星界面
        if (mAdapter != null) {
            int size = mImages.size();
            for (Integer index : deletePositions) {
                if (index >= 0 && index < size) {
                    mImages.remove(index.intValue());
                }
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void deleteImageFail(String message) {
        showMessage(message);
    }

    @Override
    public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
        mImages.get(position).isSelected = !mImages.get(position).isSelected;
        if (mRecycleView.getAdapter() != null &&
                ShowPhotosAdapter.class.isInstance(mRecycleView.getAdapter())) {
            ShowPhotosAdapter adapter = (ShowPhotosAdapter) mRecycleView.getAdapter();
            adapter.notifyItemChanged(position);
        }
    }

    @Override
    public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
        View mRootView = LayoutInflater.from(mActivity).inflate(R.layout.showphoto_magnify, null);
        //设置popupWindow的布局
        PopupWindow mPopupWindow = new PopupWindow(mRootView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new PaintDrawable());
        FrameLayout panel = (FrameLayout) mRootView.findViewById(R.id.fl_sp_magnified);
        SmoothImageView mSmoothImageView = (SmoothImageView) panel.findViewById(R.id.smooth_imageview);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        mSmoothImageView.setOriginalInfo(view.getWidth(), view.getHeight(), location[0], location[1]);
        mSmoothImageView.transformIn();
        mSmoothImageView.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        mSmoothImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        NativeImageLoader.getInstance().loadImage(mSmoothImageView,
                mImageDir + File.separator + mImages.get(position).imageName);
        mPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        return false;
    }

    private void selectAllImages() {
        Flowable.fromIterable(mImages)
                .compose(TransformerHelper.io2main())
                .subscribe(new ResourceSubscriber<ImageEntity>() {
                    @Override
                    public void onNext(ImageEntity imageEntity) {
                        imageEntity.isSelected = true;
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {
                        if (mRecycleView.getAdapter() != null &&
                                ShowPhotosAdapter.class.isInstance(mRecycleView.getAdapter())) {
                            ShowPhotosAdapter adapter = (ShowPhotosAdapter) mRecycleView.getAdapter();
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    public void networkConnectError(String retryAction) {

    }
}
