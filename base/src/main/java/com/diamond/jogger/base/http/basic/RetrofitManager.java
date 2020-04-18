package com.diamond.jogger.base.http.basic;


import com.diamond.jogger.base.BuildConfig;
import com.diamond.jogger.base.http.HttpLoggingInterceptor;
import com.diamond.jogger.base.http.basic.config.HttpCode;
import com.diamond.jogger.base.http.basic.config.HttpConfig;
import com.diamond.jogger.base.http.basic.exception.ServerResultException;
import com.diamond.jogger.base.http.basic.exception.TokenInvalidException;
import com.diamond.jogger.base.http.interceptor.FilterInterceptor;
import com.diamond.jogger.base.http.interceptor.HeaderInterceptor;
import com.diamond.jogger.base.http.interceptor.HttpInterceptor;
import com.diamond.jogger.base.http.interceptor.RetryIntercepter;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 作者：jogger
 * 时间：2019/1/8 11:02
 * 描述：
 */
public class RetrofitManager {
    private static final int READ_TIMEOUT = 10000;
    private static final int WRITE_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 10000;
    private final Map<String, Object> mServiceMap = new ConcurrentHashMap<>();
    private static RetrofitManager sRetrofitManager;

    private RetrofitManager() {

    }

    public static RetrofitManager getInstance() {
        if (sRetrofitManager == null)
            synchronized (RetrofitManager.class) {
                if (sRetrofitManager == null)
                    sRetrofitManager = new RetrofitManager();
            }
        return sRetrofitManager;
    }

    private Retrofit createRetrofit(String url) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(new RetryIntercepter(3))
                .addInterceptor(new HttpInterceptor())
                .addInterceptor(new HeaderInterceptor())
                .addInterceptor(new FilterInterceptor())
                .retryOnConnectionFailure(true);
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(httpLoggingInterceptor);
        }
//        OkHttpClient client = builder.build();
        final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    X509Certificate[] chain,
                    String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    X509Certificate[] chain,
                    String authType) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }};
        OkHttpClient client;
        try {
        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            client= builder.sslSocketFactory(sslContext.getSocketFactory())
                .hostnameVerifier((hostname, session) -> true).build();
        } catch (Exception e) {
            e.printStackTrace();
            client=builder.build();
        }
        return new Retrofit.Builder()
                .client(client)
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    <T> T getService(Class<T> clz) {
        return getService(clz, HttpConfig.getDynamicUrl());
    }

    @SuppressWarnings("unchecked")
    <T> T getService(Class<T> clz, String host) {
        T value;
        try {
            if (mServiceMap.containsKey(host)) {
                Object obj = mServiceMap.get(host);
                if (obj == null) {
                    value = createRetrofit(host).create(clz);
                    mServiceMap.put(host, value);
                } else {
                    value = (T) obj;
                }
            } else {
                value = createRetrofit(host).create(clz);
                mServiceMap.put(host, value);
            }
        } catch (NullPointerException e) {
            value = createRetrofit(host).create(clz);
            mServiceMap.put(host, value);
        }

        return value;
    }

    <T> ObservableTransformer<BaseResponse<T>, T> applySchedulers() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(result -> {
                    switch (result.getCode()) {
                        case HttpCode.CODE_SUCCESS:
                            String msg = result.getMessage();
                            return createData(result.getData());
                        case HttpCode.CODE_TOKEN_INVALID: {
//                            Intent intent = new Intent(Utils.getApp(),
// SplashActivity.class);
                            throw new TokenInvalidException(result.getMessage());
                        }
                        default: {
//                            throw new ServerResultException(result.getCode(), result.getMessage());
                            return createError(result.getCode(), result.getMessage());
                        }
                    }
                });
    }

    private <T> Observable<T> createData(T t) {
        return Observable.create(emitter -> {
            try {
                emitter.onNext(t);
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

    private <T> Observable<T> createError(int code, String msg) {
        return Observable.create(emitter -> {
            emitter.onError(new ServerResultException(code, msg));
        });
    }
}
