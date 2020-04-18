package com.diamond.jogger.base.http.basic;


import com.diamond.jogger.base.http.basic.callback.RequestCallback;
import com.diamond.jogger.base.http.basic.callback.RequestMultiplyCallback;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 作者：jogger
 * 时间：2019/1/8 11:16
 * 描述：
 */
public abstract class BaseRemoteDataSource {
    private CompositeDisposable mCompositeDisposable;

    public BaseRemoteDataSource() {
        this.mCompositeDisposable = new CompositeDisposable();
    }

    protected <T> T getService(Class<T> clz) {
        return RetrofitManager.getInstance().getService(clz);
    }

    protected <T> T getService(Class<T> clz, String host) {
        return RetrofitManager.getInstance().getService(clz, host);
    }

    private <T> ObservableTransformer<BaseResponse<T>, T> applySchedulers() {
        return RetrofitManager.getInstance().applySchedulers();
    }

    protected void execute(Observable observable, Observer observer) {
        execute(observable, observer, true, true);
    }

    protected <T> void execute(Observable observable, RequestCallback<T> callback) {
        execute(observable, new BaseSubscriber<>(callback), true, true);
    }

    protected <T> void execute(Observable observable, RequestMultiplyCallback<T> callback) {
        execute(observable, new BaseSubscriber<>(callback), true, true);
    }

    @SuppressWarnings("unchecked")
    private void execute(Observable observable, Observer observer, boolean withLoading, boolean dismissWhenFinally) {
        Disposable disposable = (Disposable) observable
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(applySchedulers())
//                .compose(loadingTransformer(withLoading, dismissWhenFinally))
                .subscribeWith(observer);
        addDisposable(disposable);
    }

    private void addDisposable(Disposable disposable) {
        mCompositeDisposable.add(disposable);
    }

}
