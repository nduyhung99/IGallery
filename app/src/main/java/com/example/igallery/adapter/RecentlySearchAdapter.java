package com.example.igallery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.animation.content.Content;
import com.example.igallery.R;

import java.util.List;

public class RecentlySearchAdapter extends RecyclerView.Adapter<RecentlySearchAdapter.RecentlySearchViewHolder>{
    private List<String> mListRecentlySearch;
    private Context mContext;
    private OnClickItemRecentlySearch mOnClickItemRecentlySearch;

    public void setmOnClickItemRecentlySearch(OnClickItemRecentlySearch mListener){
        this.mOnClickItemRecentlySearch = mListener;
    }

    public RecentlySearchAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<String> list){
        this.mListRecentlySearch = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecentlySearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recently_search,parent,false);
        return new RecentlySearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentlySearchAdapter.RecentlySearchViewHolder holder, int position) {
        String text = mListRecentlySearch.get(position);
        holder.txtSearch.setText(text);
        holder.txtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickItemRecentlySearch.onClick(text);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mListRecentlySearch!=null){
            return mListRecentlySearch.size();
        }
        return 0;
    }

    public class RecentlySearchViewHolder extends RecyclerView.ViewHolder{
        TextView txtSearch;

        public RecentlySearchViewHolder(@NonNull View itemView) {
            super(itemView);
            txtSearch = itemView.findViewById(R.id.txtSearch);
        }
    }

    public interface OnClickItemRecentlySearch{
        void onClick(String text);
    }
}
