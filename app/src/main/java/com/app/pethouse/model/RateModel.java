package com.app.pethouse.model;

import java.util.Date;

public class RateModel {
    private String key;
    private UserModel fromOwener;
    private UserModel toSupplier;
    private int rate;
    private String comment;
    private Date createdAt;
    private String userKey;

    public RateModel() {
    }

    public RateModel(String key, UserModel fromOwener, UserModel toCaregiver, int rate, String comment, Date createdAt, String userKey) {
        this.key = key;
        this.fromOwener = fromOwener;
        this.toSupplier = toCaregiver;
        this.rate = rate;
        this.comment = comment;
        this.createdAt = createdAt;
        this.userKey = userKey;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public UserModel getFromOwener() {
        return fromOwener;
    }

    public void setFromOwener(UserModel fromOwener) {
        this.fromOwener = fromOwener;
    }

    public UserModel getToSupplier() {
        return toSupplier;
    }

    public void setToSupplier(UserModel toCaregiver) {
        this.toSupplier = toCaregiver;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }
}
