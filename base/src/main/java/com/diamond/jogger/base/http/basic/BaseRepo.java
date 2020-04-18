package com.diamond.jogger.base.http.basic;

/**
 * 作者：jogger
 * 时间：2019/1/8 11:13
 * 描述：
 */
public class BaseRepo<T> {
    protected T mRemoteDataSource;

    public BaseRepo(T remoteDataSource) {
        mRemoteDataSource = remoteDataSource;
    }
}
