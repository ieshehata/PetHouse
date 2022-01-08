package com.app.pethouse.model;

import java.util.Date;

public class SuppliersReqModel {
    private String key;
    private UserModel supplier;
    private int state; // 0->Unseen, 1->Accepted, -1->Rejected
    private Date createdAt;

    public SuppliersReqModel() {
    }

    public SuppliersReqModel(String key, UserModel supplier, int state, Date createdAt) {
        this.key = key;
        this.supplier = supplier;
        this.state = state;
        this.createdAt = createdAt;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public UserModel getSupplier() {
        return supplier;
    }

    public void setSupplier(UserModel caregiver) {
        this.supplier = caregiver;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public static class  ClassNew {
        public void func() {
        }
    }
}
