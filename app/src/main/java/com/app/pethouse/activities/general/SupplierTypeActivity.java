package com.app.pethouse.activities.general;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.app.pethouse.R;
import com.app.pethouse.activities.auth.LoginActivity;
import com.app.pethouse.activities.auth.SupplierRegisterActivity;
import com.app.pethouse.utils.SharedData;

public class SupplierTypeActivity extends AppCompatActivity {

    int from = 0; // 1-> Register,  2->UserMain
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_type);
        setTitle("Supplier Type");
        from = getIntent().getIntExtra("from", 0);

        findViewById(R.id.hospital).setOnClickListener(v -> {
            SharedData.supplierType = 0;
            goToNext();
        });

        findViewById(R.id.doctor).setOnClickListener(v -> {
            SharedData.supplierType = 1;
            goToNext();
        });

        findViewById(R.id.pharmacy).setOnClickListener(v -> {
            SharedData.supplierType = 2;
            goToNext();
        });

        findViewById(R.id.store).setOnClickListener(v -> {
            SharedData.supplierType = 3;
            goToNext();
        });

        findViewById(R.id.club).setOnClickListener(v -> {
            SharedData.supplierType = 4;
            goToNext();
        });

        findViewById(R.id.caregiver).setOnClickListener(v -> {
            SharedData.supplierType = 5;
            goToNext();
        });

    }

    private void goToNext() {
        if(from == 0) { //register
            Intent intent = new Intent(this, SupplierRegisterActivity.class);
            intent.putExtra("isEditing", false);
            startActivity(intent);
        } else if(from == 1) { //user main

        }
    }
}