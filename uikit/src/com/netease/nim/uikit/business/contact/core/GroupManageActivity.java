package com.netease.nim.uikit.business.contact.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.diamond.jogger.base.entity.GroupData;
import com.diamond.jogger.base.event.GroupChangeEvent;
import com.diamond.jogger.base.http.basic.callback.RequestMultiplyCallback;
import com.diamond.jogger.base.http.basic.exception.base.BaseException;
import com.diamond.jogger.base.http.datasource.UserDataSource;
import com.diamond.jogger.base.http.datasource.base.IUserDataSource;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.wrapper.NimToolBarOptions;
import com.netease.nim.uikit.business.contact.core.model.GroupManageAdapter;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.activity.ToolBarOptions;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.dialog.CustomAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.dialog.EasyEditDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class GroupManageActivity extends UI {

    private static final String EXTRA_KEY = "EXTRA_KEY";
    private static final String EXTRA_DATA = "EXTRA_DATA";
    public static final int REQUEST_CODE = 1000;
    private IUserDataSource mUserDataSource;
    private GroupManageAdapter mAdapter;
    private RecyclerView rvContent;
    private String data;

    public static final void startActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, GroupManageActivity.class);
        ((Activity) context).startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_manage);
        ToolBarOptions options = new NimToolBarOptions();
        setToolBar(R.id.toolbar, options);
        setTitle("分组管理");
        mUserDataSource = new UserDataSource();
        data = getIntent().getStringExtra(EXTRA_DATA);
        rvContent = findView(R.id.rv_content);
        rvContent.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new GroupManageAdapter(rvContent, null);
        View header = LayoutInflater.from(this).inflate(R.layout.rv_group_manage_header, rvContent, false);
        header.findViewById(R.id.fl_header).setOnClickListener(v -> {
            //编辑分组
            EasyEditDialog easyEditDialog = new EasyEditDialog(GroupManageActivity.this);
            easyEditDialog.setEditHint("请输入分组名");
            easyEditDialog.setTitle("添加分组");
            easyEditDialog.setMessage("请输入新的分组名称");
            easyEditDialog.addNegativeButtonListener(v12 -> easyEditDialog.dismiss());
            easyEditDialog.addPositiveButtonListener(v1 -> {
                easyEditDialog.dismiss();
                DialogMaker.showProgressDialog(GroupManageActivity.this, null, true);
                mUserDataSource.addGroup(NimUIKit.getServerAccount(), easyEditDialog.getEditMessage(), new RequestMultiplyCallback<Object>() {
                    @Override
                    public void onFail(BaseException e) {
                        if (isFinishingOrDestroyed()) return;
                        DialogMaker.dismissProgressDialog();
                        ToastHelper.showToast(GroupManageActivity.this, e.getMessage());
                    }

                    @Override
                    public void onSuccess(Object o) {
                        initData(true, GroupChangeEvent.ADD);
                        if (isFinishingOrDestroyed()) return;
                        DialogMaker.dismissProgressDialog();
                    }
                });
            });
            easyEditDialog.show();
        });
        mAdapter.addHeaderView(header);
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            GroupData groupData = (GroupData) adapter.getItem(position);
            if (groupData == null) return;
            if (TextUtils.isEmpty(groupData.getId()) || "Y".equals(groupData.isDefaultFlag())) {
                ToastHelper.showToast(GroupManageActivity.this, "不能删除默认分组");
                return;
            }
            CustomAlertDialog alertDialog = new CustomAlertDialog(GroupManageActivity.this);
            alertDialog.setTitle("删除该分组后，组内联系人将移至默认分组");
            alertDialog.addItem("删除分组", new CustomAlertDialog.onSeparateItemClickListener() {
                @Override
                public void onClick() {
                    mUserDataSource.deleteGroup(groupData.getId(), new RequestMultiplyCallback<Object>() {
                        @Override
                        public void onFail(BaseException e) {
                            if (isFinishingOrDestroyed()) return;
                            DialogMaker.dismissProgressDialog();
                            ToastHelper.showToast(GroupManageActivity.this, e.getMessage());
                        }

                        @Override
                        public void onSuccess(Object o) {
                            initData(true, GroupChangeEvent.DELETE);
                            if (isFinishingOrDestroyed()) return;
                            mAdapter.remove(position);
                        }
                    });
                }
            });
            alertDialog.show();
        });
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            GroupData groupData = (GroupData) adapter.getItem(position);
            if (groupData == null) return;
            //编辑分组
            EasyEditDialog easyEditDialog = new EasyEditDialog(GroupManageActivity.this);
            easyEditDialog.setEditHint("请输入分组名");
            easyEditDialog.setTitle("编辑分组");
            easyEditDialog.setMessage("请输入新的分组名称");
            easyEditDialog.addNegativeButtonListener(v -> easyEditDialog.dismiss());
            easyEditDialog.addPositiveButtonListener(v -> {
                easyEditDialog.dismiss();
                DialogMaker.showProgressDialog(GroupManageActivity.this, null, true);
                mUserDataSource.updateGroup(easyEditDialog.getEditMessage(), groupData.getId(), new RequestMultiplyCallback<Object>() {
                    @Override
                    public void onFail(BaseException e) {
                        if (isFinishingOrDestroyed()) return;
                        DialogMaker.dismissProgressDialog();
                        ToastHelper.showToast(GroupManageActivity.this, e.getMessage());
                    }

                    @Override
                    public void onSuccess(Object o) {
                        List<GroupData> groupDatasCache = UserDataSource.getGroupDatasCache();
                        groupData.setGroupName(easyEditDialog.getEditMessage());
                        for (int i = 0; i < groupDatasCache.size(); i++) {
                            if (groupDatasCache.get(i).getId().equals(groupData.getId())) {
                                groupDatasCache.set(i, groupData);
                                EventBus.getDefault().post(new GroupChangeEvent(GroupChangeEvent.UPDATE));
                            }
                        }
                        if (isFinishingOrDestroyed()) return;
                        DialogMaker.dismissProgressDialog();
                        adapter.notifyItemChanged(position+adapter.getHeaderLayoutCount());
                    }
                });
            });
            easyEditDialog.show();


        });
        rvContent.setAdapter(mAdapter);
        OverScrollDecoratorHelper.setUpOverScroll(rvContent, RecyclerView.VERTICAL);
        initData(false, -1);
    }

    private void initData(boolean update, int event) {
        mUserDataSource.getGroupList(NimUIKit.getServerAccount(), new RequestMultiplyCallback<List<GroupData>>() {
            @Override
            public void onFail(BaseException e) {

            }

            @Override
            public void onSuccess(List<GroupData> groupData) {
                if (update) {
                    EventBus.getDefault().post(new GroupChangeEvent(event));
                }
                mAdapter.setNewData(groupData);
            }
        });
    }
}
