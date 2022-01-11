package com.app.pethouse.activities.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.app.pethouse.R;
import com.app.pethouse.activities.admin.fragment.TypePageAdapter;
import com.app.pethouse.callback.GovernorateCallback;
import com.app.pethouse.controller.GovernorateController;
import com.app.pethouse.dialogs.CityDialog;
import com.app.pethouse.dialogs.DataDialog;
import com.app.pethouse.model.GovernorateModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class TypesActivity extends AppCompatActivity implements DataDialog.DataDialogListener{
    View mRootView;
    int currentTab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_types);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        currentTab = intent.getIntExtra("tab", 0);
        mRootView = findViewById(R.id.root);
        TypePageAdapter dataSectionsPageAdapter = new TypePageAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(dataSectionsPageAdapter);
        final TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        final FloatingActionButton fab = findViewById(R.id.fab);

        tabs.selectTab(tabs.getTabAt(currentTab));

        fab.setOnClickListener(view -> {
            if(tabs.getSelectedTabPosition() == 0) { //Gov
                currentTab = 0;
                DataDialog dialog = new DataDialog();
                dialog.show(getSupportFragmentManager() , "data dialog");
            }else if(tabs.getSelectedTabPosition() == 1) { //Cities
                currentTab = 1;
                CityDialog dialog = new CityDialog();
                dialog.show(getSupportFragmentManager() , "data dialog");
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, AdminMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void showSnackbar(String message) {
        Snackbar.make(mRootView, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void getData(String name) {
        if(currentTab == 0){
            new GovernorateController().newGovernorate(name, new GovernorateCallback() {
                @Override
                public void onSuccess(ArrayList<GovernorateModel> governorates) {}

                @Override
                public void onFail(String error) {
                    showSnackbar(error);
                }
            });
        }
    }

    @Override
    public void onError(String error) {
        showSnackbar(error);
    }
}