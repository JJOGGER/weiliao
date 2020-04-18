package com.netease.nim.demo.dialog;


import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.netease.nim.demo.R;

/**
 * Created by it on 2017/3/9.
 * 提示对话框
 */

public class CommonHintDialog extends BaseDialogFragment implements View.OnClickListener {
    TextView tv_content;
    TextView tv_confirm;
    TextView tv_cancel;
    TextView tv_title;
    private String mHintContent = "";
    private String mTitle = "提示";
    private String confirmText = "";
    private String cancelText = "";

    public String getHintContent() {
        return mHintContent;
    }

    public void setHintContent(String hintContent) {
        mHintContent = hintContent;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    private OnHintDialogListener mListener;

    public interface OnHintDialogListener {
        void confirm();
    }

    public void setConfirmText(String text) {
        confirmText = text;
    }

    public void setCancelText(String text) {
        cancelText = text;
    }

    public String getConfirmText() {
        return confirmText;
    }

    public String getCancelText() {
        return cancelText;
    }

    public void setOnHintDialogListener(OnHintDialogListener listener) {
        mListener = listener;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_common_hint;
    }

    @Override
    protected void init(View view) {
        tv_content = findView(R.id.tv_content);
        tv_confirm = findView(R.id.tv_confirm);
        tv_cancel = findView(R.id.tv_cancel);
        tv_title = findView(R.id.tv_title);
        tv_confirm.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        tv_content.setText(getHintContent());
        tv_title.setText(mTitle);
        if (!TextUtils.isEmpty(getConfirmText())) {
            tv_confirm.setText(getConfirmText());
        }
        if (!TextUtils.isEmpty(getCancelText())) {
            tv_cancel.setText(getCancelText());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_confirm:
                if (mListener != null) {
                    mListener.confirm();
                }
                dismiss();
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
        }
    }
}
