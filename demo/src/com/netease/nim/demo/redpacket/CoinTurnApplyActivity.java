package com.netease.nim.demo.redpacket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.diamond.jogger.base.entity.SimpleUserInfo;
import com.diamond.jogger.base.http.basic.callback.RequestMultiplyCallback;
import com.diamond.jogger.base.http.basic.exception.base.BaseException;
import com.diamond.jogger.base.http.datasource.CoinDataSource;
import com.diamond.jogger.base.http.datasource.UserDataSource;
import com.diamond.jogger.base.http.datasource.base.ICoinDataSource;
import com.diamond.jogger.base.http.datasource.base.IUserDataSource;
import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.main.model.Extras;
import com.netease.nim.uikit.api.wrapper.NimToolBarOptions;
import com.diamond.jogger.base.event.UserInfoEvent;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.activity.ToolBarOptions;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;

import org.greenrobot.eventbus.EventBus;

public class CoinTurnApplyActivity extends UI {
    public static final int TYPE_IN = 0;
    public static final int TYPE_OUT = 1;
    private TextView tvAmount;
    private LinearLayout llAmount;
    private EditText etAmount;
    private Button btnTurnCoin;
    private ICoinDataSource mCoinDataSource;
    private IUserDataSource mUserDataSource;
    private int mType;

    public static void navTo(Context context, int type) {
        Intent intent = new Intent(context, CoinTurnApplyActivity.class);
        intent.putExtra(Extras.TYPE, type);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_turn_apply);
        ToolBarOptions options = new NimToolBarOptions();
        options.titleId = R.string.settings;
        setToolBar(R.id.toolbar, options);
        mType = getIntent().getIntExtra(Extras.TYPE, TYPE_IN);
        mCoinDataSource = new CoinDataSource();
        mUserDataSource = new UserDataSource();
        initView();
        if (mType == TYPE_OUT) {
            setTitle("转出金币");
            llAmount.setVisibility(View.GONE);
            btnTurnCoin.setText("立即转出");
        } else {
            setTitle("转入金币");
            btnTurnCoin.setText("立即转入");
        }
    }

    private void initView() {
        llAmount = findView(R.id.ll_amount);
        tvAmount = findView(R.id.tv_amount);
        etAmount = findView(R.id.et_amount);
        btnTurnCoin = findView(R.id.btn_turn_coin);
        tvAmount.setText( DemoCache.getSimpleUserInfo().getAmount());
        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnTurnCoin.setEnabled(!TextUtils.isEmpty(s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnTurnCoin.setOnClickListener(v -> {
            DialogMaker.showProgressDialog(this, "请稍候");
            if (mType == TYPE_OUT) {
                mCoinDataSource.applyTurnOut(DemoCache.getServerAccount(), etAmount.getText().toString().trim(), new RequestMultiplyCallback<Object>() {
                    @Override
                    public void onFail(BaseException e) {
                        ToastHelper.showToast(getApplicationContext(), e.getMessage());
                        DialogMaker.dismissProgressDialog();
                    }

                    @Override
                    public void onSuccess(Object o) {
                        DialogMaker.dismissProgressDialog();
                        ToastHelper.showToast(getApplicationContext(), "转出成功");
                        getUserInfo();
                        finish();
                    }
                });
            } else {
                mCoinDataSource.applyTurnIn(DemoCache.getServerAccount(), etAmount.getText().toString().trim(), new RequestMultiplyCallback<Object>() {
                    @Override
                    public void onFail(BaseException e) {
                        ToastHelper.showToast(getApplicationContext(), e.getMessage());
                        DialogMaker.dismissProgressDialog();
                    }

                    @Override
                    public void onSuccess(Object o) {
                        DialogMaker.dismissProgressDialog();
                        ToastHelper.showToast(getApplicationContext(), "申请成功，请等待审核");
                        getUserInfo();
                        finish();
                    }
                });
            }

        });
    }

    private void getUserInfo() {
        mUserDataSource.getUserInfo(DemoCache.getServerAccount(), new RequestMultiplyCallback<SimpleUserInfo>() {
            @Override
            public void onFail(BaseException e) {

            }

            @Override
            public void onSuccess(SimpleUserInfo simpleUserInfo) {
                DemoCache.setSimpleUserInfo(simpleUserInfo);
                EventBus.getDefault().post(new UserInfoEvent(UserInfoEvent.ACTION_USER_INFO_UPDATE));
            }
        });
    }
}
