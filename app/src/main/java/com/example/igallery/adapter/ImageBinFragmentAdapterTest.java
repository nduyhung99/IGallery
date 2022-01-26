package com.example.igallery.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.igallery.albumsfragment.ImageFragment;
import com.example.igallery.model.ItemBin;

import java.util.List;

public class ImageBinFragmentAdapterTest extends FragmentStateAdapter {
    List<ItemBin> mListItem;

    public void setData(List<ItemBin> list){
        this.mListItem = list;
        notifyDataSetChanged();
    }

    public ImageBinFragmentAdapterTest(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        ImageFragment imageFragment = new ImageFragment();
        Bundle b = new Bundle();
        ItemBin item = mListItem.get(position);
        b.putSerializable("IMAGE_BIN",item);
//        b.putSerializable("IMAGE",mListItem.get(position).getPathOfItem());
        imageFragment.setArguments(b);
        return imageFragment;
    }

    @Override
    public int getItemCount() {
        if (mListItem!=null){
            return mListItem.size();
        }
        return 0;
    }
}
