package com.vv.export.sandbox.menu.core;

public class DownloadRecord {

    private int number;
    private String title;
    private String downloadTimeStamp;
    private long downloadTimeStampLong;
    private boolean commitStatus;

    public DownloadRecord() {
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDownloadTimeStamp() {
        return downloadTimeStamp;
    }

    public void setDownloadTimeStamp(String downloadTimeStamp) {
        this.downloadTimeStamp = downloadTimeStamp;
    }

    public long getDownloadTimeStampLong() {
        return downloadTimeStampLong;
    }

    public void setDownloadTimeStampLong(long downloadTimeStampLong) {
        this.downloadTimeStampLong = downloadTimeStampLong;
    }

    public boolean isCommitStatus() {
        return commitStatus;
    }

    public void setCommitStatus(boolean commitStatus) {
        this.commitStatus = commitStatus;
    }
}