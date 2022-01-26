package com.example.igallery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.igallery.R;
import com.example.igallery.model.Item;
import com.example.igallery.model.ItemPhoto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TitlePhotoAdapter extends RecyclerView.Adapter<TitlePhotoAdapter.TitlePhotoViewHolder>{
    private Context context;
//    private List<ItemPhoto> list;
    private List<String> list;
    private List<Item> listItem;
    private int spanCount;
    private InterfaceTest interfaceTest;
    private ItemInTitleAdapter itemInTitleAdapter;
    private boolean openSelect, unSelectedItem=false;
    List<Item> listItemSelected = new ArrayList<>();

    public void setOnInterfaceTest(InterfaceTest listener){
        interfaceTest=listener;
    }

    public TitlePhotoAdapter(Context context) {
        this.context = context;
    }

    public void resetData(boolean reset){
        this.unSelectedItem = reset;
        notifyDataSetChanged();
    }

//    public void setData(List<ItemPhoto> list){
//        this.list = list;
//    }

    public void setData(List<String> list,List<Item> listItem,int spanCount){
        this.list = list;
        this.listItem = listItem;
        this.spanCount = spanCount;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TitlePhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_title_photo,parent,false);
        return new TitlePhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TitlePhotoAdapter.TitlePhotoViewHolder holder, int position) {
//        ItemPhoto itemPhoto = list.get(position);
        String title = list.get(position);
        List<Item> listTest = getListItem(title);

        holder.txtTitle.setText(title);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context,spanCount);
        holder.rcvItemInRcvPhoto.setLayoutManager(gridLayoutManager);
        holder.rcvItemInRcvPhoto.setHasFixedSize(true);
        itemInTitleAdapter = new ItemInTitleAdapter(context);
        itemInTitleAdapter.setData(listTest,spanCount);
        holder.rcvItemInRcvPhoto.setAdapter(itemInTitleAdapter);
        itemInTitleAdapter.setOnItemClickListener(new ItemInTitleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item,int position) {
                if (!openSelect){
                    listTest.get(position).setItemSelected(true);
                }
                interfaceTest.ITest(item);
            }
        });
        if (unSelectedItem==true){
            itemInTitleAdapter.unSelectAll();
        }
    }

    private List<Item> getListItem(String title) {
        String lol = "";
        List<Item> list = new ArrayList<>();
        String date1;
        for (int i=0; i<listItem.size();i++){
            switch (spanCount){
                case 4:
                    date1 = String.valueOf(DateFormat.getDateInstance(DateFormat.SHORT).format(listItem.get(i).getDateAdded()));
                    break;
                case 6:
                    date1 = String.valueOf(new SimpleDateFormat("MM/yyyy").format(listItem.get(i).getDateAdded()));
                    break;
                case 8:
                    date1 = String.valueOf(new SimpleDateFormat("yyyy").format(listItem.get(i).getDateAdded()));
                    break;
                default:
                    date1 = String.valueOf(DateFormat.getDateInstance(DateFormat.SHORT).format(listItem.get(i).getDateAdded()));
                    break;
            }

            if (date1.equals(title)){
                list.add(listItem.get(i));
                lol=date1;
            }
            if (lol!="" && lol!=date1){
                break;
            }
        }
        return list;
    }

    @Override
    public int getItemCount() {
        if (list!=null){
            return list.size();
        }
        return 0;
    }

    public class TitlePhotoViewHolder extends RecyclerView.ViewHolder{
        private TextView txtTitle;
        private RecyclerView rcvItemInRcvPhoto;

        public TitlePhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            rcvItemInRcvPhoto = itemView.findViewById(R.id.rcvItemInRcvPhoto);
        }
    }

    public interface InterfaceTest{
        void ITest(Item item);
    }

    public boolean isOpenSelect() {
        return openSelect;
    }

    public void setOpenSelect(boolean openSelect) {
        this.openSelect = openSelect;
    }

    public List<Item> getAllItem(){
        List<Item> list = itemInTitleAdapter.getAll();
        return list;
    }

    public List<Item> getSelectedItem(){
        List<Item> list = itemInTitleAdapter.getSelected();
        return list;
    }

    public void unSelecAllItem(){
        itemInTitleAdapter.unSelectAll();
    }

    public void selectAllItem(){
        itemInTitleAdapter.selectAll();
    }
}
