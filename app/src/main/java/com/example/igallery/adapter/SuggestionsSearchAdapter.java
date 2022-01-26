package com.example.igallery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.igallery.R;
import com.example.igallery.model.Butket;

import java.text.DateFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SuggestionsSearchAdapter extends RecyclerView.Adapter<SuggestionsSearchAdapter.SuggestionsViewHolder> {
    private List<Butket> mListButket;
    private Context mContext;
    List<Butket> mListButketOld;
    private OnItemClickListenerSuggesstion iClick;
    private ISendDataSuggesstion iSendDataSuggesstion;

    public void setOnIClick(OnItemClickListenerSuggesstion listener){
        this.iClick = listener;
    }

    public void setLol(ISendDataSuggesstion list){
        this.iSendDataSuggesstion = list;
    }

    public SuggestionsSearchAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<Butket> list){
        this.mListButketOld = new ArrayList<>(list);
        this.mListButket = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SuggestionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_suggestion,parent,false);
        GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        lp.height = parent.getMeasuredWidth() / 4;
        view.setLayoutParams(lp);
        return new SuggestionsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionsSearchAdapter.SuggestionsViewHolder holder, int position) {
        Butket butket = mListButketOld .get(position);
        int i = mContext.getResources().getDisplayMetrics().widthPixels/4;
        Glide.with(holder.item.getContext()).load(butket.getFirstImageContainedPath())
                .override(i,i)
                .dontAnimate()
                .into(holder.item);
        DateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        if (butket.getDuration()==0){
            holder.durationVideo.setText("");
        }else {
            holder.durationVideo.setText(simpleDateFormat.format(butket.getDuration()));
        }
        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClick.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mListButket!=null){
            if (mListButket.size()<12){
                return mListButket.size();
            }else {
                return 11;
            }
        }
        return 0;
    }

    public class SuggestionsViewHolder extends RecyclerView.ViewHolder{
        private RelativeLayout layoutItem;
        private ImageView item;
        private TextView durationVideo;

        public SuggestionsViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutItem = itemView.findViewById(R.id.layoutItem);
            item = itemView.findViewById(R.id.item);
            durationVideo = itemView.findViewById(R.id.durationVideo);
        }
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

    public List<Butket> getListSearch(){
        return mListButket;
    }

    public interface OnItemClickListenerSuggesstion{
        void onItemClick(int position);
    }

    public interface ISendDataSuggesstion{
        void iSenddata(List<Butket> list);
    }

    public int getSize(){
        return mListButket.size();
    }

}
