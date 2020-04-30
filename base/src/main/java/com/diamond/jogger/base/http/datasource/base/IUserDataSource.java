package com.diamond.jogger.base.http.datasource.base;


import com.diamond.jogger.base.entity.AccidResult;
import com.diamond.jogger.base.entity.AccountResult;
import com.diamond.jogger.base.entity.GroupAuth;
import com.diamond.jogger.base.entity.GroupData;
import com.diamond.jogger.base.entity.LoginResult;
import com.diamond.jogger.base.entity.SimpleUserInfo;
import com.diamond.jogger.base.entity.VersionInfo;
import com.diamond.jogger.base.http.basic.callback.RequestMultiplyCallback;

import java.util.List;

public interface IUserDataSource {
    void isSmsSwitch(RequestMultiplyCallback<Object> callback);

    void getSmsCode(String phone, RequestMultiplyCallback<Object> callback);

    void checkSmsCode(String code, String phone, RequestMultiplyCallback<Object> callback);

    void register(String phone,String account, String name, String password, RequestMultiplyCallback<Object> callback);

    void login(String account, String password, RequestMultiplyCallback<LoginResult> callback);

    void getUserInfo(String account, RequestMultiplyCallback<SimpleUserInfo> callback);

    void getAccid(String account, RequestMultiplyCallback<AccidResult> callback);

    void bindPlatformAccount(String account, String platformAccount, RequestMultiplyCallback<Object> callback);

    void getAccount(String accid, RequestMultiplyCallback<AccountResult> callback);

    void resetPwd(String account, String newPassword, String oldPassword, RequestMultiplyCallback<Object> callback);

    void getVersionInfo(RequestMultiplyCallback<VersionInfo> callback);

    void getCreateGroupAuth(String account, RequestMultiplyCallback<GroupAuth> callback);

    void getGroupList(String account, RequestMultiplyCallback<List<GroupData>> callback);

    void addGroup(String account, String groupName, RequestMultiplyCallback<Object> callback);

    void deleteGroup(String groupId, RequestMultiplyCallback<Object> callback);

    void updateGroup(String groupName, String groupId, RequestMultiplyCallback<Object> callback);

}
