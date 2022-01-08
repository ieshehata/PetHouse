package com.app.pethouse.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pethouse.R;
import com.app.pethouse.model.OrderCareModel;
import com.app.pethouse.utils.SharedData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.ViewHolder>{
    private ArrayList<OrderCareModel> mData;
    private RequestsListener mRequestsListener;
    private Context context;
    private SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

    public RequestsAdapter(ArrayList<OrderCareModel> data, RequestsListener requestsListener) {
        this.mData = data;
        this.mRequestsListener = requestsListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.row_request, parent, false);
        return new ViewHolder(view, mRequestsListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.ownerName.setText(mData.get(position).getOwener().getName());
        holder.fromDate.setText(df.format(mData.get(position).getDays().get(0)));
        holder.toDate.setText(df.format(mData.get(position).getDays().get(mData.get(position).getDays().size() - 1)));
        holder.price.setText(String.format("%.2f KWD", mData.get(position).getTotalPrice()));
        holder.description.setText(mData.get(position).getDescription());
        if(SharedData.userType == 2 && mData.get(position).getState() == 0) { //Caregiver
            holder.actionButtons.setVisibility(View.VISIBLE);
        }else {
            holder.actionButtons.setVisibility(View.GONE);
        }
        if(mData.get(position).getState() == 0){
            holder.status.setText("Waiting");
            holder.status.setBackgroundColor(context.getColor(R.color.colorYellow));
        }else if(mData.get(position).getState() == 1){
            holder.status.setText("Accepted");
            holder.status.setBackgroundColor(context.getColor(R.color.colorGreen));
        }else if(mData.get(position).getState() == -1){
            holder.status.setText("Rejected");
            holder.status.setBackgroundColor(context.getColor(R.color.colorRed));
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public ArrayList<OrderCareModel> getData() {
        return this.mData;
    }

    public void remove(int position) {
        //mData.remove(position);
        notifyItemRemoved(position);
    }

    public void restore(OrderCareModel item, int position) {
        //mData.add(position, item);
        notifyItemInserted(position);
    }

    public void updateData(ArrayList<OrderCareModel> newData) {
        mData.clear();
        mData.addAll(newData);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        LinearLayout actionButtons;
        TextView status, ownerName, fromDate, toDate,  price, description;
        Button rejectButton, acceptButton;
        RequestsListener listener;
        ViewHolder(View itemView, RequestsListener listener) {
            super(itemView);
            view = itemView.findViewById(R.id.view);
            status = itemView.findViewById(R.id.status);
            ownerName = itemView.findViewById(R.id.name);
            fromDate = itemView.findViewById(R.id.from);
            toDate = itemView.findViewById(R.id.to);
            price = itemView.findViewById(R.id.price);
            description = itemView.findViewById(R.id.description);
            actionButtons = itemView.findViewById(R.id.action_buttons);
            acceptButton = itemView.findViewById(R.id.accept);
            rejectButton = itemView.findViewById(R.id.reject);
            this.listener = listener;
            acceptButton.setOnClickListener(v -> {
                listener.response(getAdapterPosition(), true);
            });

            rejectButton.setOnClickListener(v -> {
                listener.response(getAdapterPosition(), false);
            });
        }
    }

    public interface RequestsListener {
        void response(int position, boolean isAccepted);
    }
}