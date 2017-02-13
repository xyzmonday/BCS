package com.richfit.barcodesystemproduct.module.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.jakewharton.rxbinding.view.RxView;
import com.richfit.barcodesystemproduct.R;
import com.richfit.barcodesystemproduct.base.BaseActivity;
import com.richfit.barcodesystemproduct.module.welcome.WelcomeActivity;
import com.richfit.common_lib.rxutils.RxCilck;
import com.richfit.common_lib.utils.Global;
import com.richfit.common_lib.widget.RichAutoEditText;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by monday on 2016/10/27.
 */

public class LoginActivity extends BaseActivity<LoginPresenterImp> implements LoginContract.View {

    @BindView(R.id.et_username)
    RichAutoEditText etUsername;
    @BindView(R.id.et_password)
    RichAutoEditText etPassword;
    @BindView(R.id.btn_login)
    Button btnLogin;

    @Override
    protected int getContentId() {
        return R.layout.activity_login;
    }

    @Override
    public void initInjector() {
        mActivityComponent.inject(this);
    }

    @Override
    public void initEvent() {
        etUsername.setOnRichAutoEditTouchListener((view, text) -> etUsername.setText(""));
        etPassword.setOnRichAutoEditTouchListener((view, text) -> etPassword.setText(""));

        RxView.clicks(btnLogin)
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(a -> mPresenter.login(etUsername.getText().toString(),
                        etPassword.getText().toString()));

        RxCilck.clicks(etUsername)
                .subscribe(a -> {
                    if (etUsername.getAdapter() != null) {
                        etUsername.setThreshold(0);
                        etUsername.showDropDown();
                    }
                });


    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mPresenter.readUserInfos();
    }

    @Override
    public void toHome() {
        showMessage("登陆成功");
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void loginFail(String message) {
        showMessage(message);
    }

    @Override
    public void showUserInfos(ArrayList<String> list) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, list);
        etUsername.setAdapter(adapter);
    }

    @Override
    public void loadUserInfosFail(String message) {
        showMessage(message);
    }

    @Override
    public void networkConnectError(String retryAction) {
        showNetConnectErrorDialog(Global.RETRY_LOGIN_ACTION);
    }

    @Override
    public void retry(String action) {
        switch (action) {
            case Global.RETRY_LOGIN_ACTION:
                mPresenter.login(etUsername.getText().toString(),
                        etPassword.getText().toString());
                break;
        }
        super.retry(action);
    }

}
