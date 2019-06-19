package com.vv.export.sandbox.menu.core;

public class HistoryRecord {

    private int number;
    private String title;
    private String visitTimeStamp;

    public HistoryRecord() {
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

    public String getVisitTimeStamp() {
        return visitTimeStamp;
    }

    public void setVisitTimeStamp(String visitTimeStamp) {
        this.visitTimeStamp = visitTimeStamp;
    }
}