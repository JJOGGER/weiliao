package com.diamond.jogger.base.http.basic.exception;


import com.diamond.jogger.base.http.basic.config.HttpCode;
import com.diamond.jogger.base.http.basic.exception.base.BaseException;

public class ForbiddenException extends BaseException {

    public ForbiddenException() {
        super(HttpCode.CODE_PARAMETER_INVALID, "404错误");
    }

}