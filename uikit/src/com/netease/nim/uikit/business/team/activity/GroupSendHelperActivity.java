package com.netease.nim.uikit.business.team.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.diamond.jogger.base.http.datasource.UserDataSource;
import com.diamond.jogger.base.http.datasource.base.IUserDataSource;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.model.main.CustomPushContentProvider;
import com.netease.nim.uikit.api.wrapper.NimToolBarOptions;
import com.netease.nim.uikit.business.contact.selector.activity.ContactSelectActivity;
import com.netease.nim.uikit.business.session.actions.BaseAction;
import com.netease.nim.uikit.business.session.actions.ImageAction;
import com.netease.nim.uikit.business.session.module.Container;
import com.netease.nim.uikit.business.session.module.ModuleProxy;
import com.netease.nim.uikit.business.session.module.input.InputPanel;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.activity.ToolBarOptions;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.impl.NimUIKitImpl;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomMessageConfig;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//群发助手
public class GroupSendHelperActivity extends UI implements ModuleProxy {

    private ArrayList<String> mSelected;
    private TextView mTvNum;
    private TextView mTvUsers;
    // modules
    protected InputPanel inputPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nim_group_send_helper);
        ToolBarOptions options = new NimToolBarOptions();
        setToolBar(R.id.toolbar, options);
        findViews();
        setTitle("群发");
        parseIntentData();
        loadData();
    }

    private void findViews() {
        mTvNum = findView(R.id.tv_num);
        mTvUsers = findView(R.id.tv_users);
    }

    private void appendPushConfig(IMMessage message) {
        CustomPushContentProvider customConfig = NimUIKitImpl.getCustomPushContentProvider();
        if (customConfig == null) {
            return;
        }
        String content = customConfig.getPushContent(message);
        Map<String, Object> payload = customConfig.getPushPayload(message);
        if (!TextUtils.isEmpty(content)) {
            message.setPushContent(content);
        }
        if (payload != null) {
            message.setPushPayload(payload);
        }

    }

    private void loadData() {
        mTvNum.setText("您将发送消息给" + mSelected.size() + "好友");
        IUserDataSource userDataSource = new UserDataSource();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mSelected.size(); i++) {
            UserInfo userInfo = NimUIKit.getUserInfoProvider().getUserInfo(mSelected.get(i));
            sb.append(userInfo.getName());
            if (i != mSelected.size() - 1) {
                sb.append("、");
            }
        }
        mTvUsers.setText(sb.toString());
    }

    private void parseIntentData() {
        mSelected = getIntent().getStringArrayListExtra(ContactSelectActivity.RESULT_DATA);
        Container container = new Container(this, "", SessionTypeEnum.P2P, this);
        if (inputPanel == null) {
            inputPanel = new InputPanel(container, findView(R.id.ll_nim_group_send_helper), getActionList(), true, true);
            inputPanel.setCustomization(null);
        } else {
            inputPanel.reload(container, null);
        }
        inputPanel.switchRobotMode(false);
    }


    // 操作面板集合
    protected List<BaseAction> getActionList() {
        List<BaseAction> actions = new ArrayList<>();
        actions.add(new ImageAction());
        return actions;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        inputPanel.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean sendMessage(IMMessage msg) {
        if (msg.getMsgType() != MsgTypeEnum.text)return false;
        for (int i = 0; i < mSelected.size(); i++) {
            IMMessage textMessage = MessageBuilder.createTextMessage(mSelected.get(i), SessionTypeEnum.P2P, msg.getContent());
            appendPushConfig(textMessage);
            // send message to server and save to db
            NIMClient.getService(MsgService.class).sendMessage(textMessage, false).setCallback(new RequestCallback<Void>() {
                @Override
                public void onSuccess(Void param) {
                    Log.e(GroupSendHelperActivity.class.getSimpleName(), "--------onSuccess");
                    ToastHelper.showToast(getApplicationContext(), "发送成功");
                    finish();
                }

                @Override
                public void onFailed(int code) {
                    sendFailWithBlackList(code, textMessage);
                }

                @Override
                public void onException(Throwable exception) {

                }
            });
        }
        return true;
    }

    @Override
    public boolean sendMessage(File file) {
        for (int i = 0; i < mSelected.size(); i++) {
            IMMessage msg = MessageBuilder.createImageMessage(mSelected.get(i), SessionTypeEnum.P2P, file, file.getName());
            appendPushConfig(msg);
            // send message to server and save to db
            NIMClient.getService(MsgService.class).sendMessage(msg, false).setCallback(new RequestCallback<Void>() {
                @Override
                public void onSuccess(Void param) {
                    Log.e(GroupSendHelperActivity.class.getSimpleName(), "--------onSuccess");
                    ToastHelper.showToast(getApplicationContext(), "发送成功");
                    finish();
                }

                @Override
                public void onFailed(int code) {
                    sendFailWithBlackList(code, msg);
                }

                @Override
                public void onException(Throwable exception) {

                }
            });
        }
        return true;
    }

    // 被对方拉入黑名单后，发消息失败的交互处理
    private void sendFailWithBlackList(int code, IMMessage msg) {
        if (code == ResponseCode.RES_IN_BLACK_LIST) {
            // 如果被对方拉入黑名单，发送的消息前不显示重发红点
            msg.setStatus(MsgStatusEnum.success);
            NIMClient.getService(MsgService.class).updateIMMessageStatus(msg);
            // 同时，本地插入被对方拒收的tip消息
            IMMessage tip = MessageBuilder.createTipMessage(msg.getSessionId(), msg.getSessionType());
            tip.setContent(getString(R.string.black_list_send_tip));
            tip.setStatus(MsgStatusEnum.success);
            CustomMessageConfig config = new CustomMessageConfig();
            config.enableUnreadCount = false;
            tip.setConfig(config);
            NIMClient.getService(MsgService.class).saveMessageToLocal(tip, true);
        }
    }

    @Override
    public void onInputPanelExpand() {

    }

    @Override
    public void shouldCollapseInputPanel() {
        inputPanel.collapse(false);
    }

    @Override
    public boolean isLongClickEnabled() {
        return !inputPanel.isRecording();
    }

    @Override
    public void onItemFooterClick(IMMessage message) {

    }
}
