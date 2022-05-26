package com.app.pethouse.controller;

import androidx.annotation.NonNull;

import com.app.pethouse.callback.RateCallback;
import com.app.pethouse.model.RateModel;
import com.app.pethouse.model.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RateController {
    private String node = "Rates";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference(node);
    private ArrayList<RateModel> rates = new ArrayList<>();

    public void save(final RateModel rate, final RateCallback callback) {
        if(rate.getKey() == null || rate.getKey().equals("")){
            rate.setKey(myRef.push().getKey());
        }

        myRef = database.getReference(node + "/" + rate.getKey());
        myRef.setValue(rate).addOnSuccessListener(aVoid -> {
            rates.add(rate);
            callback.onSuccess(rates);
        })
                .addOnFailureListener(e -> callback.onFail(e.toString()));
    }

    public void getRates(final RateCallback callback){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                rates = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    RateModel rate = snapshot.getValue(RateModel.class);
                    rates.add(rate);
                }
                callback.onSuccess(rates);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void getRatesAlways(final RateCallback callback){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                rates = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    RateModel rate = snapshot.getValue(RateModel.class);
                    rates.add(rate);
                }
                callback.onSuccess(rates);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void getRateByKey(final String key, final RateCallback callback){
        Query query = myRef.orderByChild("key").equalTo(key);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                rates = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    RateModel model = snapshot.getValue(RateModel.class);
                    if(model.getKey().equals(key)) {
                        rates.add(model);
                    }
                }
                callback.onSuccess(rates);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void getRateByToCaregiver(final ArrayList<RateModel> userKey, final RateCallback callback){
        Query query = myRef.orderByChild("user/key").equalTo(String.valueOf(userKey));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                rates = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    RateModel model = snapshot.getValue(RateModel.class);
                    if(model.getToSupplier().equals(userKey)) {
                        rates.add(model);
                    }
                }
                callback.onSuccess(rates);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void getRateByFromOwener(final String userKey, final RateCallback callback){
        Query query = myRef.orderByChild("user/key").equalTo(userKey);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                rates = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    RateModel model = snapshot.getValue(RateModel.class);
                    if(model.getFromOwener().getKey().equals(userKey)) {
                        rates.add(model);
                    }
                }
                callback.onSuccess(rates);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void delete(RateModel rate,final RateCallback callback) {
        myRef = database.getReference(node+"/"+rate.getKey());
        myRef.removeValue()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFail(e.toString());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        rates = new ArrayList<>();
                        callback.onSuccess(rates);
                    }
                });
    }

    public void newRate(String userKey, UserModel userModel, int value, String comment, final RateCallback callback) {
        RateModel rate = new RateModel();
        rate.setUserKey(userKey);
        rate.setToSupplier(userModel);
        rate.setFromOwener(userModel);
        rate.setRate(value);
        rate.setComment(comment);

        save(rate, new RateCallback() {
            @Override
            public void onSuccess(ArrayList<RateModel> tasks) {
                callback.onSuccess(tasks);
            }
            @Override
            public void onFail(String error) {
                callback.onFail(error);
            }
        });
    }
}
