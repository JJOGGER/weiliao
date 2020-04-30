package com.netease.nim.demo.login;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;

import com.diamond.jogger.base.http.basic.callback.RequestMultiplyCallback;
import com.diamond.jogger.base.http.basic.exception.base.BaseException;
import com.diamond.jogger.base.http.datasource.UserDataSource;
import com.diamond.jogger.base.http.datasource.base.IUserDataSource;
import com.netease.nim.demo.R;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.widget.ClearableEditTextWithIcon;
import com.netease.nim.uikit.support.permission.MPermission;
import com.netease.nim.uikit.support.permission.annotation.OnMPermissionDenied;
import com.netease.nim.uikit.support.permission.annotation.OnMPermissionGranted;
import com.netease.nim.uikit.support.permission.annotation.OnMPermissionNeverAskAgain;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.auth.LoginInfo;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 登录/注册界面
 * <p/>
 * Created by huangjun on 2015/2/1.
 */
public class SmsRegistActivity extends UI implements OnKeyListener {

    private static final String TAG = SmsRegistActivity.class.getSimpleName();
    private static final String KICK_OUT = "KICK_OUT";
    private final int BASIC_PERMISSION_REQUEST_CODE = 110;
    private Disposable mRegistDisposable;


    private ClearableEditTextWithIcon registerPhoneEdit;
    private ClearableEditTextWithIcon registerSmsEdit;


    private AbortableFuture<LoginInfo> loginRequest;
    private View btnNext;
    private View btnLogin;
    private Button getSms;
    private IUserDataSource mUserDataSource;

    public static void start(Context context) {
        start(context, false);
    }

    public static void start(Context context, boolean kickOut) {
        Intent intent = new Intent(context, SmsRegistActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(KICK_OUT, kickOut);
        context.startActivity(intent);
    }

    @Override
    protected boolean displayHomeAsUpEnabled() {
        return false;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_regist_activity);
        mUserDataSource = new UserDataSource();
        requestBasicPermission();

        setupRegisterPanel();
    }

    /**
     * 基本权限管理
     */
    private final String[] BASIC_PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private void requestBasicPermission() {
        MPermission.with(SmsRegistActivity.this)
                .setRequestCode(BASIC_PERMISSION_REQUEST_CODE)
                .permissions(BASIC_PERMISSIONS)
                .request();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @OnMPermissionGranted(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionSuccess() {
//        ToastHelper.showToast(this, "授权成功");
    }

    @OnMPermissionDenied(BASIC_PERMISSION_REQUEST_CODE)
    @OnMPermissionNeverAskAgain(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionFailed() {
        ToastHelper.showToast(this, "授权失败");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRegistDisposable != null && mRegistDisposable.isDisposed()) {
            mRegistDisposable.dispose();
        }
    }

    /**
     * 注册面板
     */
    private void setupRegisterPanel() {
        registerPhoneEdit = findView(R.id.edit_register_phone);
        registerSmsEdit = findView(R.id.edit_register_sms);
        getSms = findView(R.id.get_sms);
        btnNext = findView(R.id.btn_next);
        btnLogin = findView(R.id.btn_login);
        registerPhoneEdit.setIconResource(R.drawable.selector_login_user);
        registerSmsEdit.setIconResource(R.drawable.selector_login_nickname);

        registerPhoneEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
        registerSmsEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});

        registerPhoneEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    registerPhoneEdit.setBackgroundResource(R.drawable.et_underline_selected);
                } else {
                    registerPhoneEdit.setBackgroundResource(R.drawable.et_underline_unselected);
                }
            }
        });
        registerSmsEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    registerSmsEdit.setBackgroundResource(R.drawable.et_underline_selected);
                } else {
                    registerSmsEdit.setBackgroundResource(R.drawable.et_underline_unselected);
                }
            }
        });
        btnLogin.setOnClickListener(v -> {
            finish();
        });
        btnNext.setOnClickListener(v -> {
            next();
        });
        getSms.setOnClickListener(v -> {
            String phone = registerPhoneEdit.getText().toString().trim();
            if (TextUtils.isEmpty(phone)) {
                ToastHelper.showToast(this, "请输入手机号");
                return;
            }
            DialogMaker.showProgressDialog(this, "请求中");
            mUserDataSource.getSmsCode(phone, new RequestMultiplyCallback<Object>() {
                @Override
                public void onFail(BaseException e) {
                    DialogMaker.dismissProgressDialog();
                    ToastHelper.showToast(getBaseContext(), e.getMessage());
                }

                @Override
                public void onSuccess(Object o) {
                    DialogMaker.dismissProgressDialog();
                    handleGetSmsCode();
                }
            });
        });
    }

    private void next() {
        String phone = registerPhoneEdit.getText().toString().trim();
        String code = registerSmsEdit.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastHelper.showToast(this, "请输入手机号");
            return;
        }
        if (TextUtils.isEmpty(code)) {
            ToastHelper.showToast(this, "请输入验证码");
            return;
        }
        DialogMaker.showProgressDialog(this, "请稍侯");
        mUserDataSource.checkSmsCode(code, phone, new RequestMultiplyCallback<Object>() {
            @Override
            public void onFail(BaseException e) {
                DialogMaker.dismissProgressDialog();
                ToastHelper.showToast(getBaseContext(), e.getMessage());
            }

            @Override
            public void onSuccess(Object o) {
                DialogMaker.dismissProgressDialog();
                RegistActivity.start(SmsRegistActivity.this, phone, code);
            }
        });

    }


    private void handleGetSmsCode() {
        Observable.intervalRange(1, 120, 0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mRegistDisposable = d;
                        getSms.setEnabled(false);
                    }

                    @Override
                    public void onNext(Long aLong) {
                        getSms.setText("已发送(" + (60L - aLong) + ")");
                    }

                    @Override
                    public void onError(Throwable e) {
                        getSms.setText("获取验证码");
                        getSms.setEnabled(true);
                    }

                    @Override
                    public void onComplete() {
                        getSms.setText("获取验证码");
                        getSms.setEnabled(true);
                    }
                });
    }
}
