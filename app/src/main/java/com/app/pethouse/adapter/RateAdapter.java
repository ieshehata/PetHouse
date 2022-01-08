package com.app.pethouse.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pethouse.R;
import com.app.pethouse.model.RateModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class RateAdapter extends RecyclerView.Adapter<RateAdapter.ViewHolder> {
    private ArrayList<RateModel> mData = new ArrayList<>();
    private Context context;
    private RateListener mListener;
    private SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy - hh:mm aa");

    public RateAdapter(ArrayList<RateModel> data, RateListener listener) {
        this.mListener = listener;
        this.mData.addAll(data);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.row_rate, parent, false);
        return new ViewHolder(view, mListener);
    }

    @SuppressLint("UseCompatTextViewDrawableApis")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.ownerName.setText(mData.get(position).getFromOwener().getName());
        holder.ratingBar.setRating(mData.get(position).getRate());
        holder.ratingBar.setIsIndicator(true);
        holder.date.setText(df.format(mData.get(position).getCreatedAt()));
        if(mData.get(position).getComment() != null && !mData.get(position).getComment().isEmpty()) {
            holder.commentLayout.setVisibility(View.VISIBLE);
            holder.comment.setText(mData.get(position).getComment());
        }else {
            holder.commentLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public ArrayList<RateModel> getData() {
        return this.mData;
    }

    public void updateData(ArrayList<RateModel> newData) {
        mData.clear();
        mData.addAll(newData);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        LinearLayout commentLayout;
        TextView date, comment, ownerName;
        RatingBar ratingBar;
        ViewHolder(View itemView,RateListener listener) {
            super(itemView);
            view = itemView.findViewById(R.id.view);
            commentLayout = itemView.findViewById(R.id.comment_layout);
            date = itemView.findViewById(R.id.date);
            comment = itemView.findViewById(R.id.comment);
            ownerName = itemView.findViewById(R.id.owener_name);
            ratingBar=itemView.findViewById(R.id.simpleRatingBar);
        }
    }

    public interface RateListener {

    }
}
