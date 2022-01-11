package com.app.pethouse.utils;

import com.app.pethouse.model.CityModel;
import com.app.pethouse.model.ConversationModel;
import com.app.pethouse.model.GovernorateModel;
import com.app.pethouse.model.OrderCareModel;
import com.app.pethouse.model.RateModel;
import com.app.pethouse.model.TypeModel;
import com.app.pethouse.model.UserHeaderModel;
import com.app.pethouse.model.UserModel;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;

public class SharedData {
    public static int userType = 3; // 1->Admin, 2->Supplier, 3->Owener

    public static UserModel adminUser = new UserModel(true);
    public static UserModel currentUser;
    public static UserModel stalkedUser;
    public static UserHeaderModel currentUserHeader;
    public static UserHeaderModel stalkedUserHeader;
    public static ConversationModel currentConversation;

    public static String format = "EEE, d MMM yyyy hh:mm a";
    public static String formatTime = "hh:mm a";
    public static String formatDate = "dd/MM/yyyy";
    public static String formatDateTime = "dd/MM/yyyy hh:mm a";
    public static String imageUrl;
    public static final int NOTIFICATION_ID = 1303;
    public static final String PREF_KEY = "login";
    public static final String IS_USER_SAVED = "SAVED_USER";
    public static final String PHONE = "PHONE";
    public static final String PASS = "PASS";
    public static UserModel supplier;
    public static UserModel owner;
    public static UserModel user;
    public static OrderCareModel currentOrder;
    public static LatLng currentLatLng;
    public static double longitude;
    public static double latitude;
    public static OrderCareModel currentTime;
    public static RateModel currentRate;
    public static ArrayList<TypeModel> allTypes = new ArrayList<>();
    public static ArrayList<GovernorateModel> allGovernorates = new ArrayList<>();
    public static ArrayList<CityModel> allCities = new ArrayList<>();
    public static final String USER_TYPE = "USER_TYPE";

}
