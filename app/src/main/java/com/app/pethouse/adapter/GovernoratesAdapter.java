package com.app.pethouse.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pethouse.R;
import com.app.pethouse.model.GovernorateModel;

import java.util.ArrayList;

public class GovernoratesAdapter extends RecyclerView.Adapter<GovernoratesAdapter.ViewHolder> {
    private ArrayList<GovernorateModel> mData;
    private GovernorateListener mGovernorateListener;
    private Context context;

    public GovernoratesAdapter(ArrayList<GovernorateModel> data, GovernorateListener governorateListener) {
        this.mData = data;
        this.mGovernorateListener = governorateListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.row_data, parent, false);
        return new ViewHolder(view, mGovernorateListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(mData.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public ArrayList<GovernorateModel> getData() {
        return this.mData;
    }

    public void updateData(ArrayList<GovernorateModel> newData) {
        mData.clear();
        mData.addAll(newData);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View view;
        TextView name;
        ImageButton delete;
        GovernorateListener listener;
        ViewHolder(View itemView, GovernorateListener listener) {
            super(itemView);
            view = itemView.findViewById(R.id.view);
            name = itemView.findViewById(R.id.name);
            delete = itemView.findViewById(R.id.delete);
            this.listener = listener;
            view.setOnClickListener(this);
            delete.setOnClickListener(v -> listener.deleteItem(getAdapterPosition()));
        }

        @Override
        public void onClick(View v) {
            listener.onClick(getAdapterPosition());
        }
    }

    public interface GovernorateListener {
        void onClick(int position);
        void deleteItem(int position);

    }
}