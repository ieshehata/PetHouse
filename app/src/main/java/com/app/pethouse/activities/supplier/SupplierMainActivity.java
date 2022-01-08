package com.app.pethouse.activities.supplier;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.app.pethouse.R;
import com.app.pethouse.activities.auth.SupplierRegisterActivity;
import com.app.pethouse.activities.auth.UserTypeActivity;
import com.app.pethouse.utils.SharedData;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SupplierMainActivity extends AppCompatActivity {
    private SharedPreferences sharedPref;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplierr_main);
        sharedPref = this.getSharedPreferences(SharedData.PREF_KEY, Context.MODE_PRIVATE);
        setTitle(SharedData.supplier.getName());
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_profile:
                Intent intent = new Intent(this, SupplierRegisterActivity.class);
                intent.putExtra("isEditing", true);
                startActivity(intent);
                return true;

            case R.id.action_logout:
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(SharedData.IS_USER_SAVED, false);
                editor.putString(SharedData.PHONE, "");
                editor.putString(SharedData.PASS, "");
                editor.putInt(SharedData.USER_TYPE, 0);
                editor.apply();
                SharedData.supplier = null;
                Intent i = new Intent(this, UserTypeActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}