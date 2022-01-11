package com.app.pethouse.callback;

import com.app.pethouse.model.TypeModel;

import java.util.ArrayList;

public interface TypeCallback {
    void onSuccess(ArrayList<TypeModel> types);

    void onFail(String error);
}




    