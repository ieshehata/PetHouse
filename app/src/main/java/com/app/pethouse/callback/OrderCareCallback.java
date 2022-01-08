package com.app.pethouse.callback;

import com.app.pethouse.model.OrderCareModel;

import java.util.ArrayList;

public interface OrderCareCallback {
    void onSuccess(ArrayList<OrderCareModel> orderCares);

    void onFail(String error);
}




    