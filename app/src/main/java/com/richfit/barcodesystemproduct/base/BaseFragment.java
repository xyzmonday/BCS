package com.richfit.barcodesystemproduct.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.CheckResult;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.orhanobut.logger.Logger;
import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.SampleApplicationLike;
import com.richfit.barcodesystemproduct.di.component.DaggerFragmentComponent;
import com.richfit.barcodesystemproduct.di.component.FragmentComponent;
import com.richfit.barcodesystemproduct.di.module.FragmentModule;
import com.richfit.barcodesystemproduct.module.main.MainActivity;
import com.richfit.common_lib.IInterface.IFragmentState;
import com.richfit.common_lib.IInterface.IPresenter;
import com.richfit.common_lib.dialog.NetConnectErrorDialogFragment;
import com.richfit.common_lib.dialog.ShowErrorMessageDialog;
import com.richfit.common_lib.utils.CommonUtil;
import com.richfit.common_lib.utils.CreateExtraUIHelper;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.utils.UiUtil;
import com.richfit.domain.bean.BottomMenuEntity;
import com.richfit.domain.bean.RefDetailEntity;
import com.richfit.domain.bean.ReferenceEntity;
import com.richfit.domain.bean.RowConfig;
import com.richfit.domain.bean.SubFuncEntity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.Flowable;

/**
 * Created by monday on 2016/11/10.
 */

public abstract class BaseFragment<P extends IPresenter, M> extends Fragment implements BaseView,
        NetConnectErrorDialogFragment.INetworkConnectListener, IFragmentState<M> {

    protected static final int EXTRA_HORIZONTAL_ORIENTATION_TYPE = 0;
    protected static final int EXTRA_VERTICAL_ORIENTATION_TYPE = 1;
    public static final int HEADER_FRAGMENT_INDEX = 0x0;
    public static final int DETAIL_FRAGMENT_INDEX = 0x1;
    public static final int COLLECT_FRAGMENT_INDEX = 0x2;

    protected FragmentComponent mFragmentComponent;

    private boolean isActivityCreated;

    protected View mView;

    @Inject
    protected P mPresenter;

    protected Activity mActivity;

    private Unbinder mUnbinder;

    protected NetConnectErrorDialogFragment mNetConnectErrorDialogFragment;

    /*抬头界面，数据明细界面，数据采集界面共享的单据数据，注意我们需要将单据数据和缓存数据隔离*/
    protected static ReferenceEntity mRefData;
    /*抬头界面，数据明细界面，数据采集界面共享的额外字段配置信息*/
    protected static SubFuncEntity mSubFunEntity = new SubFuncEntity();

    /*额外控件缓存*/
    protected Map<String, View> mExtraViews;

    protected String mCompanyCode;
    protected String mModuleCode;
    protected String mBizType;
    protected String mRefType;
    protected int mFragmentType;
    //该页签名称
    protected String mTabTitle;
    protected boolean mIsOpenBatchManager = Global.batchFlag;

    public static BaseFragment findFragment(FragmentManager fm, String tag, String companyCode, String moduleCode,
                                            String bizType, String refType, int fragmentType, String title, Class clazz) {
        BaseFragment fragment = (BaseFragment) fm.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = newInstance(clazz, companyCode, moduleCode, bizType, refType, fragmentType, title);
        }
        return fragment;
    }

    public static BaseFragment findFragment(FragmentManager fm, String tag, Bundle arguments, String className) {
        BaseFragment fragment = (BaseFragment) fm.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = newInstance(className, arguments);
        }
        return fragment;
    }

    public static BaseFragment findFragment(FragmentManager fm, String tag, String companyCode, String moduleCode,
                                            String bizType, String refType, int fragmentType, String title, String className) {
        BaseFragment fragment = (BaseFragment) fm.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = newInstance(className, companyCode, moduleCode, bizType, refType, fragmentType, title);
        }
        return fragment;
    }

    private static void setFieldValue(Class clazz, Object object, String value) {
        try {
            /**
             * getDeclaredMethod*()获取的是类自身声明的所有方法，包含public、protected和private方法。
             * getMethod*()获取的是类的所有共有方法，这就包括自身的所有public方法，和从基类继承的、从接口实现的所有public方法。
             */
            Method method = clazz.getMethod("setTabTitle", String.class);
            method.setAccessible(true);
            method.invoke(object, value);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.d("反射方法出错 = " + e.getMessage());
        }
    }


    private static <T extends BaseFragment> T newInstance(String className, String companyCode, String moduleCode,
                                                          String bizType, String refType, int fragmentType, String title) {
        T instance = null;
        try {
            final Class clazz = Class.forName(className);
            instance = (T) clazz.newInstance();

            Bundle bundle = new Bundle();
            bundle.putString(Global.EXTRA_COMPANY_CODE_KEY, companyCode);
            bundle.putString(Global.EXTRA_MODULE_CODE_KEY, moduleCode);
            bundle.putString(Global.EXTRA_BIZ_TYPE_KEY, bizType);
            bundle.putString(Global.EXTRA_REF_TYPE_KEY, refType);
            bundle.putString(Global.EXTRA_TITLE_KEY, title);
            bundle.putInt(Global.EXTRA_FRAGMENT_TYPE_KEY, fragmentType);

            //反射设置TabTitle
            setFieldValue(clazz, instance, title);

            instance.setArguments(bundle);
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 生成BaseFragment的子类的实例对象
     *
     * @param clazz
     * @param moduleCode:组模块编码
     * @param bizType：子模块编码
     * @param refType：业务类型
     * @param <T>
     * @return
     */
    private static <T extends BaseFragment> T newInstance(final Class<T> clazz, String companyCode, String moduleCode,
                                                          String bizType, String refType, int fragmentType, String title) {
        T instance = null;
        try {
            instance = clazz.newInstance();
            Bundle bundle = new Bundle();
            bundle.putString(Global.EXTRA_COMPANY_CODE_KEY, companyCode);
            bundle.putString(Global.EXTRA_MODULE_CODE_KEY, moduleCode);
            bundle.putString(Global.EXTRA_BIZ_TYPE_KEY, bizType);
            bundle.putString(Global.EXTRA_REF_TYPE_KEY, refType);
            bundle.putString(Global.EXTRA_TITLE_KEY, title);
            bundle.putInt(Global.EXTRA_FRAGMENT_TYPE_KEY, fragmentType);
            setFieldValue(clazz, instance, title);
            instance.setArguments(bundle);
            return instance;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T extends BaseFragment> T newInstance(String className, Bundle arguments) {
        T instance = null;
        try {
            instance = (T) Class.forName(className).newInstance();
            instance.setArguments(arguments);
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mExtraViews = new HashMap<>();
        Bundle bundle = getArguments();
        if (bundle != null) {
            mCompanyCode = bundle.getString(Global.EXTRA_COMPANY_CODE_KEY);
            mModuleCode = bundle.getString(Global.EXTRA_MODULE_CODE_KEY);
            mBizType = bundle.getString(Global.EXTRA_BIZ_TYPE_KEY);
            mRefType = bundle.getString(Global.EXTRA_REF_TYPE_KEY);
            mTabTitle = bundle.getString(Global.EXTRA_TITLE_KEY);
            mFragmentType = bundle.getInt(Global.EXTRA_FRAGMENT_TYPE_KEY);
        }
        initVariable(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(getContentId(), container, false);
        mUnbinder = ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        mFragmentComponent = DaggerFragmentComponent.builder()
                .fragmentModule(new FragmentModule(this))
                .appComponent(SampleApplicationLike.getAppComponent())
                .build();

        initInjector();
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
        initView();
        initEvent();
        initData();
        isActivityCreated = true;
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 此方法目前仅适用于标示ViewPager中的Fragment是否真实可见.
     * 第一次执行时候，该方法可能早与onCreate方法，此时不适合做业务
     * 因为我们需要在onCreate方法初始化相关变量，所以设置isCreated标志
     * 位。如果isVisibleToUser为true，说明此时该页面真正的对用户可见。
     * 那么可以执行相关的业务。
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isActivityCreated) {
            return;
        }
        if (isVisibleToUser) {
            initDataLazily();
        } else {
            _onPause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnbinder != Unbinder.EMPTY) mUnbinder.unbind();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    @Override
    public void onDestroy() {
        SampleApplicationLike.getRefWatcher().watch(this);
        super.onDestroy();
    }

    //获取布局文件
    protected abstract int getContentId();

    public abstract void initInjector();

    protected void showMessage(String message) {
        Snackbar.make(mView, message, Snackbar.LENGTH_LONG).show();
    }

    /**
     * 获取view上的数据（该view只能是textView或者其子类）
     *
     * @param view
     * @return
     */
    @CheckResult
    protected String getString(View view) {
        if (view != null && TextView.class.isInstance(view)) {
            TextView tv = (TextView) view;
            return tv.getText().toString().trim();
        }
        return "";
    }

    protected int getInteger(int resId) {
        if (resId != 0) {
            return mActivity.getResources().getInteger(resId);
        }
        return -1;
    }

    /**
     * 通过物资条码信息和批次信息匹配
     * 这里的逻辑是：如果启用了批次管理那么明细里面可能有也可能没有批次，如果没有启用批次管理，那么
     * 明细一定没有批次
     *
     * @return:与该物料匹配的行号集合
     */
    protected Flowable<ArrayList<String>> matchMaterialInfo(final String materialNum, final String batchFlag) {
        if (mRefData == null || mRefData.billDetailList == null ||
                mRefData.billDetailList.size() == 0 || TextUtils.isEmpty(materialNum)) {
            return Flowable.error(new Throwable("请先单据明细"));
        }
        ArrayList<String> lineNums = new ArrayList<>();
        List<RefDetailEntity> list = mRefData.billDetailList;
        for (RefDetailEntity entity : list) {
            if (mIsOpenBatchManager) {
                final String lineNum = entity.lineNum;
                //如果打开了批次，那么在看明细中是否有批次
                if (!TextUtils.isEmpty(entity.batchFlag) && !TextUtils.isEmpty(batchFlag)) {
                    if (materialNum.equalsIgnoreCase(entity.materialNum) &&
                            batchFlag.equalsIgnoreCase(entity.batchFlag) &&
                            !TextUtils.isEmpty(lineNum))

                        lineNums.add(lineNum);
                } else {
                    if (materialNum.equalsIgnoreCase(entity.materialNum) &&
                            !TextUtils.isEmpty(lineNum))
                        lineNums.add(lineNum);
                }
            } else {
                final String lineNum = entity.lineNum;
                //如果明细中没有打开了批次管理,那么只匹配物料编码
                if (materialNum.equalsIgnoreCase(entity.materialNum) && !TextUtils.isEmpty(lineNum))
                    lineNums.add(entity.lineNum);

            }
        }
        if (lineNums.size() == 0) {
            return Flowable.error(new Throwable("未获取到匹配的物料"));
        }
        return Flowable.just(lineNums);
    }

    /**
     * 通过单据行的行号得到该行在单据明细列表中的位置
     *
     * @param lineNum:单据行号
     * @return 返回该行号对应的行明细在明细列表的索引
     */
    protected int getIndexByLineNum(String lineNum) {
        int index = -1;
        if (TextUtils.isEmpty(lineNum))
            return index;

        if (mRefData == null || mRefData.billDetailList == null
                || mRefData.billDetailList.size() == 0)
            return index;

        for (RefDetailEntity detailEntity : mRefData.billDetailList) {
            index++;
            if (lineNum.equalsIgnoreCase(detailEntity.lineNum))
                break;

        }
        return index;
    }

    /**
     * 获取行明细
     *
     * @param lineNum:单据行号
     * @return
     */
    protected RefDetailEntity getLineData(String lineNum) {
        int lineIndex = getIndexByLineNum(lineNum);
        return mRefData.billDetailList.get(lineIndex);
    }

    /**
     * 设置输入法模式，在该模式下视图的内容会上移，防止输入键盘覆盖住视图
     *
     * @param context
     */
    protected void setSoftInputMode(Context context) {
        AppCompatActivity activity = (AppCompatActivity) context;
        activity.getWindow().
                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN |
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    /**
     * 掩藏输入法
     */
    protected void hideKeyboard(View view) {
        if (view == null)
            view = mActivity.getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 显示网络错误重试对话框
     *
     * @param action：重试的事件类型
     */
    protected void showNetConnectErrorDialog(String action) {
        AppCompatActivity activity = (AppCompatActivity) mActivity;
        FragmentManager supportFragmentManager = activity.getSupportFragmentManager();
        mNetConnectErrorDialogFragment = (NetConnectErrorDialogFragment) supportFragmentManager.findFragmentByTag("NETWORK_CONNECT_ERROR_TAG");
        if (mNetConnectErrorDialogFragment == null) {
            mNetConnectErrorDialogFragment = NetConnectErrorDialogFragment.newInstance(action);
            mNetConnectErrorDialogFragment.setINetworkConnectListener(this);
        }
        mNetConnectErrorDialogFragment.show(supportFragmentManager, Global.NETWORK_CONNECT_ERROR_TAG);
    }

    protected void closeNetConnectErrorDialog() {
        if (mNetConnectErrorDialogFragment != null) {
            mNetConnectErrorDialogFragment.dismiss();
        }
    }

    @Override
    public void retry(String action) {
        if (mNetConnectErrorDialogFragment != null) {
            mNetConnectErrorDialogFragment.dismiss();
        }
    }

    @Override
    public void networkConnectError(String retryAction) {
        showNetConnectErrorDialog(retryAction);
    }


    /**
     * 所有对额外字段的处理都写成方法，以便子类能够扩展
     * 添加所有的额外控件
     *
     * @param configs:配置文件
     * @param orientation:0表示水平添加动态控件，1表示垂直添加动态控件
     */
    protected void createExtraUI(List<RowConfig> configs, int orientation) {
        if (mView == null) {
            return;
        }
        if (configs == null || configs.size() == 0) {
            return;
        }
        View view = mView.findViewById(R.id.root_id);
        if (!LinearLayout.class.isInstance(view)) {
            return;
        }
        LinearLayout extraRootContainer = (LinearLayout) view;
        if (configs == null || configs.size() == 0)
            return;
        switch (orientation) {
            case EXTRA_HORIZONTAL_ORIENTATION_TYPE:
                addExtraHorizontalUI(extraRootContainer, configs);
                break;
            case EXTRA_VERTICAL_ORIENTATION_TYPE:
                addExtraVerticalUI(extraRootContainer, configs);
                break;
        }
    }

    /**
     * 为数据明细界面增加额外控件
     *
     * @param extraRootContainer
     * @param configs
     */
    private void addExtraHorizontalUI(LinearLayout extraRootContainer, List<RowConfig> configs) {
        if (extraRootContainer != null) {
            extraRootContainer.setOrientation(LinearLayout.HORIZONTAL);
            if (configs != null && configs.size() > 0) {
                for (RowConfig config : configs) {
                    if ("N".equals(config.displayFlag))
                        continue;
                    View view = CreateExtraUIHelper.createTableHeaderColumn(mActivity,
                            (int) mActivity.getResources().getDimension(R.dimen.extra_column_width));
                    if (view != null && TextView.class.isInstance(view)) {
                        TextView tv = (TextView) view;
                        tv.setText(config.propertyName);
                        extraRootContainer.addView(tv);
                        extraRootContainer.addView(CreateExtraUIHelper.addHeaderTabSeparator(mActivity));
                    }
                }
            }
        }
    }

    /**
     * 为数据采集界面添加额外控件
     *
     * @param extraRootContainer
     * @param configs
     */
    private void addExtraVerticalUI(LinearLayout extraRootContainer, List<RowConfig> configs) {
        if (extraRootContainer != null) {
            extraRootContainer.setOrientation(LinearLayout.VERTICAL);
            if (configs != null && configs.size() > 0) {
                for (RowConfig config : configs) {
                    if ("N".equals(config.displayFlag))
                        continue;
                    //注意这里返回的LinearLayout
                    View childContainer = CreateExtraUIHelper.createRowView(mActivity, config);
                    if (childContainer != null && LinearLayout.class.isInstance(childContainer)) {
                        extraRootContainer.addView(childContainer);
                        LinearLayout llContainer = (LinearLayout) childContainer;
                        if (llContainer.getChildCount() >= 2)
                            mExtraViews.put(config.propertyCode, ((LinearLayout) childContainer).getChildAt(1));
                    }
                }
            }
        }
    }

    /**
     * 为所有的额外的控件赋值。configs代表了所有的控件，数据源就是每一行的extraDataMap。
     * 具体的实现步骤是，在生成额外控件的时候，将所有的额外控件都缓存到map,该map的key是propertyCode,
     * value是view，在绑定数据的时候，通过可以取出每一个view，然后绑定即可。
     *
     * @param extraDataMap:额外字段数据源
     * @param configs：额外字段配置信息
     */
    protected void bindExtraUI(final ArrayList<RowConfig> configs, final Map<String, Object> extraDataMap) {
        if (mExtraViews == null || mExtraViews.size() == 0) {
            return;
        }
        if (extraDataMap == null || extraDataMap.size() == 0)
            return;
        if (configs == null || configs.size() == 0)
            return;
        for (final RowConfig config : configs) {
            final String propertyCode = config.propertyCode;
            final String uiType = config.uiType;
            final String displayFlag = config.displayFlag;
            final View extraView = mExtraViews.get(propertyCode);
            extraView.setEnabled(true);
            //如果额外字段是TextView及其子类，而且需要显示。
            if (("0".equals(uiType) || "1".equals(uiType) || "2".equals(uiType)) && "Y".equals(displayFlag)) {
                //说明是TextView那么需要显示
                if (TextView.class.isInstance(extraView)) {
                    TextView tv = (TextView) extraView;
                    //获取数据，并且判断数据的类型
                    Object obj = extraDataMap.get(propertyCode);
                    if (obj != null && String.class.isInstance(obj)) {
                        tv.setText(obj.toString());
                    } else {
                        tv.setText("");
                    }
                }
            } else if ("3".equals(uiType) && "Y".equals(displayFlag)) {
                //说明是下拉列表
                if (MaterialSpinner.class.isInstance(extraView)) {
                    MaterialSpinner spinner = (MaterialSpinner) extraView;
                    //1.如果spiner已经绑定过，那么使用tag再次绑定数据
                    Object obj = null;
                    final Object tag = spinner.getTag();
                    if (tag != null) {
                        obj = tag;
                    } else {
                        //2.通过字典绑定spinner
                        obj = extraDataMap.get(UiUtil.MD5(propertyCode));
                    }
                    setSelection(spinner, obj, CommonUtil.Obj2String(extraDataMap.get(propertyCode)));
                }
            } else if ("4".equals(uiType)) {
                //checkBox类型
                if (CheckBox.class.isInstance(extraView)) {
                    CheckBox cb = (CheckBox) extraView;
                    Object obj = extraDataMap.get(propertyCode);
                    if (obj != null && String.class.isInstance(obj)) {
                        String isCheck = (String) obj;
                        //0表示选中
                        cb.setChecked("0".equals(isCheck));
                    }
                }
            }
        }
    }

    private void setSelection(MaterialSpinner spinner, Object o, String selectedItem) {
        if (o != null && LinkedHashMap.class.isInstance(o)) {
            Map<String, String> map = (LinkedHashMap<String, String>) o;
            if (map.size() > 0) {
                spinner.setTag(map);
                ArrayList<String> keys = new ArrayList<>();
                ArrayList<String> values = new ArrayList<>();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    keys.add(entry.getKey());
                    values.add(entry.getValue());
                }
                spinner.setItems(values);

                if (!TextUtils.isEmpty(selectedItem)) {
                    int indexOf = keys.indexOf(selectedItem);
                    if (indexOf >= 0) {
                        spinner.setSelectedIndex(indexOf);
                    }
                }
            }
        }
    }

    /**
     * 针对修改界面的额外字段，一般来说允许用户修改额外字段，所以需要将其的enable=false
     *
     * @param configs
     * @param extraDataMap
     * @param isEnable
     */
    protected void bindExtraUI(final ArrayList<RowConfig> configs, final Map<String, Object> extraDataMap,
                               boolean isEnable) {
        if (mExtraViews == null || mExtraViews.size() == 0) {
            return;
        }
        if (extraDataMap == null || extraDataMap.size() == 0)
            return;
        if (configs == null || configs.size() == 0)
            return;
        for (final RowConfig config : configs) {
            final String propertyCode = config.propertyCode;
            final String uiType = config.uiType;
            final String displayFlag = config.displayFlag;
            final View extraView = mExtraViews.get(propertyCode);
            extraView.setEnabled(isEnable);
            //如果额外字段是TextView及其子类，而且需要显示。
            if (("0".equals(uiType) || "1".equals(uiType) || "2".equals(uiType)) && "Y".equals(displayFlag)) {
                //说明是TextView那么需要显示
                if (TextView.class.isInstance(extraView)) {
                    TextView tv = (TextView) extraView;
                    //获取数据，并且判断数据的类型
                    Object obj = extraDataMap.get(propertyCode);
                    if (obj != null && String.class.isInstance(obj)) {
                        tv.setText(obj.toString());
                    } else {
                        tv.setText("");
                    }
                }
            } else if ("3".equals(uiType) && "Y".equals(displayFlag)) {
                //说明是下拉列表
                if (MaterialSpinner.class.isInstance(extraView)) {
                    MaterialSpinner spinner = (MaterialSpinner) extraView;
                    Object obj = null;
                    final Object tag = spinner.getTag();
                    if (tag != null) {
                        obj = tag;
                    } else {
                        //2.通过字典绑定spinner
                        obj = extraDataMap.get(UiUtil.MD5(propertyCode));
                    }
                    setSelection(spinner, obj, CommonUtil.Obj2String(extraDataMap.get(propertyCode)));
                    spinner.setEnabled(isEnable);
                }
            } else if ("4".equals(uiType)) {
                //checkBox类型
                if (CheckBox.class.isInstance(extraView)) {
                    CheckBox cb = (CheckBox) extraView;
                    Object obj = extraDataMap.get(propertyCode);
                    if (obj != null && String.class.isInstance(obj)) {
                        String isCheck = (String) obj;
                        //0表示选中
                        cb.setChecked("0".equals(isCheck));
                    }
                    cb.setEnabled(isEnable);
                }
            }
        }
    }

    private void setSelection(MaterialSpinner spinner, List<String> values, String selectedItem) {

    }

    /**
     * 清除额外字段绑定的数据
     *
     * @param configs
     */

    protected void clearExtraUI(List<RowConfig> configs) {
        if (configs == null || configs.size() == 0)
            return;
        for (final RowConfig config : configs) {
            final String uiType = config.uiType;
            final String propertyCode = config.propertyCode;
            if (("0".equals(uiType) || "1".equals(uiType) || "2".equals(uiType))) {
                //说明是TextView那么需要显示
                final View extraView = mExtraViews.get(propertyCode);
                if (extraView != null && TextView.class.isInstance(extraView)) {
                    TextView tv = (TextView) extraView;
                    tv.setText("");
                }
            } else if ("3".equals(uiType)) {
                //说明是下拉列表。这里先不清除下拉列表的数据源
//                final View extraView = mExtraViews.get(propertyCode);
//                if (extraView != null && MaterialSpinner.class.isInstance(extraView)) {
//                    MaterialSpinner spinner = (MaterialSpinner) extraView;
//                    spinner.setItems("");
//                }
            }
        }
    }

    /**
     * 通过配置信息保存扩展字段。这里按照uiType分类对于不同类型的控件的数据
     * 进行保存。注意凡显示的数据就保存，凡是显示的数据那么不保存。
     *
     * @param configs
     */
    protected Map<String, Object> saveExtraUIData(List<RowConfig> configs) {
        if (configs == null || configs.size() == 0)
            return null;
        final Map<String, Object> extraDataMap = new HashMap<>();
        for (RowConfig config : configs) {
            final String uiType = config.uiType;
            final String propertyCode = config.propertyCode;
            if (("0".equals(uiType) || "1".equals(uiType) || "2".equals(uiType)) &&
                    "Y".equals(config.displayFlag)) {
                if (!TextUtils.isEmpty(propertyCode)) {
                    View view = mExtraViews.get(propertyCode);
                    if (view != null && TextView.class.isInstance(view)) {
                        TextView tv = (TextView) view;
                        extraDataMap.put(propertyCode, getString(tv));
                    }
                }
            } else if ("3".equals(uiType) && "Y".equals(config.displayFlag)) {
                //下拉列表类型的控件
                if (!TextUtils.isEmpty(propertyCode)) {
                    View view = mExtraViews.get(propertyCode);
                    if (view != null && MaterialSpinner.class.isInstance(view)) {
                        MaterialSpinner spinner = (MaterialSpinner) view;

                        Object obj = spinner.getTag();
                        if (obj != null && LinkedHashMap.class.isInstance(obj)) {
                            Map<String, String> map = (LinkedHashMap<String, String>) obj;
                            ArrayList<String> items = new ArrayList<>();
                            for (Map.Entry<String, String> entry : map.entrySet()) {
                                items.add(entry.getKey());
                            }
                            String selectedItem = items.get(spinner.getSelectedIndex());
                            //保存用户选中的数据
                            extraDataMap.put(propertyCode, selectedItem);
                        }
                    }
                }
            } else if ("4".equals(uiType) && "Y".equals(config.displayFlag)) {
                if (!TextUtils.isEmpty(propertyCode)) {
                    View view = mExtraViews.get(propertyCode);
                    if (view != null && CheckBox.class.isInstance(view)) {
                        CheckBox cb = (CheckBox) view;
                        extraDataMap.put(propertyCode, cb.isChecked() ? String.valueOf(0) : String.valueOf(1));
                    }
                }
            }
        }
        return extraDataMap;
    }

    /**
     * 检查当前界面所有额外字段的必输控件是否已经输入值。
     * 这里只对于显示的扩展字段进行检查，而对于display=N，inputFlag=N
     * 的扩展字段，只需要将单据中的原始数据回传到服务端即可。
     *
     * @return
     */
    protected boolean checkExtraData(List<RowConfig> configs) {
        //如果没有额外字段那么默认为合格
        if (configs == null || configs.size() == 0)
            return true;
        if (mExtraViews == null || mExtraViews.size() == 0)
            return true;
        for (RowConfig config : configs) {
            final String uiType = config.uiType;
            //检查TextView,EditText,RichEditText控件的数据是否合格
            if (("1".equals(uiType) || "2".equals(uiType))
                    && "Y".equals(config.inputFlag) && "Y".equals(config.displayFlag)) {
                //说明是EditText而且必须输入
                View view = mExtraViews.get(config.propertyCode);
                TextView tv = (TextView) view;
                String value = getString(tv);
                if (TextUtils.isEmpty(value)) {
                    return false;
                }
            } else if ("3".equals(uiType) && "Y".equals(config.inputFlag) && "Y".equals(config.displayFlag)) {
                View view = mExtraViews.get(config.propertyCode);
                MaterialSpinner spinner = (MaterialSpinner) view;
                int selectedIndex = spinner.getSelectedIndex();
                if (selectedIndex < 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 通过配置信息检查必须输入字段是否有值
     *
     * @return 返回true说明用户保存过了
     */
    protected boolean checkExtraData(final List<RowConfig> configs, final Map<String, Object> extraMap) {
        if (extraMap == null || extraMap.size() == 0 || configs == null || configs.size() == 0)
            return true;
        boolean flag = true;
        for (RowConfig config : configs) {
            if (("1".equals(config.uiType) || "2".equals(config.uiType))
                    && "Y".equals(config.inputFlag) && "Y".equals(config.displayFlag)) {
                final String propertyCode = config.propertyCode;
                //EditText必输类型
                if (extraMap.get(propertyCode) == null ||
                        TextUtils.isEmpty(extraMap.get(propertyCode).toString())) {
                    flag = false;
                    break;
                }
            }
        }
        return flag;
    }


    /**
     * 清除公共空控件的数据
     */
    protected void clearCommonUI(View... views) {
        for (View view : views) {
            if (view == null)
                continue;
            if (CheckBox.class.isInstance(view)) {
                CheckBox cb = (CheckBox) view;
                cb.setChecked(false);
            } else if (TextView.class.isInstance(view)) {
                TextView tv = (TextView) view;
                tv.setText("");
            } else if (Spinner.class.isInstance(view)) {
                Spinner sp = (Spinner) view;
                if (sp.getAdapter() != null)
                    sp.setSelection(0);
            }
        }
    }

    protected Map<String, Object> createExtraMap(String colNum, Map<String, Object> extraLineMap,
                                                 Map<String, Object> extraLocationMap) {
        Map<String, Object> map = new HashMap<>();
        ArrayList<RowConfig> extraConfigs = getExtraConfigs();
        Map<String, Object> extraMap = getExtraMap(extraLineMap, extraLocationMap);
        for (RowConfig config : extraConfigs) {
            if (colNum.equals(config.colNum)) {
                //找出该类别的配置信息
                final String key = config.propertyCode;
                if (!TextUtils.isEmpty(key)) {
                    Object obj = extraMap.get(key);
                    String colName = config.colName;
                    if (!TextUtils.isEmpty(colName) && obj != null)
                        map.put(colName, obj);
                }
            }
        }
        return map;
    }

    /**
     * 针对没有仓位的功能模块，需要保存的额外字段
     *
     * @param colNum
     * @param extraLineMap
     * @return
     */
    protected Map<String, Object> createExtraMap(String colNum, Map<String, Object> extraLineMap) {
        Map<String, Object> map = new HashMap<>();
        ArrayList<RowConfig> extraConfigs = getExtraConfigs();
        Map<String, Object> extraMap = getExtraMap(extraLineMap);
        for (RowConfig config : extraConfigs) {
            if (colNum.equals(config.colNum)) {
                //找出该类别的配置信息
                final String key = config.propertyCode;
                if (!TextUtils.isEmpty(key)) {
                    Object obj = extraMap.get(key);
                    String colName = config.colName;
                    if (!TextUtils.isEmpty(colName) && obj != null)
                        map.put(colName, obj);
                }
            }
        }
        return map;
    }


    /**
     * 获取抬头界面中需要上传的额外字段
     *
     * @return
     */
    protected Map<String, Object> createExtraHeaderMap() {
        Map<String, Object> map = new HashMap<>();
        ArrayList<RowConfig> configs = getExtraConfigs();
        for (RowConfig config : configs) {
            if (Global.EXTRA_HEADER_MAP_TYPE.equals(config.colNum)) {
                //找出该类别的配置信息
                final String key = config.propertyCode;
                if (!TextUtils.isEmpty(key)) {
                    Object obj = mRefData.mapExt.get(key);
                    String colName = config.colName;
                    if (!TextUtils.isEmpty(colName) && obj != null)
                        map.put(colName, obj);
                }
            }
        }
        return map;
    }

    private boolean isEmpty(Map map) {
        return map == null || map.size() == 0;
    }

    private boolean isEmpty(Collection collection) {
        return collection == null || collection.size() == 0;
    }

    protected boolean isEmpty(String str) {
        return TextUtils.isEmpty(str);
    }

    protected void showSuccessDialog(String message) {
        new SweetAlertDialog(mActivity, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("温馨提示")
                .setContentText(message)
                .show();
    }

    protected void showErrorDialog(String message) {
        String[] errors = message.split("______");
        MainActivity activity = (MainActivity) mActivity;
        FragmentManager fm = activity.getSupportFragmentManager();
        ShowErrorMessageDialog dialog = ShowErrorMessageDialog.newInstance(errors);
        dialog.show(fm, "nms_show_error_messages");
    }

    protected void showErrorDialog(String[] messages) {
        MainActivity activity = (MainActivity) mActivity;
        FragmentManager fm = activity.getSupportFragmentManager();
        ShowErrorMessageDialog dialog = ShowErrorMessageDialog.newInstance(messages);
        dialog.show(fm, "nms_show_error_messages");
    }


    /**
     * 获取抬头+行+仓位的所有配置信息
     *
     * @return
     */
    private ArrayList<RowConfig> getExtraConfigs() {
        ArrayList<RowConfig> configs = new ArrayList<>();
        if (!isEmpty(mSubFunEntity.headerConfigs))
            configs.addAll(mSubFunEntity.headerConfigs);

        if (!isEmpty(mSubFunEntity.collectionConfigs))
            configs.addAll(mSubFunEntity.collectionConfigs);

        if (!isEmpty(mSubFunEntity.locationConfigs))
            configs.addAll(mSubFunEntity.locationConfigs);
        return configs;
    }

    private Map<String, Object> getExtraMap(Map<String, Object> extraLineMap,
                                            Map<String, Object> extraLocationMap) {
        Map<String, Object> tmp = new HashMap<>();
        Map<String, Object> extraCollectedLineMap = saveExtraUIData(mSubFunEntity.collectionConfigs);
        Map<String, Object> extraCollectedLocationMap = saveExtraUIData(mSubFunEntity.locationConfigs);
        //注意这里最新保存的数据需要覆盖原始单据中的数据(或者说上一次的缓存数据)
        extraLineMap = UiUtil.copyMap(extraCollectedLineMap, extraLineMap);
        extraLocationMap = UiUtil.copyMap(extraCollectedLocationMap, extraLocationMap);
        if (!isEmpty(mRefData.mapExt))
            tmp.putAll(mRefData.mapExt);

        if (!isEmpty(extraLineMap))
            tmp.putAll(extraLineMap);

        if (!isEmpty(extraLocationMap))
            tmp.putAll(extraLocationMap);
        return tmp;
    }

    private Map<String, Object> getExtraMap(Map<String, Object> extraLineMap) {
        Map<String, Object> tmp = new HashMap<>();
        Map<String, Object> extraCollectedLineMap = saveExtraUIData(mSubFunEntity.collectionConfigs);
        extraLineMap = UiUtil.copyMap(extraCollectedLineMap, extraLineMap);

        if (!isEmpty(mRefData.mapExt))
            tmp.putAll(mRefData.mapExt);

        if (!isEmpty(extraLineMap))
            tmp.putAll(extraLineMap);
        return tmp;
    }

    protected void setAutoCompleteConfig(AutoCompleteTextView autoComplete) {
        autoComplete.setThreshold(1);
        autoComplete.setDropDownWidth(autoComplete.getWidth() * 2);
        autoComplete.isPopupShowing();
    }

    /**
     * 初始化供应商下拉列表的配置
     */
    protected void showAutoCompleteConfig(AutoCompleteTextView autoComplete) {
        if (autoComplete.getAdapter() != null) {
            autoComplete.showDropDown();
        }
    }

    public String getTabTitle() {
        return mTabTitle;
    }

    public int getFragmentType() {
        return mFragmentType;
    }

    public void setTabTitle(String title) {
        this.mTabTitle = title;
    }

    protected List<BottomMenuEntity> provideDefaultBottomMenu() {
        ArrayList<BottomMenuEntity> menus = new ArrayList<>();
        BottomMenuEntity menu = new BottomMenuEntity();
        menu.menuName = "过账";
        menu.menuImageRes = R.mipmap.icon_transfer;
        menus.add(menu);

        menu = new BottomMenuEntity();
        menu.menuName = "数据上传";
        menu.menuImageRes = R.mipmap.icon_data_submit;
        menus.add(menu);

        menu = new BottomMenuEntity();
        menu.menuName = "下架";
        menu.menuImageRes = R.mipmap.icon_down_location;
        menus.add(menu);
        return menus;
    }

    //初始化相关变量
    protected void initVariable(@Nullable Bundle savedInstanceState) {

    }

    //初始化view
    protected void initView() {
    }

    //注册所有的监听事件,可以不实现
    public void initEvent() {
    }

    /**
     * 初始化数据, 可以不实现.该方法在onResume方法中调用，说明在大部分的时候
     * 该方法只会调用一次，也就是我们需要初始化一下静态的数据。比如单据类型等。
     */
    public void initData() {

    }

    //赖加载数据。该方法在Fragment可见的时候调用。可以动态的加载数据
    public void initDataLazily() {

    }

    //该方法在fragment不可见的时候调用，相当于onPause方法，用户保存抬头界面相关数据。
    public void _onPause() {

    }

    protected List<String> getStringArray(@ArrayRes int id) {
        return Arrays.asList(getResources().getStringArray(id));
    }

    @Override
    public boolean checkDataBeforeOperationOnHeader() {
        return true;
    }

    @Override
    public void operationOnHeader(final String companyCode) {

    }

    @Override
    public boolean checkDataBeforeOperationOnDetail() {
        return true;
    }

    @Override
    public void showOperationMenuOnDetail(String companyCode) {

    }

    @Override
    public boolean checkCollectedDataBeforeSave() {
        return true;
    }

    @Override
    public void saveCollectedData() {

    }

    @Override
    public void showOperationMenuOnCollection(String companyCode) {

    }

    @Override
    public void handleBarCodeScanResult(String type, String[] list) {

    }

    @Override
    public void deleteNode(M node, int position) {

    }

    @Override
    public void editNode(M node, int position) {

    }

    @Override
    public void readConfigsSuccess(List<ArrayList<RowConfig>> configs) {

    }

    @Override
    public void readConfigsFail(String message) {

    }

    @Override
    public void readConfigsComplete() {

    }

    @Override
    public void readExtraDictionarySuccess(Map<String, Object> datas) {

    }

    @Override
    public void readExtraDictionaryFail(String message) {

    }

    @Override
    public void readExtraDictionaryComplete() {

    }
}
