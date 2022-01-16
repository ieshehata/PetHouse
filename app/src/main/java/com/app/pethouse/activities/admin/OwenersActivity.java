package com.app.pethouse.activities.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pethouse.R;
import com.app.pethouse.adapter.OwenerAdapter;
import com.app.pethouse.callback.UserCallback;
import com.app.pethouse.controller.UserController;
import com.app.pethouse.model.UserModel;
import com.app.pethouse.utils.LoadingHelper;

import java.util.ArrayList;

public class OwenersActivity extends AppCompatActivity implements OwenerAdapter.OwenerListener, View.OnClickListener{
    private LoadingHelper loadingHelper;
    private RecyclerView recyclerView;
    private Button allButton, activeButton, inactiveButton;
    private OwenerAdapter adapter;
    private ArrayList<UserModel> allOweners = new ArrayList<>();
    private ArrayList<UserModel> activeOweners = new ArrayList<>();
    private ArrayList<UserModel> inactiveOweners = new ArrayList<>();
    private int listFilter = 0; //0 -> All, 1 -> active, 2 -> inactive

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oweners);
        recyclerView = findViewById(R.id.recycler_view);
        allButton = findViewById(R.id.all_button);
        activeButton = findViewById(R.id.active_button);
        inactiveButton = findViewById(R.id.inactive_button);

        allButton.setOnClickListener(OwenersActivity.this);
        activeButton.setOnClickListener(OwenersActivity.this);
        inactiveButton.setOnClickListener(OwenersActivity.this);
        loadingHelper = new LoadingHelper(this);

        load();
    }

    private void load() {
        loadingHelper.showLoading("");
        new UserController().getUsersAlways(new UserCallback() {
            @Override
            public void onSuccess(ArrayList<UserModel> users) {
                loadingHelper.dismissLoading();
                allOweners = new ArrayList<>();
                activeOweners = new ArrayList<>();
                inactiveOweners = new ArrayList<>();
                for(UserModel user : users) {
                    if(user.getState() == 1 && user.getUserType() == 3) {
                        allOweners.add(user);
                        activeOweners.add(user);
                    }else if(user.getState() == -1 && user.getUserType() == 3) {
                        allOweners.add(user);
                        inactiveOweners.add(user);
                    }
                }
                if(adapter == null || adapter.getData().size() == 0) {
                    if(listFilter == 0) {
                        adapter = new OwenerAdapter(allOweners, OwenersActivity.this);
                    }else if(listFilter == 1) {
                        adapter = new OwenerAdapter(activeOweners, OwenersActivity.this);
                    }else if(listFilter == 2) {
                        adapter = new OwenerAdapter(inactiveOweners, OwenersActivity.this);
                    }
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(OwenersActivity.this));
                }else {
                    if(listFilter == 0) {
                        adapter.updateData(allOweners);
                    }else if(listFilter == 1) {
                        adapter.updateData(activeOweners);
                    }else if(listFilter == 2) {
                        adapter.updateData(inactiveOweners);
                    }
                }
            }

            @Override
            public void onFail(String error) {
                loadingHelper.dismissLoading();
                Toast.makeText(OwenersActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.all_button:
                listFilter = 0;
                allButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryMidDark));
                allButton.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));

                activeButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorLightGray));
                activeButton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryVeryDark));

                inactiveButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorLightGray));
                inactiveButton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryVeryDark));
                filterUpdated();
                break;

            case R.id.active_button:
                listFilter = 1;
                allButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorLightGray));
                allButton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryVeryDark));

                activeButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryMidDark));
                activeButton.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));

                inactiveButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorLightGray));
                inactiveButton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryVeryDark));
                filterUpdated();
                break;

            case R.id.inactive_button:
                listFilter = 2;
                allButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorLightGray));
                allButton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryVeryDark));

                activeButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorLightGray));
                activeButton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryVeryDark));

                inactiveButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryMidDark));
                inactiveButton.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
                filterUpdated();
                break;
        }
    }

    private void filterUpdated() {
        if(listFilter == 0) {
            adapter.updateData(allOweners);
        }else if(listFilter == 1) {
            adapter.updateData(activeOweners);
        }else if(listFilter == 2) {
            adapter.updateData(inactiveOweners);
        }
    }

    @Override
    public void response(int position, boolean isBlocking) {
        loadingHelper.showLoading("");
        UserModel user = adapter.getData().get(position);
        user.setState(isBlocking ? -1 : 1);
        user.setActivated(isBlocking ? -1 : 1);
        new UserController().save(user, new UserCallback() {
            @Override
            public void onSuccess(ArrayList<UserModel> oweners) {
                loadingHelper.dismissLoading();
            }

            @Override
            public void onFail(String error) {
                loadingHelper.dismissLoading();
                Toast.makeText(OwenersActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void deleteItem(int position) {
        loadingHelper.showLoading("");
        new UserController().delete(adapter.getData().get(position), new UserCallback() {
            @Override
            public void onSuccess(ArrayList<UserModel> oweners) {
                loadingHelper.dismissLoading();
                Toast.makeText(OwenersActivity.this, "deleted!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFail(String error) {
                loadingHelper.dismissLoading();
                Toast.makeText(OwenersActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void view(int position) {

    }
}