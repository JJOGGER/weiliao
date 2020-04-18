package com.diamond.jogger.base.http.basic.exception;


import com.diamond.jogger.base.http.basic.config.HttpCode;
import com.diamond.jogger.base.http.basic.exception.base.BaseException;

public class DeviceInvalidException extends BaseException {

    public DeviceInvalidException() {
        super(HttpCode.CODE_DEVICE_INVALID, "新设备登录");
    }

    public DeviceInvalidException(String msg) {
        super(HttpCode.CODE_DEVICE_INVALID, msg);
    }

}
