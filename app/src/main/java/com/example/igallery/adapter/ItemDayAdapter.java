package com.example.igallery.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.igallery.R;
import com.example.igallery.model.Item;
import com.example.igallery.model.ItemPhoto;

import java.util.List;

public class ItemDayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private List<ItemPhoto> listItem;
    private OnItemDayClickListener mListener;
    private int size1=1000, size2=400, size3=200;

    public void setOnItemDayClickListener(OnItemDayClickListener listener){
        this.mListener = listener;
    }

    public ItemDayAdapter (Context mContext){
        this.context = mContext;
        size1 = context.getResources().getDisplayMetrics().heightPixels/3;
        size2 = context.getResources().getDisplayMetrics().widthPixels;
        size3 = context.getResources().getDisplayMetrics().widthPixels/3;
    }

    public void setData(List<ItemPhoto> list){
        this.listItem = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==0){
            View view = LayoutInflater.from(context).inflate(R.layout.item_day,parent,false);
            return new ItemDayViewHolder(view);
        }else if (viewType==1){
            View view = LayoutInflater.from(context).inflate(R.layout.item_count,parent,false);
            return new CountItemViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ItemPhoto itemPhoto = listItem.get(position);
        if (holder.getItemViewType()==0){
            ItemDayViewHolder itemDayViewHolder = (ItemDayViewHolder) holder;
            itemDayViewHolder.txtDate.setText(itemPhoto.getTitle());
            itemDayViewHolder.txtCountItem.setText(itemPhoto.getCountItem());
            List<Item> listItems = itemPhoto.getListItem();

            Glide.with(itemDayViewHolder.imgFirstItem.getContext()).load(listItems.get(0).getPathOfItem())
                    .dontAnimate()
                    .override(size1,size2)
                    .into(itemDayViewHolder.imgFirstItem);
            if (listItems.size()>2){
                Glide.with(itemDayViewHolder.imgSecondItem.getContext()).load(listItems.get(1).getPathOfItem())
                        .dontAnimate()
                        .into(itemDayViewHolder.imgSecondItem);
                Glide.with(itemDayViewHolder.imgThirdItem.getContext()).load(listItems.get(2).getPathOfItem())
                        .dontAnimate()
                        .override(size1,size2*2)
                        .into(itemDayViewHolder.imgThirdItem);
            }else if (listItems.size()==2){
                itemDayViewHolder.layoutThirdItem.setVisibility(View.GONE);
                Glide.with(itemDayViewHolder.imgSecondItem.getContext()).load(listItems.get(1).getPathOfItem())
                        .dontAnimate()
                        .override(size1,size3)
                        .into(itemDayViewHolder.imgSecondItem);
            }else {
                itemDayViewHolder.layoutTwoItem.setVisibility(View.GONE);
            }
            itemDayViewHolder.setIsRecyclable(false);

            itemDayViewHolder.imgFirstItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClick(position);
                }
            });

            itemDayViewHolder.imgSecondItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClick(position);
                }
            });

            itemDayViewHolder.imgThirdItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClick(position);
                }
            });
        }else if (holder.getItemViewType()==1){
            CountItemViewHolder countItemViewHolder = (CountItemViewHolder) holder;
            int size = listItem.size();
            if (size<2){
                countItemViewHolder.txtCountItem.setText(String.valueOf(size)+" "+context.getString(R.string.item));
            }else {
                countItemViewHolder.txtCountItem.setText(String.valueOf(listItem.size()-1)+" "+context.getString(R.string.items));
            }

        }
    }

    @Override
    public int getItemViewType(int position) {
        ItemPhoto itemPhoto = listItem.get(position);
        if (itemPhoto.getTitle()!=""){
            return 0;
        }else {
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        if (listItem!=null){
            return listItem.size();
        }
        return 0;
    }

    public class ItemDayViewHolder extends RecyclerView.ViewHolder{
        private ImageView imgFirstItem,imgSecondItem, imgThirdItem;
        private TextView txtCountItem, txtDate;
        private RelativeLayout layoutThirdItem;
        private LinearLayout layoutTwoItem;
        public ItemDayViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFirstItem = itemView.findViewById(R.id.imgFirstItem);
            imgSecondItem = itemView.findViewById(R.id.imgSecondItem);
            imgThirdItem = itemView.findViewById(R.id.imgThirdItem);
            layoutThirdItem = itemView.findViewById(R.id.layoutThirdItem);
            txtCountItem = itemView.findViewById(R.id.txtCountItem);
            txtDate = itemView.findViewById(R.id.txtDate);
            layoutTwoItem = itemView.findViewById(R.id.layoutTwoItem);
        }
    }

    public class CountItemViewHolder extends RecyclerView.ViewHolder{
        private TextView txtCountItem;

        public CountItemViewHolder(@NonNull  View itemView) {
            super(itemView);
            txtCountItem = itemView.findViewById(R.id.txtCountItem);
        }
    }

    public interface OnItemDayClickListener{
        void onClick(int position);
    }
}
