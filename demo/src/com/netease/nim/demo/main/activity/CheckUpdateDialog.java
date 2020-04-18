package com.netease.nim.demo.main.activity;


import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.netease.nim.demo.R;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Niklaus on 2017/3/7.
 * 系统升级弹窗
 */

public class CheckUpdateDialog extends Dialog {
    private OnUpdateListener listener;

    TextView tvVersion;
    TextView tvSize;
    TextView tvDesc;
    NumberProgressBar progressBar;

    TextView positiveText;
    TextView negativeText;
    private int mCurrentState;
    private boolean isUpdate = false;

    public CheckUpdateDialog(Context context) {
        super(context);
        initUI();
    }

    public CheckUpdateDialog(Context context, int theme) {
        super(context, theme);
        initUI();
    }


    private void initUI() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.main_dialog_upgrade, null);
        setContentView(view);
        EventBus.getDefault().register(this);
        tvVersion = view.findViewById(R.id.upgrade_version);
        tvSize = view.findViewById(R.id.upgrade_size);
        tvDesc = view.findViewById(R.id.upgrade_desc);
        positiveText = view.findViewById(R.id.positive);
        negativeText = view.findViewById(R.id.negative);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;
        setCancelable(false);
        positiveText.setOnClickListener(v -> {
            if (listener != null)
                listener.onUpdate();
//            CheckUpgradeResponse.AppVersion version = (CheckUpgradeResponse.AppVersion) positiveText.getTag();
//            if (listener != null && version != null) {
//                if (mCurrentState == UpgradeEvent.DOWNLOAD_FINISH) {
//                    listener.onInstallApk(version);
//                } else {
//                    positiveText.setText("正在更新");
//                    negativeText.setText("更新中...");
//                    positiveText.setEnabled(false);
//                    negativeText.setEnabled(false);
//                    listener.onDownloadUpdate(version);
//                }
//            }
        });
        negativeText.setOnClickListener(v -> {
//            CheckUpgradeResponse.AppVersion version = (CheckUpgradeResponse.AppVersion) positiveText.getTag();
//            if (null != listener && null != version) {
//                if (version.getUpdateInstall() == 0) {
//                    listener.onExitApp(version);
//                } else {
//                    listener.onUnUpdate(version);
//                }
//            }
        });
    }


    @Override
    public void dismiss() {
        super.dismiss();
        this.isUpdate = false;
    }

    public void setOnUpdateListener(OnUpdateListener onUpdateListener) {
        this.listener = onUpdateListener;
    }

    public interface OnUpdateListener {
        void onUpdate();
    }
}
