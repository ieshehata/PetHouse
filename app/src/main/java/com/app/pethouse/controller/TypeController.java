package com.app.pethouse.controller;

import androidx.annotation.NonNull;

import com.app.pethouse.callback.TypeCallback;
import com.app.pethouse.model.TypeModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TypeController {
    private String node = "Types";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference(node);
    private ArrayList<TypeModel> types = new ArrayList<>();

    public void save(final TypeModel type, final TypeCallback callback) {
        if (type.getKey() == null || type.getKey().equals("")) {
            type.setKey(myRef.push().getKey());
        }

        myRef = database.getReference(node + "/" + type.getKey());
        myRef.setValue(type)
                .addOnSuccessListener(aVoid -> {
                    types.add(type);
                    callback.onSuccess(types);
                })
                .addOnFailureListener(e -> callback.onFail(e.toString()));
    }

    public void getTypes(final TypeCallback callback) {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                types = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TypeModel model = snapshot.getValue(TypeModel.class);
                    types.add(model);
                }
                callback.onSuccess(types);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void getTypesAlways(final TypeCallback callback) {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                types = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TypeModel model = snapshot.getValue(TypeModel.class);
                    types.add(model);
                }
                callback.onSuccess(types);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void getTypeByKey(final String key, final TypeCallback callback) {
        Query query = myRef.orderByChild("key").equalTo(key);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                types = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TypeModel model = snapshot.getValue(TypeModel.class);
                    if (model.getKey().equals(key)) {
                        types.add(model);
                    }
                }
                callback.onSuccess(types);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void getTypeByKeyAlways(final String key, final TypeCallback callback) {
        Query query = myRef.orderByChild("key").equalTo(key);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                types = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TypeModel model = snapshot.getValue(TypeModel.class);
                    if (model.getKey().equals(key)) {
                        types.add(model);
                    }
                }
                callback.onSuccess(types);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }


    public void delete(TypeModel type, final TypeCallback callback) {
        myRef = database.getReference(node + "/" + type.getKey());
        myRef.removeValue()
                .addOnFailureListener(e -> callback.onFail(e.toString()))
                .addOnSuccessListener(aVoid -> {
                    types = new ArrayList<>();
                    callback.onSuccess(types);
                });
    }

    public void newType(String name, final TypeCallback callback) {
        TypeModel type = new TypeModel();
        type.setName(name);
        save(type, new TypeCallback() {
            @Override
            public void onSuccess(ArrayList<TypeModel> tasks) {
                callback.onSuccess(tasks);
            }
            @Override
            public void onFail(String error) {
                callback.onFail(error);
            }
        });
        }


}
