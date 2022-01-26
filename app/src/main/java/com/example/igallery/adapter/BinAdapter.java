package com.example.igallery.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
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
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.igallery.R;
import com.example.igallery.model.Item;
import com.example.igallery.model.ItemBin;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class BinAdapter extends RecyclerView.Adapter<BinAdapter.BinViewHolder>{

    private Context mContext;
    private List<ItemBin> mListItemBin;
    private BinAdapter.OnItemClickListener mListener;
    public void setOnItemClickListener(BinAdapter.OnItemClickListener listener){
        mListener=listener;
    }

    public BinAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<ItemBin> list){
        this.mListItemBin = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public BinViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_bin,parent,false);
        GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        lp.height = parent.getMeasuredWidth() / 4;
        view.setLayoutParams(lp);
        return new BinViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BinAdapter.BinViewHolder holder, int position) {
        ItemBin itemBin = mListItemBin.get(position);
        holder.itemBinSelected.setVisibility(itemBin.isSelected() ? View.VISIBLE : View.GONE);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop());
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
            int day = (int) ((itemBin.getDate()-System.currentTimeMillis())/(86400000));
            holder.countDay.setText(String.valueOf(day)+" days");

            String path = itemBin.getPath();
            if (path.endsWith(".mp4") || path.endsWith(".avi") || path.endsWith(".mov") || path.endsWith(".flv") || path.endsWith(".wmv")){
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(mContext, itemBin.getUri());
                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                long timeInMillisec = Long.parseLong(time );
                retriever.release();
                DateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
                holder.txtDuration.setText(simpleDateFormat.format(timeInMillisec));
            }
            Glide.with(holder.imageItemBin.getContext()).load(itemBin.getUri())
                    .override(200,200)
                    .placeholder(R.drawable.ic_outline_image)
                    .error(R.drawable.ic_outline_image)
                    .apply(requestOptions)
                    .dontAnimate()
                    .into(holder.imageItemBin);
        }else {
            int day = (int) ((System.currentTimeMillis()-itemBin.getDate())/(86400000));
            holder.countDay.setText((15-day)+" days");
            Glide.with(holder.imageItemBin.getContext()).load(itemBin.getPath())
                    .override(200,200)
                    .placeholder(R.drawable.ic_outline_image)
                    .error(R.drawable.ic_outline_image)
                    .apply(requestOptions)
                    .dontAnimate()
                    .into(holder.imageItemBin);
            String path = itemBin.getPath();
            if (path.endsWith(".mp4") || path.endsWith(".avi") || path.endsWith(".mov") || path.endsWith(".flv") || path.endsWith(".wmv")){
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(mContext, Uri.fromFile(new File(itemBin.getPath())));
                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                long timeInMillisec = Long.parseLong(time );
                retriever.release();
                DateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
                holder.txtDuration.setText(simpleDateFormat.format(timeInMillisec));
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mListItemBin!=null){
            return mListItemBin.size();
        }
        return 0;
    }

    public class BinViewHolder extends RecyclerView.ViewHolder{
        ImageView imageItemBin;
        TextView countDay, txtDuration;
        RelativeLayout layoutItemBin, itemBinSelected;
        public BinViewHolder(@NonNull View itemView,final BinAdapter.OnItemClickListener listener) {
            super(itemView);
            imageItemBin = itemView.findViewById(R.id.imageItemBin);
            countDay = itemView.findViewById(R.id.countDay);
            itemBinSelected = itemView.findViewById(R.id.itemBinSelected);
            layoutItemBin = itemView.findViewById(R.id.layoutItemBin);
            txtDuration = itemView.findViewById(R.id.txtDuration);

            layoutItemBin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        int position=getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                        ItemBin itemBin = mListItemBin.get(position);
                        itemBin.setSelected(!itemBin.isSelected());
                        itemBinSelected.setVisibility(itemBin.isSelected() ? View.VISIBLE : View.GONE);
                    }
                }
            });
        }
    }

    public List<ItemBin> getAll(){
        return mListItemBin;
    }

    public List<ItemBin> getSelected(){
        List<ItemBin> selected = new ArrayList<>();

        for(int i=0; i< mListItemBin.size(); i++){
            if (mListItemBin.get(i).isSelected()){
                selected.add(mListItemBin.get(i));
            }
        }
        return selected;
    }

    public void unSelectAll(){
        for (int i=0; i<mListItemBin.size(); i++){
            mListItemBin.get(i).setSelected(false);
        }
        notifyDataSetChanged();
    }

    public void selectAll(){
        for (int i=0; i<mListItemBin.size(); i++){
            mListItemBin.get(i).setSelected(true);
        }
        notifyDataSetChanged();
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
}
