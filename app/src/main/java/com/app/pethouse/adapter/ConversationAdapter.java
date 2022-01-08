package com.app.pethouse.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pethouse.R;
import com.app.pethouse.activities.general.ChatActivity;
import com.app.pethouse.callback.ConversationCallback;
import com.app.pethouse.controller.ConversationController;
import com.app.pethouse.model.ConversationModel;
import com.app.pethouse.model.UserHeaderModel;
import com.app.pethouse.utils.SharedData;
import com.app.pethouse.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {
    private ArrayList<ConversationModel> mData = new ArrayList<>();
    private Context context;

    public ConversationAdapter(ArrayList<ConversationModel> data) {
        mData.clear();
        this.mData.addAll(data);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.row_conversation, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("UseCompatTextViewDrawableApis")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        UserHeaderModel other = new UserHeaderModel();
        for (UserHeaderModel participant : mData.get(position).getParticipants()) {
            if (!participant.getKey().equals(SharedData.currentUser.getKey()) && mData.get(position).getParticipants().size() == 2) {
                other = participant;
            }
        }

        holder.title.setText(other.getName());

        if (!TextUtils.isEmpty(other.getProfileImage())) {
            holder.profileImage.setImageTintList(null);
            Picasso.get()
                    .load(other.getProfileImage())
                    .into(holder.profileImage);
        }else if(other.getKey().equals(SharedData.adminUser.getKey())) {
            holder.profileImage.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPrimaryDark)));
            holder.profileImage.setBackgroundResource(R.drawable.gradient_back);
            holder.profileImage.setImageResource(R.drawable.ic_report_24);

        }

        if (mData.get(position).getLastMessage() != null) {
            holder.lastMessage.setText(mData.get(position).getLastMessage());
        } else {
            holder.lastMessage.setText("");
        }

        if (mData.get(position).getLastMessageDate() != null) {
            holder.date.setText(Utils.getTimeSince(mData.get(position).getLastMessageDate()));
        } else {
            holder.date.setText(Utils.getTimeSince(mData.get(position).getCreatedAt()));
        }

        if (mData.get(position).getLastMessageUserKey() != null && mData.get(position).getLastMessageUserKey().equals(SharedData.currentUser.getKey())) {
            holder.stateIcon.setVisibility(View.VISIBLE);
            if (mData.get(position).getLastMessageState() == 1) {
                holder.stateIcon.setImageResource(R.drawable.ic_seen_24);
                holder.stateIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPrimaryDark)));
            } else {
                holder.stateIcon.setImageResource(R.drawable.ic_delivered_24);
                holder.stateIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorDarkGray)));
            }
        } else {
            holder.stateIcon.setVisibility(View.GONE);
        }

    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public ArrayList<ConversationModel> getData() {
        return this.mData;
    }

    public void updateData(ArrayList<ConversationModel> newData) {
        mData.clear();
        mData.addAll(newData);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;

        ImageView profileImage, stateIcon;
        TextView title, lastMessage, date;

        ViewHolder(View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.view);
            profileImage = itemView.findViewById(R.id.profile_image);
            stateIcon = itemView.findViewById(R.id.state_icon);
            title = itemView.findViewById(R.id.title);
            lastMessage = itemView.findViewById(R.id.last_message);
            date = itemView.findViewById(R.id.date);

            view.setOnClickListener(v -> {
                if (!mData.get(getAdapterPosition()).getLastMessageUserKey().equals(SharedData.currentUser.getKey())) {
                    mData.get(getAdapterPosition()).setLastMessageState(1);
                    new ConversationController().save(mData.get(getAdapterPosition()), new ConversationCallback() {
                        @Override
                        public void onSuccess(ArrayList<ConversationModel> conversations) {
                        }

                        @Override
                        public void onFail(String error) {
                            Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                        }
                    });
                }
                SharedData.currentConversation = mData.get(getAdapterPosition());
                Intent postDetails = new Intent(context, ChatActivity.class);
                context.startActivity(postDetails);
            });
        }
    }
}