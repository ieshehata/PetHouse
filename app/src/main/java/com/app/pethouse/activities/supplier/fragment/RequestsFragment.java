package com.app.pethouse.activities.supplier.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pethouse.R;
import com.app.pethouse.adapter.RequestsAdapter;
import com.app.pethouse.callback.OrderCareCallback;
import com.app.pethouse.controller.OrderCareController;
import com.app.pethouse.model.OrderCareModel;
import com.app.pethouse.utils.LoadingHelper;
import com.app.pethouse.utils.SharedData;

import java.util.ArrayList;

public class RequestsFragment extends Fragment implements RequestsAdapter.RequestsListener{
    private LinearLayout root;
    private LoadingHelper loadingHelper;
    private RequestsAdapter adapter;
    private RecyclerView recyclerView;
    private TextView noList;
    private ArrayList<OrderCareModel> currentList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        root = view.findViewById(R.id.root);
        recyclerView = view.findViewById(R.id.recycler_view);
        noList = view.findViewById(R.id.no_items);
        loadingHelper = new LoadingHelper(getActivity());

        getData();
        return view;
    }

    private void getData() {
        loadingHelper.showLoading("");
        if(SharedData.userType == 2) {
            new OrderCareController().getOrderBySupplier(SharedData.currentUser.getKey(), new OrderCareCallback() {
                @Override
                public void onSuccess(ArrayList<OrderCareModel> requests) {
                    loadingHelper.dismissLoading();
                    currentList =requests;
                    noList.setText("No Requests Found!");
                    if (currentList.size() > 0) {
                        recyclerView.setVisibility(View.VISIBLE);
                        noList.setVisibility(View.GONE);
                        if (adapter == null || adapter.getData().size() == 0) {
                            adapter = new RequestsAdapter(currentList, RequestsFragment.this);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
                    Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                }
            });
        }else if(SharedData.userType == 3) {
            new OrderCareController().getOrderByOwener(SharedData.currentUser.getKey(), new OrderCareCallback() {
                @Override
                public void onSuccess(ArrayList<OrderCareModel> requests) {
                    loadingHelper.dismissLoading();
                    currentList = requests;
                    noList.setText("No Requests Found!");
                    if (currentList.size() > 0) {
                        recyclerView.setVisibility(View.VISIBLE);
                        noList.setVisibility(View.GONE);
                        if (adapter == null || adapter.getData().size() == 0) {
                            adapter = new RequestsAdapter(currentList, RequestsFragment.this);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
                    Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void response(int position, boolean isAccepted) {
        loadingHelper.showLoading("");
        new OrderCareController().responseOnRequest(adapter.getData().get(position), isAccepted, new OrderCareCallback() {
            @Override
            public void onSuccess(ArrayList<OrderCareModel> orders) {
                loadingHelper.dismissLoading();
                Toast.makeText(getActivity(), "Done!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFail(String error) {
                loadingHelper.dismissLoading();
                Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
