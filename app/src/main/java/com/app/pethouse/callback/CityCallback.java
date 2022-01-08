package com.app.pethouse.callback;

import com.app.pethouse.model.CityModel;

import java.util.ArrayList;

public interface CityCallback {
    void onSuccess(ArrayList<CityModel> citys);

    void onFail(String error);
}




    