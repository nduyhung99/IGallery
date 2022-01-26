package com.example.igallery.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.igallery.R;
import com.example.igallery.model.Butket;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class ButketAlbumAdapter extends RecyclerView.Adapter<ButketAlbumAdapter.ButketViewHolder> implements Filterable {
    private Context mContext;
    private ArrayList<Butket> mListButket;
    private ButketAlbumAdapter.OnItemClickListener mListener, mListener1;
    private ArrayList<Butket> mListButketOld;
    private ButketAlbumAdapter.ISendData mISendData;
    private boolean isDelete = false;

    public void setOnItemClickListener(ButketAlbumAdapter.OnItemClickListener listener){
        mListener=listener;
    }

    public void setOnItemClickListener1(ButketAlbumAdapter.OnItemClickListener listener1){
        mListener1=listener1;
    }

    public void setDelete(boolean isDelete){
        this.isDelete = isDelete;
        notifyDataSetChanged();
    }

    public void setiSendData(ButketAlbumAdapter.ISendData iSendData){
        mISendData=iSendData;
    }

    public ButketAlbumAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(ArrayList<Butket> list){
        this.mListButket = list;
        this.mListButketOld = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ButketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_bucket_album,parent,false);
//        GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
//        lp.width = parent.getMeasuredWidth()/3;
//        view.setLayoutParams(lp);
        return new ButketViewHolder(view,mListener,mListener1);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ButketAlbumAdapter.ButketViewHolder holder, int position) {
        Butket butket =mListButket.get(position);
        holder.folderName.setText(butket.getName());
        if (butket.getName()!=null){
            if (butket.getName().equals("Favorite")){
                if (butket.getTotalItem()!=0){
                    holder.countItemInFolder.setText(String.valueOf(butket.getTotalItem()));
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions = requestOptions.transforms(new CenterCrop());
                    Glide.with(holder.imageButket.getContext()).load(butket.getFirstImageContainedPath())
                            .override(300,300)
                            .apply(requestOptions)
                            .dontAnimate()
                            .into(holder.imageButket);
                }else {
                    holder.countItemInFolder.setText(String.valueOf(butket.getTotalItem()));
                    holder.imageButket.setVisibility(View.GONE);
                    holder.layoutEmptyFavorite.setVisibility(View.VISIBLE);
                }
                holder.imgFavorite.setVisibility(View.VISIBLE);
            }else {
                holder.countItemInFolder.setText(String.valueOf(butket.getTotalItem()));
                if (butket.getTotalItem()==0){
                    holder.layoutEmptyFavorite.setVisibility(View.VISIBLE);
                }else {
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions = requestOptions.transforms(new CenterCrop());
                    String lol = butket.getFirstImageContainedPath();
                    Glide.with(holder.imageButket.getContext()).load(lol)
                            .override(300,300)
                            .apply(requestOptions)
                            .dontAnimate()
                            .into(holder.imageButket);
                }
            }
        }
        if (isDelete==true){
            enableDisableView(holder.linear,false);
            if (butket.getType()==1){
                holder.imgGray.setVisibility(View.VISIBLE);
            }else {
                holder.imgDeleteAlbum.setVisibility(View.VISIBLE);
            }
        }
        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        if (mListButket!=null){
            return mListButket.size();
        }
        return 0;
    }

    public class ButketViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageButket, imgFavorite, imgDeleteAlbum, imgGray;
        private TextView folderName, countItemInFolder;
        private RelativeLayout layoutEmptyFavorite;
        private RelativeLayout layoutButket;
        private LinearLayout linear;
        public ButketViewHolder(@NonNull View itemView,final ButketAlbumAdapter.OnItemClickListener listener,final ButketAlbumAdapter.OnItemClickListener listener1) {
            super(itemView);
            imageButket = itemView.findViewById(R.id.imageButket);
            folderName = itemView.findViewById(R.id.folderName);
            countItemInFolder = itemView.findViewById(R.id.countItemInFolder);
            layoutEmptyFavorite = itemView.findViewById(R.id.layoutEmptyFavorite);
            layoutButket = itemView.findViewById(R.id.layoutButket);
            imgFavorite = itemView.findViewById(R.id.imgFavorite);
            imgDeleteAlbum = itemView.findViewById(R.id.imgDeleteAlbum);
            imgGray = itemView.findViewById(R.id.imgGray);
            linear = itemView.findViewById(R.id.linear);

            imgDeleteAlbum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener1!=null){
                        int position=getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            listener1.onItemClick(position);
                        }
                    }
                }
            });

            linear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        int position=getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public interface ISendData{
        void iSenddata(int size);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String strSearch = constraint.toString();
                String nfdNormalizedString = Normalizer.normalize(strSearch, Normalizer.Form.NFD);
                Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
                String strSearch1 = pattern.matcher(nfdNormalizedString).replaceAll("");

                if (strSearch.isEmpty()){
                    mListButket = mListButketOld;
                }else {
                    ArrayList<Butket> list = new ArrayList<>();
                    for (Butket butket : mListButketOld){
                        String lol = Normalizer.normalize(butket.getName(), Normalizer.Form.NFD);
                        String lol1 = pattern.matcher(lol).replaceAll("");
                        if (lol1.toLowerCase().contains(strSearch1.toLowerCase())){
                            list.add(butket);
                        }
                    }

                    mListButket = list;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mListButket;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mListButket = (ArrayList<Butket>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public int getListSearch(){
        return mListButket.size();
    }

    public static void enableDisableView(View view, boolean enabled) {
        view.setEnabled(enabled);
        if ( view instanceof ViewGroup ) {
            ViewGroup group = (ViewGroup)view;

            for ( int idx = 0 ; idx < group.getChildCount() ; idx++ ) {
                enableDisableView(group.getChildAt(idx), enabled);
            }
        }
    }

    public boolean getDelete(){
        return isDelete;
    }
}
