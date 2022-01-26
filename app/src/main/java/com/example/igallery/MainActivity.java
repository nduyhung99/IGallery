package com.example.igallery;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentOnAttachListener;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.camera2.params.Capability;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.UserDictionary;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.igallery.albumsfragment.AlbumsFragment;
import com.example.igallery.albumsfragment.BinFragment;
import com.example.igallery.albumsfragment.FavoriteFragment;
import com.example.igallery.albumsfragment.ItemFragment;
import com.example.igallery.albumsfragment.ItemInAlbumsFragment;
import com.example.igallery.rm.a_pro.MyBilling;
import com.example.igallery.rm.utils.RmSave;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.hsalf.smileyrating.SmileyRating;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemInAlbumsFragment.ISendDataListener, BinFragment.IControlFromBin, ItemFragment.IListener {

    private static final int REQUEST_PERMISSION_CODE = 1;
    private static final String keyShareReload = "ReloadData_1";
    private BottomNavigationView bottomNavigationView;
    private CustomViewPager viewPager;
    private ItemInAlbumsFragment itemInAlbumsFragment;
    private int positionPhotosFragment = -1, positionItemInAlbumFragment = -1, positionFavoriteFragment = -1, positionBinFragment = -1, positionSearchFragment = -1, positionAlbumFragment = -1;
    private long backPressTime;
    Toast toast;
    String rateStatus = "RateStatus";
    int countingsStars;

    private MyBilling myBilling;

    BroadcastReceiver mediaBroadcastReceiver;
    static int lol=0;
    JobSchedulerService jobSchedulerService;

    SearchFragment searchFragment;
    AlbumsFragment albumsFragment;
    PhotosFragment photosFragment;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (viewPager == null)
            return;
        AlbumsFragment currentFragment = (AlbumsFragment) viewPager.getAdapter().instantiateItem(viewPager, 1);
        if (currentFragment instanceof IOnFocusListenable) {
            ((IOnFocusListenable) currentFragment).onWindowFocusChanged(hasFocus);
        }
    }

    public void registerMediaBroadcastReceiver(){
        if (mediaBroadcastReceiver==null){
            mediaBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (action.equals(Intent.ACTION_MEDIA_SCANNER_FINISHED)) {
                        System.out.println("I'm here!");
                    }
                }
            };
            IntentFilter iFilter = new IntentFilter();
            iFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
            registerReceiver(mediaBroadcastReceiver, iFilter);
        }
    }

    //    private View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetThemeColor.setThemeColor(Color.WHITE, ContextCompat.getColor(this, R.color.white), false, false, MainActivity.this);
        setContentView(R.layout.activity_main);
//        createBin();
//        addControls();
//        setUpViewPager();
//
//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.action_photos:
//                        viewPager.setCurrentItem(0);
//                        PhotosFragment photosFragment = (PhotosFragment) viewPager.getAdapter().instantiateItem(viewPager, 0);
////                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
////                            photosFragment.reloadData();
////                        }
//                        break;
//                    case R.id.action_albums:
//                        viewPager.setCurrentItem(1);
//                        AlbumsFragment albumsFragment = (AlbumsFragment) viewPager.getAdapter().instantiateItem(viewPager, 1);
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                            albumsFragment.reloadData();
//                        }
//                        break;
//                    case R.id.action_search:
//                        viewPager.setCurrentItem(2);
//                        SearchFragment searchFragment = (SearchFragment) viewPager.getAdapter().instantiateItem(viewPager, 2);
//
//                        break;
//                    case R.id.action_settings:
//                        viewPager.setCurrentItem(3);
//                        SettingsFragment settingsFragment = (SettingsFragment) viewPager.getAdapter().instantiateItem(viewPager, 3);
//
//                        break;
//                }
//                return true;
//            }
//        });


        new Handler().postDelayed(() -> {
            createBin();
            addControls();
            setUpViewPager();
            photosFragment = (PhotosFragment) viewPager.getAdapter().instantiateItem(viewPager, 0);
            albumsFragment = (AlbumsFragment) viewPager.getAdapter().instantiateItem(viewPager, 1);
            searchFragment = (SearchFragment) viewPager.getAdapter().instantiateItem(viewPager, 2);

            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_photos:
                            viewPager.setCurrentItem(0);
                            PhotosFragment photosFragment = (PhotosFragment) viewPager.getAdapter().instantiateItem(viewPager, 0);
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                                photosFragment.reloadData();
//                            }
                            break;
                        case R.id.action_albums:
                            viewPager.setCurrentItem(1);
                            AlbumsFragment albumsFragment = (AlbumsFragment) viewPager.getAdapter().instantiateItem(viewPager, 1);
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                                albumsFragment.reloadData();
//                            }
                            break;
                        case R.id.action_search:
                            viewPager.setCurrentItem(2);
                            SearchFragment searchFragment = (SearchFragment) viewPager.getAdapter().instantiateItem(viewPager, 2);
//                            searchFragment.reloaddData();
                            break;
                        case R.id.action_settings:
                            viewPager.setCurrentItem(3);
                            SettingsFragment settingsFragment = (SettingsFragment) viewPager.getAdapter().instantiateItem(viewPager, 3);

                            break;
                    }
                    return true;
                }
            });
        }, 500);

        if (!RmSave.getPay(this))
            myBilling = new MyBilling(this, null);
    }

    private void createBin() {
        String path = getStore(this);
        String path1 = path + "/Bin";
        File file = new File(path);
        File file1 = new File(path1);
        if (!file.isDirectory()) {
            file.mkdir();
        }
        if (!file1.isDirectory()) {
            file1.mkdir();
        }
    }

    public static final String getKeyShareReload() {
        return keyShareReload;
    }

    private void setUpViewPager() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.action_photos).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.action_albums).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.action_search).setChecked(true);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.action_settings).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void addControls() {
        bottomNavigationView = findViewById(R.id.bottom_nav);
        viewPager = findViewById(R.id.viewPager);
        itemInAlbumsFragment = new ItemInAlbumsFragment();
        albumsFragment = new AlbumsFragment();

        registerMediaBroadcastReceiver();

        SharedPreferences preferences = getSharedPreferences(keyShareReload, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("RELOAD", 0);
        editor.commit();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            JobSchedulerService.scheduleJob(this);
        }
    }

    @Override
    public void sendData(String name, int dateAdded) {
        ItemFragment itemFragment = ItemFragment.newInstance(name, dateAdded);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack("ITEM_FRAGMENT");
        fragmentTransaction.add(R.id.fragmentContainer1, itemFragment, "ITEM_FRAGMENT").commit();
        bottomNavigationView.setVisibility(View.GONE);
    }

    @Override
    public void openSelect() {
        bottomNavigationView.setVisibility(View.GONE);
        viewPager.setPagingEnabled(false);
    }

    @Override
    public void closeSelect() {
        bottomNavigationView.setVisibility(View.VISIBLE);
        viewPager.setPagingEnabled(true);
    }

    @Override
    public void reloadOrtherFragment(int number) {

        switch (number){
            case 0:
                albumsFragment = (AlbumsFragment) viewPager.getAdapter().instantiateItem(viewPager, 1);
                albumsFragment.reloadData();
                searchFragment = (SearchFragment) viewPager.getAdapter().instantiateItem(viewPager, 2);
                searchFragment.reloaddData();
                break;
            case 1:
                photosFragment = (PhotosFragment) viewPager.getAdapter().instantiateItem(viewPager, 0);
                photosFragment.reloadData();
                searchFragment = (SearchFragment) viewPager.getAdapter().instantiateItem(viewPager, 2);
                searchFragment.reloaddData();
                break;
            case 2:
                photosFragment = (PhotosFragment) viewPager.getAdapter().instantiateItem(viewPager, 0);
                photosFragment.reloadData();
                albumsFragment = (AlbumsFragment) viewPager.getAdapter().instantiateItem(viewPager, 1);
                albumsFragment.reloadData();
                searchFragment = (SearchFragment) viewPager.getAdapter().instantiateItem(viewPager, 2);
                searchFragment.reloaddData();
                break;
            default:
                break;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBackPressed() {
        SharedPreferences preferences2 = getSharedPreferences(rateStatus, MODE_PRIVATE);
        int star = preferences2.getInt("STAR", 0);

        SharedPreferences preferences = getSharedPreferences(keyShareReload, MODE_PRIVATE);
        int reload = preferences.getInt("RELOAD", 0);
        List<Fragment> list = getSupportFragmentManager().getFragments();
        int openFragmentInAlbum = 0;

        if (list.size() > 0) {
            int count = 0;
            for (Fragment fragment : list) {
                if (fragment instanceof ItemInAlbumsFragment) {
                    ItemInAlbumsFragment itemInAlbumsFragment = (ItemInAlbumsFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
                    ItemInAlbumsFragment itemInAlbumsFragment1 = (ItemInAlbumsFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerSearch);
                    ItemInAlbumsFragment itemInAlbumsFragment2 = (ItemInAlbumsFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerPhotos);
                    if (viewPager.getCurrentItem() == 1) {
                        if (itemInAlbumsFragment.onBackInFragment()) {
                            return;
                        } else {
                            positionItemInAlbumFragment = count;
                        }
                    } else if (viewPager.getCurrentItem() == 2) {
                        if (itemInAlbumsFragment1.onBackInFragment()) {
                            return;
                        } else {
                            positionItemInAlbumFragment = count;
                        }
                    } else if (viewPager.getCurrentItem() == 0) {
                        if (itemInAlbumsFragment2!=null){
                            if (itemInAlbumsFragment2.onBackInFragment()) {
                                return;
                            }else {
                                positionItemInAlbumFragment = count;
                            }
                        }else {
                            positionItemInAlbumFragment = count;
                        }
                    }

                } else if (fragment instanceof BinFragment) {
                    BinFragment binFragment = (BinFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
                    if (binFragment.onBackInFragment()) {
                        return;
                    } else {
                        positionBinFragment = count;
                    }
                } else if (fragment instanceof PhotosFragment) {
                    PhotosFragment photosFragment = (PhotosFragment) viewPager.getAdapter().instantiateItem(viewPager, 0);
                    if (photosFragment.onBackInFragment()) {
                        return;
                    } else {
                        positionPhotosFragment = count;
                    }
                } else if (fragment instanceof FavoriteFragment) {
                    FavoriteFragment favoriteFragment = (FavoriteFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
                    if (favoriteFragment.onBackInFragment()) {
                        return;
                    } else {
                        positionFavoriteFragment = count;
                    }
                } else if (fragment instanceof SearchFragment) {
                    SearchFragment searchFragment = (SearchFragment) viewPager.getAdapter().instantiateItem(viewPager, 2);
//                    if (searchFragment.onBack()){
//                        searchFragment.setOnTouch();
//                        return;
//                    }else {
//                        positionSearchFragment=count;
//                    }

                    if (searchFragment.getTouch() == 1) {
                        searchFragment.setOnTouch();
                    } else if (searchFragment.onBack()) {
                        return;
                    } else {
                        positionSearchFragment = count;
                    }
                } else if (fragment instanceof AlbumsFragment) {
                    AlbumsFragment albumsFragment = (AlbumsFragment) viewPager.getAdapter().instantiateItem(viewPager, 1);
                    if (albumsFragment.getTouch() == 1) {
                        albumsFragment.setOnTouch();
                    } else if (albumsFragment.onBackInFragment()) {
                        return;
                    } else {
                        positionAlbumFragment = count;
                    }
                }
                count++;
            }
        }

        if (positionPhotosFragment >= 0) {
            if (list.get(positionPhotosFragment) instanceof PhotosFragment){
                if (((PhotosFragment) list.get(positionPhotosFragment)).onBackInFragment()) {
                    return;
                }
            }
            positionPhotosFragment = -1;
        }

        if (positionAlbumFragment >= 0) {
            if (((AlbumsFragment) list.get(positionAlbumFragment)).onBackInFragment()) {
                return;
            }
            positionAlbumFragment = -1;
        }

        if (positionSearchFragment >= 0) {
            if (((SearchFragment) list.get(positionSearchFragment)).onBack()) {
                return;
            }
            positionSearchFragment = -1;
        }

        if (reload == 1) {
            AlbumsFragment albumsFragment = (AlbumsFragment) viewPager.getAdapter().instantiateItem(viewPager, 1);
            albumsFragment.reloadData();
            PhotosFragment photosFragment = (PhotosFragment) viewPager.getAdapter().instantiateItem(viewPager, 0);
            photosFragment.reloadData();
            SearchFragment searchFragment = (SearchFragment) viewPager.getAdapter().instantiateItem(viewPager, 2);
            searchFragment.reloaddData();
            SharedPreferences preferences1 = getSharedPreferences("ReloadData", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences1.edit();
            editor.putInt("RELOAD", 0);
            editor.commit();
        }

        if (positionItemInAlbumFragment >= 0 || positionFavoriteFragment >= 0 || positionBinFragment >= 0 || positionSearchFragment >= 0 || positionAlbumFragment >= 0 || positionPhotosFragment >=0) {
            positionItemInAlbumFragment = -1;
            positionFavoriteFragment = -1;
            positionBinFragment = -1;
            positionSearchFragment = -1;
            positionAlbumFragment = -1;
            positionPhotosFragment = -1;
        } else {
            if (star < 3) {
                showRatingDialog();
            } else {
                finishAppDialog();
            }
            return;
        }

        super.onBackPressed();

    }

    @Override
    public void resetData(String name) {
        itemInAlbumsFragment.resetRecyclerViewAlbum(name, MainActivity.this);
        albumsFragment.resetRecyclerViewBucket(name);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    public static String getStore(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            File f = c.getExternalFilesDir(null);
            if (f != null)
                return f.getAbsolutePath();
            else
                return "/storage/emulated/0/Android/data/" + c.getPackageName();
        } else {
            return Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/Android/data/" + c.getPackageName();
        }
    }

    public static void copyStream(InputStream input, OutputStream output)
            throws IOException {

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
    }

    public void finishApp() {
        if (backPressTime + 2000 > System.currentTimeMillis()) {
            toast.cancel();
            finishAffinity();
        } else {
            toast = Toast.makeText(this, R.string.press_back_againt, Toast.LENGTH_LONG);
            toast.show();
        }
        backPressTime = System.currentTimeMillis();
    }

    public void finishAppDialog() {
        View view1 = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_exit, null);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(view1);

        TextView btnExit = view1.findViewById(R.id.btnExit);
        TextView btnCancel = view1.findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });

        bottomSheetDialog.show();
    }

    private void showRatingDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_layout);
        dialog.getWindow().setLayout(getWindowWidth() - 50, LinearLayout.LayoutParams.WRAP_CONTENT);

        SmileyRating rating = dialog.findViewById(R.id.smileyRating);
        rating.setSmileySelectedListener(new SmileyRating.OnSmileySelectedListener() {
            @Override
            public void onSmileySelected(SmileyRating.Type type) {
                switch (type) {
                    case TERRIBLE:
                        countingsStars = 1;
                        ratePkg(MainActivity.this, getPackageName());
                        break;
                    case BAD:
                        countingsStars = 2;
                        ratePkg(MainActivity.this, getPackageName());
                        break;
                    case OKAY:
                        countingsStars = 3;
                        ratePkg(MainActivity.this, getPackageName());
                        break;
                    case GOOD:
                        countingsStars = 4;
                        ratePkg(MainActivity.this, getPackageName());
                        break;
                    case GREAT:
                        countingsStars = 5;
                        ratePkg(MainActivity.this, getPackageName());
                        break;
                }
                SharedPreferences preferences = getSharedPreferences(rateStatus, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("STAR", countingsStars);
                editor.commit();
            }
        });
        dialog.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.btnExit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });
        dialog.show();
    }

    public void ratePkg(Context context, String pkg) {
        if (pkg == null)
            return;
        Uri uri = Uri.parse("market://details?id=" + pkg);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private int getWindowWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    @Override
    protected void onDestroy() {
        if (myBilling != null){
            myBilling.onDestroy();
        }
        SharedPreferences sharedPreferences = getSharedPreferences("RELOAD_ALL_DATA",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("reload_all_data",0);
        editor.commit();
        super.onDestroy();
    }

    public void goPremium() {
        if (myBilling != null)
            myBilling.makePurchase(getString(R.string.id_pay), null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("RELOAD_ALL_DATA",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int lol = sharedPreferences.getInt("reload_all_data",0);
        if (lol==1){
            reloadAllData();
            editor.putInt("reload_all_data",0);
            editor.commit();
        }
    }

    private void reloadAllData() {
        if (photosFragment!=null && albumsFragment!=null && searchFragment !=null){
            photosFragment.reloadData();
            albumsFragment.reloadData();
            searchFragment.reloaddData();
        }
    }

}