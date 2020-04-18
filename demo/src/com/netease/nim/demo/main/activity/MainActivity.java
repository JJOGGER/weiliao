package com.netease.nim.demo.main.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.diamond.jogger.base.entity.AccountResult;
import com.diamond.jogger.base.entity.GroupAuth;
import com.diamond.jogger.base.entity.QRCodeInfo;
import com.diamond.jogger.base.entity.VersionInfo;
import com.diamond.jogger.base.http.basic.callback.RequestMultiplyCallback;
import com.diamond.jogger.base.http.basic.exception.base.BaseException;
import com.diamond.jogger.base.http.datasource.UserDataSource;
import com.diamond.jogger.base.http.datasource.base.IUserDataSource;
import com.diamond.jogger.base.scan.CaptureActivity;
import com.google.gson.Gson;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;
import com.netease.nim.demo.BuildConfig;
import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.common.ui.viewpager.FadeInOutPageTransformer;
import com.netease.nim.demo.common.ui.viewpager.PagerSlidingTabStrip;
import com.netease.nim.demo.config.preference.Preferences;
import com.netease.nim.demo.contact.activity.AddFriendActivity;
import com.netease.nim.demo.contact.activity.UserProfileActivity;
import com.netease.nim.demo.login.LoginActivity;
import com.netease.nim.demo.login.LogoutHelper;
import com.netease.nim.demo.main.adapter.MainTabPagerAdapter;
import com.netease.nim.uikit.business.helper.SystemMessageUnreadManager;
import com.netease.nim.demo.main.model.MainTab;
import com.netease.nim.uikit.business.reminder.ReminderItem;
import com.netease.nim.uikit.business.reminder.ReminderManager;
import com.netease.nim.demo.main.version.DownloadAppService;
import com.netease.nim.demo.main.version.UpdateAppService;
import com.netease.nim.demo.main.viewholder.MyViewPager;
import com.netease.nim.demo.session.SessionHelper;
import com.netease.nim.demo.session.extension.CustomNotificationAttachment;
import com.netease.nim.demo.team.TeamCreateHelper;
import com.netease.nim.demo.team.activity.AdvancedTeamSearchActivity;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.model.main.LoginSyncDataStatusObserver;
import com.netease.nim.uikit.business.contact.selector.activity.ContactSelectActivity;
import com.netease.nim.uikit.business.team.helper.TeamHelper;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.drop.DropCover;
import com.netease.nim.uikit.common.ui.drop.DropManager;
import com.netease.nim.uikit.support.permission.MPermission;
import com.netease.nim.uikit.support.permission.annotation.OnMPermissionDenied;
import com.netease.nim.uikit.support.permission.annotation.OnMPermissionGranted;
import com.netease.nim.uikit.support.permission.annotation.OnMPermissionNeverAskAgain;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.friend.model.AddFriendNotify;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.SystemMessageObserver;
import com.netease.nimlib.sdk.msg.SystemMessageService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SystemMessageType;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.msg.model.SystemMessage;
import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.download.DownloadListener;

import java.io.File;
import java.util.ArrayList;

/**
 * 主界面
 * Created by huangjun on 2015/3/25.
 */
public class MainActivity extends UI implements ViewPager.OnPageChangeListener, ReminderManager.UnreadNumChangedCallback {

    private static final String EXTRA_APP_QUIT = "APP_QUIT";
    private static final int REQUEST_CODE_NORMAL = 1;
    private static final int REQUEST_CODE_ADVANCED = 2;
    private static final int BASIC_PERMISSION_REQUEST_CODE = 100;
    private static final int CAPTURE_REQUEST_CODE = 101;
    private static final String[] BASIC_PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private IUserDataSource mUserDataSource;
    private CheckUpdateDialog mCheckUpdateDialog;


    private PagerSlidingTabStrip tabs;
    private MyViewPager pager;
    private int scrollState;
    private MainTabPagerAdapter adapter;
    private ProgressDialog mProgressDialog;


    private boolean isFirstIn;
    private Observer<Integer> sysMsgUnreadCountChangedObserver = new Observer<Integer>() {
        @Override
        public void onEvent(Integer unreadCount) {
            SystemMessageUnreadManager.getInstance().setSysMsgUnreadCount(unreadCount);
            ReminderManager.getInstance().updateContactUnreadNum(unreadCount);
        }
    };


    public static void start(Context context) {
        start(context, null);
    }

    public static void start(Context context, Intent extras) {
        Intent intent = new Intent();
        intent.setClass(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (extras != null) {
            intent.putExtras(extras);
        }
        context.startActivity(intent);
    }

    // 注销
    public static void logout(Context context, boolean quit) {
        Intent extra = new Intent();
        extra.putExtra(EXTRA_APP_QUIT, quit);
        start(context, extra);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setToolBar(R.id.toolbar, R.string.app_name, 0);
        setTitle(R.string.app_name);
        isFirstIn = true;
        mUserDataSource = new UserDataSource();
        //不保留后台活动，从厂商推送进聊天页面，会无法退出聊天页面
        if (savedInstanceState == null && parseIntent()) {
            return;
        }
        init();
//        getToken();
    }

    private void getToken() {
        new Thread() {
            @Override
            public void run() {
                try {
                    // read from agconnect-services.json
                    String appId = AGConnectServicesConfig.fromContext(MainActivity.this).getString("client/app_id");
                    String token = HmsInstanceId.getInstance(MainActivity.this).getToken(appId, "HCM");
                    Log.i(MainActivity.class.getSimpleName(), "---------get token:" + token);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ClipboardManager clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            clip.setPrimaryClip(ClipData.newPlainText("Label", token));
                            ToastHelper.showToast(MainActivity.this, "token:" + token);
                        }
                    });

                    if (!TextUtils.isEmpty(token)) {
                    }
                } catch (ApiException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ClipboardManager clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            clip.setPrimaryClip(ClipData.newPlainText("Label", e.getMessage()));
                            ToastHelper.showToast(MainActivity.this, "---------------get token failed, " + e);
                        }
                    });
                    Log.e(MainActivity.class.getSimpleName(), "---------------get token failed, " + e);
                }
            }
        }.start();
    }

    private void init() {
        observerSyncDataComplete();
        findViews();
        setupPager();
        setupTabs();
        registerMsgUnreadInfoObserver(true);
        registerSystemMessageObservers(true);
        requestSystemMessageUnreadCount();
        initUnreadCover();
        requestBasicPermission();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("开始下载新版本APP");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mUserDataSource.getVersionInfo(new RequestMultiplyCallback<VersionInfo>() {
            @Override
            public void onFail(BaseException e) {

            }

            @Override
            public void onSuccess(VersionInfo versionInfo) {
                if (versionInfo == null || versionInfo.getVersion() == null) return;
                int versionCode = Integer.valueOf(versionInfo.getVersion());
                if (versionCode > BuildConfig.VERSION_CODE) {
                    update(versionInfo);
                }
            }

        });
    }

    private void update(VersionInfo versionInfo) {
        UpdateDialog dialog = new UpdateDialog();
        dialog.setAppVersion(versionInfo);
        dialog.show(getSupportFragmentManager(), UpdateDialog.class.getSimpleName());
    }

    private void downloadApp(VersionInfo versionInfo) {
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
            mProgressDialog.setCancelable(false);
        }
        updateApp(versionInfo.getDownloadUrl());
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
            UpdateAppService.installApp(MainActivity.this, new File(filePath));
        }

        @Override
        public void onCancel(int what) {
            if (isFinishingOrDestroyed()) return;
            if (mProgressDialog != null && mProgressDialog.isShowing())
                mProgressDialog.dismiss();
        }
    };


    private boolean parseIntent() {

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_APP_QUIT)) {
            intent.removeExtra(EXTRA_APP_QUIT);
            onLogout();
            return true;
        }

        if (intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT)) {
            IMMessage message = (IMMessage) intent.getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
            intent.removeExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
            switch (message.getSessionType()) {
                case P2P:
                    SessionHelper.startP2PSession(this, message.getSessionId());
                    break;
                case Team:
                    SessionHelper.startTeamSession(this, message.getSessionId());
                    break;
            }

            return true;
        }

//        if (intent.hasExtra(AVChatActivity.INTENT_ACTION_AVCHAT) && AVChatProfile.getInstance().isAVChatting()) {
//            intent.removeExtra(AVChatActivity.INTENT_ACTION_AVCHAT);
//            Intent localIntent = new Intent();
//            localIntent.setClass(this, AVChatActivity.class);
//            startActivity(localIntent);
//            return true;
//        }
//
//        String account = intent.getStringExtra(AVChatExtras.EXTRA_ACCOUNT);
//        if (intent.hasExtra(AVChatExtras.EXTRA_FROM_NOTIFICATION) && !TextUtils.isEmpty(account)) {
//            intent.removeExtra(AVChatExtras.EXTRA_FROM_NOTIFICATION);
//            SessionHelper.startP2PSession(this, account);
//            return true;
//        }

        return false;
    }

    private void observerSyncDataComplete() {
        boolean syncCompleted = LoginSyncDataStatusObserver.getInstance().observeSyncDataCompletedEvent(new Observer<Void>() {
            @Override
            public void onEvent(Void v) {
                DialogMaker.dismissProgressDialog();
            }
        });
        //如果数据没有同步完成，弹个进度Dialog
        if (!syncCompleted) {
            DialogMaker.showProgressDialog(MainActivity.this, getString(R.string.prepare_data)).setCanceledOnTouchOutside(false);
        }
    }

    private void findViews() {
        tabs = findView(R.id.tabs);
        pager = findView(R.id.main_tab_pager);
    }

    private void setupPager() {
        adapter = new MainTabPagerAdapter(getSupportFragmentManager(), this, pager);
        pager.setOffscreenPageLimit(adapter.getCacheCount());
        pager.setPageTransformer(true, new FadeInOutPageTransformer());
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(this);
    }

    private void setupTabs() {
        tabs.setOnCustomTabListener(new PagerSlidingTabStrip.OnCustomTabListener() {
            @Override
            public int getTabLayoutResId(int position) {
                return R.layout.tab_layout_main;
            }

            @Override
            public boolean screenAdaptation() {
                return true;
            }
        });
        tabs.setViewPager(pager);
        tabs.setOnTabClickListener(adapter);
        tabs.setOnTabDoubleTapListener(adapter);
    }


    /**
     * 注册未读消息数量观察者
     */
    private void registerMsgUnreadInfoObserver(boolean register) {
        if (register) {
            ReminderManager.getInstance().registerUnreadNumChangedCallback(this);
        } else {
            ReminderManager.getInstance().unregisterUnreadNumChangedCallback(this);
        }
    }

    /**
     * 注册/注销系统消息未读数变化
     */
    private void registerSystemMessageObservers(boolean register) {
        NIMClient.getService(SystemMessageObserver.class).observeUnreadCountChange(sysMsgUnreadCountChangedObserver, register);
        NIMClient.getService(SystemMessageObserver.class).observeReceiveSystemMsg(systemMessageObserver, register);
    }

    /**
     * 查询系统消息未读数
     */
    private void requestSystemMessageUnreadCount() {
        int unread = NIMClient.getService(SystemMessageService.class).querySystemMessageUnreadCountBlock();
        SystemMessageUnreadManager.getInstance().setSysMsgUnreadCount(unread);
        ReminderManager.getInstance().updateContactUnreadNum(unread);
    }

    Observer<SystemMessage> systemMessageObserver = new Observer<SystemMessage>() {
        @Override
        public void onEvent(SystemMessage systemMessage) {
            onIncomingMessage(systemMessage);
        }
    };

    /**
     * 新消息到来
     */
    private void onIncomingMessage(final SystemMessage message) {
        // 同一个账号的好友申请仅保留最近一条
        SystemMessage del = null;
        if (message.getType() == SystemMessageType.AddFriend) {
            if (message.getFromAccount().equals(DemoCache.getAccount())) return;
            AddFriendNotify attachData = (AddFriendNotify) message.getAttachObject();
            if (attachData != null && attachData.getEvent() == AddFriendNotify.Event.RECV_AGREE_ADD_FRIEND) {
                CustomNotificationAttachment attachment = new CustomNotificationAttachment();
                new UserDataSource().getAccount(message.getFromAccount(), new RequestMultiplyCallback<AccountResult>() {
                    @Override
                    public void onFail(BaseException e) {

                    }

                    @Override
                    public void onSuccess(AccountResult accountResult) {
                        attachment.setContent("已成为你的好友");
                        attachment.setFromAccount(accountResult.getAccount());
                        IMMessage customMessage = MessageBuilder.createCustomMessage(message.getFromAccount(), SessionTypeEnum.P2P, attachment);
// send message to server and save to db
                        NIMClient.getService(MsgService.class).sendMessage(customMessage, false).setCallback(new RequestCallback<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }

                            @Override
                            public void onFailed(int i) {

                            }

                            @Override
                            public void onException(Throwable throwable) {

                            }
                        });
                    }
                });
            }
        }
    }

    //初始化未读红点动画
    private void initUnreadCover() {
        DropManager.getInstance().init(this, (DropCover) findView(R.id.unread_cover),
                new DropCover.IDropCompletedListener() {
                    @Override
                    public void onCompleted(Object id, boolean explosive) {
                        if (id == null || !explosive) {
                            return;
                        }

                        if (id instanceof RecentContact) {
                            RecentContact r = (RecentContact) id;
                            NIMClient.getService(MsgService.class).clearUnreadCount(r.getContactId(), r.getSessionType());
                            return;
                        }

                        if (id instanceof String) {
                            if (((String) id).contentEquals("0")) {
                                NIMClient.getService(MsgService.class).clearAllUnreadCount();
                            } else if (((String) id).contentEquals("1")) {
                                NIMClient.getService(SystemMessageService.class).resetSystemMessageUnreadCount();
                            }
                        }
                    }
                });
    }

    private void requestBasicPermission() {
        MPermission.printMPermissionResult(true, this, BASIC_PERMISSIONS);
        MPermission.with(MainActivity.this)
                .setRequestCode(BASIC_PERMISSION_REQUEST_CODE)
                .permissions(BASIC_PERMISSIONS)
                .request();
    }

    private void onLogout() {
        Preferences.saveUserToken("");
        // 清理缓存&注销监听
        LogoutHelper.logout();
        // 启动登录
        LoginActivity.start(this);
        finish();
    }

    private void selectPage() {
        if (scrollState == ViewPager.SCROLL_STATE_IDLE) {
            adapter.onPageSelected(pager.getCurrentItem());
        }
    }

    /**
     * 设置最近联系人的消息为已读
     * <p>
     * account, 聊天对象帐号，或者以下两个值：
     * {@link MsgService#MSG_CHATTING_ACCOUNT_ALL} 目前没有与任何人对话，但能看到消息提醒（比如在消息列表界面），不需要在状态栏做消息通知
     * {@link MsgService#MSG_CHATTING_ACCOUNT_NONE} 目前没有与任何人对话，需要状态栏消息通知
     */
    private void enableMsgNotification(boolean enable) {
        boolean msg = (pager.getCurrentItem() != MainTab.RECENT_CONTACTS.tabIndex);
        if (enable | msg) {
            NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE, SessionTypeEnum.None);
        } else {
            NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_ALL, SessionTypeEnum.None);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scan:
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(intent, CAPTURE_REQUEST_CODE);
                break;
//            case R.id.create_normal_team:
//                ContactSelectActivity.Option option = TeamHelper.getCreateContactSelectOption(null, 50);
//                NimUIKit.startContactSelector(MainActivity.this, option, REQUEST_CODE_NORMAL);
//                break;
            case R.id.create_regular_team:
                //获取权限
                mUserDataSource.getCreateGroupAuth(DemoCache.getServerAccount(), new RequestMultiplyCallback<GroupAuth>() {
                    @Override
                    public void onFail(BaseException e) {
                        ToastHelper.showToast(MainActivity.this, e.getMessage());
                    }

                    @Override
                    public void onSuccess(GroupAuth groupAuth) {
                        if (!groupAuth.isGroupFlag()) {
                            ToastHelper.showToast(MainActivity.this, "没有创建群的权限");
                        } else {
                            ContactSelectActivity.Option advancedOption = TeamHelper.getCreateContactSelectOption(null, 50);
                            NimUIKit.startContactSelector(MainActivity.this, advancedOption, REQUEST_CODE_ADVANCED);
                        }
                    }
                });

                break;
            case R.id.search_advanced_team:
                AdvancedTeamSearchActivity.start(MainActivity.this);
                break;
            case R.id.add_buddy:
                AddFriendActivity.start(MainActivity.this);
                break;
            case R.id.search_btn:
                GlobalSearchActivity.start(MainActivity.this);
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        parseIntent();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 第一次 ， 三方通知唤起进会话页面之类的，不会走初始化过程
        boolean temp = isFirstIn;
        isFirstIn = false;
        if (pager == null && temp) {
            return;
        }
        //如果不是第一次进 ， eg: 其他页面back
        if (pager == null) {
            init();
        }
        enableMsgNotification(false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (pager == null) {
            return;
        }
        enableMsgNotification(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        registerMsgUnreadInfoObserver(false);
        registerSystemMessageObservers(false);
        DropManager.getInstance().destroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_NORMAL) {
            final ArrayList<String> selected = data.getStringArrayListExtra(ContactSelectActivity.RESULT_DATA);
            if (selected != null && !selected.isEmpty()) {
                TeamCreateHelper.createNormalTeam(MainActivity.this, selected, false, null);
            } else {
                ToastHelper.showToast(MainActivity.this, "请选择至少一个联系人！");
            }
        } else if (requestCode == REQUEST_CODE_ADVANCED) {
            final ArrayList<String> selected = data.getStringArrayListExtra(ContactSelectActivity.RESULT_DATA);
            TeamCreateHelper.createAdvancedTeam(MainActivity.this, selected);
        } else if (requestCode == CAPTURE_REQUEST_CODE) {
            String result = data.getStringExtra("result");
            try {
                QRCodeInfo qrCodeInfo = new Gson().fromJson(result, QRCodeInfo.class);
                if (qrCodeInfo == null) return;
                if ("user".equals(qrCodeInfo.getType())) {
                    UserProfileActivity.start(MainActivity.this, qrCodeInfo.getContent());
                }
            } catch (Exception ignore) {
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        tabs.onPageScrolled(position, positionOffset, positionOffsetPixels);
        adapter.onPageScrolled(position);
    }

    @Override
    public void onPageSelected(int position) {
        if (position != 2 && position != 3) {
            getToolBar().setVisibility(View.VISIBLE);
        } else {
            getToolBar().setVisibility(View.GONE);
        }
        tabs.onPageSelected(position);
        selectPage();
        enableMsgNotification(false);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        tabs.onPageScrollStateChanged(state);
        scrollState = state;
        selectPage();
    }

    //未读消息数量观察者实现
    @Override
    public void onUnreadNumChanged(ReminderItem item) {
        MainTab tab = MainTab.fromReminderId(item.getId());
        if (tab != null) {
            tabs.updateTab(tab.tabIndex, item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @OnMPermissionGranted(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionSuccess() {
        try {
//            ToastHelper.showToast(this, "授权成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        MPermission.printMPermissionResult(false, this, BASIC_PERMISSIONS);
    }

    @OnMPermissionDenied(BASIC_PERMISSION_REQUEST_CODE)
    @OnMPermissionNeverAskAgain(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionFailed() {
        try {
            ToastHelper.showToast(this, "未全部授权，部分功能可能无法正常运行！");
        } catch (Exception e) {
            e.printStackTrace();
        }
        MPermission.printMPermissionResult(false, this, BASIC_PERMISSIONS);
    }

    @Override
    protected boolean displayHomeAsUpEnabled() {
        return false;
    }


}
