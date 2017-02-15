package com.richfit.barcodesystemproduct.di.component;

import android.app.Activity;
import android.content.Context;

import com.richfit.barcodesystemproduct.camera.ShowAndTakePhotoFragment;
import com.richfit.barcodesystemproduct.di.ContextLife;
import com.richfit.barcodesystemproduct.di.FragmentScope;
import com.richfit.barcodesystemproduct.di.module.FragmentModule;
import com.richfit.barcodesystemproduct.module_acceptstore.qingyang_asn.collect.QingYangASNCollectFragment;
import com.richfit.barcodesystemproduct.module_acceptstore.qingyang_asn.detail.QingYangASNDetailFragment;
import com.richfit.barcodesystemproduct.module_acceptstore.qingyang_asn.edit.QingYangASNEditFragment;
import com.richfit.barcodesystemproduct.module_acceptstore.qingyang_asn.header.QingYangASNHeaderFragment;
import com.richfit.barcodesystemproduct.module_approval.qingyang_ao.collect.QingYangAOCollectFragment;
import com.richfit.barcodesystemproduct.module_approval.qingyang_ao.detail.QingYangAODetailFragment;
import com.richfit.barcodesystemproduct.module_approval.qingyang_ao.edit.QingYangAOEditFragment;
import com.richfit.barcodesystemproduct.module_approval.qingyang_ao.header.QingYangAOHeaderFragment;
import com.richfit.barcodesystemproduct.module_delivery.qinghai_dsxs.QingHaiDSXSCollectFragment;
import com.richfit.barcodesystemproduct.module_delivery.qinghai_dsxs.QingHaiDSXSDetailFFragment;
import com.richfit.barcodesystemproduct.module_delivery.qinghai_dsxs.QingHaiDSXSEditFragment;
import com.richfit.barcodesystemproduct.module_delivery.qinghai_dsxs.QingHaiDSXSHeaderFragment;
import com.richfit.barcodesystemproduct.module_delivery.qingyang_dsy.QingYangDSYCollectFragment;
import com.richfit.barcodesystemproduct.module_delivery.qingyang_dsy.QingYangDSYDetailFragment;
import com.richfit.barcodesystemproduct.module_delivery.qingyang_dsy.QingYangDSYEditFragment;
import com.richfit.barcodesystemproduct.module_delivery.qingyang_dsy.QingYangDSYHeaderFragment;
import com.richfit.barcodesystemproduct.module_locationadjust.collect.LACollectFragment;
import com.richfit.barcodesystemproduct.module_locationadjust.header.LAHeaderFragment;
import com.richfit.barcodesystemproduct.module_movestore.qinghai_ubsto101.QingHaiUbSto101CollectFragment;
import com.richfit.barcodesystemproduct.module_movestore.qinghai_ubsto101.QingHaiUbSto101DetailFragment;
import com.richfit.barcodesystemproduct.module_movestore.qinghai_ubsto101.QingHaiUbSto101EditFragment;
import com.richfit.barcodesystemproduct.module_movestore.qinghai_ubsto101.QingHaiUbSto101HeaderFragment;
import com.richfit.barcodesystemproduct.module_movestore.qinghai_ubsto351.QingHaiUbSto351CollectFragment;
import com.richfit.barcodesystemproduct.module_movestore.qinghai_ubsto351.QingHaiUbSto351DetailFragment;
import com.richfit.barcodesystemproduct.module_movestore.qinghai_ubsto351.QingHaiUbSto351EditFragment;
import com.richfit.barcodesystemproduct.module_movestore.qinghai_ubsto351.QingHaiUbSto351HeaderFragment;
import com.richfit.barcodesystemproduct.module_movestore.qingyang_301n.QingYangNMS301CollectFragment;
import com.richfit.barcodesystemproduct.module_movestore.qingyang_301n.QingYangNMS301DetailFragment;
import com.richfit.barcodesystemproduct.module_movestore.qingyang_301n.QingYangNMS301EditFragment;
import com.richfit.barcodesystemproduct.module_movestore.qingyang_301n.QingYangNMS301HeaderFragment;

import dagger.Component;

@FragmentScope
@Component(modules = FragmentModule.class, dependencies = AppComponent.class)
public interface FragmentComponent {

    @ContextLife("Application")
    Context getApplicationContext();

    @ContextLife("Activity")
    Context getActivityContext();

    Activity getActivity();

    void inject(QingYangASNHeaderFragment fragment);
    void inject(QingYangASNEditFragment fragment);
    void inject(QingYangASNDetailFragment fragment);
    void inject(QingYangASNCollectFragment fragment);
    void inject(QingYangAOHeaderFragment fragment);
    void inject(QingYangAODetailFragment fragment);
    void inject(QingYangAOCollectFragment fragment);
    void inject(QingYangAOEditFragment fragment);
    void inject(ShowAndTakePhotoFragment fragment);
    void inject(QingYangDSYHeaderFragment fragment);
    void inject(QingYangDSYDetailFragment fragment);
    void inject(QingYangDSYCollectFragment fragment);
    void inject(QingYangDSYEditFragment fragment);
    void inject(QingYangNMS301HeaderFragment fragment);
    void inject(QingYangNMS301DetailFragment fragment);
    void inject(QingYangNMS301CollectFragment fragment);
    void inject(QingYangNMS301EditFragment fragment);

    void inject(QingHaiDSXSHeaderFragment fragment);
    void inject(QingHaiDSXSDetailFFragment fragment);
    void inject(QingHaiDSXSCollectFragment fragment);
    void inject(QingHaiDSXSEditFragment fragment);

    void inject(QingHaiUbSto351HeaderFragment fragment);
    void inject(QingHaiUbSto351DetailFragment fragment);
    void inject(QingHaiUbSto351CollectFragment fragment);
    void inject(QingHaiUbSto351EditFragment fragment);

    void inject(QingHaiUbSto101HeaderFragment fragment);
    void inject(QingHaiUbSto101DetailFragment fragment);
    void inject(QingHaiUbSto101CollectFragment fragment);
    void inject(QingHaiUbSto101EditFragment fragment);

    void inject(LAHeaderFragment fragment);
    void inject(LACollectFragment fragment);



}