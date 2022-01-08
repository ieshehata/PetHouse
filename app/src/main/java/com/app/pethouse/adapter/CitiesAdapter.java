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
import com.app.pethouse.model.CityModel;

import java.util.ArrayList;

public class CitiesAdapter extends RecyclerView.Adapter<CitiesAdapter.ViewHolder> {
    private ArrayList<CityModel> mData;
    private CityListener mCityListener;
    private Context context;

    public CitiesAdapter(ArrayList<CityModel> data, CityListener cityListener) {
        this.mData = data;
        this.mCityListener = cityListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.row_data, parent, false);
        return new ViewHolder(view, mCityListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(mData.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public ArrayList<CityModel> getData() {
        return this.mData;
    }

    public void remove(int position) {
        //mData.remove(position);
        notifyItemRemoved(position);
    }

    public void restore(CityModel item, int position) {
        //mData.add(position, item);
        notifyItemInserted(position);
    }

    public void updateData(ArrayList<CityModel> newData) {
        mData.clear();
        mData.addAll(newData);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View view;
        TextView name;
        ImageButton delete;

        CityListener listener;
        ViewHolder(View itemView, CityListener listener) {
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

    public interface CityListener {
        void onClick(int position);
        void deleteItem(int position);
    }
}
