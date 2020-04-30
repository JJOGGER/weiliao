package com.netease.nim.demo.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.diamond.jogger.base.entity.SimpleUserInfo;
import com.diamond.jogger.base.http.basic.callback.RequestMultiplyCallback;
import com.diamond.jogger.base.http.basic.exception.base.BaseException;
import com.diamond.jogger.base.http.datasource.UserDataSource;
import com.diamond.jogger.base.http.datasource.base.IUserDataSource;
import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.contact.activity.UserProfileSettingActivity;
import com.netease.nim.demo.main.activity.SettingsActivity;
import com.netease.nim.demo.main.activity.SignInActivity;
import com.netease.nim.demo.main.model.MainTab;
import com.netease.nim.demo.redpacket.CoinManageActivity;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.model.SimpleCallback;
import com.netease.nim.uikit.business.contact.selector.activity.ContactSelectActivity;
import com.diamond.jogger.base.event.RedPacketEvent;
import com.netease.nim.uikit.business.team.helper.TeamHelper;
import com.netease.nim.uikit.common.fragment.TFragment;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 我的
 * Created by huangjun on 2015/12/11.
 */
public class MineFragment extends TFragment {

    // 基本信息
    private HeadImageView headImageView;
    private IUserDataSource mUserDataSource;
    private TextView tvAmount;

    public MineFragment() {
        setContainerId(MainTab.MINE.fragmentId);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mine, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        mUserDataSource = new UserDataSource();
        findViews();
        getUserInfo();
    }

    public void initData() {
        getUserInfo();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    private void getUserInfo() {
        mUserDataSource.getUserInfo(DemoCache.getServerAccount(), new RequestMultiplyCallback<SimpleUserInfo>() {
            @Override
            public void onFail(BaseException e) {

            }

            @Override
            public void onSuccess(SimpleUserInfo simpleUserInfo) {
                DemoCache.setSimpleUserInfo(simpleUserInfo);
                if (tvAmount != null)
                    tvAmount.setText(DemoCache.getSimpleUserInfo().getAmount());
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPacketEvent(RedPacketEvent event) {
        if (RedPacketEvent.ACTION_SEND_RED_PACKET == event.getAction() || RedPacketEvent.ACTION_READ_RED_PACKET == event.getAction()) {
            getUserInfo();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        tvAmount.setText(DemoCache.getSimpleUserInfo().getAmount());
    }

    private void findViews() {
        headImageView = findView(R.id.user_head_image);
        findView(R.id.rl_info).setOnClickListener(v -> {
            UserProfileSettingActivity.start(getContext(), DemoCache.getAccount());
        });
        TextView tvName = findView(R.id.tv_name);
        TextView tvAccount = findView(R.id.tv_account);
        tvAmount = findView(R.id.tv_amount);
        findView(R.id.fl_group_send).setOnClickListener(v -> {
            ContactSelectActivity.Option advancedOption = TeamHelper.getCreateContactSelectOption(null, Integer.MAX_VALUE);
            advancedOption.isGroupSendHelper = true;
            NimUIKit.startContactSelector(getContext(), advancedOption, 0);
        });
        findView(R.id.fl_setting).setOnClickListener(v -> {
            startActivity(new Intent(getContext(), SettingsActivity.class));
        });
        findView(R.id.ll_right).setOnClickListener(v -> {
            CoinManageActivity.navTo(getContext());
        });
        findView(R.id.fl_sign_in).setOnClickListener(v -> {
            SignInActivity.navTo(getContext());
        });
        headImageView.loadBuddyAvatar(DemoCache.getAccount());
        tvAccount.setText(DemoCache.getServerAccount());
        final UserInfo userInfo = NimUIKit.getUserInfoProvider().getUserInfo(DemoCache.getAccount());
        if (userInfo == null) {
            NimUIKit.getUserInfoProvider().getUserInfoAsync(DemoCache.getAccount(), new SimpleCallback<NimUserInfo>() {
                @Override
                public void onResult(boolean success, NimUserInfo result, int code) {
                    tvName.setText(result.getName());
                }
            });
            return;
        } else {
            tvName.setText(userInfo.getName());
        }

    }
}
