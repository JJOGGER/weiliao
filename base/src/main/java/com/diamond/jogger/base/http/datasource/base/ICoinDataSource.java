package com.diamond.jogger.base.http.datasource.base;

import com.diamond.jogger.base.entity.CoinDetailResponse;
import com.diamond.jogger.base.entity.IntoRecordResponse;
import com.diamond.jogger.base.entity.SignData;
import com.diamond.jogger.base.entity.SignResponse;
import com.diamond.jogger.base.http.basic.callback.RequestMultiplyCallback;

import java.util.List;

/**
 * Created by jogger on 2020/1/2
 * 描述：
 */
public interface ICoinDataSource {
    void getCoinDetail(String account, int page, RequestMultiplyCallback<CoinDetailResponse> callback);

    void applyTurnIn(String account, String amount, RequestMultiplyCallback<Object> callback);

    void applyTurnOut(String account, String amount, RequestMultiplyCallback<Object> callback);

    void getSignList(String account, RequestMultiplyCallback<List<SignData>> callback);

    void sign(String account, RequestMultiplyCallback<SignResponse> callback);


    void getTurnInList(String account,int page, RequestMultiplyCallback<IntoRecordResponse> callback);

    void getTurnOutList(String account, int page,RequestMultiplyCallback<IntoRecordResponse> callback);
}
