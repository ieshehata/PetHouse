package com.app.pethouse.callback;

import com.app.pethouse.model.GovernorateModel;

import java.util.ArrayList;

public interface GovernorateCallback {
    void onSuccess(ArrayList<GovernorateModel> governorates);

    void onFail(String error);
}




    