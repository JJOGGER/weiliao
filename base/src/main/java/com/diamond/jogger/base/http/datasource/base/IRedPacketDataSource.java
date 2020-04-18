package com.diamond.jogger.base.http.datasource.base;

import com.diamond.jogger.base.entity.RedPacketStatusResponse;
import com.diamond.jogger.base.entity.SendRedPacketResponse;
import com.diamond.jogger.base.http.basic.callback.RequestMultiplyCallback;

/**
 * Created by jogger on 2019/12/18
 * 描述：
 */
public interface IRedPacketDataSource {

    void sendPointRedPacket(String account, String amount, String avatar, String greetings,String name,String receiverAccount,  RequestMultiplyCallback<SendRedPacketResponse> callback);

    void sendGroupRedPacket(String account, String amount, String avatar, int count, String greetings, String groupId, String name, String redType, RequestMultiplyCallback<SendRedPacketResponse> callback);

    void openPointRedPacket(String account, String avatar, String name, String redId, RequestMultiplyCallback<RedPacketStatusResponse> callback);

    void getRedPacketStatus(String account, String redId, RequestMultiplyCallback<RedPacketStatusResponse> callback);
}
