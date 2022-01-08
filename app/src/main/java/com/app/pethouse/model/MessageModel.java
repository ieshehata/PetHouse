package com.app.pethouse.model;

import java.util.Date;

public class MessageModel implements Comparable<MessageModel> {
    private int no;
    private String chatKey;
    private UserHeaderModel author;
    private String text;
    private String image;
    private Date createdAt;
    private int state; //  0->delivered, 1->seen

    public MessageModel() {
    }

    public MessageModel(int no, String chatKey, UserHeaderModel author, String text, String image, Date createdAt, int state) {
        this.no = no;
        this.chatKey = chatKey;
        this.author = author;
        this.text = text;
        this.image = image;
        this.createdAt = createdAt;
        this.state = state;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getChatKey() {
        return chatKey;
    }

    public void setChatKey(String chatKey) {
        this.chatKey = chatKey;
    }

    public UserHeaderModel getAuthor() {
        return author;
    }

    public void setAuthor(UserHeaderModel author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public int compareTo(MessageModel o) {
        return o.getNo() - this.getNo();
    }
}
