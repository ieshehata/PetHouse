package com.app.pethouse.model;

import com.google.gson.annotations.SerializedName;

public class NotificationResponseModel {
    @SerializedName("success")
    public int success;
    @SerializedName("failure")
    public int failure;
}
