package com.app.pethouse.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pethouse.R;
import com.app.pethouse.model.TypeModel;

import java.util.ArrayList;

public class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.ViewHolder> {
    private ArrayList<TypeModel> mData = new ArrayList<>();
    private Context context;
    private TypeAdapter.TypeListener mTypeListener;


    public TypeAdapter(ArrayList<TypeModel> data, TypeAdapter.TypeListener typeListener) {
        this.mData = data;
        this.mTypeListener = typeListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.row_type, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("UseCompatTextViewDrawableApis")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(mData.get(position).getName());

    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public ArrayList<TypeModel> getData() {
        return this.mData;
    }

    public void updateData(ArrayList<TypeModel> newData) {
        mData.clear();
        mData.addAll(newData);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        View view;
        TextView name;
        ImageButton delete;
        TypeAdapter.TypeListener listener;

        ViewHolder(View itemView) {
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


    public interface TypeListener {
        void onClick(int position);
        void deleteItem(int position);

    }

}
