package com.diamond.jogger.base.http.basic.callback;


import com.diamond.jogger.base.http.basic.exception.base.BaseException;

/**
 * 作者：leavesC
 * 时间：2018/10/27 20:53
 * 描述：
 *
 *
 */
public interface RequestMultiplyCallback<T> extends RequestCallback<T> {

    void onFail(BaseException e);

}