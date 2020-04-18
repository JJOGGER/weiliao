package com.diamond.jogger.base.entity;

/**
 * Created by jogger on 2019/12/23
 * 描述：
 */
public class SimpleUserInfo {
    private boolean bindFlag;
    private String amount;
    private String website;
    private String platformAccount;


    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public boolean isBindFlag() {
        return bindFlag;
    }

    public void setBindFlag(boolean bindFlag) {
        this.bindFlag = bindFlag;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPlatformAccount() {
        return platformAccount;
    }

    public void setPlatformAccount(String platformAccount) {
        this.platformAccount = platformAccount;
    }
}
