package com.app.pethouse.model;

import java.util.ArrayList;

public class ChatModel {
    private String key;
    private ArrayList<MessageModel> messages = new ArrayList<>();

    public ChatModel() {
    }

    public ChatModel(String key, ArrayList<MessageModel> messages) {
        this.key = key;
        this.messages = messages;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ArrayList<MessageModel> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<MessageModel> messages) {
        this.messages = messages;
    }
}
