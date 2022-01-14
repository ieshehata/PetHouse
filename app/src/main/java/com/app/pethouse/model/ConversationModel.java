package com.app.pethouse.model;

import java.util.ArrayList;
import java.util.Date;

public class ConversationModel {
    private String key;
    private ArrayList<UserHeaderModel> participants = new ArrayList<>();
    private String lastMessage;
    private Date lastMessageDate;
    private int lastMessageState; // 0->delivered, 1->seen
    private String lastMessageUserKey;
    private String chatKey;
    private Date createdAt;
    private String blockedBy = "";
    private int type; // 0->general, 1->group, 2->private
    private int state; // -1->deleted, 1->Active
    private int isFeedback; // 0->No, 1->Yes

    public ConversationModel() {
    }

    public ConversationModel(String key, ArrayList<UserHeaderModel> participants, String lastMessage, Date lastMessageDate, int lastMessageState, String lastMessageUserKey, String chatKey, Date createdAt, String blockedBy, int type, int state, int isFeedback) {
        this.key = key;
        this.participants = participants;
        this.lastMessage = lastMessage;
        this.lastMessageDate = lastMessageDate;
        this.lastMessageState = lastMessageState;
        this.lastMessageUserKey = lastMessageUserKey;
        this.chatKey = chatKey;
        this.createdAt = createdAt;
        this.blockedBy = blockedBy;
        this.type = type;
        this.state = state;
        this.isFeedback = isFeedback;
    }

    public String getBlockedBy() {
        return blockedBy;
    }

    public void setBlockedBy(String blockedBy) {
        this.blockedBy = blockedBy;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ArrayList<UserHeaderModel> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<UserHeaderModel> participants) {
        this.participants = participants;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Date getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(Date lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }

    public int getLastMessageState() {
        return lastMessageState;
    }

    public void setLastMessageState(int lastMessageState) {
        this.lastMessageState = lastMessageState;
    }

    public String getLastMessageUserKey() {
        return lastMessageUserKey;
    }

    public void setLastMessageUserKey(String lastMessageUserKey) {
        this.lastMessageUserKey = lastMessageUserKey;
    }

    public String getChatKey() {
        return chatKey;
    }

    public void setChatKey(String chatKey) {
        this.chatKey = chatKey;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getIsFeedback() {
        return isFeedback;
    }

    public void setIsFeedback(int isFeedback) {
        this.isFeedback = isFeedback;
    }
}
