package com.app.pethouse.callback;

public interface BooleanCallback {
    void onSuccess(boolean bool);

    void onFail(String error);
}
