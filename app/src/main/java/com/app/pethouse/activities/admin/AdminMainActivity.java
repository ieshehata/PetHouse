package com.app.pethouse.activities.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.app.pethouse.R;
import com.app.pethouse.activities.auth.UserTypeActivity;

public class AdminMainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        (findViewById(R.id.system_data)).setOnClickListener(v -> startActivity(new Intent(this, GovernoratesActivity.class)));

        (findViewById(R.id.suppliers)).setOnClickListener(v -> startActivity(new Intent(this, SuppliersActivity.class)));

        (findViewById(R.id.owners)).setOnClickListener(v -> startActivity(new Intent(this, OwenersActivity.class)));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_logout:
                Intent intent = new Intent(this, UserTypeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}