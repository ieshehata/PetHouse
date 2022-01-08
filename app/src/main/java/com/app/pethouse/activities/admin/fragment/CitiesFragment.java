package com.app.pethouse.activities.admin.fragment;

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
import com.app.pethouse.adapter.CitiesAdapter;
import com.app.pethouse.callback.CityCallback;
import com.app.pethouse.controller.CityController;
import com.app.pethouse.dialogs.CityDialog;
import com.app.pethouse.model.CityModel;
import com.app.pethouse.model.GovernorateModel;
import com.app.pethouse.utils.LoadingHelper;
import com.app.pethouse.utils.SharedData;

import java.util.ArrayList;

public class CitiesFragment extends Fragment implements CitiesAdapter.CityListener, CityDialog.DataDialogListener {

    private LinearLayout root;
    private LoadingHelper loadingHelper;
    private CitiesAdapter adapter;
    private RecyclerView recyclerView;
    private TextView noList;
    private ArrayList<CityModel> currentList = new ArrayList<>();
    private CityModel chosenCity = new CityModel();

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
        new CityController().getCitiesAlways(new CityCallback() {
            @Override
            public void onSuccess(ArrayList<CityModel> cities) {
                loadingHelper.dismissLoading();
                currentList = cities;
                SharedData.allCities = cities;
                noList.setText("No Cities Found!");
                if (currentList.size() > 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    noList.setVisibility(View.GONE);
                    if (adapter == null || adapter.getData().size() == 0) {
                        adapter = new CitiesAdapter(currentList, CitiesFragment.this);
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
    public void onClick(int position) {
        chosenCity = adapter.getData().get(position);
        CityDialog dialog = new CityDialog(adapter.getData().get(position).getName(), chosenCity.getGovernorateKey());
        dialog.show(CitiesFragment.this.getChildFragmentManager(), "dialog");
    }

    @Override
    public void deleteItem(int position) {
        loadingHelper.showLoading("");
        chosenCity = adapter.getData().get(position);
        new CityController().delete(chosenCity, new CityCallback() {
            @Override
            public void onSuccess(ArrayList<CityModel> governorates) {
                loadingHelper.dismissLoading();
                Toast.makeText(getActivity(), "deleted!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFail(String error) {
                loadingHelper.dismissLoading();
                Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void getData(String name, GovernorateModel governorate) {
        chosenCity.setName(name);
        chosenCity.setGovernorateKey(governorate.getKey());
        new CityController().save(chosenCity, new CityCallback() {
            @Override
            public void onSuccess(ArrayList<CityModel> cities) {}

            @Override
            public void onFail(String error) {
                Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onError(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
    }
}
