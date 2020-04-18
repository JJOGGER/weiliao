package com.netease.nim.demo.session.extension;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by jogger on 2019/12/25
 * 描述：
 */
public class CustomNotificationAttachment extends CustomAttachment {
    private String content;
    private String fromAccount;
    private int type;
    private static final String CONTENT = "content";
    private static final String FROM_ACCOUNT = "from_account";

    public void setType(int type) {
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int getType() {
        return type;
    }

    public CustomNotificationAttachment() {
        super(7);
    }

    @Override
    protected void parseData(JSONObject data) {
        content = data.getString(CONTENT);
        fromAccount = data.getString(FROM_ACCOUNT);
    }

    @Override
    protected JSONObject packData() {
        JSONObject data = new JSONObject();
        data.put(CONTENT, content);
        data.put(FROM_ACCOUNT, fromAccount);
        return data;
    }

    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public String getContent() {
        return content;
    }
}
