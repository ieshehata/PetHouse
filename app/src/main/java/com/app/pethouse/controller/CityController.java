package com.app.pethouse.controller;

import androidx.annotation.NonNull;

import com.app.pethouse.callback.CityCallback;
import com.app.pethouse.model.CityModel;
import com.app.pethouse.model.GovernorateModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CityController {
    private String node = "Cities";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference(node);
    private ArrayList<CityModel> cities = new ArrayList<>();

    public void save(final CityModel city, final CityCallback callback) {
        if(city.getKey() == null || city.getKey().equals("")){
            city.setKey(myRef.push().getKey());
        }

        myRef = database.getReference(node + "/" + city.getKey());
        myRef.setValue(city)
                .addOnSuccessListener(aVoid -> {
                    cities.add(city);
                    callback.onSuccess(cities);
                })
                .addOnFailureListener(e -> callback.onFail(e.toString()));
    }

    public void getCities(final CityCallback callback){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cities = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    CityModel city = snapshot.getValue(CityModel.class);
                    cities.add(city);
                }
                callback.onSuccess(cities);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void getCitiesAlways(final CityCallback callback){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cities = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    CityModel city = snapshot.getValue(CityModel.class);
                    cities.add(city);
                }
                callback.onSuccess(cities);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void getCityByKey(final String key, final CityCallback callback){
        Query query = myRef.orderByChild("key").equalTo(key);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cities = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    CityModel model = snapshot.getValue(CityModel.class);
                    if(model.getKey().equals(key)) {
                        cities.add(model);
                    }
                }
                callback.onSuccess(cities);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void delete(CityModel city,final CityCallback callback) {
        myRef = database.getReference(node+"/"+city.getKey());
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
                        cities = new ArrayList<>();
                        callback.onSuccess(cities);
                    }
                });
    }

    public void newCity(String name, GovernorateModel governorate, final CityCallback callback) {
        CityModel city = new CityModel();
        city.setName(name);
        city.setGovernorateKey(governorate.getKey());
        save(city, new CityCallback() {
            @Override
            public void onSuccess(ArrayList<CityModel> tasks) {
                callback.onSuccess(tasks);
            }
            @Override
            public void onFail(String error) {
                callback.onFail(error);
            }
        });
    }
}
