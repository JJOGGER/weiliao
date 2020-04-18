package com.diamond.jogger.base.http.basic;


import android.accounts.AccountsException;

import com.diamond.jogger.base.http.basic.callback.RequestCallback;
import com.diamond.jogger.base.http.basic.callback.RequestMultiplyCallback;
import com.diamond.jogger.base.http.basic.config.HttpCode;
import com.diamond.jogger.base.http.basic.exception.ConnectionException;
import com.diamond.jogger.base.http.basic.exception.base.BaseException;

import io.reactivex.observers.DisposableObserver;

public class BaseSubscriber<T> extends DisposableObserver<T> {

    private RequestCallback<T> mRequestCallback;

    private RequestMultiplyCallback<T> mRequestMultiplyCallback;

    public BaseSubscriber(RequestCallback<T> requestCallback) {
        this.mRequestCallback = requestCallback;
    }

    public BaseSubscriber(RequestMultiplyCallback<T>
                                  requestMultiplyCallback) {
        this.mRequestMultiplyCallback = requestMultiplyCallback;
    }

    @Override
    public void onNext(T t) {
        if (mRequestCallback != null) {
            mRequestCallback.onSuccess(t);
        } else if (mRequestMultiplyCallback != null) {
            mRequestMultiplyCallback.onSuccess(t);
        }
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        if (mRequestMultiplyCallback != null) {
            if (e instanceof BaseException) {
                mRequestMultiplyCallback.onFail((BaseException) e);
            } else {
                mRequestMultiplyCallback.onFail(new BaseException(HttpCode.CODE_UNKNOWN, e
                        .getMessage()));
            }
        } else {
            if (e instanceof AccountsException) {

            } else if (e instanceof ConnectionException) {
            } else {
            }
        }
    }

    @Override
    public void onComplete() {

    }

}