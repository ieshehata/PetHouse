package com.app.pethouse.callback;

import com.app.pethouse.model.ConversationModel;

import java.util.ArrayList;

public interface ConversationCallback {
    void onSuccess(ArrayList<ConversationModel> conversations);

    void onFail(String error);
}




    