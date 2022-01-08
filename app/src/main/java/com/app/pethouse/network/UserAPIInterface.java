package com.app.pethouse.network;

import com.app.pethouse.model.NotificationResponseModel;
import com.app.pethouse.model.SendNotificationModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserAPIInterface {
    @POST("send")
    Call<NotificationResponseModel> sendNotification(@Body SendNotificationModel notification);
}
