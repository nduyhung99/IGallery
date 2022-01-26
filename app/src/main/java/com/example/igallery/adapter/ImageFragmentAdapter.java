package com.example.igallery.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.igallery.albumsfragment.ImageFragment;
import com.example.igallery.model.Item;

import java.util.List;

public class ImageFragmentAdapter extends FragmentStatePagerAdapter {
    List<Item> mListItem;

    public ImageFragmentAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    public void setData(List<Item> list){
        this.mListItem = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        ImageFragment imageFragment = new ImageFragment();
        Bundle b = new Bundle();
        Item item = mListItem.get(position);
        b.putSerializable("IMAGE",item);
//        b.putSerializable("IMAGE",mListItem.get(position).getPathOfItem());
        imageFragment.setArguments(b);
        return imageFragment;
    }

    @Override
    public int getCount() {
        if (mListItem!=null){
            return mListItem.size();
        }
        return 0;
    }
}
