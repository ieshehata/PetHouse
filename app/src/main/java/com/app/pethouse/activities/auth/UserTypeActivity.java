package com.app.pethouse.activities.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.app.pethouse.R;
import com.app.pethouse.callback.CityCallback;
import com.app.pethouse.callback.GovernorateCallback;
import com.app.pethouse.callback.TypeCallback;
import com.app.pethouse.controller.CityController;
import com.app.pethouse.controller.GovernorateController;
import com.app.pethouse.controller.TypeController;
import com.app.pethouse.model.CityModel;
import com.app.pethouse.model.GovernorateModel;
import com.app.pethouse.model.TypeModel;
import com.app.pethouse.utils.SharedData;

import java.util.ArrayList;

public class UserTypeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type);
        Button admin = findViewById(R.id.admin);
        Button owener = findViewById(R.id.pet_owner);
        Button supplier = findViewById(R.id.pet_supplier);
        ImageView logo = findViewById(R.id.logo);

        new GovernorateController().getGovernoratesAlways(new GovernorateCallback() {
            @Override
            public void onSuccess(ArrayList<GovernorateModel> governorates) {
                SharedData.allGovernorates = governorates;
            }

            @Override
            public void onFail(String error) {}
        });

        new CityController().getCitiesAlways(new CityCallback() {
            @Override
            public void onSuccess(ArrayList<CityModel> citys) {
                SharedData.allCities = citys;
            }

            @Override
            public void onFail(String error) {}
        });


        new TypeController().getTypesAlways(new TypeCallback() {
            @Override
            public void onSuccess(ArrayList<TypeModel> types) {
                SharedData.allTypes = types;
            }

            @Override
            public void onFail(String error) {}
        });


        admin.setOnClickListener(v -> {
            SharedData.userType = 1;
            Intent intent = new Intent(UserTypeActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        supplier.setOnClickListener(v -> {
            SharedData.userType = 2;
            Intent intent = new Intent(UserTypeActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        owener.setOnClickListener(v -> {
            SharedData.userType = 3;
            Intent intent = new Intent(UserTypeActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.about).setOnClickListener(v -> {
            Intent intent = new Intent(UserTypeActivity.this, AboutUsActivity.class);
            startActivity(intent);
        });
    }

}