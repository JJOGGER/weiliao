package com.diamond.jogger.base.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jogger on 2019/12/18
 * 描述：
 */
public class RedPacketStatusResponse implements Serializable {//获取红包状态(acceptNo-未领完,acceptYes-已领完,acceptOver-已抢光,refund-已退款)
    private String redStatus;
    private String totalAmount;
    private int totalCount;

    private String redType;
    private List<RedPacketUserInfo> accountList;
    private String name;
    private int count;
    private String avatar;
    private String retainAmount;
    private String account;
    private String sendTime;
    private int retainCount;
    private String finishTime;

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public int getRetainCount() {
        return retainCount;
    }

    public void setRetainCount(int retainCount) {
        this.retainCount = retainCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getRedType() {
        return redType;
    }

    public void setRedType(String redType) {
        this.redType = redType;
    }

    public List<RedPacketUserInfo> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<RedPacketUserInfo> accountList) {
        this.accountList = accountList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRetainAmount() {
        return retainAmount;
    }

    public void setRetainAmount(String retainAmount) {
        this.retainAmount = retainAmount;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getRedStatus() {
        return redStatus;
    }

    public void setRedStatus(String redStatus) {
        this.redStatus = redStatus;
    }
}
