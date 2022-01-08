package com.app.pethouse.model;

public class NotificationModel {
    private String title;
    private String text;
    private String call;

    public NotificationModel() {
    }

    public NotificationModel(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public NotificationModel(String title, String text, String call) {
        this.title = title;
        this.text = text;
        this.call = call;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCall() {
        return call;
    }

    public void setCall(String call) {
        this.call = call;
    }
}
