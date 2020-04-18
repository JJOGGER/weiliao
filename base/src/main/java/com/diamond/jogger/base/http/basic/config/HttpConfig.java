package com.diamond.jogger.base.http.basic.config;

/**
 * 作者：jogger
 * 时间：2019/1/8 11:11
 * 描述：
 */
public class HttpConfig {
    public static final String KEY = "key";

    public static final String HTTP_REQUEST_TYPE_KEY = "requestType";
    public static final String HTTP_REQUEST_WEATHER = "weather";
    public static final String HTTP_REQUEST_QR_CODE = "qrCode";
    public static final String HTTP_REQUEST_ID_CARD = "idCard";

    public static final String KEY_ID_CARD = "0b1a6413e51ee152045cd7fe7355b81b";
    public static final String KEY_QR_CODE = "527d0174f4269329f38003575b27a769";
    public static final String KEY_WEATHER = "afc28ae28c6f1b520dab5d1ed537f6c0";

    /**
     * 获取动态域名网址
     */
//    public static String getDynamicUrl() {
//        return "http://123.57.224.49:9600/";
//    }
    public static String getDynamicUrl() {
        return "http://yunxin777.com:8080/im/";
//        return "http://wl2013.com:8082/im/";
    }
}
