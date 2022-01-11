package com.app.pethouse.model;

import android.text.TextUtils;

import java.util.Date;

public class UserModel {
    private String key;
    private String name = "";
    private String email;
    private String phone;
    private String pass;
    private String description;
    private int gender;
    private String nationality;
    private String petName;
    private String petKind;
    private String petGander;
    private Double latitude;
    private Double longitude;
    private double price = 0.0;
    private int state; //-1>Blocked, 0->waiting, 1->Allowed
    private TypeModel type = new TypeModel();
    private GovernorateModel governorate = new GovernorateModel();
    private CityModel city = new CityModel();
    private String profileImage = "";
    private String imagePet;
    private int activated; // -1->Blocked, 0->waiting, 1->Active
    private int userType = 0;
    private Date createdAt;

    private boolean isPublic = true;

    private String fcmToken = "";

    public UserModel() {
    }

    public UserModel(boolean isAdmin) {
        this.key = "Admin";
        this.name = "Admin";
        this.email = "Admin";
        this.userType = 1;
    }

    public UserModel(String key, String name, String email, String phone, String pass, String description, int gender, String nationality, String petName, String petKind, String petGander, Double latitude, Double longitude, double price, int state, TypeModel type, GovernorateModel governorate, CityModel city, String profileImage, String imagePet, int activated, int userType, Date createdAt, boolean isPublic, String fcmToken) {
        this.key = key;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.pass = pass;
        this.description = description;
        this.gender = gender;
        this.nationality = nationality;
        this.petName = petName;
        this.petKind = petKind;
        this.petGander = petGander;
        this.latitude = latitude;
        this.longitude = longitude;
        this.price = price;
        this.state = state;
        this.type = type;
        this.governorate = governorate;
        this.city = city;
        this.profileImage = profileImage;
        this.imagePet = imagePet;
        this.activated = activated;
        this.userType = userType;
        this.createdAt = createdAt;
        this.isPublic = isPublic;
        this.fcmToken = fcmToken;
    }

    public UserModel(String phone, String pass, int userType) {
        this.phone = phone;
        this.pass = pass;
        this.userType = userType;
    }

    public UserHeaderModel toHeader() {
        return new UserHeaderModel(this);
    }


    public boolean validate() {
        return  (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(phone) );
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getPetKind() {
        return petKind;
    }

    public void setPetKind(String petKind) {
        this.petKind = petKind;
    }

    public String getPetGander() {
        return petGander;
    }

    public void setPetGander(String petGander) {
        this.petGander = petGander;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public TypeModel getType() {
        return type;
    }

    public void setType(TypeModel type) {
        this.type = type;
    }

    public GovernorateModel getGovernorate() {
        return governorate;
    }

    public void setGovernorate(GovernorateModel governorate) {
        this.governorate = governorate;
    }

    public CityModel getCity() {
        return city;
    }

    public void setCity(CityModel city) {
        this.city = city;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getImagePet() {
        return imagePet;
    }

    public void setImagePet(String imagePet) {
        this.imagePet = imagePet;
    }

    public int getActivated() {
        return activated;
    }

    public void setActivated(int activated) {
        this.activated = activated;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
