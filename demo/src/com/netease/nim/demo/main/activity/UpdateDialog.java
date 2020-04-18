package com.netease.nim.demo.main.activity;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.diamond.jogger.base.entity.VersionInfo;
import com.netease.nim.demo.R;
import com.netease.nim.demo.main.version.DownloadAppService;
import com.netease.nim.demo.main.version.UpdateAppService;
import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.download.DownloadListener;

import java.io.File;
import java.util.Objects;


public class UpdateDialog extends DialogFragment {

    @SuppressLint("InflateParams")
    private View mView;
    private VersionInfo mAppVersion;
    private ProgressDialog mProgressDialog;

    public void setAppVersion(VersionInfo contents) {
        mAppVersion = contents;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Translucent);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@android.support.annotation.Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(Objects.requireNonNull(getActivity()), R.style.style_dailog_from_transparent);
        mView = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_update
                        , null);
        mView.findViewById(R.id.iv_close).setOnClickListener(v -> {
            dialog.dismiss();
        });
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle("开始下载新版本APP");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        if (mAppVersion != null && !TextUtils.isEmpty(mAppVersion.getUpdateContent())) {
            ((TextView) mView.findViewById(R.id.tv_msg)).setText(mAppVersion.getUpdateContent());
            mView.findViewById(R.id.btn_update).setOnClickListener(v -> {
                downloadApp(mAppVersion);
            });
        }
        dialog.setContentView(mView);
//        final WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
//        params.height = ScreenUtil.dp2px(200);
//        dialog.getWindow().setAttributes(params);
        return dialog;
    }

    private void downloadApp(VersionInfo appVersion) {
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
            mProgressDialog.setCancelable(false);
        }
        updateApp(appVersion.getDownloadUrl());
    }

    private void updateApp(String url) {
        DownloadAppService.downloadApp(url, mDownloadListener);
    }

    private DownloadListener mDownloadListener = new DownloadListener() {
        @Override
        public void onDownloadError(int what, Exception exception) {
            if (isFinishingOrDestroyed()) return;
            if (mProgressDialog != null && mProgressDialog.isShowing())
                mProgressDialog.dismiss();
        }

        @Override
        public void onStart(int what, boolean isResume, long rangeSize, Headers responseHeaders,
                            long allCount) {
            if (isFinishingOrDestroyed()) return;
            if (mProgressDialog != null && !mProgressDialog.isShowing()) {
                mProgressDialog.setMax(100);
                mProgressDialog.setProgress(0);
                mProgressDialog.show();
            }
        }

        @Override
        public void onProgress(int what, int progress, long fileCount, long speed) {
            if (isFinishingOrDestroyed()) return;
            if (mProgressDialog != null && mProgressDialog.isShowing())
                mProgressDialog.setProgress(progress);
        }

        @Override
        public void onFinish(int what, String filePath) {
            if (isFinishingOrDestroyed()) return;
            if (mProgressDialog != null && mProgressDialog.isShowing())
                mProgressDialog.dismiss();
            UpdateAppService.installApp(getContext(), new File(filePath));
        }

        @Override
        public void onCancel(int what) {
            if (isFinishingOrDestroyed()) return;
            if (mProgressDialog != null && mProgressDialog.isShowing())
                mProgressDialog.dismiss();
        }
    };

    private boolean isFinishingOrDestroyed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return getActivity() == null || getActivity().isFinishing() || getActivity().isDestroyed();
        } else {
            return getActivity() == null || getActivity().isFinishing();
        }
    }
}
