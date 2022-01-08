package com.app.pethouse.activities.supplier;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.app.pethouse.R;
import com.app.pethouse.adapter.RequestsAdapter;
import com.app.pethouse.callback.OrderCareCallback;
import com.app.pethouse.controller.OrderCareController;
import com.app.pethouse.model.OrderCareModel;
import com.app.pethouse.utils.LoadingHelper;
import com.app.pethouse.utils.SharedData;

import java.util.ArrayList;

public class RequestsActivity extends AppCompatActivity implements RequestsAdapter.RequestsListener{
    private LoadingHelper loadingHelper;
    private RequestsAdapter adapter;
    private RecyclerView recyclerView;
    private TextView noList;
    private ArrayList<OrderCareModel> currentList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        recyclerView = findViewById(R.id.recycler_view);
        noList = findViewById(R.id.no_items);
        loadingHelper = new LoadingHelper(this);
        getData();
    }

    private void getData() {
        loadingHelper.showLoading("");
        new OrderCareController().getOrderByTime(SharedData.currentTime.getDays(), new OrderCareCallback() {
            @Override
            public void onSuccess(ArrayList<OrderCareModel> orders) {
                loadingHelper.dismissLoading();
                currentList = orders;
                noList.setText("No Orders Found!");
                if (currentList.size() > 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    noList.setVisibility(View.GONE);
                    if (adapter == null || adapter.getData().size() == 0) {
                        adapter = new RequestsAdapter(currentList, RequestsActivity.this);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(RequestsActivity.this));
                    } else {
                        adapter.updateData(currentList);
                    }
                } else {
                    noList.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFail(String error) {
                loadingHelper.dismissLoading();
                Toast.makeText(RequestsActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void response(int position, boolean isAccepted) {
        loadingHelper.showLoading("");
        new OrderCareController().responseOnRequest(adapter.getData().get(position), isAccepted, new OrderCareCallback() {
            @Override
            public void onSuccess(ArrayList<OrderCareModel> reservations) {
                loadingHelper.dismissLoading();
                Toast.makeText(RequestsActivity.this, "Done!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFail(String error) {
                loadingHelper.dismissLoading();
                Toast.makeText(RequestsActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }
}