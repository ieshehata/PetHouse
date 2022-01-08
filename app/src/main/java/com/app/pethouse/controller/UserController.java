package com.app.pethouse.controller;

import androidx.annotation.NonNull;

import com.app.pethouse.callback.BooleanCallback;
import com.app.pethouse.callback.UserCallback;
import com.app.pethouse.model.UserModel;
import com.app.pethouse.utils.SharedData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserController {
    private String node = "Users";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference(node);
    private ArrayList<UserModel> users = new ArrayList<>();

    public void save(final UserModel user, final UserCallback callback) {
        if(user.getKey() == null || user.getKey().equals("")){
            user.setKey(myRef.push().getKey());
        }

        myRef = database.getReference(node + "/" + user.getKey());
        myRef.setValue(user)
                .addOnSuccessListener(aVoid -> {
                    users.add(user);
                    callback.onSuccess(users);
                })
                .addOnFailureListener(e -> callback.onFail(e.toString()));
    }

    public void getUsers(final UserCallback callback){
        Query query = myRef.orderByChild("userType").equalTo(SharedData.userType);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    UserModel user = snapshot.getValue(UserModel.class);
                    if(user.getUserType() == SharedData.userType) {
                        users.add(user);
                    }
                }
                callback.onSuccess(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void getUsersAlways(final UserCallback callback){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    UserModel user= snapshot.getValue(UserModel.class);
                    users.add(user);
                }
                callback.onSuccess(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });

    }

    public void getUserByKey(final String key, final UserCallback callback){
        Query query = myRef.orderByChild("key").equalTo(key);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    UserModel user = snapshot.getValue(UserModel.class);
                    if(user.getKey().equals(key)) {
                        users.add(user);
                    }
                }
                callback.onSuccess(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void getUserByUserType(final int userType, final UserCallback callback){
        Query query = myRef.orderByChild("userType").equalTo(userType);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users = new ArrayList<>();
                if (SharedData.userType==3){
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        UserModel user = snapshot.getValue(UserModel.class);
                        if(user.getKey().equals(userType)) {
                            users.add(user);
                        }
                    }
                    callback.onSuccess(users);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void getUserByKeyAlways(final String key, final UserCallback callback){
        Query query = myRef.orderByChild("key").equalTo(key);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    UserModel user = snapshot.getValue(UserModel.class);
                    if(user.getKey().equals(key)) {
                        users.add(user);
                    }
                }
                callback.onSuccess(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void getUserByPhone(final String phone, final UserCallback callback){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    UserModel user1 = snapshot.getValue(UserModel.class);
                    assert user1 != null;
                    if(user1.getPhone().equals(phone)) {
                        users.add(user1);
                    }
                }
                callback.onSuccess(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void checkLogin(final UserModel user, final UserCallback callback){
        Query query = myRef.orderByChild("phone").equalTo(user.getPhone());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    UserModel model = snapshot.getValue(UserModel.class);
                    assert model != null;
                    if(model.getPhone().equals(user.getPhone()) && model.getPass().equals(user.getPass()) && model.getUserType() == user.getUserType())
                        users.add(model);
                }
                callback.onSuccess(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void checkEmail(final String email, final int userType, final BooleanCallback callback){
        Query query = myRef.orderByChild("email").equalTo(email);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean check = true;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    UserModel user = snapshot.getValue(UserModel.class);
                    assert user != null;
                    if(user.getEmail().equals(email) && user.getUserType() == userType)
                        check = false;
                }
                callback.onSuccess(check);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void checkPhone(final String phone, final BooleanCallback callback){
        Query query = myRef.orderByChild("phone").equalTo(phone);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean check = true;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    UserModel user1 = snapshot.getValue(UserModel.class);
                    assert user1 != null;
                    if(user1.getPhone().equals(phone))
                        check = false;

                }
                callback.onSuccess(check);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void delete(UserModel user, final UserCallback callback) {
        myRef = database.getReference(node+"/"+user.getKey());
        myRef.removeValue()
                .addOnFailureListener(e -> callback.onFail(e.toString()))
                .addOnSuccessListener(aVoid -> {
                    users = new ArrayList<>();
                    callback.onSuccess(users);
                });
    }

    public void newOwner(UserModel user, final UserCallback userCallback) {
        user.setUserType(3);
        user.setActivated(1);
        if(user.validate()) {
            checkEmail(user.getEmail(), user.getUserType(), new BooleanCallback() {
                @Override
                public void onSuccess(boolean bool) {
                    if(bool) {
                        save(user, new UserCallback() {
                            @Override
                            public void onSuccess(ArrayList<UserModel> users) {
                                userCallback.onSuccess(users);
                            }
                            @Override
                            public void onFail(String error) {
                                userCallback.onFail(error);
                            }
                        });
                    }else {
                        userCallback.onFail("Email is used before!");
                    }
                }
                @Override
                public void onFail(String error) {
                    userCallback.onFail(error);
                }
            });
        }else {
            userCallback.onFail("Not Valid Data!");
        }
    }

    public void newSupplier(UserModel user, final UserCallback userCallback) {
        user.setUserType(2);
        user.setActivated(0);
        if(user.validate()) {
            checkEmail(user.getEmail(), user.getUserType(), new BooleanCallback() {
                @Override
                public void onSuccess(boolean bool) {
                    if(bool) {
                        save(user, new UserCallback() {
                            @Override
                            public void onSuccess(ArrayList<UserModel> users) {
                                userCallback.onSuccess(users);
                            }
                            @Override
                            public void onFail(String error) {
                                userCallback.onFail(error);
                            }
                        });
                    }else {
                        userCallback.onFail("Email is used before!");
                    }
                }
                @Override
                public void onFail(String error) {
                    userCallback.onFail(error);
                }
            });
        } else {
            userCallback.onFail("Not Valid Data!");
        }
    }
}
