package com.app.pethouse.callback;

import com.app.pethouse.model.SuppliersReqModel;

import java.util.ArrayList;

public interface SupplierReqCallback {
    void onSuccess(ArrayList<SuppliersReqModel> supplierReqs);

    void onFail(String error);
}




    