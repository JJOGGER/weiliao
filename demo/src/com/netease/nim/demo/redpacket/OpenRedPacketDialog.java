package com.netease.nim.demo.redpacket;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.netease.nim.demo.R;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;

import java.util.Objects;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;

public class OpenRedPacketDialog extends DialogFragment {
    private String mAccount;
    private String mName;
    private String mAvatar;
    private OnOpenRedPacketDialogListener mListener;

    public interface OnOpenRedPacketDialogListener {
        void onOpen();
    }

    public void setOnOpenRedPacketDialogListener(OnOpenRedPacketDialogListener listener) {
        mListener = listener;
    }


    public void setRedPacketInfo(String account, String name, String avatar) {
        mAccount = account;
        mName = name;
        mAvatar = avatar;
    }

    @Override
    public void onStart() {
        super.onStart();
//        getDialog().getWindow().setLayout(QMUIStatusBarHelper.dp2px(getContext(), 335), QMUIStatusBarHelper.dp2px(getContext(), 335));
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        @SuppressLint("InflateParams") View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_open_red_packet
                , null);
        builder.setView(view);
        view.findViewById(R.id.img_close).setOnClickListener(v -> {
            dismiss();
        });
        view.findViewById(R.id.img_open).setOnClickListener(v -> {
            if (mListener != null)
                mListener.onOpen();
        });
        if (!TextUtils.isEmpty(mName)) {
            ((TextView) view.findViewById(R.id.tv_name)).setText(mName + "的红包");
        }
        if (!TextUtils.isEmpty(mAvatar)) {
            ((HeadImageView) view.findViewById(R.id.user_head_image)).loadAvatar(mAvatar);
        }
        AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new
                ColorDrawable(Color.TRANSPARENT));
        return alertDialog;
    }
}
