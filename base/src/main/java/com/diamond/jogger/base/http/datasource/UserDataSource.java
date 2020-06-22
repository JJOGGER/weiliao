package com.diamond.jogger.base.http.datasource;


import android.text.TextUtils;

import com.diamond.jogger.base.entity.AccidResult;
import com.diamond.jogger.base.entity.AccountResult;
import com.diamond.jogger.base.entity.GroupAuth;
import com.diamond.jogger.base.entity.GroupData;
import com.diamond.jogger.base.entity.LoginResult;
import com.diamond.jogger.base.entity.MessageSwitch;
import com.diamond.jogger.base.entity.SimpleUserInfo;
import com.diamond.jogger.base.entity.VersionInfo;
import com.diamond.jogger.base.http.basic.BaseRemoteDataSource;
import com.diamond.jogger.base.http.basic.callback.RequestMultiplyCallback;
import com.diamond.jogger.base.http.basic.exception.base.BaseException;
import com.diamond.jogger.base.http.datasource.base.IUserDataSource;
import com.diamond.jogger.base.http.service.ApiService;
import com.diamond.jogger.base.utils.MD5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UserDataSource extends BaseRemoteDataSource implements IUserDataSource {
    private static Map<String, String> sAccountMap = new HashMap<>();
    private static List<GroupData> sGroupDatas = new ArrayList<>();//分组信息

    @Override
    public void isSmsSwitch(RequestMultiplyCallback<MessageSwitch> callback) {
        execute(getService(ApiService.class).isSmsSwitch(), callback);

    }

    @Override
    public void getSmsCode(String phone, RequestMultiplyCallback<Object> callback) {
        execute(getService(ApiService.class).sendSmsCode(phone), callback);
    }

    @Override
    public void checkSmsCode(String code, String phone, RequestMultiplyCallback<Object> callback) {
        execute(getService(ApiService.class).checkSmsCode(code, phone), callback);
    }

    @Override
    public void register(String phone, String account, String name, String password, RequestMultiplyCallback<Object> callback) {
        password = MD5.getStringMD5(password);
        execute(getService(ApiService.class).register(phone, account, name, password), callback);
    }

    @Override
    public void login(String account, String password, RequestMultiplyCallback<LoginResult> callback) {
        password = MD5.getStringMD5(password);
        execute(getService(ApiService.class).login(account, password, 1), callback);
    }

    @Override
    public void getUserInfo(String account, RequestMultiplyCallback<SimpleUserInfo> callback) {
        execute(getService(ApiService.class).getUserInfo(account, 1), callback);
    }

    @Override
    public void getAccid(String account, RequestMultiplyCallback<AccidResult> callback) {
        String accid = null;
        for (Map.Entry<String, String> entry :
                sAccountMap.entrySet()) {
            if (entry.getKey().equals(account)) {
                accid = entry.getValue();
            }
        }
        if (TextUtils.isEmpty(accid)) {
            execute(getService(ApiService.class).getAccid(account), new RequestMultiplyCallback<AccidResult>() {
                @Override
                public void onFail(BaseException e) {
                    callback.onFail(e);
                }

                @Override
                public void onSuccess(AccidResult accidResult) {
                    sAccountMap.put(account, accidResult.getAccid());
                    callback.onSuccess(accidResult);
                }
            });
        } else {
            AccidResult accidResult = new AccidResult();
            accidResult.setAccid(accid);
            callback.onSuccess(accidResult);
        }
    }

    @Override
    public void bindPlatformAccount(String account, String platformAccount, RequestMultiplyCallback<Object> callback) {
        execute(getService(ApiService.class).bindPlatformAccount(account, platformAccount), callback);
    }

    @Override
    public void getAccount(String accid, RequestMultiplyCallback<AccountResult> callback) {
        String account = null;
        for (Map.Entry<String, String> entry :
                sAccountMap.entrySet()) {
            if (entry.getValue().equals(accid)) {
                account = entry.getKey();
            }
        }
        if (TextUtils.isEmpty(account)) {
            execute(getService(ApiService.class).getAccount(accid), new RequestMultiplyCallback<AccountResult>() {
                @Override
                public void onFail(BaseException e) {
                    callback.onFail(e);
                }

                @Override
                public void onSuccess(AccountResult accountResult) {
                    sAccountMap.put(accountResult.getAccount(), accid);
                    callback.onSuccess(accountResult);
                }
            });
        } else {
            AccountResult accountResult = new AccountResult();
            accountResult.setAccount(account);
            callback.onSuccess(accountResult);
        }
    }

    @Override
    public void resetPwd(String account, String newPassword, String oldPassword, RequestMultiplyCallback<Object> callback) {
        newPassword = MD5.getStringMD5(newPassword);
        oldPassword = MD5.getStringMD5(oldPassword);
        execute(getService(ApiService.class).resetPassword(account, newPassword, oldPassword), callback);
    }

    @Override
    public void getVersionInfo(RequestMultiplyCallback<VersionInfo> callback) {
        execute(getService(ApiService.class).getVersionInfo("android"), callback);
    }

    @Override
    public void getCreateGroupAuth(String account, RequestMultiplyCallback<GroupAuth> callback) {
        execute(getService(ApiService.class).getCreateGroupAuth(account), callback);
    }

    @Override
    public void getGroupList(String account, RequestMultiplyCallback<List<GroupData>> callback) {
        execute(getService(ApiService.class).getGroupList(account), new RequestMultiplyCallback<List<GroupData>>() {
            @Override
            public void onFail(BaseException e) {
                if (callback != null)
                    callback.onFail(e);
            }

            @Override
            public void onSuccess(List<GroupData> groupData) {
                sGroupDatas.clear();
                sGroupDatas.addAll(groupData);
                if (callback == null) return;
                callback.onSuccess(groupData);
            }
        });
    }

    @Override
    public void addGroup(String account, String groupName, RequestMultiplyCallback<Object> callback) {
        execute(getService(ApiService.class).addGroup(account, groupName), callback);
    }

    @Override
    public void deleteGroup(String groupId, RequestMultiplyCallback<Object> callback) {
        execute(getService(ApiService.class).deleteGroup(groupId), callback);
    }

    @Override
    public void updateGroup(String groupName, String groupId, RequestMultiplyCallback<Object> callback) {
        execute(getService(ApiService.class).updateGroup(groupName, groupId), callback);
    }

    public static List<GroupData> getGroupDatasCache() {
        return sGroupDatas;
    }
}
