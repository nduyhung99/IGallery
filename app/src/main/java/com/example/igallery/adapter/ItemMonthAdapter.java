package com.example.igallery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.igallery.R;
import com.example.igallery.model.Item;
import com.example.igallery.model.ItemPhoto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ItemMonthAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private List<ItemPhoto> listItemPhoto;
    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener=listener;
    }

    public ItemMonthAdapter(Context mContext){
        this.context = mContext;
    }

    public void setData(List<ItemPhoto> list){
        this.listItemPhoto = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==0){
            View view  = LayoutInflater.from(context).inflate(R.layout.item_month,parent,false);
            return new ItemMonthViewHolder(view,mListener);
        }else if (viewType==1){
            View view = LayoutInflater.from(context).inflate(R.layout.item_count,parent,false);
            return new CountItemViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ItemPhoto itemPhoto = listItemPhoto.get(position);
        if (holder.getItemViewType()==0){
            Item item = itemPhoto.getListItem().get(0);
            ItemMonthViewHolder itemMonthViewHolder = (ItemMonthViewHolder) holder;
            String month = new SimpleDateFormat("MMMM").format(item.getDateAdded());
            itemMonthViewHolder.txtTitle.setText(month.substring(0,1).toUpperCase()+month.substring(1));
            itemMonthViewHolder.txtTitle1.setText(new SimpleDateFormat("yyyy").format(item.getDateAdded()));
            List<Item> listItems = itemPhoto.getListItem();
            String stringDate = DateFormat.getDateInstance(DateFormat.LONG).format(listItems.get(0).getDateAdded());
            Glide.with(itemMonthViewHolder.imgItemMonth.getContext()).load(listItems.get(0).getPathOfItem())
//                .placeholder(R.drawable.ic_outline_image)
//                .error(R.drawable.ic_outline_image)
                    .dontAnimate()
                    .into(itemMonthViewHolder.imgItemMonth);
            if (itemPhoto.getListItem().get(0).getDurationVideo()==0){
                itemMonthViewHolder.txtDuration.setText("");
            }else {
                DateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
                itemMonthViewHolder.txtDuration.setText(simpleDateFormat.format(listItems.get(0).getDurationVideo()));
            }
//            if (listItems.size()>1){
//                String stringDate1 = DateFormat.getDateInstance(DateFormat.LONG).format(listItems.get(listItems.size()-1).getDateAdded());
//                itemMonthViewHolder.txtMonth.setText(stringDate1.substring(0,stringDate1.length()-6)+" - "+stringDate);
//            }else {
//                itemMonthViewHolder.txtMonth.setText(stringDate);
//            }
            String stringDate1 = DateFormat.getDateInstance(DateFormat.LONG).format(listItems.get(listItems.size()-1).getDateAdded());
            if (!stringDate1.equals(stringDate)){
                itemMonthViewHolder.txtMonth.setText(stringDate1.substring(0,stringDate1.length()-6)+" - "+stringDate);
            }else{
                itemMonthViewHolder.txtMonth.setText(stringDate);
            }
        }else if (holder.getItemViewType()==1){
            CountItemViewHolder countItemViewHolder = (CountItemViewHolder) holder;
            int size = listItemPhoto.size();
            if (size<2){
                countItemViewHolder.txtCountItem.setText(String.valueOf(size)+" "+context.getString(R.string.item));
            }else {
                countItemViewHolder.txtCountItem.setText(String.valueOf(size)+" "+context.getString(R.string.items));
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        ItemPhoto itemPhoto = listItemPhoto.get(position);
        if (itemPhoto.getTitle()!=""){
            return 0;
        }else {
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        if (listItemPhoto!=null){
            return listItemPhoto.size();
        }
        return 0;
    }

    public class ItemMonthViewHolder extends RecyclerView.ViewHolder{
        private RelativeLayout layoutNext;
        private TextView txtTitle, txtDuration, txtMonth, txtTitle1;
        private CardView cardItemMonth;
        private ImageView imgItemMonth;

        public ItemMonthViewHolder(@NonNull View itemView,final OnItemClickListener listener) {
            super(itemView);
            layoutNext = itemView.findViewById(R.id.layoutNext);
            txtDuration = itemView.findViewById(R.id.txtDuration);
            txtMonth = itemView.findViewById(R.id.txtMonth);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            cardItemMonth = itemView.findViewById(R.id.cardItemMonth);
            imgItemMonth = itemView.findViewById(R.id.imgItemMonth);
            txtTitle1 = itemView.findViewById(R.id.txtTitle1);

            cardItemMonth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        int position=getAdapterPosition();
                        ItemPhoto itemPhoto = listItemPhoto.get(position);
                        if (position!=RecyclerView.NO_POSITION){
                            listener.onItemClick(itemPhoto.getListItem().get(0));
                        }
                    }
                }
            });

            layoutNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        int position=getAdapterPosition();
                        ItemPhoto itemPhoto = listItemPhoto.get(position);
                        if (position!=RecyclerView.NO_POSITION){
                            listener.onItemClick(itemPhoto.getListItem().get(0));
                        }
                    }
                }
            });
        }
    }

    public class CountItemViewHolder extends RecyclerView.ViewHolder{
        private TextView txtCountItem;

        public CountItemViewHolder(@NonNull  View itemView) {
            super(itemView);
            txtCountItem = itemView.findViewById(R.id.txtCountItem);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(Item item);
    }
}
