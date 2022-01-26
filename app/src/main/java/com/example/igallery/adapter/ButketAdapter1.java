package com.example.igallery.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

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

public class ButketAdapter1 extends RecyclerView.Adapter<ButketAdapter1.ButketViewHolder> implements Filterable {
    private Context mContext;
    private ArrayList<Butket> mListButket;
    private ButketAdapter1.OnItemClickListener mListener;
    private ArrayList<Butket> mListButketOld;
    private ButketAdapter1.ISendData mISendData;

    public void setOnItemClickListener(ButketAdapter1.OnItemClickListener listener){
        mListener=listener;
    }

    public void setiSendData(ButketAdapter1.ISendData iSendData){
        mISendData=iSendData;
    }

    public ButketAdapter1(Context mContext) {
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_bucket_1,parent,false);
//        GridLayoutManager.LayoutParams lol = (GridLayoutManager.LayoutParams) view.getLayoutParams();
//        lol.height = parent.getMinimumWidth()/2;
//        view.setLayoutParams(lol);
        return new ButketViewHolder(view,mListener);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ButketAdapter1.ButketViewHolder holder, int position) {
        Butket butket =mListButket.get(position);
        holder.folderName.setText(butket.getName());
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transforms(new CenterCrop());
            Glide.with(holder.imageButket.getContext()).load(butket.getFirstImageContainedPath())
                    .override(300,300)
                    .apply(requestOptions)
                    .dontAnimate()
                    .into(holder.imageButket);
    }

    @Override
    public int getItemCount() {
        if (mListButket!=null){
            return mListButket.size();
        }
        return 0;
    }

    public class ButketViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageButket;
        private TextView folderName;
        public ButketViewHolder(@NonNull View itemView,final ButketAdapter1.OnItemClickListener listener) {
            super(itemView);
            imageButket = itemView.findViewById(R.id.imageButket);
            folderName = itemView.findViewById(R.id.folderName);
            imageButket.setOnClickListener(new View.OnClickListener() {
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
}
