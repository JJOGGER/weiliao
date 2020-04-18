package com.netease.nim.demo.redpacket;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.diamond.jogger.base.entity.SimpleUserInfo;
import com.diamond.jogger.base.http.basic.callback.RequestMultiplyCallback;
import com.diamond.jogger.base.http.basic.exception.base.BaseException;
import com.diamond.jogger.base.http.datasource.UserDataSource;
import com.diamond.jogger.base.http.datasource.base.IUserDataSource;
import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.dialog.CommonHintDialog;
import com.netease.nim.uikit.api.wrapper.NimToolBarOptions;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.activity.ToolBarOptions;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;

public class BindAccountActivity extends UI implements View.OnClickListener, TextWatcher {

    private Button btnBind;
    private EditText etAccount;
    private IUserDataSource mUserDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_account);
        ToolBarOptions options = new NimToolBarOptions();
        options.titleId = R.string.settings;
        setToolBar(R.id.toolbar, options);
        setTitle("绑定账号");
        mUserDataSource = new UserDataSource();
        initView();
    }

    private void initView() {
        btnBind = findView(R.id.btn_bind);
        etAccount = findView(R.id.et_account);
        if (DemoCache.getSimpleUserInfo().isBindFlag()) {
            btnBind.setVisibility(View.GONE);
            etAccount.setEnabled(false);
            etAccount.setText(DemoCache.getSimpleUserInfo().getPlatformAccount());
        } else {
            btnBind.setVisibility(View.VISIBLE);
            etAccount.addTextChangedListener(this);
        }
        btnBind.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bind:
                bindAccount();
                break;
        }
    }

    private void bindAccount() {
        CommonHintDialog commonHintDialog = new CommonHintDialog();
        commonHintDialog.setHintContent("账号绑定后将不予修改！");
        commonHintDialog.setOnHintDialogListener(() -> {
            DialogMaker.showProgressDialog(BindAccountActivity.this, "请稍候");
            String content
                    = etAccount.getText().toString().trim();
            mUserDataSource.bindPlatformAccount(DemoCache.getServerAccount(), content, new RequestMultiplyCallback<Object>() {
                @Override
                public void onFail(BaseException e) {
                    DialogMaker.dismissProgressDialog();
                    ToastHelper.showToast(getApplicationContext(), e.getMessage());
                }

                @Override
                public void onSuccess(Object o) {
                    if (isFinishingOrDestroyed()) return;
                    DialogMaker.dismissProgressDialog();
                    ToastHelper.showToast(getApplicationContext(), "绑定成功");
                    SimpleUserInfo simpleUserInfo = DemoCache.getSimpleUserInfo();
                    simpleUserInfo.setBindFlag(true);
                    simpleUserInfo.setPlatformAccount(content);
                    DemoCache.setSimpleUserInfo(simpleUserInfo);
                    etAccount.setEnabled(false);
                    btnBind.setVisibility(View.GONE);
                }
            });
        });
        commonHintDialog.show(getSupportFragmentManager(), CommonHintDialog.class.getSimpleName());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        btnBind.setEnabled(!TextUtils.isEmpty(s));
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
