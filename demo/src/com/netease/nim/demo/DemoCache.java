package com.netease.nim.demo;

import android.content.Context;
import android.text.TextUtils;

import com.diamond.jogger.base.entity.SimpleUserInfo;
import com.diamond.jogger.base.utils.GsonUtil;
import com.netease.nim.demo.config.preference.Preferences;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;

/**
 * Created by jezhee on 2/20/15.
 */
public class DemoCache {

    private static Context context;

    private static String account;
    private static String serverAccount;
    private static SimpleUserInfo simpleUserInfo;
    private static String tabWebSite;

    private static StatusBarNotificationConfig notificationConfig;

    public static void clear() {
        account = null;
    }

    public static String getAccount() {
        return account;
    }

    public static String getServerAccount() {
        return serverAccount;
    }

    private static boolean mainTaskLaunching;

    public static void setSimpleUserInfo(SimpleUserInfo userInfo) {
        DemoCache.simpleUserInfo = userInfo;
        setTabWebSite(userInfo.getWebsite());
        Preferences.saveSimpleUserInfo(GsonUtil.toJson(userInfo));
    }

    public static SimpleUserInfo getSimpleUserInfo() {
        if (DemoCache.simpleUserInfo == null) {
            String json = Preferences.getSimpleUserInfo();
            DemoCache.simpleUserInfo = GsonUtil.fromJson(json, SimpleUserInfo.class);
        }
        if (DemoCache.simpleUserInfo == null)
            DemoCache.simpleUserInfo = new SimpleUserInfo();
        return DemoCache.simpleUserInfo;
    }

    public static void setAccount(String account) {
        DemoCache.account = account;
        NimUIKit.setAccount(account);
//        AVChatKit.setAccount(account);
//        RTSKit.setAccount(account);
    }

    public static void setServerAccount(String account) {
        DemoCache.serverAccount = account;
        NimUIKit.setServerAccount(account);
//        AVChatKit.setAccount(account);
//        RTSKit.setAccount(account);
    }

    public static void setTabWebSite(String webSite) {
        DemoCache.tabWebSite = webSite;
        Preferences.saveTabWebSite(webSite);
    }

    public static String getTabWebSite() {
        if (TextUtils.isEmpty(DemoCache.tabWebSite)) {
            return Preferences.getTabWebSite();
        }
        return DemoCache.tabWebSite;
    }

    public static void setNotificationConfig(StatusBarNotificationConfig notificationConfig) {
        DemoCache.notificationConfig = notificationConfig;
    }

    public static StatusBarNotificationConfig getNotificationConfig() {
        return notificationConfig;
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        DemoCache.context = context.getApplicationContext();

//        AVChatKit.setContext(context);
//        RTSKit.setContext(context);
    }

    public static void setMainTaskLaunching(boolean mainTaskLaunching) {
        DemoCache.mainTaskLaunching = mainTaskLaunching;

//        AVChatKit.setMainTaskLaunching(mainTaskLaunching);
    }

    public static boolean isMainTaskLaunching() {
        return mainTaskLaunching;
    }


}
