package com.example.igallery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.igallery.R;
import com.example.igallery.model.Item;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class ItemYearAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private List<Item> listItem;
    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener=listener;
    }

    public ItemYearAdapter (Context mContext){
        this.context = mContext;
    }

    public void setData(List<Item> list){
        this.listItem = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==0){
            View view = LayoutInflater.from(context).inflate(R.layout.item_year,parent,false);
            return new ItemYearViewHolder(view,mListener);
        }else if (viewType==1){
            View view = LayoutInflater.from(context).inflate(R.layout.item_count,parent,false);
            return new CountItemViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull  RecyclerView.ViewHolder holder, int position) {
        Item item = listItem.get(position);
        if (holder.getItemViewType()==0){
            ItemYearViewHolder itemYearViewHolder = (ItemYearViewHolder) holder;
            Glide.with(itemYearViewHolder.imgItemYear.getContext()).load(item.getPathOfItem())
                    .dontAnimate()
                    .into(itemYearViewHolder.imgItemYear);
            String date1 = String.valueOf(new SimpleDateFormat("yyyy").format(item.getDateAdded()));
            itemYearViewHolder.txtYear.setText(date1);
            if (item.getDurationVideo()==0){
                itemYearViewHolder.txtDuration.setText("");
            }else {
                DateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
                itemYearViewHolder.txtDuration.setText(simpleDateFormat.format(item.getDurationVideo()));
            }
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
        Item item = listItem.get(position);
        if (item.getPathOfItem()!=""){
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

    public class ItemYearViewHolder extends RecyclerView.ViewHolder{
        private CardView cardItemYear;
        private TextView txtDuration, txtYear;
        private ImageView imgItemYear;

        public ItemYearViewHolder(@NonNull View itemView,final OnItemClickListener listener) {
            super(itemView);
            cardItemYear = itemView.findViewById(R.id.cardItemYear);
            txtDuration = itemView.findViewById(R.id.txtDuration);
            txtYear = itemView.findViewById(R.id.txtYear);
            imgItemYear = itemView.findViewById(R.id.imgItemYear);

            cardItemYear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        int position=getAdapterPosition();
                        Item item = listItem.get(position);
                        if (position!=RecyclerView.NO_POSITION){
                            listener.onItemClick(item);
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
