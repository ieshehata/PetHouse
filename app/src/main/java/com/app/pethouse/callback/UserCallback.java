package com.app.pethouse.callback;

import com.app.pethouse.model.UserModel;

import java.util.ArrayList;

public interface UserCallback {
    void onSuccess(ArrayList<UserModel> users);

    void onFail(String error);
}




    