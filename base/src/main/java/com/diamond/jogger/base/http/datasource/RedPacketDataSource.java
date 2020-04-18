package com.diamond.jogger.base.http.datasource;

import com.diamond.jogger.base.entity.RedPacketStatusResponse;
import com.diamond.jogger.base.entity.SendRedPacketResponse;
import com.diamond.jogger.base.http.basic.BaseRemoteDataSource;
import com.diamond.jogger.base.http.basic.callback.RequestMultiplyCallback;
import com.diamond.jogger.base.http.datasource.base.IRedPacketDataSource;
import com.diamond.jogger.base.http.service.ApiService;

/**
 * Created by jogger on 2019/12/18
 * 描述：
 */
public class RedPacketDataSource extends BaseRemoteDataSource implements IRedPacketDataSource {
    @Override
    public void sendPointRedPacket(String account, String amount, String avatar, String greetings,String name,String receiverAccount,  RequestMultiplyCallback<SendRedPacketResponse> callback) {
        execute(getService(ApiService.class).sendPointRedPacket(account, amount,avatar, greetings,name,receiverAccount), callback);
    }

    @Override
    public void sendGroupRedPacket(String account, String amount, String avatar, int count, String greetings, String groupId, String name, String redType, RequestMultiplyCallback<SendRedPacketResponse> callback) {
        execute(getService(ApiService.class).sendGroupRedPacket(account, Integer.valueOf(amount), avatar, count, greetings, groupId, name, redType), callback);
    }

    @Override
    public void openPointRedPacket(String account, String avatar, String name, String redId, RequestMultiplyCallback<RedPacketStatusResponse> callback) {
        execute(getService(ApiService.class).openPointRedPacket(account, avatar, name, redId), callback);
    }

    @Override
    public void getRedPacketStatus(String account, String redId, RequestMultiplyCallback<RedPacketStatusResponse> callback) {
        execute(getService(ApiService.class).getRedPacketStatus(account,  redId), callback);
    }
}
