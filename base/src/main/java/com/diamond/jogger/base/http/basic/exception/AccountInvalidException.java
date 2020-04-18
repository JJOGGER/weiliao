package com.diamond.jogger.base.http.basic.exception;


import com.diamond.jogger.base.http.basic.config.HttpCode;
import com.diamond.jogger.base.http.basic.exception.base.BaseException;

/**
 * 作者：leavesC
 * 时间：2018/10/27 8:11
 * 描述：
 */
public class AccountInvalidException extends BaseException {

    public AccountInvalidException() {
        super(HttpCode.CODE_ACCOUNT_INVALID, "账号或者密码错误");
    }

}
