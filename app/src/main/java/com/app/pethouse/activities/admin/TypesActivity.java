package com.app.pethouse.activities.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pethouse.R;
import com.app.pethouse.adapter.TypeAdapter;
import com.app.pethouse.callback.TypeCallback;
import com.app.pethouse.controller.TypeController;
import com.app.pethouse.dialogs.DataDialog;
import com.app.pethouse.dialogs.RateDialog;
import com.app.pethouse.model.TypeModel;
import com.app.pethouse.utils.LoadingHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class TypesActivity extends AppCompatActivity implements TypeAdapter.TypeListener, DataDialog.DataDialogListener{
     LoadingHelper loadingHelper;
     FloatingActionButton add;
     LinearLayout root;
     TypeAdapter adapter;
     RecyclerView recyclerView;
     TextView noList;
     ArrayList<TypeModel> currentList = new ArrayList<>();
     TypeModel chosenType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_types);
        add = findViewById(R.id.fab);
        loadingHelper = new LoadingHelper(this);
        root = findViewById(R.id.root);
        recyclerView = findViewById(R.id.recycler_view);
        noList = findViewById(R.id.no_items);

        // perform click event on button
        add.setOnClickListener(v -> {
            RateDialog dialog = new RateDialog(false);
            dialog.show(getSupportFragmentManager(), "dialog");
        });

        new TypeController().getTypes(new TypeCallback() {
            @Override
            public void onSuccess(ArrayList<TypeModel> rates) {
                loadingHelper.dismissLoading();
                currentList = rates;
                noList.setText("No Types Found!");
                if (currentList.size() > 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    noList.setVisibility(View.GONE);
                    if (adapter == null || adapter.getData().size() == 0) {
                        adapter = new TypeAdapter(currentList, TypesActivity.this);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(TypesActivity.this));
                    } else {
                        adapter.updateData(currentList);
                    }
                } else {
                    noList.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }

            }
            @Override
            public void onFail(String msg) {
                loadingHelper.dismissLoading();
                Toast.makeText(TypesActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onClick(int position) {
        chosenType = adapter.getData().get(position);
        DataDialog dialog = new DataDialog(adapter.getData().get(position).getName());
        dialog.show(TypesActivity.this.getSupportFragmentManager(), "dialog");
    }

    @Override
    public void deleteItem(int position) {
        loadingHelper.showLoading("");
        new TypeController().delete(adapter.getData().get(position), new TypeCallback() {
            @Override
            public void onSuccess(ArrayList<TypeModel> types) {
                loadingHelper.dismissLoading();
                Toast.makeText(TypesActivity.this, "deleted!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFail(String error) {
                loadingHelper.dismissLoading();
                Toast.makeText(TypesActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void getData(String name) {
        chosenType.setName(name);
        new TypeController().save(chosenType, new TypeCallback() {
            @Override
            public void onSuccess(ArrayList<TypeModel> rates) {
                Toast.makeText(TypesActivity.this, "Send", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFail(String msg) {
                Toast.makeText(TypesActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();

    }
}