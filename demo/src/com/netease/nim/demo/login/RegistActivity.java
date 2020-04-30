package com.netease.nim.demo.login;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;

import com.diamond.jogger.base.entity.LoginResult;
import com.diamond.jogger.base.http.basic.callback.RequestMultiplyCallback;
import com.diamond.jogger.base.http.basic.exception.base.BaseException;
import com.diamond.jogger.base.http.datasource.UserDataSource;
import com.diamond.jogger.base.http.datasource.base.IUserDataSource;
import com.diamond.jogger.base.utils.MD5;
import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.config.preference.Preferences;
import com.netease.nim.demo.config.preference.UserPreferences;
import com.netease.nim.demo.event.RegistEvent;
import com.netease.nim.demo.main.activity.MainActivity;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.widget.ClearableEditTextWithIcon;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.sys.NetworkUtil;
import com.netease.nim.uikit.support.permission.MPermission;
import com.netease.nim.uikit.support.permission.annotation.OnMPermissionDenied;
import com.netease.nim.uikit.support.permission.annotation.OnMPermissionGranted;
import com.netease.nim.uikit.support.permission.annotation.OnMPermissionNeverAskAgain;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.LoginInfo;

import org.greenrobot.eventbus.EventBus;

/**
 * 登录/注册界面
 * <p/>
 * Created by huangjun on 2015/2/1.
 */
public class RegistActivity extends UI implements OnKeyListener {

    private static final String TAG = RegistActivity.class.getSimpleName();
    private static final String PHONE = "phone";
    private static final String CODE = "code";
    private final int BASIC_PERMISSION_REQUEST_CODE = 110;


    private ClearableEditTextWithIcon registerAccountEdit;
    private ClearableEditTextWithIcon registerNickNameEdit;
    private ClearableEditTextWithIcon registerPasswordEdit;


    private AbortableFuture<LoginInfo> loginRequest;
    private View btnRegist;
    private View btnLogin;


    public static void start(Context context, String phone, String code) {
        Intent intent = new Intent(context, RegistActivity.class);
        intent.putExtra(PHONE, phone);
        intent.putExtra(CODE, code);
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
        setContentView(R.layout.regist_activity);
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
        MPermission.with(RegistActivity.this)
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


    /**
     * 注册面板
     */
    private void setupRegisterPanel() {
        registerAccountEdit = findView(R.id.edit_register_account);
        registerNickNameEdit = findView(R.id.edit_register_nickname);
        btnRegist = findView(R.id.btn_regist);
        btnLogin = findView(R.id.btn_login);
        registerPasswordEdit = findView(R.id.edit_register_password);

        registerAccountEdit.setIconResource(R.drawable.selector_login_user);
        registerNickNameEdit.setIconResource(R.drawable.selector_login_nickname);
        registerPasswordEdit.setIconResource(R.drawable.selector_login_pwd);

        registerAccountEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        registerNickNameEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        registerPasswordEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});

        registerAccountEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    registerAccountEdit.setBackgroundResource(R.drawable.et_underline_selected);
                } else {
                    registerAccountEdit.setBackgroundResource(R.drawable.et_underline_unselected);
                }
            }
        });
        registerPasswordEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    registerPasswordEdit.setBackgroundResource(R.drawable.et_underline_selected);
                } else {
                    registerPasswordEdit.setBackgroundResource(R.drawable.et_underline_unselected);
                }
            }
        });
        registerNickNameEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    registerNickNameEdit.setBackgroundResource(R.drawable.et_underline_selected);
                } else {
                    registerNickNameEdit.setBackgroundResource(R.drawable.et_underline_unselected);
                }
            }
        });
        btnLogin.setOnClickListener(v -> {
            finish();
        });
        btnRegist.setOnClickListener(v -> {
            register();
        });
    }


    private void saveLoginInfo(final String accid, final String token, String account, String password) {
        Preferences.saveUserAccid(accid);
        Preferences.saveUserAccount(account);
        Preferences.saveUserToken(token);
        Preferences.saveUserPassword(password);
    }


    //DEMO中使用 username 作为 NIM 的account ，md5(password) 作为 token
    //开发者需要根据自己的实际情况配置自身用户系统和 NIM 用户系统的关系
    private String tokenFromPassword(String password) {
        String appKey = readAppKey(this);
        boolean isDemo = "45c6af3c98409b18a84451215d0bdd6e".equals(appKey) ||
                "fe416640c8e8a72734219e1847ad2547".equals(appKey);

        return isDemo ? MD5.getStringMD5(password) : password;
    }

    private static String readAppKey(Context context) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo != null) {
                return appInfo.metaData.getString("com.netease.nim.appKey");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ***************************************** 注册 **************************************
     */
    private void register() {
        if (!checkRegisterContentValid()) {
            return;
        }

        if (!NetworkUtil.isNetAvailable(RegistActivity.this)) {
            ToastHelper.showToast(RegistActivity.this, R.string.network_is_not_available);
            return;
        }

        DialogMaker.showProgressDialog(this, getString(R.string.registering), false);

        // 注册流程
        final String account = registerAccountEdit.getText().toString();
        final String nickName = registerNickNameEdit.getText().toString();
        final String password = registerPasswordEdit.getText().toString();
        String phone="";
        IUserDataSource userDataSource = new UserDataSource();
        userDataSource.register(phone,account, nickName, password, new RequestMultiplyCallback<Object>() {
            @Override
            public void onFail(BaseException e) {
                ToastHelper.showToast(RegistActivity.this, e.getMessage());
                DialogMaker.dismissProgressDialog();
                Log.e(TAG, "---------------e:" + e.getMessage());
            }

            @Override
            public void onSuccess(Object o) {
                Log.e(TAG, "---------------onSuccess");
                ToastHelper.showToast(RegistActivity.this, R.string.register_success);
                DialogMaker.dismissProgressDialog();
                login(account, password);
            }
        });
    }

    private boolean checkRegisterContentValid() {
        // 帐号检查
        String account = registerAccountEdit.getText().toString().trim();
        if (account.length() <= 0 || account.length() > 20) {
            ToastHelper.showToast(this, R.string.register_account_tip);
            return false;
        }

        // 昵称检查
        String nick = registerNickNameEdit.getText().toString().trim();
        if (nick.length() <= 0 || nick.length() > 10) {
            ToastHelper.showToast(this, R.string.register_nick_name_tip);
            return false;
        }

        // 密码检查
        String password = registerPasswordEdit.getText().toString().trim();
        if (password.length() < 6 || password.length() > 20) {
            ToastHelper.showToast(this, R.string.register_password_tip);
            return false;
        }

        return true;
    }

    /**
     * ***************************************** 登录 **************************************
     */
    private void onLoginDone() {
        loginRequest = null;
        DialogMaker.dismissProgressDialog();
    }

    private void login(String account, String password) {
        DialogMaker.showProgressDialog(this, null, getString(R.string.logining), true, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (loginRequest != null) {
                    loginRequest.abort();
                    onLoginDone();
                }
            }
        }).setCanceledOnTouchOutside(false);

        // 云信只提供消息通道，并不包含用户资料逻辑。开发者需要在管理后台或通过服务器接口将用户帐号和token同步到云信服务器。
        // 在这里直接使用同步到云信服务器的帐号和token登录。
        // 这里为了简便起见，demo就直接使用了密码的md5作为token。
        // 如果开发者直接使用这个demo，只更改appkey，然后就登入自己的账户体系的话，需要传入同步到云信服务器的token，而不是用户密码。
        IUserDataSource userDataSource = new UserDataSource();
        userDataSource.login(account, password, new RequestMultiplyCallback<LoginResult>() {
            @Override
            public void onFail(BaseException e) {
                LogUtil.i(TAG, "-------login onFail(");
                onLoginDone();
//                if (code == 302 || code == 404) {
//                    ToastHelper.showToast(LoginActivity.this, R.string.login_failed);
//                } else {
                ToastHelper.showToast(RegistActivity.this, "登录失败: " + e.getMessage());
//                }
            }

            @Override
            public void onSuccess(LoginResult loginResult) {
                LogUtil.i(TAG, "-------login success");
                // 登录
                loginRequest = NimUIKit.login(new LoginInfo(loginResult.getAccid(), loginResult.getToken()), new RequestCallback<LoginInfo>() {
                    @Override
                    public void onSuccess(LoginInfo param) {
                        LogUtil.i(TAG, "login success");

                        onLoginDone();
                        DemoCache.setAccount(loginResult.getAccid());
                        DemoCache.setServerAccount(account);
                        DemoCache.setTabWebSite(loginResult.getWebsite());
                        saveLoginInfo(loginResult.getAccid(), loginResult.getToken(), account, password);
                        // 初始化消息提醒配置
                        initNotificationConfig();
                        // 进入主界面
                        MainActivity.start(RegistActivity.this, null);
                        EventBus.getDefault().post(new RegistEvent(-1));
                        finish();
                    }

                    @Override
                    public void onFailed(int code) {
                        onLoginDone();
                        if (code == 302 || code == 404) {
                            ToastHelper.showToast(RegistActivity.this, R.string.login_failed);
                        } else {
                            ToastHelper.showToast(RegistActivity.this, "登录失败: " + code);
                        }
                    }

                    @Override
                    public void onException(Throwable exception) {
                        ToastHelper.showToast(RegistActivity.this, R.string.login_exception);
                        onLoginDone();
                    }
                });

            }
        });
    }

    private void initNotificationConfig() {
        // 初始化消息提醒
        NIMClient.toggleNotification(UserPreferences.getNotificationToggle());
        // 加载状态栏配置
        StatusBarNotificationConfig statusBarNotificationConfig = UserPreferences.getStatusConfig();
        if (statusBarNotificationConfig == null) {
            statusBarNotificationConfig = DemoCache.getNotificationConfig();
            UserPreferences.setStatusConfig(statusBarNotificationConfig);
        }
        // 更新配置
        NIMClient.updateStatusBarNotificationConfig(statusBarNotificationConfig);
    }
}
