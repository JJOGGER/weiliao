package com.diamond.jogger.base.http.interceptor;


import android.text.TextUtils;
import android.util.Log;

import com.diamond.jogger.base.http.basic.BaseResponse;
import com.diamond.jogger.base.http.basic.exception.ConnectionException;
import com.diamond.jogger.base.http.basic.exception.ForbiddenException;
import com.diamond.jogger.base.http.basic.exception.ResultInvalidException;
import com.diamond.jogger.base.utils.GsonUtil;

import java.io.IOException;

import io.reactivex.annotations.NonNull;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;

/**
 * 作者：leavesC
 * 时间：2018/10/25 21:16
 * 描述：
 */
public class HttpInterceptor implements Interceptor {

    public HttpInterceptor() {
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request original = chain.request();
        Request request = original.newBuilder()
                .header("User-Agent", "Your-App-Name")
                .header("Accept", "application/json")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .method(original.method(), original.body())
                .build();
        Response originalResponse;
        try {
            originalResponse = chain.proceed(request);
        } catch (Exception e) {
            Log.e(HttpInterceptor.class.getSimpleName(), "---------e:" + e.getMessage() + ":" + request.url());
            throw new ConnectionException();
        }
        if (originalResponse.code() != 200) {
            if (originalResponse.code() == 404) {
                throw new ForbiddenException();
            }
            throw new ResultInvalidException();
        }
        assert originalResponse.body() != null;
        BufferedSource source = originalResponse.body().source();
        source.request(Integer.MAX_VALUE);
        String byteString = source.buffer().snapshot().utf8();
        BaseResponse response = GsonUtil.fromJson(byteString, BaseResponse.class);
        if ((response.getData() instanceof String && TextUtils.isEmpty((String) response.getData())) && response.getCode() != 200) {
            response.setData(null);
            byteString = GsonUtil.toJson(response);
        }
        ResponseBody responseBody = ResponseBody.create(null, byteString);
        return originalResponse.newBuilder().body(responseBody).build();
    }

}
