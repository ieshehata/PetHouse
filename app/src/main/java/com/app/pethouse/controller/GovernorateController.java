package com.app.pethouse.controller;

import androidx.annotation.NonNull;

import com.app.pethouse.callback.GovernorateCallback;
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

public class GovernorateController {
    private String node = "Governorates";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference(node);
    private ArrayList<GovernorateModel> governorates = new ArrayList<>();

    public void save(final GovernorateModel tip, final GovernorateCallback callback) {
        if(tip.getKey() == null || tip.getKey().equals("")){
            tip.setKey(myRef.push().getKey());
        }

        myRef = database.getReference(node + "/" + tip.getKey());
        myRef.setValue(tip)
                .addOnSuccessListener(aVoid -> {
                    governorates.add(tip);
                    callback.onSuccess(governorates);
                })
                .addOnFailureListener(e -> callback.onFail(e.toString()));
    }

    public void getGovernorates(final GovernorateCallback callback){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                governorates = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    GovernorateModel tip = snapshot.getValue(GovernorateModel.class);
                    governorates.add(tip);
                }
                callback.onSuccess(governorates);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void getGovernoratesAlways(final GovernorateCallback callback){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                governorates = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    GovernorateModel tip = snapshot.getValue(GovernorateModel.class);
                    governorates.add(tip);
                }
                callback.onSuccess(governorates);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void getGovernorateByKey(final String key, final GovernorateCallback callback){
        Query query = myRef.orderByChild("key").equalTo(key);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                governorates = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    GovernorateModel model = snapshot.getValue(GovernorateModel.class);
                    if(model.getKey().equals(key)) {
                        governorates.add(model);
                    }
                }
                callback.onSuccess(governorates);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void delete(GovernorateModel tip,final GovernorateCallback callback) {
        myRef = database.getReference(node+"/"+tip.getKey());
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
                        governorates = new ArrayList<>();
                        callback.onSuccess(governorates);
                    }
                });
    }

    public void newGovernorate(String name, final GovernorateCallback callback) {
        GovernorateModel governorate = new GovernorateModel();
        governorate.setName(name);
        save(governorate, new GovernorateCallback() {
            @Override
            public void onSuccess(ArrayList<GovernorateModel> tasks) {
                callback.onSuccess(tasks);
            }
            @Override
            public void onFail(String error) {
                callback.onFail(error);
            }
        });
    }
}
