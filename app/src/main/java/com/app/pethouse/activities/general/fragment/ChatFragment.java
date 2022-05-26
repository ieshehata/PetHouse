package com.app.pethouse.activities.general.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pethouse.R;
import com.app.pethouse.adapter.ConversationAdapter;
import com.app.pethouse.callback.ConversationCallback;
import com.app.pethouse.controller.ConversationController;
import com.app.pethouse.model.ConversationModel;
import com.app.pethouse.utils.LoadingHelper;
import com.app.pethouse.utils.SharedData;

import java.util.ArrayList;

public class ChatFragment extends Fragment {

    private ArrayList<ConversationModel> currentList = new ArrayList<>();
    private LoadingHelper loadingHelper;
    private ConversationAdapter adapter;
    private TextView noList;
    private RecyclerView recyclerView;




    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        noList = view.findViewById(R.id.no_list);
        loadingHelper = new LoadingHelper(getActivity());

        getData();
        return view;

    }
    private void getData() {
        loadingHelper.showLoading("");
        new ConversationController().getConversationsByUser(SharedData.currentUser.getKey(), new ConversationCallback() {
            @Override
            public void onSuccess(ArrayList<ConversationModel> conversations) {
                currentList = conversations;
                if (currentList.size() > 0) {
                    loadingHelper.dismissLoading();
                    recyclerView.setVisibility(View.VISIBLE);
                    noList.setVisibility(View.GONE);
                    if (adapter == null || adapter.getData().size() == 0) {
                        adapter = new ConversationAdapter(currentList);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    } else {
                        adapter.updateData(currentList);
                    }
                } else {
                    if(!SharedData.adminUser.getKey().equals(SharedData.currentUser.getKey()) && SharedData.userType != 1) {
                        new ConversationController().newConversation(SharedData.adminUser, new ConversationCallback() {
                            @Override
                            public void onSuccess(ArrayList<ConversationModel> conversations) {
                                loadingHelper.dismissLoading();
                                SharedData.currentConversation = conversations.get(0);
                            }

                            @Override
                            public void onFail(String error) {
                                loadingHelper.dismissLoading();
                                Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                            }
                        });
                    }

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
