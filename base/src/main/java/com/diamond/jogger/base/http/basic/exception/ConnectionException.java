package com.diamond.jogger.base.http.basic.exception;


import com.diamond.jogger.base.http.basic.config.HttpCode;
import com.diamond.jogger.base.http.basic.exception.base.BaseException;

public class ConnectionException extends BaseException {

    public ConnectionException() {
        super(HttpCode.CODE_CONNECTION_FAILED,"网络连接失败");
    }

}
