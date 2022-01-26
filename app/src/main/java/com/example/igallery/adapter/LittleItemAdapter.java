package com.example.igallery.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.igallery.R;
import com.example.igallery.model.Item;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class LittleItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<Item> mListItem;
    private Context mContext;
    private OnItemLittleClick mListener;
    private int lol=0;

    public LittleItemAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<Item> list){
        this.mListItem = list;
        notifyDataSetChanged();
    }

    public void setOnItemLittleClick(OnItemLittleClick mListener) {
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==1){
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_little,parent,false);
            ViewGroup.LayoutParams layoutParams = (ViewGroup.LayoutParams) view.getLayoutParams();
//            layoutParams.height = parent.getMeasuredHeight();
            layoutParams.width = parent.getMeasuredWidth() / 12;
            view.setLayoutParams(layoutParams);
            return new LittleItemViewHolder(view);
        }else if (viewType==0){
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_space,parent,false);
            ViewGroup.LayoutParams layoutParams = (ViewGroup.LayoutParams) view.getLayoutParams();
            layoutParams.width = parent.getMeasuredWidth() / 12;
            view.setLayoutParams(layoutParams);
            return new SpaceViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if (holder.getItemViewType()==1){
            LittleItemViewHolder littleItemViewHolder = (LittleItemViewHolder) holder;
            Item item = mListItem.get(position);
            Glide.with(littleItemViewHolder.imgLittleItem.getContext()).load(item.getPathOfItem())
                    .dontAnimate()
                    .override(100,120)
                    .into(littleItemViewHolder.imgLittleItem);
            DateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
            if (item.getDurationVideo()==0){
                littleItemViewHolder.txtDuration.setText("");
            }else {
                littleItemViewHolder.txtDuration.setText(simpleDateFormat.format(item.getDurationVideo()));
            }
            littleItemViewHolder.imgLittleItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClick(position);
                }
            });
            littleItemViewHolder.setIsRecyclable(false);
        }else if (holder.getItemViewType()==0){
        }

    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public int getItemViewType(int position) {
        if (mListItem.get(position).getPathOfItem().equals("")){
            return 0;
        }
        return 1;
    }

    @Override
    public int getItemCount() {
        if (mListItem!=null){
            return mListItem.size();
        }
        return 0;
    }

    public class SpaceViewHolder extends RecyclerView.ViewHolder{
        View viewSpace;

        public SpaceViewHolder(@NonNull View itemView) {
            super(itemView);
            viewSpace = itemView.findViewById(R.id.viewSpace);
        }
    }

    public class LittleItemViewHolder extends RecyclerView.ViewHolder{
        ImageView imgLittleItem;
        TextView txtDuration;

        public LittleItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imgLittleItem = itemView.findViewById(R.id.imgLittleItem);
            txtDuration = itemView.findViewById(R.id.txtDuration);
        }
    }

    public interface OnItemLittleClick{
        void onClick(int position);
    }
}
