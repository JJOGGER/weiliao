package com.netease.nim.demo.main.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import com.diamond.jogger.base.http.basic.callback.RequestMultiplyCallback;
import com.diamond.jogger.base.http.basic.exception.base.BaseException;
import com.diamond.jogger.base.http.datasource.UserDataSource;
import com.diamond.jogger.base.http.datasource.base.IUserDataSource;
import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.config.preference.Preferences;
import com.netease.nim.uikit.api.wrapper.NimToolBarOptions;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.activity.ToolBarOptions;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;

public class ResetPwdActivity extends UI {

    private EditText mEtOriginPwd;
    private EditText mEtNewPwd;
    private EditText mEtConfirmPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd);
        ToolBarOptions options = new NimToolBarOptions();
        options.titleId = R.string.settings;
        setToolBar(R.id.toolbar, options);
        setTitle("密码修改");
        initView();
    }

    private void initView() {
        mEtOriginPwd = findView(R.id.et_origin_pwd);
        mEtNewPwd = findView(R.id.et_new_pwd);
        mEtConfirmPwd = findView(R.id.et_confirm_pwd);
        findView(R.id.btn_confirm).setOnClickListener(v -> {
            String originPwd = mEtOriginPwd.getText().toString().trim();
            String newPwd = mEtNewPwd.getText().toString().trim();
            String confirmPwd = mEtConfirmPwd.getText().toString().trim();
            if (TextUtils.isEmpty(originPwd)) {
                ToastHelper.showToast(getApplicationContext(), "请输入原密码");
                return;
            }
            if (TextUtils.isEmpty(newPwd)) {
                ToastHelper.showToast(getApplicationContext(), "请输入新密码");
                return;
            }
            if (TextUtils.isEmpty(confirmPwd)) {
                ToastHelper.showToast(getApplicationContext(), "请输入确认密码");
                return;
            }
            if (!confirmPwd.equals(newPwd)) {
                ToastHelper.showToast(getApplicationContext(), "两次密码输入不一致");
                return;
            }
            IUserDataSource userDataSource = new UserDataSource();
            DialogMaker.showProgressDialog(this, "请稍候...");
            userDataSource.resetPwd(DemoCache.getServerAccount(), newPwd, originPwd, new RequestMultiplyCallback<Object>() {
                @Override
                public void onFail(BaseException e) {
                    DialogMaker.dismissProgressDialog();
                    ToastHelper.showToast(getApplicationContext(), e.getMessage());
                }

                @Override
                public void onSuccess(Object o) {
                    DialogMaker.dismissProgressDialog();
                    ToastHelper.showToast(getApplicationContext(), "修改成功");
                    Preferences.saveUserPassword(newPwd);
                    finish();
                }
            });
        });

    }
}
