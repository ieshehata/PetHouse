package com.app.pethouse.activities.admin.fragment;

import android.content.Intent;
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
import com.app.pethouse.activities.general.SuppliersDetailsActivity;
import com.app.pethouse.adapter.SupplierReqAdapter;
import com.app.pethouse.callback.SupplierReqCallback;
import com.app.pethouse.controller.SupplierReqController;
import com.app.pethouse.model.SuppliersReqModel;
import com.app.pethouse.utils.LoadingHelper;
import com.app.pethouse.utils.SharedData;

import java.util.ArrayList;

public class SupplierReqFragment extends Fragment implements SupplierReqAdapter.SupplierReqListener {

    private LinearLayout root;
    private LoadingHelper loadingHelper;
    private SupplierReqAdapter adapter;
    private RecyclerView recyclerView;
    private SuppliersReqModel req = new SuppliersReqModel();
    private SuppliersReqModel.ClassNew newClass = new SuppliersReqModel.ClassNew();
    private TextView noList;
    private ArrayList<SuppliersReqModel> currentList = new ArrayList<>();

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
        new SupplierReqController().geRequestsAlways(new SupplierReqCallback() {
            @Override
            public void onSuccess(ArrayList<SuppliersReqModel> requests) {
                loadingHelper.dismissLoading();
                currentList = requests;
                noList.setText("No Requests Found!");
                if (currentList.size() > 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    noList.setVisibility(View.GONE);
                    if (adapter == null || adapter.getData().size() == 0) {
                        adapter = new SupplierReqAdapter(currentList, SupplierReqFragment.this);
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

    @Override
    public void response(int position, boolean isAccepted) {
        loadingHelper.showLoading("");
        new SupplierReqController().responseOnRequest(adapter.getData().get(position), isAccepted, new SupplierReqCallback() {
            @Override
            public void onSuccess(ArrayList<SuppliersReqModel> requests) {
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

    @Override
    public void view(int position) {
        SharedData.supplier = adapter.getData().get(position).getSupplier();
        Intent intent = new Intent(getActivity(), SuppliersDetailsActivity.class);
        startActivity(intent);
    }
}