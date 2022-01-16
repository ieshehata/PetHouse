package com.app.pethouse.activities.supplier.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pethouse.R;
import com.app.pethouse.adapter.ConversationAdapter;
import com.app.pethouse.callback.ConversationCallback;
import com.app.pethouse.controller.ConversationController;
import com.app.pethouse.model.ConversationModel;
import com.app.pethouse.utils.LoadingHelper;

import java.util.ArrayList;

public class SupplierChatFragment extends Fragment implements ConversationAdapter.ConversationListener, View.OnClickListener{
    private LoadingHelper loadingHelper;
    private RecyclerView recyclerView;
    private Button allButton, groupButton, privateButton;
    private ConversationAdapter adapter;
    private ArrayList<ConversationModel> generalChats = new ArrayList<>();
    private ArrayList<ConversationModel> groupChats = new ArrayList<>();
    private ArrayList<ConversationModel> privateChats = new ArrayList<>();
    private int listFilter = 0; //0 -> general, 1 -> group, 2 -> private


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        allButton = view.findViewById(R.id.all_button);
        groupButton = view.findViewById(R.id.group_button);
        privateButton = view.findViewById(R.id.private_button);
        allButton.setOnClickListener(SupplierChatFragment.this);
        groupButton.setOnClickListener(SupplierChatFragment.this);
        privateButton.setOnClickListener(SupplierChatFragment.this);
        loadingHelper = new LoadingHelper(getActivity());

        load();
        return view;

    }

    private void load() {
        loadingHelper.showLoading("");
        new ConversationController().getConversationsAlways(new ConversationCallback() {
            @Override
            public void onSuccess(ArrayList<ConversationModel> conversations) {
                loadingHelper.dismissLoading();
                generalChats = new ArrayList<>();
                groupChats = new ArrayList<>();
                privateChats = new ArrayList<>();
                for(ConversationModel conversation : conversations) {
                    if(conversation.getType() == 0 ) {
                        generalChats.add(conversation);
                    }else if(conversation.getType() == 1  ) {
                        groupChats.add(conversation);
                    }else if(conversation.getType() == 2 ){
                        privateChats.add(conversation);
                    }
                }

                if(adapter == null || adapter.getData().size() == 0) {
                    if(listFilter == 0) {
                        adapter = new ConversationAdapter(generalChats, SupplierChatFragment.this);
                    }else if(listFilter == 1) {
                        adapter = new ConversationAdapter(groupChats, SupplierChatFragment.this);
                    }else if(listFilter == 2) {
                        adapter = new ConversationAdapter(privateChats, SupplierChatFragment.this);
                    }
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                }else {
                    if(listFilter == 0) {
                        adapter.updateData(generalChats);
                    }else if(listFilter == 1) {
                        adapter.updateData(groupChats);
                    }else if(listFilter == 2) {
                        adapter.updateData(privateChats);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.all_button:
                listFilter = 0;
                allButton.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryMidDark));
                allButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorWhite));

                groupButton.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorLightGray));
                groupButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryVeryDark));

                privateButton.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorLightGray));
                privateButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryVeryDark));
                filterUpdated();
                break;

            case R.id.group_button:
                listFilter = 1;
                allButton.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorLightGray));
                allButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryVeryDark));

                groupButton.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryMidDark));
                groupButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorWhite));

                privateButton.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorLightGray));
                privateButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryVeryDark));
                filterUpdated();
                break;

            case R.id.private_button:
                listFilter = 2;
                allButton.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorLightGray));
                allButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryVeryDark));

                groupButton.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorLightGray));
                groupButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryVeryDark));

                privateButton.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryMidDark));
                privateButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorWhite));
                filterUpdated();
                break;
        }
    }

    private void filterUpdated() {
        if(listFilter == 0) {
            adapter.updateData(generalChats);
        }else if(listFilter == 1) {
            adapter.updateData(groupChats);
        }else if(listFilter == 2) {
            adapter.updateData(privateChats);
        }
    }

    @Override
    public void response(int position, boolean isBlocking) {
        loadingHelper.showLoading("");
        ConversationModel conversation = adapter.getData().get(position);
        conversation.setState(isBlocking ? -1 : 1);
        new ConversationController().save(conversation, new ConversationCallback() {
            @Override
            public void onSuccess(ArrayList<ConversationModel> conversations) {
                loadingHelper.dismissLoading();
            }

            @Override
            public void onFail(String error) {
                loadingHelper.dismissLoading();
                Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void deleteItem(int position) {
        loadingHelper.showLoading("");
        new ConversationController().delete(adapter.getData().get(position), new ConversationCallback() {
            @Override
            public void onSuccess(ArrayList<ConversationModel> conversations) {
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
    public void view(int position) {

    }
}
