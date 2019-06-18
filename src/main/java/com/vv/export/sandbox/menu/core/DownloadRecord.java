package com.vv.export.sandbox.menu.core;

public class DownloadRecord {

    private int number;
    private String title;
    private String downloadTimeStamp;
    private long downloadTimeStampLong;
    private boolean commitStatus;

    public DownloadRecord() {
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDownloadTimeStamp(String downloadTimeStamp) {
        this.downloadTimeStamp = downloadTimeStamp;
    }

    public void setCommitStatus(boolean commitStatus) {
        this.commitStatus = commitStatus;
    }

    public long getDownloadTimeStampLong() {
        return downloadTimeStampLong;
    }

    public void setDownloadTimeStampLong(long downloadTimeStampLong) {
        this.downloadTimeStampLong = downloadTimeStampLong;
    }
}