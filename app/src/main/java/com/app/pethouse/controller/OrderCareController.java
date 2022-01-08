package com.app.pethouse.controller;

import androidx.annotation.NonNull;

import com.app.pethouse.callback.OrderCareCallback;
import com.app.pethouse.model.OrderCareModel;
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
import java.util.Date;

public class OrderCareController {
    private String node = "Orders";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference(node);
    private ArrayList<OrderCareModel> orders = new ArrayList<>();

    public void save(final OrderCareModel order, final OrderCareCallback callback) {
        if(order.getKey() == null || order.getKey().equals("")){
            order.setKey(myRef.push().getKey());
        }

        myRef = database.getReference(node + "/" + order.getKey());
        myRef.setValue(order)
                .addOnSuccessListener(aVoid -> {
                    orders.add(order);
                    callback.onSuccess(orders);
                })
                .addOnFailureListener(e -> callback.onFail(e.toString()));
    }

    public void getOrders(final OrderCareCallback callback){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orders = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    OrderCareModel order = snapshot.getValue(OrderCareModel.class);
                    orders.add(order);
                }
                callback.onSuccess(orders);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void getOrdersAlways(final OrderCareCallback callback){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orders = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    OrderCareModel model = snapshot.getValue(OrderCareModel.class);
                    orders.add(model);
                }
                callback.onSuccess(orders);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void getOrderByKey(final String key, final OrderCareCallback callback){
        Query query = myRef.orderByChild("key").equalTo(key);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orders = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    OrderCareModel model = snapshot.getValue(OrderCareModel.class);
                    if(model.getKey().equals(key)) {
                        orders.add(model);
                    }
                }
                callback.onSuccess(orders);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void getOrderByTime(final ArrayList<Date> day, final OrderCareCallback callback){
        Query query = myRef.orderByChild("time/key").equalTo(String.valueOf(day));
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orders = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    OrderCareModel model = snapshot.getValue(OrderCareModel.class);
                    if(model.getDays().equals(day)) {
                        orders.add(model);
                    }
                }
                callback.onSuccess(orders);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void getOrderBySupplier(final String userKey, final OrderCareCallback callback){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orders = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    OrderCareModel model = snapshot.getValue(OrderCareModel.class);
                    if(model.getSupplier() != null && model.getSupplier().getKey() != null && model.getSupplier().getKey().equals(userKey) && model.getOwener() != null) {
                        orders.add(model);
                    }
                }
                callback.onSuccess(orders);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void getOrderByOwener(final String userKey, final OrderCareCallback callback){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orders = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    OrderCareModel model = snapshot.getValue(OrderCareModel.class);
                    if(model.getOwener() != null && model.getOwener().getKey() != null && model.getOwener().getKey().equals(userKey)) {
                        orders.add(model);
                    }
                }
                callback.onSuccess(orders);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void delete(OrderCareModel order,final OrderCareCallback callback) {
        myRef = database.getReference(node+"/"+order.getKey());
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
                        orders = new ArrayList<>();
                        callback.onSuccess(orders);
                    }
                });
    }

    public void newOrder(UserModel userModel, int value, String description, final OrderCareCallback callback) {
        OrderCareModel order = new OrderCareModel();
        order.setSupplier(userModel);
        order.setOwener(userModel);
        order.setTotalPrice(value);
        order.setDescription(description);

        save(order, new OrderCareCallback() {
            @Override
            public void onSuccess(ArrayList<OrderCareModel> tasks) {
                callback.onSuccess(tasks);
            }
            @Override
            public void onFail(String error) {
                callback.onFail(error);
            }
        });
    }

    public void responseOnRequest(OrderCareModel order, Boolean isAccepted, final OrderCareCallback callback) {
        order.setState(isAccepted ? 1 : -1);
        save(order, new OrderCareCallback() {
            @Override
            public void onSuccess(ArrayList<OrderCareModel> orders) {
                callback.onSuccess(orders);
            }

            @Override
            public void onFail(String error) {
                callback.onFail(error);
            }
        });
    }
}
