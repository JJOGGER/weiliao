package com.netease.nim.demo.config.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.netease.nim.demo.DemoCache;

/**
 * Created by hzxuwen on 2015/4/13.
 */
public class Preferences {
    private static final String KEY_USER_ACCID = "accid";
    private static final String KEY_USER_ACCOUNT = "account";
    private static final String KEY_USER_TOKEN = "token";
    private static final String KEY_USER_PASSWORD = "password";
    private static final String KEY_VERSION_ID = "version_id";
    private static final String KEY_USER_INFO = "user_info";
    private static final String KEY_TAB_WEB_SITE = "tab_web_site";
    private static final String KEY_MESSAGE_SWITCH_FLAG = "key_message_switch_flag";

    public static void saveUserAccount(String account) {
        saveString(KEY_USER_ACCOUNT, account);
    }

    public static String getUserAccount() {
        return getString(KEY_USER_ACCOUNT);
    }

    public static String getUserAccid() {
        return getString(KEY_USER_ACCID);
    }

    public static void saveUserAccid(String account) {
        saveString(KEY_USER_ACCID, account);
    }

    public static void saveUserPassword(String password) {
        saveString(KEY_USER_PASSWORD, password);
    }

    public static String getUserPassword() {
        return getString(KEY_USER_PASSWORD);
    }

    public static void saveUserToken(String token) {
        saveString(KEY_USER_TOKEN, token);
    }

    public static String getUserToken() {
        return getString(KEY_USER_TOKEN);
    }

    public static void saveSimpleUserInfo(String simpleUserInfo) {
        saveString(KEY_USER_INFO, simpleUserInfo);
    }

    public static String getSimpleUserInfo() {
        return getString(KEY_USER_INFO);
    }

    public static void saveVersionId(long id) {
        saveLong(KEY_VERSION_ID, id);
    }

    public static long getVersionId() {
        return getLong(KEY_USER_ACCOUNT);
    }

    private static void saveString(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(key, value);
        editor.commit();
    }

    private static void saveLong(String key, long value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putLong(key, value);
        editor.commit();
    }

    private static void saveBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    private static long getLong(String key) {
        return getSharedPreferences().getLong(key, 0);
    }

    private static String getString(String key) {
        return getSharedPreferences().getString(key, null);
    }

    private static boolean getBoolean(String key) {
        return getSharedPreferences().getBoolean(key, false);
    }

    static SharedPreferences getSharedPreferences() {
        return DemoCache.getContext().getSharedPreferences("Demo", Context.MODE_PRIVATE);
    }

    public static void saveTabWebSite(String website) {
        saveString(KEY_TAB_WEB_SITE, website);
    }

    public static String getTabWebSite() {
        return getString(KEY_TAB_WEB_SITE);
    }

    public static boolean isMessageSwitchFlag() {
        return getBoolean(KEY_MESSAGE_SWITCH_FLAG);
    }

    public static void setMessageSwitchFlag(boolean messageSwitchFlag) {
        saveBoolean(KEY_MESSAGE_SWITCH_FLAG, messageSwitchFlag);
    }
}
