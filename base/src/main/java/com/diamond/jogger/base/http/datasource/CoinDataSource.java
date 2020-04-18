package com.diamond.jogger.base.http.datasource;

import com.diamond.jogger.base.entity.CoinDetailResponse;
import com.diamond.jogger.base.entity.IntoRecordResponse;
import com.diamond.jogger.base.entity.SignData;
import com.diamond.jogger.base.entity.SignResponse;
import com.diamond.jogger.base.http.basic.BaseRemoteDataSource;
import com.diamond.jogger.base.http.basic.callback.RequestMultiplyCallback;
import com.diamond.jogger.base.http.datasource.base.ICoinDataSource;
import com.diamond.jogger.base.http.service.ApiService;

import java.util.List;

/**
 * Created by jogger on 2020/1/2
 * 描述：
 */
public class CoinDataSource extends BaseRemoteDataSource implements ICoinDataSource {
    @Override
    public void getCoinDetail(String account, int page, RequestMultiplyCallback<CoinDetailResponse> callback) {
        execute(getService(ApiService.class).getCoinDetail(account, page), callback);
    }

    @Override
    public void applyTurnIn(String account, String amount, RequestMultiplyCallback<Object> callback) {
        execute(getService(ApiService.class).applyTurnIn(account, amount), callback);
    }

    @Override
    public void applyTurnOut(String account, String amount, RequestMultiplyCallback<Object> callback) {
        execute(getService(ApiService.class).applyTurnOut(account, amount), callback);
    }

    @Override
    public void getSignList(String account, RequestMultiplyCallback<List<SignData>> callback) {
        execute(getService(ApiService.class).getSignList(account), callback);
    }

    @Override
    public void sign(String account, RequestMultiplyCallback<SignResponse> callback) {
        execute(getService(ApiService.class).sign(account), callback);
    }

    @Override
    public void getTurnInList(String account,int page, RequestMultiplyCallback<IntoRecordResponse> callback) {
        execute(getService(ApiService.class).getTurnInList(account,page), callback);
    }

    @Override
    public void getTurnOutList(String account,int page, RequestMultiplyCallback<IntoRecordResponse> callback) {
        execute(getService(ApiService.class).getTurnOutList(account,page), callback);
    }
}
