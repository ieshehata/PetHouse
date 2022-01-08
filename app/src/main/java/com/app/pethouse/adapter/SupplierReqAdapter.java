package com.app.pethouse.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pethouse.R;
import com.app.pethouse.model.SuppliersReqModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class SupplierReqAdapter extends RecyclerView.Adapter<SupplierReqAdapter.ViewHolder> {
    private ArrayList<SuppliersReqModel> mData = new ArrayList<>();
    private SupplierReqListener mListener;
    private Context context;
    private SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy - hh:mm aa");
    @SuppressLint("SimpleDateFormat")

    public SupplierReqAdapter(ArrayList<SuppliersReqModel> data, SupplierReqListener listener) {
        mData.clear();
        this.mData.addAll(data);
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.row_supplier_request, parent, false);
        return new ViewHolder(view, mListener);
    }

    @SuppressLint({"UseCompatTextViewDrawableApis", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(mData.get(position).getSupplier().getName());
        holder.email.setText(mData.get(position).getSupplier().getEmail());
        holder.phone.setText(mData.get(position).getSupplier().getPhone());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public ArrayList<SuppliersReqModel> getData() {
        return this.mData;
    }

    public void updateData(ArrayList<SuppliersReqModel> newData) {
        mData.clear();
        mData.addAll(newData);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView date, name, email,  phone;
        Button acceptButton, rejectButton, show;
        SupplierReqListener mListener;
        ViewHolder(View itemView, SupplierReqListener listener) {
            super(itemView);
            view = itemView.findViewById(R.id.view);
            date = itemView.findViewById(R.id.date);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            phone = itemView.findViewById(R.id.phone);
            acceptButton = itemView.findViewById(R.id.accept);
            rejectButton = itemView.findViewById(R.id.reject);
            show = itemView.findViewById(R.id.show);

            this.mListener = listener;

            view.setOnClickListener(v -> {
                listener.view(getAdapterPosition());
            });

            show.setOnClickListener(v -> {
                listener.view(getAdapterPosition());
            });

            acceptButton.setOnClickListener(v -> {
                listener.response(getAdapterPosition(), true);
            });

            rejectButton.setOnClickListener(v -> {
                listener.response(getAdapterPosition(), false);
            });
        }
    }

    public interface SupplierReqListener {
        void response(int position, boolean isAccepted);
        void view(int position);
    }
}