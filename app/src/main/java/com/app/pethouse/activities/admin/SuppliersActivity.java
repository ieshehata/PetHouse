package com.app.pethouse.activities.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.app.pethouse.R;
import com.app.pethouse.activities.admin.fragment.SupplierPageAdapter;
import com.google.android.material.tabs.TabLayout;

public class SuppliersActivity extends AppCompatActivity {
    View mRootView;
    int currentTab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suppliers);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        currentTab = intent.getIntExtra("tab", 0);
        mRootView = findViewById(R.id.root);
        SupplierPageAdapter sectionsPageAdapter = new SupplierPageAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPageAdapter);
        final TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        tabs.selectTab(tabs.getTabAt(currentTab));
    }
}