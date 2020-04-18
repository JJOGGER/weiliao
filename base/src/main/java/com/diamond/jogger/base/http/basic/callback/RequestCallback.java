package com.diamond.jogger.base.http.basic.callback;

/**
 * 作者：leavesC
 * 时间：2018/10/27 20:53
 * 描述：
 *
 *
 */
public interface RequestCallback<T> {

    void onSuccess(T t);

}
