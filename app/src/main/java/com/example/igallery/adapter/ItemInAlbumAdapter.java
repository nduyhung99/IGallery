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

public class ItemInAlbumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context mContext;
    private List<Item> mListItem;
    private OnItemClickListener mListener;
    private OnItemClickListener_1 mListener1;
    private boolean isCenterCrop = true;
    private int spanCount = 3;
    private boolean isSelect = false;

    public void setSelect(boolean select){
        this.isSelect = select;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener=listener;
    }

    public void setOnItemClickListener_1(OnItemClickListener_1 listener1){
        mListener1=listener1;
    }

    public ItemInAlbumAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setSpanCount(int spanCount){
        this.spanCount = spanCount;
        notifyDataSetChanged();
    }

    public void setCenterCrop(boolean centerCrop){
        this.isCenterCrop = centerCrop;
        notifyDataSetChanged();
    }

    public void setData(List<Item> list){
        this.mListItem = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        
        if (viewType==0){
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_item,parent,false);
            GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
            lp.height = parent.getMeasuredWidth() / spanCount;
            view.setLayoutParams(lp);
            return new ItemViewHolder(view,mListener);
        }else if (viewType==1){
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_add,parent,false);
            GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
            lp.height = parent.getMeasuredWidth() / spanCount;
            view.setLayoutParams(lp);
            return new CountItemViewHolder(view,mListener1);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType()==0){
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            Item item = mListItem.get(position);
            itemViewHolder.itemSelected.setVisibility(item.isItemSelected() ? View.VISIBLE : View.GONE);
            int i = mContext.getResources().getDisplayMetrics().widthPixels/spanCount;
            if (isCenterCrop){
                Glide.with(itemViewHolder.item.getContext()).load(item.getPathOfItem())
                        .override(i,i)
                        .dontAnimate()
                        .centerCrop()
                        .into(itemViewHolder.item);
            }else {
                Glide.with(itemViewHolder.item.getContext()).load(item.getPathOfItem())
                        .override(i,i)
                        .dontAnimate()
                        .into(itemViewHolder.item);
            }

            DateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
            if (item.getDurationVideo()==0){
                itemViewHolder.durationVideo.setText("");
            }else {
                itemViewHolder.durationVideo.setText(simpleDateFormat.format(item.getDurationVideo()));
            }
        }else {
            CountItemViewHolder countItemViewHolder = (CountItemViewHolder) holder;
        }
    }

    @Override
    public int getItemCount() {
        if (mListItem!=null){
            if (isSelect==false){
                return mListItem.size()+1;
            }else {
                return mListItem.size();
            }
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position<mListItem.size()){
            return 0;
        }
        return 1;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        ImageView item;
        RelativeLayout itemSelected;
        TextView durationVideo;
        RelativeLayout layoutItem;
        public ItemViewHolder(@NonNull View itemView,final OnItemClickListener listener) {
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
                        if (position!=RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                        Item item = mListItem.get(position);
                        item.setItemSelected(!item.isItemSelected());
                        itemSelected.setVisibility(item.isItemSelected() ? View.VISIBLE : View.GONE);
                    }
                }
            });
        }
    }

    public class CountItemViewHolder extends RecyclerView.ViewHolder{
        private RelativeLayout layoutItem;
        public CountItemViewHolder(@NonNull View itemView,final OnItemClickListener_1 listener1) {
            super(itemView);
            layoutItem = itemView.findViewById(R.id.layoutItem);
            layoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener1!=null){
                        mListener1.onItemClick_1();
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
        void onItemClick(int position);
    }

    public interface OnItemClickListener_1{
        void onItemClick_1();
    }
}
