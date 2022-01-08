package com.app.pethouse.controller;

import androidx.annotation.NonNull;

import com.app.pethouse.callback.SupplierReqCallback;
import com.app.pethouse.callback.UserCallback;
import com.app.pethouse.model.SuppliersReqModel;
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

public class SupplierReqController {
    private String node = "RegistrationRequests";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference(node);
    private ArrayList<SuppliersReqModel> requests = new ArrayList<>();

    public void save(final SuppliersReqModel request, final SupplierReqCallback callback) {
        if(request.getKey() == null){
            request.setKey(myRef.push().getKey());
        }else if(request.getKey().equals("")){
            request.setKey(myRef.push().getKey());
        }
        myRef = database.getReference(node + "/" + request.getKey());
        myRef.setValue(request)
                .addOnSuccessListener(aVoid -> {
                    requests.add(request);
                    callback.onSuccess(requests);
                })
                .addOnFailureListener(e -> callback.onFail(e.toString()));
    }

    public void getRequests(final SupplierReqCallback callback){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                requests = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    SuppliersReqModel request = snapshot.getValue(SuppliersReqModel.class);
                    requests.add(request);
                }
                callback.onSuccess(requests);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void geRequestsAlways(final SupplierReqCallback callback){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                requests = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    SuppliersReqModel request = snapshot.getValue(SuppliersReqModel.class);
                    if(request.getState() == 0) {
                        requests.add(request);
                    }
                }
                callback.onSuccess(requests);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void getRequestByKey(final String key, final SupplierReqCallback callback){
        Query query = myRef.orderByChild("key").equalTo(key);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                requests = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    SuppliersReqModel a = snapshot.getValue(SuppliersReqModel.class);
                    if(a.getKey().equals(key)) {
                        requests.add(a);
                    }
                }
                callback.onSuccess(requests);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void delete(SuppliersReqModel request, final SupplierReqCallback callback) {
        myRef = database.getReference(node+"/"+request.getKey());
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
                        requests = new ArrayList<>();
                        callback.onSuccess(requests);
                    }
                });
    }

    public void newRequest(UserModel supplier, final SupplierReqCallback callback) {
        final SuppliersReqModel request = new SuppliersReqModel();
        request.setSupplier(supplier);
        request.setState(0);

        save(request, new SupplierReqCallback() {
            @Override
            public void onSuccess(ArrayList<SuppliersReqModel> caregiverReqs) {
                callback.onSuccess(requests);
            }

            @Override
            public void onFail(String error) {
                callback.onFail(error);
            }
        });
    }

    public void responseOnRequest(SuppliersReqModel request, Boolean isAccepted, final SupplierReqCallback callback) {
        request.setState(isAccepted ? 1 : -1);
        save(request, new SupplierReqCallback() {
            @Override
            public void onSuccess(ArrayList<SuppliersReqModel> requests) {
                new UserController().getUserByKey(request.getSupplier().getKey(), new UserCallback() {
                    @Override
                    public void onSuccess(ArrayList<UserModel> suppliers) {
                        if(suppliers.size() > 0) {
                            UserModel caregiver = suppliers.get(0);
                            caregiver.setState(isAccepted ? 1 : -1);
                            new UserController().save(caregiver, new UserCallback() {
                                @Override
                                public void onSuccess(ArrayList<UserModel> suppliers) {
                                    callback.onSuccess(requests);

                                }

                                @Override
                                public void onFail(String error) {
                                    callback.onFail(error);
                                }
                            });
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        callback.onFail(error);
                    }

                });
            }
            @Override
            public void onFail(String error) {
                callback.onFail(error);
            }
        });
    }

}
