package com.netease.nim.demo.session.viewholder;

import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;

import com.netease.nim.demo.R;
import com.netease.nim.demo.session.extension.CustomNotificationAttachment;
import com.netease.nim.uikit.business.session.emoji.MoonUtil;
import com.netease.nim.uikit.business.session.viewholder.MsgViewHolderBase;
import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

/**
 * Created by hzliuxuanlin on 17/9/15.
 */

public class MsgViewHolderCustomNotification extends MsgViewHolderBase {

    private CustomNotificationAttachment customNotificationAttachment;

    public MsgViewHolderCustomNotification(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    protected TextView notificationTextView;

    @Override
    protected int getContentResId() {
        return R.layout.nim_message_item_notification;
    }

    @Override
    protected void inflateContentView() {
        notificationTextView = (TextView) view.findViewById(R.id.message_item_notification_label);
    }

    @Override
    protected void bindContentView() {
        handleTextNotification(getDisplayText());
        readReceiptTextView.setVisibility(View.GONE);
    }

    protected String getDisplayText() {
        CustomNotificationAttachment attachment = (CustomNotificationAttachment) message.getAttachment();
//        if (message.getFromAccount().equals(DemoCache.getAccount())) {
            return UserInfoHelper.getUserTitleName(message.getSessionId(), SessionTypeEnum.P2P) + attachment.getContent();
//        } else {
//            return attachment.getFromAccount() + attachment.getContent();
//        }

    }

    private void handleTextNotification(String text) {
        MoonUtil.identifyFaceExpressionAndATags(context, notificationTextView, text, ImageSpan.ALIGN_BOTTOM);
        notificationTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected boolean isMiddleItem() {
        return true;
    }
}
