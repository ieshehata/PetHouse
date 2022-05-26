package com.app.pethouse.activities.owener.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pethouse.R;
import com.app.pethouse.activities.general.ChatActivity;
import com.app.pethouse.activities.general.SuppliersDetailsActivity;
import com.app.pethouse.adapter.SuppliersAdapter;
import com.app.pethouse.callback.CityCallback;
import com.app.pethouse.callback.ConversationCallback;
import com.app.pethouse.callback.GovernorateCallback;
import com.app.pethouse.callback.UserCallback;
import com.app.pethouse.controller.CityController;
import com.app.pethouse.controller.ConversationController;
import com.app.pethouse.controller.GovernorateController;
import com.app.pethouse.controller.UserController;
import com.app.pethouse.model.CityModel;
import com.app.pethouse.model.ConversationModel;
import com.app.pethouse.model.GovernorateModel;
import com.app.pethouse.model.UserModel;
import com.app.pethouse.utils.LoadingHelper;
import com.app.pethouse.utils.SharedData;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class OwenerHomeFragment extends Fragment implements SuppliersAdapter.SupplierListener{
    private LoadingHelper loadingHelper;
    private SuppliersAdapter adapter;
    private RecyclerView recyclerView;
    private TextView noList;
    private ArrayList<UserModel> allUsers = new ArrayList<>();
    private ArrayList<UserModel> filteredUsers = new ArrayList<>();
    private AutoCompleteTextView governorate, city;
    private TextInputLayout cityLayout;
    private ArrayList<GovernorateModel> governorates = new ArrayList<>();
    private ArrayList<CityModel> allCities = new ArrayList<>();
    private ArrayList<CityModel> cities = new ArrayList<>();
    private ArrayList<String> governoratesNames = new ArrayList<>();
    private ArrayList<String> citiesNames = new ArrayList<>();
    private GovernorateModel chosenGovernorate;
    private CityModel chosenCity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_profile, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        noList = view.findViewById(R.id.no_items);
        loadingHelper = new LoadingHelper(getActivity());
        governorate = view.findViewById(R.id.governorate);
        city = view.findViewById(R.id.city);
        cityLayout = view.findViewById(R.id.city_field);
        cityLayout.setVisibility(View.GONE);
        governorate.setOnItemClickListener((parent, view1, position, id) -> {
            chosenGovernorate = governorates.get(position);
            cityLayout.setVisibility(View.VISIBLE);
            cities = new ArrayList<>();
            city.setText("");
            chosenCity = null;
            for(CityModel city : allCities) {
                if(city.getGovernorateKey().equals(chosenGovernorate.getKey())) {
                    cities.add(city);
                }
            }
            if(cities.size() > 0) {
                citiesNames = new ArrayList<>();
                for(CityModel city: cities) {
                    citiesNames.add(city.getName());
                }
                ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), R.layout.list_item, citiesNames);
                city.setAdapter(adapter);
            }else {
                cityLayout.setVisibility(View.GONE);
            }

            refreshList();
        });

        city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                chosenCity = cities.get(position);
                refreshList();
            }
        });

        getData();
        return view;
    }

    private void getData() {
        loadingHelper.showLoading("");
        new GovernorateController().getGovernorates(new GovernorateCallback() {
            @Override
            public void onSuccess(ArrayList<GovernorateModel> governoratesList) {
                governorates = governoratesList;
                new CityController().getCities(new CityCallback() {
                    @Override
                    public void onSuccess(ArrayList<CityModel> citiesList) {
                        allCities = citiesList;
                        new UserController().getUsersAlways(new UserCallback() {
                            @Override
                            public void onSuccess(ArrayList<UserModel> users) {
                                loadingHelper.dismissLoading();
                                allUsers = new ArrayList<>();
                                for(UserModel cargiver : users) {
                                    if(cargiver.getUserType() == 2 && cargiver.getState() == 1 && cargiver.getActivated() == 1) {
                                        allUsers.add(cargiver);
                                    }
                                }
                                setData();
                            }
                            @Override
                            public void onFail(String error) {
                                loadingHelper.dismissLoading();
                                Toast.makeText(getActivity(),error, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    @Override
                    public void onFail(String error) {
                        loadingHelper.dismissLoading();
                        Toast.makeText(getActivity(),error, Toast.LENGTH_LONG).show();
                    }
                });
            }
            @Override
            public void onFail(String error) {
                loadingHelper.dismissLoading();
                Toast.makeText(getActivity(),error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setData() {
        governoratesNames = new ArrayList<>();
        for(GovernorateModel governorate: governorates) {
            governoratesNames.add(governorate.getName());
        }
        try {
            ArrayAdapter governoratesAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item, governoratesNames);
            governorate.setAdapter(governoratesAdapter);
            refreshList();
        } catch (Exception e) {}

    }

    private void refreshList() {
        filteredUsers = new ArrayList<>();
        if(chosenGovernorate != null) {
            for(UserModel user : allUsers) {
                if(user.getGovernorate().getKey().equals(chosenGovernorate.getKey()) && (chosenCity == null || user.getCity().getKey().equals(chosenCity.getKey()))) {
                    filteredUsers.add(user);
                }
            }
        } else {
            filteredUsers.addAll(allUsers);
        }
        noList.setText("No suppliers found with this filter!");
        if (filteredUsers.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            noList.setVisibility(View.GONE);
            if (adapter == null || adapter.getData().size() == 0) {
                adapter = new SuppliersAdapter(filteredUsers, OwenerHomeFragment.this);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            } else {
                adapter.updateData(filteredUsers);
            }
        } else {
            noList.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void response(int position, boolean isBlocking) {

    }

    @Override
    public void view(int position) {
        SharedData.stalkedUser = adapter.getData().get(position);
        Intent intent = new Intent(getActivity(), SuppliersDetailsActivity.class);
        startActivity(intent);
    }

    @Override
    public void deleteItem(int position) {

    }

    @Override
    public void chat(int position) {
        UserModel user = adapter.getData().get(position);
        loadingHelper.showLoading("");
        new ConversationController().getConversationsByTwoUsers(SharedData.currentUser.getKey(),
                user.getKey(), new ConversationCallback() {
                    @Override
                    public void onSuccess(ArrayList<ConversationModel> conversations) {
                        if (conversations.size() > 0) {
                            loadingHelper.dismissLoading();
                            SharedData.currentConversation = conversations.get(0);
                            Intent intent = new Intent(getActivity(), ChatActivity.class);
                            startActivity(intent);
                        } else {
                            new ConversationController().newConversation(user, new ConversationCallback() {
                                @Override
                                public void onSuccess(ArrayList<ConversationModel> conversations) {
                                    loadingHelper.dismissLoading();
                                    SharedData.currentConversation = conversations.get(0);
                                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                                    startActivity(intent);
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
                    public void onFail(String error) {
                        loadingHelper.dismissLoading();
                        Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                    }
                });
    }
}