package com.app.pethouse.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pethouse.R;
import com.app.pethouse.activities.auth.RegisterActivity;
import com.app.pethouse.activities.auth.SupplierRegisterActivity;
import com.app.pethouse.activities.general.ChatActivity;
import com.app.pethouse.activities.general.ImageActivity;
import com.app.pethouse.model.MessageModel;
import com.app.pethouse.utils.SharedData;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private ArrayList<MessageModel> mData = new ArrayList<>();
    private Context context;
    private SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());

    public MessageAdapter(ArrayList<MessageModel> data) {
        mData.clear();
        this.mData.addAll(data);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.row_message, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("UseCompatTextViewDrawableApis")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (mData.get(position).getAuthor().getKey().equals(SharedData.currentUser.getKey())) {
            if(mData.get(position).getText().equals("Ring")) {
                holder.myLayout.setVisibility(View.GONE);
                holder.otherLayout.setVisibility(View.GONE);
                holder.ringLayout.setVisibility(View.VISIBLE);

                holder.ringText.setText("Ring from me");
            }else {
                holder.myLayout.setVisibility(View.VISIBLE);
                holder.otherLayout.setVisibility(View.GONE);
                holder.ringLayout.setVisibility(View.GONE);

                holder.myText.setText(mData.get(position).getText());
                holder.myText.setVisibility(mData.get(position).getText().trim().isEmpty() ? View.GONE : View.VISIBLE);
                holder.myDate.setText(format.format(mData.get(position).getCreatedAt()));

                if (!TextUtils.isEmpty(mData.get(position).getImage())) {
                    holder.myImage.setVisibility(View.VISIBLE);
                    Picasso.get()
                            .load(mData.get(position).getImage())
                            .into(holder.myImage);
                } else {
                    holder.myImage.setVisibility(View.GONE);
                }

                if (mData.get(position).getState() == 1) {
                    holder.stateIcon.setImageResource(R.drawable.ic_seen_24);
                } else {
                    holder.stateIcon.setImageResource(R.drawable.ic_delivered_24);
                }
            }




        } else {
            if(mData.get(position).getText().equals("Ring")) {
                holder.myLayout.setVisibility(View.GONE);
                holder.otherLayout.setVisibility(View.GONE);
                holder.ringLayout.setVisibility(View.VISIBLE);

                holder.ringText.setText("Ring from " + mData.get(position).getAuthor().getName());
            }else {
                holder.otherLayout.setVisibility(View.VISIBLE);
                holder.myLayout.setVisibility(View.GONE);
                holder.ringLayout.setVisibility(View.GONE);

                holder.otherText.setText(mData.get(position).getText());
                holder.otherText.setVisibility(mData.get(position).getText().trim().isEmpty() ? View.GONE : View.VISIBLE);
                holder.otherDate.setText(format.format(mData.get(position).getCreatedAt()));

                if (!TextUtils.isEmpty(mData.get(position).getAuthor().getProfileImage())) {
                    holder.otherProfileImage.setVisibility(View.VISIBLE);
                    Picasso.get()
                            .load(mData.get(position).getAuthor().getProfileImage())
                            .into(holder.otherProfileImage);
                } else {
                    holder.otherProfileImage.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(mData.get(position).getImage())) {
                    holder.otherImage.setVisibility(View.VISIBLE);
                    Picasso.get()
                            .load(mData.get(position).getImage())
                            .into(holder.otherImage);
                } else {
                    holder.otherImage.setVisibility(View.GONE);
                }
            }

        }
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public ArrayList<MessageModel> getData() {
        return this.mData;
    }

    public void updateData(ArrayList<MessageModel> newData) {
        mData.clear();
        mData.addAll(newData);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;

        LinearLayout otherLayout, myLayout, ringLayout;
        TextView otherText, otherDate, myText, myDate, ringText;
        ImageView otherImage, myImage, stateIcon, otherProfileImage;

        ViewHolder(View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.view);
            otherLayout = itemView.findViewById(R.id.other_layout);
            myLayout = itemView.findViewById(R.id.my_layout);
            ringLayout = itemView.findViewById(R.id.ring_layout);
            otherText = itemView.findViewById(R.id.other_text);
            otherDate = itemView.findViewById(R.id.other_date);
            myText = itemView.findViewById(R.id.my_text);
            myDate = itemView.findViewById(R.id.my_date);
            ringText = itemView.findViewById(R.id.ring_text);
            stateIcon = itemView.findViewById(R.id.state_icon);
            otherImage = itemView.findViewById(R.id.other_image);
            myImage = itemView.findViewById(R.id.my_image);
            otherProfileImage = itemView.findViewById(R.id.other_profile_image);

            otherImage.setOnClickListener(v -> {
                SharedData.imageUrl = mData.get(getAdapterPosition()).getImage();
                Intent intent = new Intent(context, ImageActivity.class);
                context.startActivity(intent);
            });

            myImage.setOnClickListener(v -> {
                SharedData.imageUrl = mData.get(getAdapterPosition()).getImage();
                Intent intent = new Intent(context, ImageActivity.class);
                context.startActivity(intent);
            });

            otherProfileImage.setOnClickListener(v -> {
                SharedData.stalkedUserHeader = mData.get(getAdapterPosition()).getAuthor();
                if(SharedData.userType == 3) { //Owener
                    Intent intent = new Intent(context, RegisterActivity.class);
                    intent.putExtra("isEditing", false);
                    context.startActivity(intent);
                }else if(SharedData.userType == 2) { //Supplier
                    Intent intent = new Intent(context, SupplierRegisterActivity.class);
                    intent.putExtra("isEditing", false);
                    context.startActivity(intent);
                }

            });
        }
    }
}
