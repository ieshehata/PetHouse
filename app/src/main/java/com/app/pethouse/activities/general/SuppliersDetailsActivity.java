package com.app.pethouse.activities.general;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.pethouse.R;
import com.app.pethouse.activities.supplier.SupplierNavigationActivity;
import com.app.pethouse.callback.ConversationCallback;
import com.app.pethouse.callback.UserCallback;
import com.app.pethouse.controller.ConversationController;
import com.app.pethouse.controller.UserController;
import com.app.pethouse.model.ConversationModel;
import com.app.pethouse.model.UserModel;
import com.app.pethouse.utils.LoadingHelper;
import com.app.pethouse.utils.SharedData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SuppliersDetailsActivity extends AppCompatActivity {
    ViewPager viewPager;
    TextView name, governorate, city, description, phone, email, price;
    Button calender,location,rate,sendMsg;
    ImageView avatar;
    LoadingHelper loadingHelper;
    UserModel supplier;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_details);
        avatar = findViewById(R.id.avatar);
        name = findViewById(R.id.name);
        governorate = findViewById(R.id.governorate);
        city = findViewById(R.id.city);
        description = findViewById(R.id.description);
        phone = findViewById(R.id.phone);
        price = findViewById(R.id.price);
        email = findViewById(R.id.email);
        calender = findViewById(R.id.calender);
        rate=findViewById(R.id.rate);
        sendMsg =findViewById(R.id.chat);
        location = findViewById(R.id.location);

        loadingHelper = new LoadingHelper(this);

        calender.setOnClickListener(v -> {
            Intent intent = new Intent(SuppliersDetailsActivity.this, SuppliersCalenderActivity.class);
            startActivity(intent);
        });

        rate.setOnClickListener(v -> {
            Intent intent = new Intent(SuppliersDetailsActivity.this, RateActivity.class);
            startActivity(intent);
        });

        sendMsg.setOnClickListener(v -> {
            chat(supplier);
        });

        location.setOnClickListener(v -> {
            Intent intent = new Intent(this, SupplierNavigationActivity.class);
            startActivity(intent);
        });

        getData();
    }

    private void getData() {
        loadingHelper.showLoading("");
        new UserController().getUserByKey(SharedData.stalkedUser.getKey(), new UserCallback() {
            @Override
            public void onSuccess(ArrayList<UserModel> suppliers) {
                loadingHelper.dismissLoading();
                if(suppliers.size() > 0) {
                    SharedData.stalkedUser = suppliers.get(0);
                    supplier = suppliers.get(0);
                    setData();
                }else {
                    Toast.makeText(SuppliersDetailsActivity.this, "error!", Toast.LENGTH_LONG).show();
                    onBackPressed();
                }
            }

            @Override
            public void onFail(String error) {
                loadingHelper.dismissLoading();
                Toast.makeText(SuppliersDetailsActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private void setData() {
        name.setText(supplier.getName());
        phone.setText(supplier.getPhone());
        email.setText(supplier.getEmail());
        governorate.setText(supplier.getGovernorate().getName());
        city.setText(supplier.getCity().getName());
        description.setText(supplier.getDescription());
        price.setText(String.format("%.3f KWD", supplier.getPrice()));

        if (!TextUtils.isEmpty(SharedData.stalkedUser.getProfileImage())) {
            Picasso.get()
                    .load(SharedData.stalkedUser.getProfileImage())
                    .into(avatar);
        }
    }

    public void chat(UserModel user) {
        loadingHelper.showLoading("");
        new ConversationController().getConversationsByTwoUsers(SharedData.currentUser.getKey(),
                user.getKey(), new ConversationCallback() {
                    @Override
                    public void onSuccess(ArrayList<ConversationModel> conversations) {
                        if (conversations.size() > 0) {
                            loadingHelper.dismissLoading();
                            SharedData.currentConversation = conversations.get(0);
                            Intent intent = new Intent(SuppliersDetailsActivity.this, ChatActivity.class);
                            startActivity(intent);
                        } else {
                            new ConversationController().newConversation(user, new ConversationCallback() {
                                @Override
                                public void onSuccess(ArrayList<ConversationModel> conversations) {
                                    loadingHelper.dismissLoading();
                                    SharedData.currentConversation = conversations.get(0);
                                    Intent intent = new Intent(SuppliersDetailsActivity.this, ChatActivity.class);
                                    startActivity(intent);
                                }

                                @Override
                                public void onFail(String error) {
                                    loadingHelper.dismissLoading();
                                    Toast.makeText(SuppliersDetailsActivity.this, error, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        loadingHelper.dismissLoading();
                        Toast.makeText(SuppliersDetailsActivity.this, error, Toast.LENGTH_LONG).show();
                    }
                });
    }
}