package com.netease.nim.demo.contact.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.diamond.jogger.base.entity.GroupData;
import com.diamond.jogger.base.event.GroupChangeEvent;
import com.diamond.jogger.base.http.basic.callback.RequestMultiplyCallback;
import com.diamond.jogger.base.http.basic.exception.base.BaseException;
import com.diamond.jogger.base.http.datasource.UserDataSource;
import com.diamond.jogger.base.http.datasource.base.IUserDataSource;
import com.diamond.jogger.base.utils.GsonUtil;
import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.adapter.SetGroupAdapter;
import com.netease.nim.demo.contact.constant.UserConstant;
import com.netease.nim.demo.main.model.Extras;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.model.SimpleCallback;
import com.netease.nim.uikit.api.wrapper.NimToolBarOptions;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.activity.ToolBarOptions;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.constant.FriendFieldEnum;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.netease.nimlib.service.NimService;

import org.greenrobot.eventbus.EventBus;

import java.security.acl.Group;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SetGroupActivity extends UI {

    private static final String EXTRA_KEY = "EXTRA_KEY";
    private static final String EXTRA_DATA = "EXTRA_DATA";
    public static final int REQUEST_CODE = 1000;
    private IUserDataSource mUserDataSource;
    private SetGroupAdapter mAdapter;
    private RecyclerView rvContent;
    private String data;

    public static final void startActivity(Context context, int key, String data) {
        Intent intent = new Intent();
        intent.setClass(context, SetGroupActivity.class);
        intent.putExtra(EXTRA_KEY, key);
        intent.putExtra(EXTRA_DATA, data);
        ((Activity) context).startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_group);
        ToolBarOptions options = new NimToolBarOptions();
        options.titleId = R.string.settings;
        setToolBar(R.id.toolbar, options);
        setTitle("移至分组");
        getToolBar().findViewById(R.id.tv_completed).setOnClickListener(v -> finish());
        data = getIntent().getStringExtra(EXTRA_DATA);
        rvContent = findView(R.id.rv_content);
        rvContent.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SetGroupAdapter(rvContent, null);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            GroupData groupData = (GroupData) adapter.getItem(position);
            if (groupData == null) return;
            for (int i = 0; i < adapter.getItemCount(); i++) {
                ((GroupData) adapter.getItem(i)).setSelected(false);
            }
            groupData.setSelected(true);
            adapter.notifyDataSetChanged();
            RequestCallbackWrapper callback = new RequestCallbackWrapper() {
                @Override
                public void onResult(int code, Object result, Throwable exception) {
                    DialogMaker.dismissProgressDialog();
                    if (code == ResponseCode.RES_SUCCESS) {
                        EventBus.getDefault().post(new GroupChangeEvent(GroupChangeEvent.UPDATE));
                        ToastHelper.showToast(SetGroupActivity.this, "设置成功");
                    } else if (code == ResponseCode.RES_ETIMEOUT) {
                        ToastHelper.showToast(SetGroupActivity.this, R.string.user_info_update_failed);
                    }
                }
            };

            DialogMaker.showProgressDialog(SetGroupActivity.this, null, true);
            Map<FriendFieldEnum, Object> map = new HashMap<>();
            Map<String, Object> exts = new HashMap<>();
            exts.put("ext", GsonUtil.toJson(groupData));
            map.put(FriendFieldEnum.EXTENSION, exts);
            NIMClient.getService(FriendService.class).updateFriendFields(data, map).setCallback(callback);
        });
        rvContent.setAdapter(mAdapter);
        mUserDataSource = new UserDataSource();
        mUserDataSource.getGroupList(DemoCache.getServerAccount(), new RequestMultiplyCallback<List<GroupData>>() {
            @Override
            public void onFail(BaseException e) {

            }

            @Override
            public void onSuccess(List<GroupData> groupDatas) {
                if (isFinishingOrDestroyed())return;
                if (NimUIKit.getUserInfoProvider().getUserInfo(data) != null) {
                    updateUserInfoView(groupDatas);
                    return;
                }
                NimUIKit.getUserInfoProvider().getUserInfoAsync(data, new SimpleCallback<NimUserInfo>() {
                    @Override
                    public void onResult(boolean success, NimUserInfo result, int code) {
                        if (isFinishingOrDestroyed())return;
                        updateUserInfoView(groupDatas);
                    }
                });

            }
        });
    }

    private void updateUserInfoView(List<GroupData> groupDatas) {
        //获取当前好友所在分组
        final NimUserInfo userInfo = (NimUserInfo) NimUIKit.getUserInfoProvider().getUserInfo(data);
        GroupData groupData = NimUIKit.getContactProvider().getGroupData(data);
        if (groupData == null) {
            //默认分组
            for (int i = 0; i < groupDatas.size(); i++) {
                if (TextUtils.isEmpty(groupDatas.get(i).getId()) || "Y".equals(groupDatas.get(i).isDefaultFlag())) {
                    groupDatas.get(i).setSelected(true);
                    break;
                }
            }
        } else {
            for (int i = 0; i < groupDatas.size(); i++) {
                if (groupData.getId().equals(groupDatas.get(i).getId())) {
                    groupDatas.get(i).setSelected(true);
                    break;
                }
            }
        }
        mAdapter.setNewData(groupDatas);
    }
}
