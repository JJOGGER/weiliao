package com.diamond.jogger.base.entity;

/**
 * Created by jogger on 2019/12/7
 * 描述：
 */
public class VersionInfo {
    private String version;
    private String downloadUrl;
    private String updateContent;

    public String getUpdateContent() {
        return updateContent;
    }

    public void setUpdateContent(String updateContent) {
        this.updateContent = updateContent;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
