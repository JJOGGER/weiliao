package com.netease.nim.demo.session.action;

import android.app.Activity;
import android.content.Intent;

import com.netease.nim.demo.R;
import com.netease.nim.demo.redpacket.SendGroupRedPacketActivity;
import com.netease.nim.demo.redpacket.SendPointRedPacketActivity;
import com.netease.nim.uikit.business.session.actions.BaseAction;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

public class RedPacketAction extends BaseAction {

    public RedPacketAction() {
        super(R.drawable.message_plus_rp_selector, R.string.red_packet);
    }

    private static final int CREATE_GROUP_RED_PACKET = 51;
    private static final int CREATE_SINGLE_RED_PACKET = 10;

    @Override
    public void onClick() {
        int requestCode;
        if (getContainer().sessionType == SessionTypeEnum.Team) {
            requestCode = makeRequestCode(CREATE_GROUP_RED_PACKET);
        } else if (getContainer().sessionType == SessionTypeEnum.P2P) {
            requestCode = makeRequestCode(CREATE_SINGLE_RED_PACKET);
        } else {
            return;
        }
        sendRpMessage(null);
//        NIMRedPacketClient.startSendRpActivity(getActivity(), getContainer().sessionType, getAccount(), requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        sendRpMessage(data);
    }

    private void sendRpMessage(Intent data) {
        if (getContainer().sessionType == SessionTypeEnum.Team) {
            SendGroupRedPacketActivity.navTo(getActivity(),getAccount());
        } else if (getContainer().sessionType == SessionTypeEnum.P2P) {
            SendPointRedPacketActivity.navTo(getActivity(),getAccount());
        } else {
            return;
        }

//        EnvelopeBean groupRpBean = JrmfRpClient.getEnvelopeInfo(data);
//        if (groupRpBean == null) {
//            return;
//        }
//        RedPacketAttachment attachment = new RedPacketAttachment();
////        // 红包id，红包信息，红包名称
//        attachment.setRpId(String.valueOf(UUID.randomUUID()));
//        String content = getActivity().getString(R.string.rp_push_content);
//        attachment.setRpContent(content);
//        attachment.setRpTitle("红包");
//        // 不存云消息历史记录
//        CustomMessageConfig config = new CustomMessageConfig();
//        config.enableHistory = false;
//
//        IMMessage message = MessageBuilder.createCustomMessage(getAccount(), getSessionType(), content, attachment, config);
////
//        sendMessage(message);
    }
}
