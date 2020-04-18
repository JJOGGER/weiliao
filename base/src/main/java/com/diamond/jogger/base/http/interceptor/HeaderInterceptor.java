package com.diamond.jogger.base.http.interceptor;


import android.text.TextUtils;

import com.diamond.jogger.base.http.basic.config.HttpConfig;

import java.io.IOException;

import io.reactivex.annotations.NonNull;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 作者：leavesC
 * 时间：2018/10/27 7:34
 * 描述：
 */
public class HeaderInterceptor implements Interceptor {

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();

        Request.Builder builder = originalRequest.newBuilder();
        Request.Builder requestBuilder = builder
                .addHeader("Accept-Encoding", "gzip")
                .addHeader("Accept", "application/x-www-form-urlencoded")
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
//                .addHeader(HttpConfig.API_KEY, HttpConfig.API_KEY_VALUE)
//                .addHeader("phoneCode",String.valueOf(1))
                .method(originalRequest.method(), originalRequest.body());
        return chain.proceed(requestBuilder.build());
    }

}
