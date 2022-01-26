package com.example.igallery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.igallery.R;
import com.example.igallery.model.Item;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ItemInTitleAdapter extends RecyclerView.Adapter<ItemInTitleAdapter.ItemInTitleViewHolder>{
    private Context mContext;
    private List<Item> mListItem;
    private ItemInTitleAdapter.OnItemClickListener mListener;
    private int spanCount;

    public void setOnItemClickListener(ItemInTitleAdapter.OnItemClickListener listener){
        mListener=listener;
    }

    public ItemInTitleAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<Item> list,int spanCount){
        this.mListItem = list;
        this.spanCount = spanCount;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ItemInTitleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_item,parent,false);
        GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        lp.height = parent.getMeasuredWidth() / spanCount;
        view.setLayoutParams(lp);
        return new ItemInTitleViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemInTitleAdapter.ItemInTitleViewHolder holder, int position) {
        Item item = mListItem.get(position);
        holder.itemSelected.setVisibility(item.isItemSelected() ? View.VISIBLE : View.GONE);
        Glide.with(holder.item.getContext()).load(item.getPathOfItem())
                .placeholder(R.drawable.ic_outline_image)
                .error(R.drawable.ic_outline_image)
                .dontAnimate()
                .into(holder.item);
        DateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        if (item.getDurationVideo()==0){
            holder.durationVideo.setText("");
        }else {
            holder.durationVideo.setText(simpleDateFormat.format(item.getDurationVideo()));
        }
    }

    @Override
    public int getItemCount() {
        if (mListItem!=null){
            return mListItem.size();
        }
        return 0;
    }

    public class ItemInTitleViewHolder extends RecyclerView.ViewHolder{
        ImageView item;
        RelativeLayout itemSelected;
        TextView durationVideo;
        RelativeLayout layoutItem;
        public ItemInTitleViewHolder(@NonNull View itemView,final ItemInTitleAdapter.OnItemClickListener listener) {
            super(itemView);
            item = itemView.findViewById(R.id.item);
            itemSelected = itemView.findViewById(R.id.itemSelected);
            durationVideo = itemView.findViewById(R.id.durationVideo);
            layoutItem = itemView.findViewById(R.id.layoutItem);

            layoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        int position=getAdapterPosition();
                        Item item = mListItem.get(position);
                        item.setItemSelected(!item.isItemSelected());
                        if (position!=RecyclerView.NO_POSITION){
                            listener.onItemClick(item,position);
                        }
                        itemSelected.setVisibility(item.isItemSelected() ? View.VISIBLE : View.GONE);
                    }
                }
            });
        }
    }

    public List<Item> getAll(){
        return mListItem;
    }

    public List<Item> getSelected(){
        List<Item> selected = new ArrayList<>();

        for(int i=0; i< mListItem.size(); i++){
            if (mListItem.get(i).isItemSelected()){
                selected.add(mListItem.get(i));
            }
        }
        return selected;
    }

    public void unSelectAll(){
        for (int i=0; i<mListItem.size(); i++){
            mListItem.get(i).setItemSelected(false);
        }
        notifyDataSetChanged();
    }

    public void selectAll(){
        for (int i=0; i<mListItem.size(); i++){
            mListItem.get(i).setItemSelected(true);
        }
        notifyDataSetChanged();
    }

    public interface OnItemClickListener{
        void onItemClick(Item item,int position);
    }
}
