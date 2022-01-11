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
import com.app.pethouse.adapter.TypeAdapter;
import com.app.pethouse.callback.TypeCallback;
import com.app.pethouse.controller.TypeController;
import com.app.pethouse.dialogs.DataDialog;
import com.app.pethouse.model.TypeModel;
import com.app.pethouse.utils.LoadingHelper;
import com.app.pethouse.utils.SharedData;

import java.util.ArrayList;

public class TypesFragment extends Fragment implements TypeAdapter.TypeListener, DataDialog.DataDialogListener{
    private LinearLayout root;
    private LoadingHelper loadingHelper;
    private TypeAdapter adapter;
    private RecyclerView recyclerView;
    private TextView noList;
    private ArrayList<TypeModel> currentList = new ArrayList<>();
    private TypeModel chosenType;
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
        new TypeController().getTypesAlways(new TypeCallback() {
            @Override
            public void onSuccess(ArrayList<TypeModel> types) {
                loadingHelper.dismissLoading();
                currentList = types;
                SharedData.allTypes = types;
                noList.setText("No Types Found!");
                if (currentList.size() > 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    noList.setVisibility(View.GONE);
                    if (adapter == null || adapter.getData().size() == 0) {
                        adapter = new TypeAdapter(currentList, TypesFragment.this);
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
        chosenType = adapter.getData().get(position);
        DataDialog dialog = new DataDialog(adapter.getData().get(position).getName());
        dialog.show(TypesFragment.this.getChildFragmentManager(), "dialog");
    }

    @Override
    public void deleteItem(int position) {
        loadingHelper.showLoading("");
        new TypeController().delete(adapter.getData().get(position), new TypeCallback() {
            @Override
            public void onSuccess(ArrayList<TypeModel> types) {
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
    public void getData(String name) {
        chosenType.setName(name);
        new TypeController().save(chosenType, new TypeCallback() {
            @Override
            public void onSuccess(ArrayList<TypeModel> types) { }

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
