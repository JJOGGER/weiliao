package com.diamond.jogger.base.http.basic.exception;


import com.diamond.jogger.base.http.basic.config.HttpCode;
import com.diamond.jogger.base.http.basic.exception.base.BaseException;

public class TokenInvalidException extends BaseException {

    public TokenInvalidException() {
        super(HttpCode.CODE_TOKEN_INVALID, "Token失效");
    }

    public TokenInvalidException(String msg) {
        super(HttpCode.CODE_TOKEN_INVALID, msg);
    }

}
