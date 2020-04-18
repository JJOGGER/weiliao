package com.netease.nim.demo.redpacket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.uikit.api.wrapper.NimToolBarOptions;
import com.diamond.jogger.base.event.UserInfoEvent;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.activity.ToolBarOptions;
import com.netease.nim.uikit.common.activity.UI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class CoinManageActivity extends UI implements View.OnClickListener {

    private TextView tvAmount;

    public static void navTo(Context context) {
        Intent intent = new Intent(context, CoinManageActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_manage);
        EventBus.getDefault().register(this);
        ToolBarOptions options = new NimToolBarOptions();
        options.titleId = R.string.settings;
        setToolBar(R.id.toolbar, options);
        setTitle("金币管理");
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserInfoEvent(UserInfoEvent event) {
        if (event.getAction() == UserInfoEvent.ACTION_USER_INFO_UPDATE) {
            tvAmount.setText( DemoCache.getSimpleUserInfo().getAmount());
        }
    }

    private void initView() {
        tvAmount = findView(R.id.tv_amount);
        tvAmount.setText(DemoCache.getSimpleUserInfo().getAmount());
        findView(R.id.fl_bind_account).setOnClickListener(this);
        findView(R.id.fl_single_into).setOnClickListener(this);
        findView(R.id.fl_all_into).setOnClickListener(this);
        findView(R.id.fl_into_detail).setOnClickListener(this);
        findView(R.id.fl_out_detail).setOnClickListener(this);
        findView(R.id.fl_coin_detail).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_bind_account:
                startActivity(new Intent(this, BindAccountActivity.class));
                break;
            case R.id.fl_single_into:
                if (!DemoCache.getSimpleUserInfo().isBindFlag()) {
                    ToastHelper.showToast(this, "请先绑定平台账号");
                    return;
                }
                CoinTurnApplyActivity.navTo(this, CoinTurnApplyActivity.TYPE_IN);
                break;
            case R.id.fl_all_into:
                if (!DemoCache.getSimpleUserInfo().isBindFlag()) {
                    ToastHelper.showToast(this, "请先绑定平台账号");
                    return;
                }
                CoinTurnApplyActivity.navTo(this, CoinTurnApplyActivity.TYPE_OUT);
                break;
            case R.id.fl_into_detail:
                CoinIntoRecordActivity.navTo(this, CoinIntoRecordActivity.TYPE_IN);
                break;
            case R.id.fl_out_detail:
                CoinIntoRecordActivity.navTo(this, CoinIntoRecordActivity.TYPE_OUT);
                break;
            case R.id.fl_coin_detail:
                CoinDetailActivity.navTo(this);
                break;
        }
    }
}
