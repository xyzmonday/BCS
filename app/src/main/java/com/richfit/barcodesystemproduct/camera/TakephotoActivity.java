package com.richfit.barcodesystemproduct.camera;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;

import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.base.BaseFragment;
import com.richfit.common_lib.utils.AppCompat;
import com.richfit.common_lib.utils.FileUtil;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.StatusBarCompat;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 照相activity
 * Created by monday on 2016/11/26.
 */

public class TakephotoActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;

    private Unbinder mUnbinder;

    protected String mTitle;
    protected String mCompanyCode;
    protected String mBizType;
    protected String mRefType;

    private boolean mTakePhotoMode;
    private static int index = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        mUnbinder = ButterKnife.bind(this);
        initVariable();
        setupToolbar();
        setupFragment();
        StatusBarCompat.compat(this, AppCompat.getColor(R.color.colorPrimaryDark, this));
    }

    private void initVariable() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            mTakePhotoMode = bundle.getBoolean(Global.EXTRA_IS_LOCAL_KEY);
            mTitle = bundle.getString(Global.EXTRA_TITLE_KEY);
            mCompanyCode = bundle.getString(Global.EXTRA_COMPANY_CODE_KEY);
            mBizType = bundle.getString(Global.EXTRA_BIZ_TYPE_KEY);
            mRefType = bundle.getString(Global.EXTRA_REF_TYPE_KEY);
        }
    }

    private void setupToolbar() {
        mToolbarTitle.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        mToolbarTitle.setText(mTitle);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        BaseFragment fragment = (BaseFragment) fragmentManager.findFragmentByTag(ShowAndTakePhotoFragment.PHOTO_FRAGMENT_TAG);
        if (fragment == null) {
            fragment = ShowAndTakePhotoFragment.newInstance(getIntent().getExtras());
            fragmentManager.beginTransaction().replace(R.id.content, fragment, ShowAndTakePhotoFragment.PHOTO_FRAGMENT_TAG).commit();
        }
    }

    @OnClick(R.id.floating_button)
    public void onClick(View v) {
        final Intent intent = getIntent();
        toTake(intent, mTakePhotoMode);
    }

    private void toTake(Intent intent, boolean isLocal) {
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            String refNum = bundle.getString(Global.EXTRA_REF_NUM_KEY);
            String refLineNum = bundle.getString(Global.EXTRA_REF_LINE_NUM_KEY);
            int takePhotoType = bundle.getInt(Global.EXTRA_TAKE_PHOTO_TYPE);
            long takeTime = System.currentTimeMillis();
            StringBuilder sb = new StringBuilder();
            String imageDir = "";
            if (TextUtils.isEmpty(refLineNum)) {
                imageDir = isLocal ? FileUtil.getImageCacheDir(this.getApplicationContext(), refNum, takePhotoType, isLocal).getAbsolutePath() :
                        FileUtil.getImageCacheDir(this.getApplicationContext(), refNum, takePhotoType, isLocal).getAbsolutePath();
            } else {
                imageDir = isLocal ? FileUtil.getImageCacheDir(this.getApplicationContext(), refNum, refLineNum, takePhotoType, isLocal).getAbsolutePath() :
                        FileUtil.getImageCacheDir(this.getApplicationContext(), refNum, refLineNum, takePhotoType, isLocal).getAbsolutePath();
            }
            sb.append(imageDir);
            sb.append(File.separator);
            sb.append(String.valueOf(takeTime));
            sb.append("_");
            sb.append(String.valueOf(index++));
            sb.append(Global.IMAGE_DEFAULT_FORMAT);
            File file = new File(sb.toString());
            Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            imageIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
            imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            startActivity(imageIntent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.slide_still, R.anim.slide_out_right);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.take_photo_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 反射显示OverFlowMenu的icon
     *
     * @param featureId
     * @param menu
     * @return
     */
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        setShortcutsVisible(menu);
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    private void setShortcutsVisible(Menu menu) {
        if (MenuBuilder.class.isInstance(menu)) {
            MenuBuilder builder = (MenuBuilder) menu;
            builder.setShortcutsVisible(true);
            try {
                Method m = menu.getClass().getDeclaredMethod(
                        "setOptionalIconsVisible", Boolean.TYPE);
                m.setAccessible(true);
                m.invoke(builder, true);
            } catch (Exception ie) {
                ie.printStackTrace();
            }
        }
    }

    /**
     * 强制actionbar显示overflow菜单
     */
    private void setOverflowMenu() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}