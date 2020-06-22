package com.diamond.jogger.base.http.service;


import com.diamond.jogger.base.entity.AccidResult;
import com.diamond.jogger.base.entity.AccountResult;
import com.diamond.jogger.base.entity.CoinDetailResponse;
import com.diamond.jogger.base.entity.GroupAuth;
import com.diamond.jogger.base.entity.GroupData;
import com.diamond.jogger.base.entity.IntoRecordResponse;
import com.diamond.jogger.base.entity.LoginResult;
import com.diamond.jogger.base.entity.MessageSwitch;
import com.diamond.jogger.base.entity.RedPacketStatusResponse;
import com.diamond.jogger.base.entity.SendRedPacketResponse;
import com.diamond.jogger.base.entity.SignData;
import com.diamond.jogger.base.entity.SignResponse;
import com.diamond.jogger.base.entity.SimpleUserInfo;
import com.diamond.jogger.base.entity.VersionInfo;
import com.diamond.jogger.base.http.basic.BaseResponse;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 作者：jogger
 * 时间：2019/1/8 10:46
 * 描述：
 */
public interface ApiService {

    @POST("user/message/switch")
    Observable<BaseResponse<MessageSwitch>> isSmsSwitch();

    @POST("user/send/code")
    Observable<BaseResponse<Object>> sendSmsCode(@Query("phone") String phone);

    @POST("user/verify/code")
    Observable<BaseResponse<Object>> checkSmsCode(@Query("code") String code,
                                                  @Query("phone") String phone);

    @POST("user/create")
    Observable<BaseResponse<Object>> register(@Query("phone") String phone,
                                              @Query("account") String account,
                                              @Query("name") String name,
                                              @Query("password") String password);

    @POST("user/login")
    Observable<BaseResponse<LoginResult>> login(@Query("account") String account,
                                                @Query("password") String password,
                                                @Query("websiteType") int website);


    @POST("user/info/get")
    Observable<BaseResponse<SimpleUserInfo>> getUserInfo(@Query("account") String account,
                                                         @Query("websiteType") int website);

    @POST("user/account/get")
    Observable<BaseResponse<AccountResult>> getAccount(@Query("accid") String accid);

    @POST("user/accid/get")
    Observable<BaseResponse<AccidResult>> getAccid(@Query("account") String account);

    @POST("user/platform/update")
    Observable<BaseResponse<Object>> bindPlatformAccount(@Query("account") String account,
                                                         @Query("platformAccount") String platformAccount);

    @POST("user/password/update")
    Observable<BaseResponse<Object>> resetPassword(@Query("account") String account,
                                                   @Query("newPassword") String newPassword,
                                                   @Query("oldPassword") String oldPassword);

    @POST("user/app/version")
    Observable<BaseResponse<VersionInfo>> getVersionInfo(@Query("appType") String appType);

    @POST("user/group/auth")
    Observable<BaseResponse<GroupAuth>> getCreateGroupAuth(@Query("account") String account);

    @POST("red/send/single")
    Observable<BaseResponse<SendRedPacketResponse>> sendPointRedPacket(@Query("account") String account,
                                                                       @Query("amount") String amount,
                                                                       @Query("avatar") String avatar,
                                                                       @Query("greetings") String greetings,
                                                                       @Query("name") String name,
                                                                       @Query("receiverAccount") String receiverAccount);

    //红包类型(groupLuck, groupNormal)
    @POST("red/send/group")
    Observable<BaseResponse<SendRedPacketResponse>> sendGroupRedPacket(@Query("account") String account,
                                                                       @Query("amount") int amount,
                                                                       @Query("avatar") String avatar,
                                                                       @Query("count") int count,
                                                                       @Query("greetings") String greetings,
                                                                       @Query("groupId") String groupId,
                                                                       @Query("name") String name,
                                                                       @Query("redType") String redType);

    @POST("red/receive/group")
    Observable<BaseResponse<RedPacketStatusResponse>> openPointRedPacket(@Query("account") String account,
                                                                         @Query("avatar") String avatar,
                                                                         @Query("name") String name,
                                                                         @Query("redId") String redId);


    @POST("red/status/get")
    Observable<BaseResponse<RedPacketStatusResponse>> getRedPacketStatus(@Query("account") String account,
                                                                         @Query("redId") String redId);

    @POST("coin/page")
    Observable<BaseResponse<CoinDetailResponse>> getCoinDetail(@Query("account") String account, @Query("page") int page);

    @POST("coin/into/apply")
    Observable<BaseResponse<Object>> applyTurnIn(@Query("account") String account,
                                                 @Query("amount") String amount);

    @POST("coin/out/apply")
    Observable<BaseResponse<Object>> applyTurnOut(@Query("account") String account,
                                                  @Query("amount") String amount);

    @POST("coin/sign/list")
    Observable<BaseResponse<List<SignData>>> getSignList(@Query("account") String account);

    @POST("coin/sign")
    Observable<BaseResponse<SignResponse>> sign(@Query("account") String account);


    @POST("coin/into/list")
    Observable<BaseResponse<IntoRecordResponse>> getTurnInList(@Query("account") String account, @Query("page") int page);

    @POST("coin/out/list")
    Observable<BaseResponse<IntoRecordResponse>> getTurnOutList(@Query("account") String account, @Query("page") int page);

    @POST("friend/group/add")
    Observable<BaseResponse<Object>> addGroup(@Query("account") String account, @Query("groupName") String groupName);

    @POST("friend/group/delete")
    Observable<BaseResponse<Object>> deleteGroup(@Query("id") String id);

    @POST("friend/group/list")
    Observable<BaseResponse<List<GroupData>>> getGroupList(@Query("account") String account);

    @POST("friend/group/update")
    Observable<BaseResponse<Object>> updateGroup(@Query("groupName") String groupName, @Query("id") String id
    );

}