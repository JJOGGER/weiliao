package com.netease.nim.demo.contact.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.diamond.jogger.base.entity.QRCodeInfo;
import com.diamond.jogger.base.utils.QrUtils;
import com.google.gson.Gson;
import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.uikit.api.wrapper.NimToolBarOptions;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.activity.ToolBarOptions;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;

/**
 * Created by hzxuwen on 2015/9/14.
 */
public class UserQrCodeActivity extends UI {

    public static final void startActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, UserQrCodeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_qr_code_layout);
        ToolBarOptions options = new NimToolBarOptions();
        setToolBar(R.id.toolbar, options);
        setTitle("二维码");
        findView(R.id.ibtn_more).setOnClickListener(v -> {
            ToastHelper.showToast(this, "more");
        });
        HeadImageView ivHeader = findView(R.id.user_head_image);
        TextView tvAccount = findView(R.id.tv_account);
        ivHeader.loadBuddyAvatar(DemoCache.getAccount());
        tvAccount.setText("用户名：" + DemoCache.getServerAccount());
        QRCodeInfo qrCodeInfo = new QRCodeInfo();
        qrCodeInfo.setType("user");
        qrCodeInfo.setContent(DemoCache.getAccount());
        Bitmap qrCode = QrUtils.getQrCode(this, null, new Gson().toJson(qrCodeInfo));
        ImageView ivQrCode = findView(R.id.iv_qr_code);
        ivQrCode.setImageBitmap(qrCode);
    }

}
