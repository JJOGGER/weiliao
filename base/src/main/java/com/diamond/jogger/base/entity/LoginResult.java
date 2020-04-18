package com.diamond.jogger.base.entity;

/**
 * Created by jogger on 2019/12/5
 * 描述：
 */
public class LoginResult {
    private String accid;
    private String token;
    private String website;

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getAccid() {
        return accid;
    }

    public void setAccid(String accid) {
        this.accid = accid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
