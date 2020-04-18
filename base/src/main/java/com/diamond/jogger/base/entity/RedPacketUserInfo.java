package com.diamond.jogger.base.entity;

import java.io.Serializable;

/**
 * Created by jogger on 2019/12/18
 * 描述：
 */
public class RedPacketUserInfo implements Serializable {
    private String account;
    private int amount;
    private String avatar;
    private boolean luckFlag;
    private String name;
    private String receiveTime;
    private String amountYuan;

    public String getAmountYuan() {
        return amountYuan;
    }

    public void setAmountYuan(String amountYuan) {
        this.amountYuan = amountYuan;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isLuckFlag() {
        return luckFlag;
    }

    public void setLuckFlag(boolean luckFlag) {
        this.luckFlag = luckFlag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(String receiveTime) {
        this.receiveTime = receiveTime;
    }
}
