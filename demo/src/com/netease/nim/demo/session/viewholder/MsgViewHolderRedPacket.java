package com.netease.nim.demo.session.viewholder;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netease.nim.demo.R;
import com.netease.nim.demo.redpacket.NIMOpenRpCallback;
import com.netease.nim.demo.redpacket.NIMRedPacketClient;
import com.netease.nim.demo.session.extension.RedPacketAttachment;
import com.netease.nim.uikit.business.chatroom.adapter.ChatRoomMsgAdapter;
import com.netease.nim.uikit.business.session.module.ModuleProxy;
import com.netease.nim.uikit.business.session.module.list.MsgAdapter;
import com.netease.nim.uikit.business.session.viewholder.MsgViewHolderBase;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;

public class MsgViewHolderRedPacket extends MsgViewHolderBase {

    private RelativeLayout sendView, revView;
    private TextView sendContentText, revContentText;    // 红包描述
    private TextView sendTitleText, revTitleText;    // 红包名称

    public MsgViewHolderRedPacket(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.red_packet_item;
    }

    @Override
    protected void inflateContentView() {
        sendContentText = findViewById(R.id.tv_bri_mess_send);
        sendTitleText = findViewById(R.id.tv_bri_name_send);
        sendView = findViewById(R.id.bri_send);
        revContentText = findViewById(R.id.tv_bri_mess_rev);
        revTitleText = findViewById(R.id.tv_bri_name_rev);
        revView = findViewById(R.id.bri_rev);
    }

    @Override
    protected void bindContentView() {
        RedPacketAttachment attachment = (RedPacketAttachment) message.getAttachment();

        if (!isReceivedMessage()) {// 消息方向，自己发送的
            sendView.setVisibility(View.VISIBLE);
            if (message.getStatus() == MsgStatusEnum.read) {
                sendView.setBackgroundDrawable(((MsgAdapter) adapter).getContainer().activity.getResources().getDrawable(R.drawable.red_packet_send_press));
            } else {
                sendView.setBackgroundDrawable(((MsgAdapter) adapter).getContainer().activity.getResources().getDrawable(R.drawable.red_packet_send_normal));
            }
            revView.setVisibility(View.GONE);
            sendContentText.setText(attachment.getRpContent());
            sendTitleText.setText(attachment.getRpTitle());
        } else {
            sendView.setVisibility(View.GONE);
            revView.setVisibility(View.VISIBLE);
            if (message.getStatus() == MsgStatusEnum.read) {
                revView.setBackgroundDrawable(((MsgAdapter) adapter).getContainer().activity.getResources().getDrawable(R.drawable.red_packet_rev_press));
            } else {
                revView.setBackgroundDrawable(((MsgAdapter) adapter).getContainer().activity.getResources().getDrawable(R.drawable.red_packet_rev_normal));
            }
            revContentText.setText(attachment.getRpContent());
            revTitleText.setText(attachment.getRpTitle());
        }
    }

    @Override
    protected int leftBackground() {
        return R.color.transparent;
    }

    @Override
    protected int rightBackground() {
        return R.color.transparent;
    }

    @Override
    protected void onItemClick() {
        // 拆红包
        RedPacketAttachment attachment = (RedPacketAttachment) message.getAttachment();
//        if (message.getSessionType() == SessionTypeEnum.Team) {
//            Team t = NimUIKit.getTeamProvider().getTeamById(message.getSessionId());
//            if (t != null) {
//                TeamMember teamMember = NimUIKit.getTeamProvider().getTeamMember(message.getSessionId(), DemoCache.getAccount());
//                if (teamMember.isMute()) {
//                    ToastHelper.showToast(((MsgAdapter) adapter).getContainer().activity, "您已被禁言");
//                    return;
//                }
//            }
//        }
        BaseMultiItemFetchLoadAdapter adapter = getAdapter();
        ModuleProxy proxy = null;
        if (adapter instanceof MsgAdapter) {
            proxy = ((MsgAdapter) adapter).getContainer().proxy;
        } else if (adapter instanceof ChatRoomMsgAdapter) {
            proxy = ((ChatRoomMsgAdapter) adapter).getContainer().proxy;
        }
        NIMOpenRpCallback cb = new NIMOpenRpCallback(message.getFromAccount(), message.getSessionId(), message.getSessionType(), proxy);
        NIMRedPacketClient.startOpenRpDialog((AppCompatActivity) context, message.getSessionType(), attachment.getRpId(), message, cb);
    }
}
