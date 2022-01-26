package com.example.igallery.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.igallery.albumsfragment.ImageFragment;
import com.example.igallery.model.Item;

import java.util.List;

public class ImageFragmentAdapterTest extends FragmentStateAdapter {
    List<Item> mListItem;

    public void setData(List<Item> list){
        this.mListItem = list;
        notifyDataSetChanged();
    }

    public ImageFragmentAdapterTest(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        ImageFragment imageFragment = new ImageFragment();
        Bundle b = new Bundle();
        Item item = mListItem.get(position);
        b.putSerializable("IMAGE",item);
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
