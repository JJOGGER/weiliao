package com.diamond.jogger.base.http.basic.exception;


import com.diamond.jogger.base.http.basic.exception.base.BaseException;

public class ServerResultException extends BaseException {

    public ServerResultException(int code, String message) {
        super(code, message);
    }

}
