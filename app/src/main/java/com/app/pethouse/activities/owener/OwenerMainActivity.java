package com.app.pethouse.activities.owener;

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
import com.app.pethouse.activities.auth.UserTypeActivity;
import com.app.pethouse.utils.SharedData;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class OwenerMainActivity extends AppCompatActivity {
    private NavController navController;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owener_main);
        sharedPref = this.getSharedPreferences(SharedData.PREF_KEY, Context.MODE_PRIVATE);
        setTitle(SharedData.owner.getName());
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case R.id.action_logout:
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(SharedData.IS_USER_SAVED, false);
                editor.putString(SharedData.PHONE, "");
                editor.putString(SharedData.PASS, "");
                editor.putInt(SharedData.USER_TYPE, 0);
                editor.apply();
                SharedData.owner = null;
                Intent i = new Intent(this, UserTypeActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}