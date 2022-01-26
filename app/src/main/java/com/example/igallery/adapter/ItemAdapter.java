package com.example.igallery.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.animation.content.Content;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.igallery.R;
import com.example.igallery.model.Item;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context mContext;
    private List<Item> mListItem;
    private OnItemClickListener mListener;
    private OnItemClickListener_1 mListener1;
    private boolean isCenterCrop = true;
    private int spanCount = 4;
    private int itemCount = 1;
    private int i=120;

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener=listener;
    }

    public void setOnItemClickListener_1(OnItemClickListener_1 listener1){
        mListener1=listener1;
    }

    public ItemAdapter(Context mContext) {
        this.mContext = mContext;
        i = mContext.getResources().getDisplayMetrics().widthPixels/spanCount;
    }

    public void setSpanCount(int spanCount){
        this.spanCount = spanCount;
        this.i = mContext.getResources().getDisplayMetrics().widthPixels/spanCount;
        notifyDataSetChanged();
    }

    public void setCenterCrop(boolean centerCrop){
        this.isCenterCrop = centerCrop;
        notifyDataSetChanged();
    }

    public void setItemCount(int type){
        this.itemCount = type;
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_item,parent,false);
            GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
            lp.height = parent.getMeasuredWidth() / spanCount;
//            lp.width = parent.getMeasuredWidth() / 4;
            view.setLayoutParams(lp);
            return new ItemViewHolder(view,mListener,mListener1);
        }else if (viewType==1){
            View view = null;
            if (itemCount==1){
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_count,parent,false);
            }else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_count_1,parent,false);
            }
            return new CountItemViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType()==0){
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            Item item = mListItem.get(position);
            itemViewHolder.itemSelected.setVisibility(item.isItemSelected() ? View.VISIBLE : View.GONE);
//            int i = mContext.getResources().getDisplayMetrics().widthPixels/spanCount;
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
            int countPhoto = 0;
            int countVideo = 0;
            String photo, video;
            for (Item item : mListItem) {
                String path = item.getPathOfItem();
                if (path.endsWith(".mp4") || path.endsWith(".avi") || path.endsWith(".mov") || path.endsWith(".flv") || path.endsWith(".wmv")){
                    countVideo++;
                }else {
                    countPhoto++;
                }
            }
            if (countPhoto==0 && countVideo==0){
                countItemViewHolder.txtCountItem.setText(mContext.getText(R.string.no_photo_or_video));
            }else {
                if (countPhoto<2){
                    photo = countPhoto +" "+ mContext.getString(R.string.photo);
                }else {
                    photo = countPhoto +" "+ mContext.getString(R.string.photos);
                }

                if (countVideo<2){
                    video = countVideo +" "+ mContext.getString(R.string.video_1);
                }else {
                    video = countVideo +" "+ mContext.getString(R.string.videos);
                }
                if (countPhoto==0){
                    countItemViewHolder.txtCountItem.setText(video);
                }else if (countVideo==0){
                    countItemViewHolder.txtCountItem.setText(photo);
                }else {
                    countItemViewHolder.txtCountItem.setText(photo+", "+video);
                }

            }
        }
    }

    @Override
    public int getItemCount() {
        if (mListItem!=null){
            return mListItem.size()+1;
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
        public ItemViewHolder(@NonNull View itemView,final OnItemClickListener listener,final OnItemClickListener_1 listener1) {
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
            
            layoutItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (listener1!=null){
                        int position=getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            listener1.onItemClick_1(position);
                        }
                    }
                    return true;
                }
            });
        }
    }

    public class CountItemViewHolder extends RecyclerView.ViewHolder{
        private TextView txtCountItem;
        public CountItemViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCountItem = itemView.findViewById(R.id.txtCountItem);
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
        void onItemClick_1(int position);
    }
}
