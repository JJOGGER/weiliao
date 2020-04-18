package com.netease.nim.demo.config;

public class DemoServers {

    //
    // 好友列表信息服务器地址
    //www.foolishsister.com
//    private static final String API_SERVER_TEST = "http://apptest.netease.im/api/"; // 测试
//    private static final String API_SERVER = "https://app.netease.im/api/"; // 线上
    private static final String API_SERVER_TEST = "http://www.foolishsister.com/im/"; // 测试
    private static final String API_SERVER = "http://www.foolishsister.com/im/"; // 线上
    public static String apiServer() {
        return ServerConfig.testServer() ? API_SERVER_TEST : API_SERVER;
    }

    public static String chatRoomAPIServer() {
        return apiServer() + "chatroom/";
    }
}
