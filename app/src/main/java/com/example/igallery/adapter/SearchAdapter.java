package com.example.igallery.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.igallery.R;
import com.example.igallery.model.Butket;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
    private List<Butket> mListButket;
    private List<Butket> mListButketOld;
    private Context mContext;
    private IOnClickItemSearch mListener;
    private ISendSize iSendSize;

    private boolean collapse=true;


    public void setOnItemClickListener(IOnClickItemSearch listener){
        mListener=listener;
    }

    public void setSize(ISendSize listenerSize){
        iSendSize = listenerSize;
    }

    public void setCollapse(boolean isCollapse){
        this.collapse = isCollapse;
        notifyDataSetChanged();
    }

    public SearchAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<Butket> list){
        this.mListButket = list;
        this.mListButketOld = new ArrayList<>(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search,parent,false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.SearchViewHolder holder, int position) {
        Butket butket = mListButket.get(position);
        holder.txtTitleSearch.setText(butket.getName());
        holder.txtCountItem.setText(String.valueOf(butket.getTotalItem()));
        String date1 = String.valueOf(DateFormat.getDateInstance(DateFormat.SHORT).format(butket.getDateAdded()));
        holder.txtDateImageTitle.setText(date1);
        Glide.with(holder.imgTitle.getContext()).load(butket.getFirstImageContainedPath())
                .override(120,120)
                .dontAnimate()
                .into(holder.imgTitle);
        holder.layoutEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClickSearch(butket.getName());
            }
        });
    }


    @Override
    public int getItemCount() {
        if (mListButket!=null){
            if (mListButket.size()>3){
                if (collapse==true){
                    return 3;
                }else {
                    return mListButket.size();
                }
            }else {
                return mListButket.size();
            }
        }
        return 0;
    }

    public void search(String strSearch){
//        mListButket.clear();
            String nfdNormalizedString = Normalizer.normalize(strSearch, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            String strSearch1 = pattern.matcher(nfdNormalizedString).replaceAll("");

            if (strSearch.isEmpty()){
                mListButket = mListButketOld;
            }else {
                ArrayList<Butket> list = new ArrayList<>();
                for (Butket butket : mListButketOld){
                    if (butket.getName()!=null){
                        String lol = Normalizer.normalize(butket.getName(), Normalizer.Form.NFD);
                        String lol1 = pattern.matcher(lol).replaceAll("");
                        if (lol1.toLowerCase().contains(strSearch1.toLowerCase())){
                            list.add(butket);
                        }
                    }
                }

                mListButket = list;
            }
            notifyDataSetChanged();
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder{
        ImageView imgTitle;
        TextView txtTitleSearch,txtDateImageTitle,txtCountItem;
        RelativeLayout layoutEnter;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            imgTitle = itemView.findViewById(R.id.imgTitle);
            txtTitleSearch = itemView.findViewById(R.id.txtTitleSearch);
            txtDateImageTitle = itemView.findViewById(R.id.txtDateImageTitle);
            txtCountItem = itemView.findViewById(R.id.txtCountItem);
            layoutEnter = itemView.findViewById(R.id.layoutEnter);
        }
    }

    public interface IOnClickItemSearch{
        void onItemClickSearch(String name);
    }

    public int getSize(){
        if (mListButket!=null){
            return mListButket.size();
        }
        return 0;
    }

    public interface ISendSize{
        void iSenddata(int size);
    }
}
