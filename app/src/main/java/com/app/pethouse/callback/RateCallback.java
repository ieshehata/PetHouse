package com.app.pethouse.callback;

import com.app.pethouse.model.RateModel;

import java.util.ArrayList;

public interface RateCallback {
    void onSuccess(ArrayList<RateModel> rates);

    void onFail(String error);
}




    