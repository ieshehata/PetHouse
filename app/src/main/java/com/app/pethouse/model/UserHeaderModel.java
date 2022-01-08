package com.app.pethouse.model;

import java.util.Objects;

public class UserHeaderModel {
    private String key;
    private String name;
    private String fcmToken;
    private String profileImage = "";
    private int userType; // 1->Admin, 2->Adviser, 3->Patient
    private int gender; // 0->Female, 1->Male

    public UserHeaderModel() {
    }


    public UserHeaderModel(UserModel user) {
        this.key = user.getKey();
        this.name = user.getName();
        this.profileImage = user.getProfileImage();
        this.userType = user.getUserType();
        this.gender = user.getGender();
        this.fcmToken = user.getFcmToken();

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserHeaderModel userModel = (UserHeaderModel) o;
        return key.equals(userModel.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }
}
