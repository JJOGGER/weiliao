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
import android.widget.TextView;

import com.diamond.jogger.base.entity.SendRedPacketResponse;
import com.diamond.jogger.base.http.basic.callback.RequestMultiplyCallback;
import com.diamond.jogger.base.http.basic.exception.base.BaseException;
import com.diamond.jogger.base.http.datasource.RedPacketDataSource;
import com.diamond.jogger.base.http.datasource.base.IRedPacketDataSource;
import com.diamond.jogger.base.utils.BigDecimalUtils;
import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.main.model.Extras;
import com.netease.nim.demo.session.extension.RedPacketAttachment;
import com.netease.nim.uikit.api.wrapper.NimToolBarOptions;
import com.diamond.jogger.base.event.RedPacketEvent;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.activity.ToolBarOptions;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.widget.AmountEditText;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomMessageConfig;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by jogger on 2019/12/18
 * 描述：
 */
public class SendPointRedPacketActivity extends UI implements View.OnClickListener, TextWatcher {

    private Button btnPutin;
    private IRedPacketDataSource mPacketDataSource;
    private AmountEditText etAmount;
    private static final String TYPE_LUCK = "groupLuck";
    private static final String TYPE_NORMAL = "groupNormal";
    private String mCurrentType = TYPE_NORMAL;
    private TextView tvPeakAmount;
    private TextView tvAmountForShow;
    private EditText etPeakMessage;
    private String mReceiveAccount;

    public static void navTo(Context context, String account) {
        Intent intent = new Intent(context, SendPointRedPacketActivity.class);
        intent.putExtra(Extras.EXTRA_ACCOUNT, account);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jrmf_rp_activity_send_single_peak);
        ToolBarOptions options = new NimToolBarOptions();
        setToolBar(R.id.toolbar, options);
        setTitle("发红包");
        findViews();
        mReceiveAccount = getIntent().getStringExtra(Extras.EXTRA_ACCOUNT);
        mPacketDataSource = new RedPacketDataSource();
    }

    private void findViews() {
        btnPutin = findView(R.id.btn_putin);
        etAmount = findView(R.id.et_amount);
        tvAmountForShow = findView(R.id.tv_amount);
        etPeakMessage = findView(R.id.et_message);
        etAmount.addTextChangedListener(this);
        btnPutin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_putin:
                sendRedPacket();
                break;
        }
    }

    private void sendRedPacket() {
        NimUserInfo userInfo = NIMClient.getService(UserService.class).getUserInfo(DemoCache.getAccount());
        String content = TextUtils.isEmpty(etPeakMessage.getText().toString()) ? "恭喜发财，大吉大利" : etPeakMessage.getText().toString();
        DialogMaker.showProgressDialog(this, "请稍候");
        mPacketDataSource.sendPointRedPacket(DemoCache.getServerAccount(),
                etAmount.getText().toString(),
                userInfo.getAvatar(),
                content,
                userInfo.getName(),
                mReceiveAccount, new RequestMultiplyCallback<SendRedPacketResponse>() {
                    @Override
                    public void onFail(BaseException e) {
                        ToastHelper.showToast(getApplicationContext(), e.getMessage());
                        DialogMaker.dismissProgressDialog();
                    }

                    @Override
                    public void onSuccess(SendRedPacketResponse o) {
                        RedPacketAttachment attachment = new RedPacketAttachment();
//        // 红包id，红包信息，红包名称
                        attachment.setRpId(o.getRedId());
                        attachment.setRpContent(content);
                        attachment.setRpTitle("红包");
                        attachment.setRead(false);
                        // 不存云消息历史记录
                        CustomMessageConfig config = new CustomMessageConfig();
                        config.enableHistory = false;

                        IMMessage message = MessageBuilder.createCustomMessage(mReceiveAccount, SessionTypeEnum.P2P, content, attachment, config);
                        DialogMaker.dismissProgressDialog();
                        RedPacketEvent redPacketEvent = new RedPacketEvent(RedPacketEvent.ACTION_SEND_RED_PACKET);
                        redPacketEvent.putExtra(Extras.MESSAGE, message);
                        EventBus.getDefault().post(redPacketEvent);
                        finish();
                    }
                });
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
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String amount = etAmount.getText().toString().trim();
        btnPutin.setEnabled(!TextUtils.isEmpty(amount));
        if (!TextUtils.isEmpty(amount))
            tvAmountForShow.setText(BigDecimalUtils.mul2(amount, "1"));
        else {
            tvAmountForShow.setText("0.00");
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
