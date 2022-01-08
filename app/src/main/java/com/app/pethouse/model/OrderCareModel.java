package com.app.pethouse.model;

import java.util.ArrayList;
import java.util.Date;

public class OrderCareModel {
    private String key = "";
    private UserModel owener;
    private UserModel supplier;
    private double totalPrice;
    private int state;//1->booked, 2->maintenance, 0->waiting
    private ArrayList<Date> days = new ArrayList<>();
    private String description = "";
    private Date createdAt;

    public OrderCareModel() {
    }

    public OrderCareModel(String key, UserModel owener, UserModel supplier, double totalPrice, int state, ArrayList<Date> days, String description, Date createdAt) {
        this.key = key;
        this.owener = owener;
        this.supplier = supplier;
        this.totalPrice = totalPrice;
        this.state = state;
        this.days = days;
        this.description = description;
        this.createdAt = createdAt;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public UserModel getOwener() {
        return owener;
    }

    public void setOwener(UserModel owener) {
        this.owener = owener;
    }

    public UserModel getSupplier() {
        return supplier;
    }

    public void setSupplier(UserModel caregiver) {
        this.supplier = supplier;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public ArrayList<Date> getDays() {
        return days;
    }

    public void setDays(ArrayList<Date> days) {
        this.days = days;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
