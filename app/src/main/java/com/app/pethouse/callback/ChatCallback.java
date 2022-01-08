package com.app.pethouse.callback;

import com.app.pethouse.model.ChatModel;

import java.util.ArrayList;

public interface ChatCallback {
    void onSuccess(ArrayList<ChatModel> chats);

    void onFail(String error);
}




    