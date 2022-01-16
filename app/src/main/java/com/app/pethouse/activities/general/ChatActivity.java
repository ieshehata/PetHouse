package com.app.pethouse.activities.general;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pethouse.R;
import com.app.pethouse.activities.auth.RegisterActivity;
import com.app.pethouse.activities.auth.SupplierRegisterActivity;
import com.app.pethouse.adapter.MessageAdapter;
import com.app.pethouse.callback.ChatCallback;
import com.app.pethouse.callback.ConversationCallback;
import com.app.pethouse.callback.StringCallback;
import com.app.pethouse.controller.ChatController;
import com.app.pethouse.controller.ConversationController;
import com.app.pethouse.controller.UploadController;
import com.app.pethouse.model.ChatModel;
import com.app.pethouse.model.ConversationModel;
import com.app.pethouse.model.UserHeaderModel;
import com.app.pethouse.utils.LoadingHelper;
import com.app.pethouse.utils.SharedData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

public class ChatActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 25;
    private final ChatController controller = new ChatController();
    private ImageView profileImage;
    private TextView title, noMessages;
    private RecyclerView recyclerView;
    private Button block;
    private ImageButton ring;
    private ImageButton imageMessage, sendMessage;
    private EditText messageET;
    private LoadingHelper loadingHelper;
    private ChatModel currentChat = new ChatModel();
    private UserHeaderModel other;
    private MessageAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        profileImage = findViewById(R.id.profile_image);
        title = findViewById(R.id.title);
        block = findViewById(R.id.block);
        ring = findViewById(R.id.ring);
        noMessages = findViewById(R.id.no_messages);
        recyclerView = findViewById(R.id.recycler_view);
        imageMessage = findViewById(R.id.image_message);
        sendMessage = findViewById(R.id.send_message);
        messageET = findViewById(R.id.message_et);

        loadingHelper = new LoadingHelper(ChatActivity.this);
        setListener();
        getData();
    }

    @Override
    public void onBackPressed() {
        if (currentChat.getMessages().size() == 0) {
            new ConversationController().delete(SharedData.currentConversation, currentChat, new ConversationCallback() {
                @Override
                public void onSuccess(ArrayList<ConversationModel> conversations) {
                }

                @Override
                public void onFail(String error) {
                    Toast.makeText(ChatActivity.this, error, Toast.LENGTH_LONG).show();
                }
            });
        }
        controller.detachListener();
        super.onBackPressed();
    }

    private void getData() {
        loadingHelper.showLoading("");
        controller.getChatAlways(SharedData.currentConversation.getChatKey(), new ChatCallback() {
            @Override
            public void onSuccess(ArrayList<ChatModel> chats) {
                loadingHelper.dismissLoading();
                if (chats.size() > 0) {
                    currentChat = chats.get(0);
                    setData();

                } else {
                    onBackPressed();
                }
            }

            @Override
            public void onFail(String error) {
                loadingHelper.dismissLoading();
                Toast.makeText(ChatActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setData() {
        for (UserHeaderModel participant : SharedData.currentConversation.getParticipants()) {
            if (!participant.getKey().equals(SharedData.currentUser.getKey()) && SharedData.currentConversation.getParticipants().size() == 2) {
                other = participant;
            }
        }

        if (other != null) {
            title.setText(other.getName());
            if (!TextUtils.isEmpty(other.getProfileImage())) {
                profileImage.setImageTintList(null);
                Picasso.get()
                        .load(other.getProfileImage())
                        .into(profileImage);
            }else if(other.getKey().equals(SharedData.adminUser.getKey())) {
                profileImage.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimaryMidDark)));
                profileImage.setBackgroundResource(R.drawable.gradient_back);
                profileImage.setImageResource(R.drawable.ic_report_24);

            }
        } else {
            onBackPressed();
        }

        if(SharedData.currentConversation.getBlockedBy().equals(SharedData.currentUser.getKey())) {
            block.setText("Unblock");
        }

        if (currentChat.getMessages().size() > 0) {
            Collections.sort(currentChat.getMessages());
            recyclerView.setVisibility(View.VISIBLE);
            noMessages.setVisibility(View.GONE);
            if (adapter == null || adapter.getData().size() == 0) {
                adapter = new MessageAdapter(currentChat.getMessages());
                recyclerView.setAdapter(adapter);
                LinearLayoutManager layoutManager = new LinearLayoutManager(ChatActivity.this);
                layoutManager.setReverseLayout(true);
                recyclerView.setLayoutManager(layoutManager);
            } else {
                adapter.updateData(currentChat.getMessages());
                if(!currentChat.getMessages().get(0).getAuthor().getKey().equals(SharedData.currentUser.getKey())) {
                    final MediaPlayer receivedSound = MediaPlayer.create(this, R.raw.received);
                    receivedSound.start();
                }
            }
        } else {
            noMessages.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        new ConversationController().getConversationByKey(SharedData.currentConversation.getKey(), new ConversationCallback() {
            @Override
            public void onSuccess(ArrayList<ConversationModel> conversations) {
                if (conversations.size() > 0) {
                    SharedData.currentConversation = conversations.get(0);
                }
            }

            @Override
            public void onFail(String error) {}
        });
    }

    private void setListener() {
        sendMessage.setOnClickListener(v -> {
            String message = messageET.getText().toString();
            messageET.setText("");
            if (!TextUtils.isEmpty(message.trim())) {
                if(TextUtils.isEmpty(SharedData.currentConversation.getBlockedBy())) {
                    disableSend();
                    new ChatController().newMessage(SharedData.currentConversation, currentChat, message, "", other, new ChatCallback() {
                        @Override
                        public void onSuccess(ArrayList<ChatModel> chats) {
                            loadingHelper.dismissLoading();
                            enableSend();
                        }

                        @Override
                        public void onFail(String error) {
                            loadingHelper.dismissLoading();
                            enableSend();
                            Toast.makeText(ChatActivity.this, error, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(ChatActivity.this, "You can't send messages to this person!", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(ChatActivity.this, "Type a Message!", Toast.LENGTH_LONG).show();
            }
        });

        block.setOnClickListener(v -> {
            if(TextUtils.isEmpty(SharedData.currentConversation.getBlockedBy())) {
                SharedData.currentConversation.setBlockedBy(SharedData.currentUser.getKey());
            }else if(SharedData.currentConversation.getBlockedBy().equals(SharedData.currentUser.getKey())) {
                SharedData.currentConversation.setBlockedBy("");
            }

            new ConversationController().save(SharedData.currentConversation, new ConversationCallback() {
                @Override
                public void onSuccess(ArrayList<ConversationModel> conversations) {
                    getData();
                }

                @Override
                public void onFail(String error) {
                    Toast.makeText(ChatActivity.this, error, Toast.LENGTH_LONG).show();
                }
            });
        });

        ring.setOnClickListener(v -> {
            new ChatController().newRing(SharedData.currentConversation, currentChat, other, new ChatCallback() {
                @Override
                public void onSuccess(ArrayList<ChatModel> chats) {}

                @Override
                public void onFail(String error) {}
            });
        });

        imageMessage.setOnClickListener(v -> {
            if (checkReadPermission()) {
                pickImage();
            }
        });

        profileImage.setOnClickListener(v -> {
            SharedData.stalkedUserHeader = other;
            if(SharedData.userType == 3) { //Owener
                Intent intent = new Intent(ChatActivity.this, RegisterActivity.class);
                intent.putExtra("isEditing", false);
                startActivity(intent);
            }else if(SharedData.userType == 2) { //Supplier
                Intent intent = new Intent(ChatActivity.this, SupplierRegisterActivity.class);
                intent.putExtra("isEditing", false);
                startActivity(intent);
            }


        });

        title.setOnClickListener(v -> {
            SharedData.stalkedUserHeader = other;

            if(SharedData.userType == 3) { //Owener
                Intent intent = new Intent(ChatActivity.this, RegisterActivity.class);
                intent.putExtra("isEditing", false);
                startActivity(intent);
            }else if(SharedData.userType == 2) { //Supplier
                Intent intent = new Intent(ChatActivity.this, SupplierRegisterActivity.class);
                intent.putExtra("isEditing", false);
                startActivity(intent);
            }
        });
    }

    private void disableSend() {
        sendMessage.setEnabled(false);
        imageMessage.setEnabled(false);
        final MediaPlayer sentSound = MediaPlayer.create(ChatActivity.this, R.raw.sent);
        sentSound.start();
    }

    private void enableSend() {
        sendMessage.setEnabled(true);
        imageMessage.setEnabled(true);
    }

    private boolean checkReadPermission() {
        int permissionWriteExternal = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionWriteExternal != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 3) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            }
        }
    }

    private void pickImage() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            if(TextUtils.isEmpty(SharedData.currentConversation.getBlockedBy())) {
                loadingHelper.showLoading("");
                disableSend();
                new UploadController().uploadImage(data.getData(), new StringCallback() {
                    @Override
                    public void onSuccess(String text) {
                        new ChatController().newMessage(SharedData.currentConversation, currentChat, "", text, other, new ChatCallback() {
                            @Override
                            public void onSuccess(ArrayList<ChatModel> chats) {
                                loadingHelper.dismissLoading();
                                enableSend();
                            }

                            @Override
                            public void onFail(String error) {
                                loadingHelper.dismissLoading();
                                enableSend();
                                Toast.makeText(ChatActivity.this, error, Toast.LENGTH_LONG).show();

                            }
                        });
                    }

                    @Override
                    public void onFail(String error) {
                        loadingHelper.dismissLoading();
                        enableSend();
                        Toast.makeText(ChatActivity.this, error, Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                Toast.makeText(ChatActivity.this, "You can't send messages to this person!", Toast.LENGTH_LONG).show();
            }
        }
    }
}