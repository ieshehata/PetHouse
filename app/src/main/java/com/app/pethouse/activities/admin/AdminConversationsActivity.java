package com.app.pethouse.activities.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.app.pethouse.R;
import com.app.pethouse.adapter.ConversationAdapter;
import com.app.pethouse.callback.ConversationCallback;
import com.app.pethouse.controller.ConversationController;
import com.app.pethouse.model.ConversationModel;
import com.app.pethouse.utils.LoadingHelper;
import com.app.pethouse.utils.SharedData;

import java.util.ArrayList;

public class AdminConversationsActivity extends AppCompatActivity {

    private ArrayList<ConversationModel> currentList = new ArrayList<>();
    private LoadingHelper loadingHelper;
    private ConversationAdapter adapter;
    private TextView noList;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_conversations);

        setContentView(R.layout.activity_admin_conversations);
        setTitle("Conversations");
        recyclerView = findViewById(R.id.recycler_view);
        noList = findViewById(R.id.no_list);
        loadingHelper = new LoadingHelper(this);

        getData();
    }


    private void getData() {
        loadingHelper.showLoading("");
        new ConversationController().getConversationsByUser(SharedData.currentUser.getKey(), new ConversationCallback() {
            @Override
            public void onSuccess(ArrayList<ConversationModel> conversations) {
                loadingHelper.dismissLoading();
                currentList = conversations;
                noList.setText("No Conversation Found!");
                if (currentList.size() > 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    noList.setVisibility(View.GONE);
                    if (adapter == null || adapter.getData().size() == 0) {
                        adapter = new ConversationAdapter(currentList);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(AdminConversationsActivity.this));
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
                Toast.makeText(AdminConversationsActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }
}