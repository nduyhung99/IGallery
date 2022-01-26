package com.example.igallery;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.igallery.albumsfragment.AlbumsFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new PhotosFragment();
            case 1:
                return new AlbumsFragment();
            case 2:
                return new SearchFragment();
            case 3:
                return new SettingsFragment();
            default:
                return new PhotosFragment();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
