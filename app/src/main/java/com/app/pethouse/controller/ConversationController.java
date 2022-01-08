package com.app.pethouse.controller;

import androidx.annotation.NonNull;

import com.app.pethouse.callback.ChatCallback;
import com.app.pethouse.callback.ConversationCallback;
import com.app.pethouse.model.ChatModel;
import com.app.pethouse.model.ConversationModel;
import com.app.pethouse.model.UserHeaderModel;
import com.app.pethouse.model.UserModel;
import com.app.pethouse.utils.SharedData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class ConversationController {
    private String node = "Conversations";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference(node);
    private ArrayList<ConversationModel> conversations = new ArrayList<>();

    public void save(final ConversationModel model, final ConversationCallback callback) {
        if(model.getKey() == null || model.getKey().equals("")){
            model.setKey(myRef.push().getKey());
        }

        myRef = database.getReference(node + "/" + model.getKey());
        myRef.setValue(model)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        conversations.add(model);
                        callback.onSuccess(conversations);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFail(e.toString());
                    }
                });
    }

    public void getConversations(final ConversationCallback callback){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                conversations = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ConversationModel model = snapshot.getValue(ConversationModel.class);
                    for(UserHeaderModel user : model.getParticipants()) {
                        if(SharedData.currentUserHeader.equals(user) && model.getState() == 1) {
                            conversations.add(model);
                        }
                    }
                }
                Collections.reverse(conversations);
                callback.onSuccess(conversations);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void getConversationsAlways(final ConversationCallback callback){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                conversations = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ConversationModel model = snapshot.getValue(ConversationModel.class);
                    for(UserHeaderModel user : model.getParticipants()) {
                        if(SharedData.currentUserHeader.equals(user) && model.getState() == 1) {
                            conversations.add(model);
                        }
                    }
                }
                Collections.reverse(conversations);
                callback.onSuccess(conversations);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void getConversationByKey(final String key, final ConversationCallback callback){
        Query query = myRef.orderByChild("key").equalTo(key);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                conversations = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ConversationModel model = snapshot.getValue(ConversationModel.class);
                    if(model.getKey().equals(key) && model.getState() == 1) {
                        conversations.add(model);
                    }
                }
                callback.onSuccess(conversations);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void getConversationByChatKey(final String key, final ConversationCallback callback){
        Query query = myRef.orderByChild("chatKey").equalTo(key);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                conversations = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ConversationModel model = snapshot.getValue(ConversationModel.class);
                    if(model.getChatKey().equals(key) && model.getState() == 1) {
                        conversations.add(model);
                    }
                }
                callback.onSuccess(conversations);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void getConversationsByUser(final String key, final ConversationCallback callback){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                conversations = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ConversationModel model = snapshot.getValue(ConversationModel.class);
                    for(UserHeaderModel user : model.getParticipants()) {
                        if(user.getKey().equals(key) && model.getState() == 1) {
                            conversations.add(model);
                        }
                    }
                }
                Collections.reverse(conversations);
                callback.onSuccess(conversations);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void getConversationsByTwoUsers(final String first, final String second, final ConversationCallback callback) {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                conversations = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ConversationModel model = snapshot.getValue(ConversationModel.class);
                    for (UserHeaderModel user : model.getParticipants()) {
                        if (user.getKey().equals(first) && model.getState() == 1) {
                            for (UserHeaderModel other : model.getParticipants()) {
                                if (other.getKey().equals(second) && model.getState() == 1) {
                                    conversations.add(model);
                                }
                            }
                        }
                    }
                }
                Collections.reverse(conversations);
                callback.onSuccess(conversations);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void getFeedback(final ConversationCallback callback) {
        Query query = myRef.orderByChild("isFeedback").equalTo(1);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                conversations = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ConversationModel model = snapshot.getValue(ConversationModel.class);
                    if (model.getIsFeedback() == 1 && model.getState() == 1) {
                        conversations.add(model);
                    }
                }
                Collections.reverse(conversations);
                callback.onSuccess(conversations);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void delete(ConversationModel model, ChatModel chatModel, final ConversationCallback callback) {
        new ChatController().delete(chatModel, new ChatCallback() {
            @Override
            public void onSuccess(ArrayList<ChatModel> chats) {
                myRef = database.getReference(node + "/" + model.getKey());
                myRef.removeValue()
                        .addOnFailureListener(e -> callback.onFail(e.toString()))
                        .addOnSuccessListener(aVoid -> {
                            conversations = new ArrayList<>();
                            callback.onSuccess(conversations);
                        });
            }

            @Override
            public void onFail(String error) {
                callback.onFail(error);
            }
        });
    }

    public void newConversation(UserModel to, final ConversationCallback callback) {
        ConversationModel conversation = new ConversationModel();
        ArrayList<UserHeaderModel> participants = new ArrayList<>();
        participants.add(SharedData.currentUser.toHeader());
        participants.add(to.toHeader());

        conversation.setParticipants(participants);
        conversation.setCreatedAt(Calendar.getInstance().getTime());
        conversation.setState(1);
        conversation.setIsFeedback(to.getKey().equals(SharedData.adminUser.getKey()) ? 1 : 0);
        new ChatController().save(new ChatModel(), new ChatCallback() {
            @Override
            public void onSuccess(ArrayList<ChatModel> chats) {
                conversation.setChatKey(chats.get(0).getKey());
                save(conversation, new ConversationCallback() {
                    @Override
                    public void onSuccess(ArrayList<ConversationModel> conversations) {
                        callback.onSuccess(conversations);
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
