package com.example.igallery.albumsfragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.igallery.IOnFocusListenable;
import com.example.igallery.MainActivity;
import com.example.igallery.PhotosFragment;
import com.example.igallery.R;
import com.example.igallery.ZoomCenterLinearLayoutManager;
import com.example.igallery.adapter.ButketAdapter;
import com.example.igallery.adapter.ButketAlbumAdapter;
import com.example.igallery.adapter.ImageFragmentAdapterTest;
import com.example.igallery.adapter.ItemAdapter;
import com.example.igallery.adapter.ItemInAlbumAdapter;
import com.example.igallery.adapter.LittleItemAdapter;
import com.example.igallery.database.Database;
import com.example.igallery.database.Favorite;
import com.example.igallery.model.Butket;
import com.example.igallery.model.Item;
import com.example.igallery.model.ItemBin;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class AlbumsFragment extends Fragment implements IOnFocusListenable {
    private RecyclerView rcvAlbums;
    private ButketAdapter butketAdapter;
    private ArrayList<Butket> buckets = new ArrayList<Butket>();
    Database database;
    private List<Favorite> listFavorite = new ArrayList<>();
    private List<String> listFavoriteString = new ArrayList<>();
    private static final String keyShareReload = "ReloadData_1";

    private RelativeLayout tiXoa, tiPhoto, tiVideo;
    private TextView txtCountPhoto, txtCountVideo, txtCountXoa, txtSeeAllAlbum;
    private TextView txtAdd, txtNoItem;

    private LinearLayout layoutMainAlbums;
    View fragmentView;

    private FrameLayout fragmentContainer;
    long onClickTime = 0;

    private ImageView viewAdd;
    Point p;
    int lol = 0, onTouch = 0;

    private RelativeLayout nesAlbum, layoutProgressBar;
    private RelativeLayout layoutAllAlbums;
    private LinearLayout clickBack;
    private ImageView clickNewAlbum;
    private TextView clickEdit, clickDone;
    private RecyclerView rcvAlbum;
    private ButketAlbumAdapter butketAlbumAdapter;
    private ItemAdapter itemAdapter;
    private List<Item> listItem = new ArrayList<>();

    private RelativeLayout toolbarMyAlbums, layoutItemInAlbum, layoutContainerItemSelected, layoutAllItemInAlbum;
    private LinearLayout clickBackMyAlbum1, clickBackSelected;
    private TextView txtTitle, txtSelect, txtCancel, txtSelectAll, txtCountItem, dateAdded, txtTime;
    private RecyclerView rcvItemInAlbum, littleRcv;
    private ViewPager2 viewPager2;
    private ItemInAlbumAdapter itemInAlbumAdapter;
    private List<Item> listItemInAlbum = new ArrayList<>();
    private ItemInAlbumsFragment.ISendDataListener iSendDataListener;
    private List<Item> listItemSelected = new ArrayList<>();
    private TextView viewCountItemSelected;
    private ImageView viewDelete, viewShare;
    private ImageFragmentAdapterTest imageFragmentAdapterTest;
    private LittleItemAdapter littleItemAdapter;
    private List<Item> listItemLittle = new ArrayList<>();
    int currentPosition = 0, lol2 = 0;
    View fragmentPhotosView;
    private ImageView viewFavoriteBlue, viewDeleteBlue, viewShareBlue, viewFavoriteBlue1;
    private ImageView imgMenuPopup;

    private String itemBin="0", totalPhoto="0", totalVideo="0";

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        iSendDataListener = (ItemInAlbumsFragment.ISendDataListener) getActivity();
    }

    private String blockCharacterSet = "~#^|$%&*!/";
    private InputFilter inputFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_albums, container, false);
        fragmentContainer = view.findViewById(R.id.fragmentContainer);

//        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer,new FolderInAlbumsFragment()).commit();

        rcvAlbums = view.findViewById(R.id.rcvAlbums);
        layoutMainAlbums = view.findViewById(R.id.layoutMainAlbums);
        txtCountPhoto = view.findViewById(R.id.txtCountPhoto);
        txtCountVideo = view.findViewById(R.id.txtCountVideo);
        txtCountXoa = view.findViewById(R.id.txtCountXoa);
        tiPhoto = view.findViewById(R.id.tiPhoto);
        tiVideo = view.findViewById(R.id.tiVideo);
        tiXoa = view.findViewById(R.id.tiXoa);

        layoutProgressBar = view.findViewById(R.id.layoutProgressBar);

        nesAlbum = view.findViewById(R.id.nesAlbum);
        layoutAllAlbums = view.findViewById(R.id.layoutAllAlbums);

        txtSeeAllAlbum = view.findViewById(R.id.txtSeeAllAlbum);
        clickBack = view.findViewById(R.id.clickBack);
        clickNewAlbum = view.findViewById(R.id.clickNewAlbum);
        clickEdit = view.findViewById(R.id.clickEdit);
        clickDone = view.findViewById(R.id.clickDone);
        rcvAlbum = view.findViewById(R.id.rcvAlbum);
        txtTime = view.findViewById(R.id.txtTime);


        layoutItemInAlbum = view.findViewById(R.id.layoutItemInAlbum);
        toolbarMyAlbums = view.findViewById(R.id.toolbarMyAlbums);
        clickBackMyAlbum1 = view.findViewById(R.id.clickBackMyAlbum1);
        txtTitle = view.findViewById(R.id.txtTitle);
        txtSelect = view.findViewById(R.id.txtSelect);
        txtCancel = view.findViewById(R.id.txtCancel);
        txtSelectAll = view.findViewById(R.id.txtSelectAll);
        rcvItemInAlbum = view.findViewById(R.id.rcvItemInAlbum);
        txtCountItem = view.findViewById(R.id.txtCountItem);

        layoutContainerItemSelected = view.findViewById(R.id.layoutContainerItemSelected);
        layoutAllItemInAlbum = view.findViewById(R.id.layoutAllItemInAlbum);
        clickBackSelected = view.findViewById(R.id.clickBackSelected);
        dateAdded = view.findViewById(R.id.dateAdded);
        littleRcv = view.findViewById(R.id.littleRcv);
        viewPager2 = view.findViewById(R.id.viewPager2);

        txtAdd = view.findViewById(R.id.txtAdd);
        txtNoItem = view.findViewById(R.id.txtNoItem);
        imgMenuPopup = view.findViewById(R.id.imgMenuPopup);


        database = new Database(getActivity());
        listFavoriteString = getListFavoriteString();


        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, RecyclerView.HORIZONTAL, false);
        rcvAlbums.setLayoutManager(gridLayoutManager);
        butketAdapter = new ButketAdapter(getContext());
        butketAdapter.setData(buckets);
        rcvAlbums.setAdapter(butketAdapter);
        rcvAlbums.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(getContext(), 2, RecyclerView.VERTICAL, false);
        rcvAlbum.setLayoutManager(gridLayoutManager1);
        butketAlbumAdapter = new ButketAlbumAdapter(getContext());
        butketAlbumAdapter.setData(buckets);
        rcvAlbum.setAdapter(butketAlbumAdapter);
        rcvAlbum.setHasFixedSize(true);

        MyTask2 myTask2 = new MyTask2();
        myTask2.execute();

        clickNewAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAlbum();
            }
        });

        clickBackSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutContainerItemSelected.setVisibility(View.GONE);
                layoutAllItemInAlbum.setVisibility(View.VISIBLE);
            }
        });

        txtSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listItemSelected.isEmpty()) {
                    itemInAlbumAdapter.selectAll();
                    listItemSelected = itemInAlbumAdapter.getSelected();
                    viewDelete.setColorFilter(Color.parseColor("#0b8bf4"));
                    viewShare.setColorFilter(Color.parseColor("#0b8bf4"));
                    setCountItem(listItemSelected, 1);
//                    viewCountItemSelected.setText(listItemSelected.size() + " " + getString(R.string.selected));
                } else if (listItemSelected.size() < listItemInAlbum.size()) {
                    itemInAlbumAdapter.selectAll();
                    listItemSelected = itemInAlbumAdapter.getSelected();
                    setCountItem(listItemSelected, 1);
//                    viewCountItemSelected.setText(listItemSelected.size() + " " + getString(R.string.selected));
                } else {
                    itemInAlbumAdapter.unSelectAll();
                    listItemSelected = itemInAlbumAdapter.getSelected();
                    viewCountItemSelected.setText("Select item");
                    viewShare.setColorFilter(Color.parseColor("#535759"));
                    viewDelete.setColorFilter(Color.parseColor("#535759"));
                }
            }
        });

        txtSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtSelect.setVisibility(View.GONE);
                imgMenuPopup.setVisibility(View.GONE);
                txtCancel.setVisibility(View.VISIBLE);
                clickBackMyAlbum1.setVisibility(View.GONE);
                txtSelectAll.setVisibility(View.VISIBLE);
                iSendDataListener.openSelect();
                itemInAlbumAdapter.setSelect(true);
            }
        });

        clickBackMyAlbum1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TranslateAnimation animate = new TranslateAnimation(0, nesAlbum.getWidth(), 0, 0);
                animate.setDuration(500);
                animate.setFillAfter(false);
                layoutItemInAlbum.startAnimation(animate);
                layoutItemInAlbum.setVisibility(View.GONE);
            }
        });

        butketAlbumAdapter.setOnItemClickListener1(new ButketAlbumAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.custom_delete_album_dialog);
                dialog.getWindow().setLayout(getWindowWidth() - 50, LinearLayout.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                TextView txtDelete = dialog.findViewById(R.id.txtDelete);
                TextView txtDelete1 = dialog.findViewById(R.id.txtDelete1);
                String name = buckets.get(position).getName();
                txtDelete.setText(getString(R.string.delete) + " " + "'" + name + "'");
                txtDelete1.setText(getString(R.string.want_to_delete_album) + " " + "'" + name + "'" + "? " + getString(R.string.photo_not_delete));
                dialog.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.findViewById(R.id.deleteItem).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        database.queryData("delete from Album where AlbumName='" + name + "'");
                        butketAlbumAdapter.setData(getImageButket(getContext()));
                        butketAdapter.setData(getImageButket(getContext()));
                        dialog.dismiss();
                        butketAlbumAdapter.setDelete(false);
                        clickBack.setVisibility(View.VISIBLE);
                        clickEdit.setVisibility(View.VISIBLE);
                        clickDone.setVisibility(View.GONE);
                        clickNewAlbum.setVisibility(View.GONE);
                        Toast.makeText(getContext(), getString(R.string.delete_successfully), Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.show();
            }
        });

        butketAlbumAdapter.setOnItemClickListener(new ButketAlbumAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (buckets.get(position).getType() == 2) {
                    openAlbum(position, false);
                    return;
                }
                if (onClickTime == 0 || onClickTime + 1000 < System.currentTimeMillis()) {
                    if (buckets.get(position).getId() != null) {
                        Bundle bundle = new Bundle();
                        bundle.putString("NAME_FOLDER", buckets.get(position).getName());
                        bundle.putInt("TYPE", 0);
                        onTouch = 1;
                        ItemInAlbumsFragment itemInAlbumsFragment = new ItemInAlbumsFragment();
                        itemInAlbumsFragment.setArguments(bundle);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
                        fragmentTransaction.addToBackStack(ItemInAlbumsFragment.nameFragment);
                        fragmentTransaction.add(R.id.fragmentContainer, itemInAlbumsFragment, ItemInAlbumsFragment.nameFragment).commit();
                        onClickTime = System.currentTimeMillis();
                    } else {
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
                        FavoriteFragment favoriteFragment = new FavoriteFragment();
                        fragmentTransaction.addToBackStack(FavoriteFragment.nameFragment);
                        fragmentTransaction.replace(R.id.fragmentContainer, favoriteFragment, FavoriteFragment.nameFragment).commit();
                        onClickTime = System.currentTimeMillis();
                    }
                }
            }
        });

        clickDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                butketAlbumAdapter.setDelete(false);
                clickBack.setVisibility(View.VISIBLE);
                clickEdit.setVisibility(View.VISIBLE);
                clickDone.setVisibility(View.GONE);
                clickNewAlbum.setVisibility(View.GONE);
            }
        });

        clickEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                butketAlbumAdapter.setDelete(true);
                clickEdit.setVisibility(View.GONE);
                clickBack.setVisibility(View.GONE);
                clickDone.setVisibility(View.VISIBLE);
                clickNewAlbum.setVisibility(View.VISIBLE);
            }
        });

        txtSeeAllAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TranslateAnimation animate = new TranslateAnimation(nesAlbum.getWidth(), 0, 0, 0);
                animate.setDuration(500);
                animate.setFillAfter(true);
                layoutAllAlbums.startAnimation(animate);
                layoutAllAlbums.setVisibility(View.VISIBLE);
            }
        });

        clickBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TranslateAnimation animate = new TranslateAnimation(0, nesAlbum.getWidth(), 0, 0);
                animate.setDuration(500);
                animate.setFillAfter(false);
                layoutAllAlbums.startAnimation(animate);
                layoutAllAlbums.setVisibility(View.GONE);
            }
        });

        tiVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("NAME_FOLDER", "Videos");
                bundle.putInt("TYPE", 5);

                ItemInAlbumsFragment itemInAlbumsFragment = new ItemInAlbumsFragment();
                itemInAlbumsFragment.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
                fragmentTransaction.addToBackStack(ItemInAlbumsFragment.nameFragment);
                fragmentTransaction.add(R.id.fragmentContainer, itemInAlbumsFragment, ItemInAlbumsFragment.nameFragment).commit();
            }
        });

        tiPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("NAME_FOLDER", "Photos");
                bundle.putInt("TYPE", 6);

                ItemInAlbumsFragment itemInAlbumsFragment = new ItemInAlbumsFragment();
                itemInAlbumsFragment.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
                fragmentTransaction.addToBackStack(ItemInAlbumsFragment.nameFragment);
                fragmentTransaction.add(R.id.fragmentContainer, itemInAlbumsFragment, ItemInAlbumsFragment.nameFragment).commit();
            }
        });

        tiXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
                BinFragment binFragment = new BinFragment();
                fragmentTransaction.addToBackStack(BinFragment.nameFragment);
                fragmentTransaction.replace(R.id.fragmentContainer, binFragment, BinFragment.nameFragment).commit();
            }
        });


        butketAdapter.setOnItemClickListener(new ButketAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (buckets.get(position).getType() == 2) {
                    openAlbum(position, false);
                    return;
                }
                if (onClickTime == 0 || onClickTime + 1000 < System.currentTimeMillis()) {
                    if (buckets.get(position).getId() != null) {
                        Bundle bundle = new Bundle();
                        bundle.putString("NAME_FOLDER", buckets.get(position).getName());
                        bundle.putInt("TYPE", 0);
                        onTouch = 1;
                        ItemInAlbumsFragment itemInAlbumsFragment = new ItemInAlbumsFragment();
                        itemInAlbumsFragment.setArguments(bundle);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
                        fragmentTransaction.addToBackStack(ItemInAlbumsFragment.nameFragment);
                        fragmentTransaction.add(R.id.fragmentContainer, itemInAlbumsFragment, ItemInAlbumsFragment.nameFragment).commit();
                        onClickTime = System.currentTimeMillis();
                    } else {
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
                        FavoriteFragment favoriteFragment = new FavoriteFragment();
                        fragmentTransaction.addToBackStack(FavoriteFragment.nameFragment);
                        fragmentTransaction.replace(R.id.fragmentContainer, favoriteFragment, FavoriteFragment.nameFragment).commit();
                    }
                }
            }
        });

//        imageFolderFavorite.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_right,R.anim.enter_from_right,R.anim.exit_to_right);
//                FavoriteFragment favoriteFragment = new FavoriteFragment();
//                fragmentTransaction.addToBackStack(FavoriteFragment.nameFragment);
//                fragmentTransaction.replace(R.id.fragmentContainer,favoriteFragment,FavoriteFragment.nameFragment).commit();
//            }
//        });

        return view;
    }

    private void setCountItem(List<Item> listItemSelected, int i) {
        int countPhoto = 0;
        int countVideo = 0;
        String photo, video;
        for (Item item : listItemSelected) {
            String path = item.getPathOfItem();
            if (path.endsWith(".mp4") || path.endsWith(".avi") || path.endsWith(".mov") || path.endsWith(".flv") || path.endsWith(".wmv")){
                countVideo++;
            }else {
                countPhoto++;
            }
        }

        if (countPhoto<2){
            photo = countPhoto +" "+ getString(R.string.photo);
        }else {
            photo = countPhoto +" "+ getString(R.string.photos);
        }

        if (countVideo<2){
            video = countVideo +" "+ getString(R.string.video_1);
        }else {
            video = countVideo +" "+ getString(R.string.videos);
        }
        if (countPhoto==0){
            if (i==1){
                viewCountItemSelected.setText(video);
            }else {
                txtCountItem.setText(video);
            }
        }else if (countVideo==0){
            if (i==1){
                viewCountItemSelected.setText(photo);
            }else {
                txtCountItem.setText(photo);
            }
        }else {
            if (i==1){
                viewCountItemSelected.setText(photo+", "+video);
            }else {
                txtCountItem.setText(photo+", "+video);
            }
        }

    }

    private void openAlbum(int position, boolean type) {
        lol = 0;
        currentPosition = 0;
        lol2 = 0;
        TranslateAnimation animate = new TranslateAnimation(nesAlbum.getWidth(), 0, 0, 0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        layoutItemInAlbum.startAnimation(animate);
        layoutItemInAlbum.setVisibility(View.VISIBLE);
        txtTitle.setText(buckets.get(position).getName());
        GridLayoutManager gridLayoutManager2 = new GridLayoutManager(getContext(), 3);
        rcvItemInAlbum.setLayoutManager(gridLayoutManager2);
        itemInAlbumAdapter = new ItemInAlbumAdapter(getContext());
        listItemInAlbum = getListItemAlbum(buckets.get(position).getName());
        itemInAlbumAdapter.setData(listItemInAlbum);
        rcvItemInAlbum.setAdapter(itemInAlbumAdapter);
        rcvItemInAlbum.setHasFixedSize(true);
        setCountItem(listItemInAlbum,2);
//        txtCountItem.setText(String.valueOf(listItemInAlbum.size()) + " " + getString(R.string.items));
        rcvItemInAlbum.scrollToPosition(listItemInAlbum.size());

        imageFragmentAdapterTest = new ImageFragmentAdapterTest(getActivity());
        imageFragmentAdapterTest.setData(listItemInAlbum);
        viewPager2.setAdapter(imageFragmentAdapterTest);
        viewPager2.setOffscreenPageLimit(2);

        listItemLittle = getListTittle(position);
        littleRcv.setLayoutManager(new ZoomCenterLinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        littleRcv.setHasFixedSize(true);
        littleItemAdapter = new LittleItemAdapter(getActivity());
        littleItemAdapter.setData(listItemLittle);
        littleRcv.setAdapter(littleItemAdapter);
//        sdfsdafsadgsga
        LinearSnapHelper snapHelper = new LinearSnapHelper();
        littleRcv.setOnFlingListener(null);
        snapHelper.attachToRecyclerView(littleRcv);

        setSizeListItemInAlbum(listItemInAlbum);

        toolbarMyAlbums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                return;
            }
        });

        imgMenuPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPopupAlbum(position,getActivity());
            }
        });
//        asdfasdggh

        littleItemAdapter.setOnItemLittleClick(new LittleItemAdapter.OnItemLittleClick() {
            @Override
            public void onClick(int position) {
                lol = 1;
                viewPager2.setCurrentItem(position - 5);
                currentPosition = position - 5;
            }
        });

        littleRcv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager lol1 = (LinearLayoutManager) littleRcv.getLayoutManager();
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {

                } else if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {

                } else {
                    int position = lol1.findFirstVisibleItemPosition();
                    if (position == 0) {
                        viewPager2.setCurrentItem(0, false);
                    } else if (position == 1 && lol == 0) {
                        viewPager2.setCurrentItem(position, false);
                    } else {
                        lol2 = 1;
                        viewPager2.setCurrentItem(position + 1, false);
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Item item = listItemInAlbum.get(position);
                long dateAdded1 = item.getDateAdded();
                String date1 = String.valueOf(DateFormat.getDateInstance(DateFormat.LONG).format(dateAdded1));
                String time = String.valueOf(new SimpleDateFormat("hh:mm aa").format(dateAdded1));
                dateAdded.setText(date1);
                txtTime.setText(time);
                setFavorite(item);

                if (currentPosition > position) {
                    littleRcv.scrollToPosition(position);
                } else if (currentPosition < position) {
                    if (lol == 0) {
                        lol = 1;
                        littleRcv.scrollToPosition(position);
                    } else {
                        if (lol2 == 0) {
                            littleRcv.scrollToPosition(position + 10);
                        }
                        lol2 = 0;
                    }
                }
                currentPosition = position;
            }
        });

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();

            }
        });

        itemInAlbumAdapter.setOnItemClickListener_1(new ItemInAlbumAdapter.OnItemClickListener_1() {
            @Override
            public void onItemClick_1() {
//                asdfdsafsdagsa
                showBottomSheetAlbum(txtTitle.getText().toString(), true, position);
            }
        });

        txtAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetAlbum(txtTitle.getText().toString(), true, position);
            }
        });

//        layoutContainerItemSelected = view.findViewById(R.id.layoutContainerItemSelected);
//        layoutAllItemInAlbum = view.findViewById(R.id.layoutAllItemInAlbum);
//        clickBackSelected = view.findViewById(R.id.clickBackSelected);
//        dateAdded = view.findViewById(R.id.dateAdded);
//        littleRcv = view.findViewById(R.id.littleRcv);
//        viewPager2 = view.findViewById(R.id.viewPager2);

        itemInAlbumAdapter.setOnItemClickListener(new ItemInAlbumAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (txtSelect.getVisibility() == View.VISIBLE) {
                    listItemInAlbum.get(position).setItemSelected(true);
                    layoutAllItemInAlbum.setVisibility(View.GONE);
                    layoutContainerItemSelected.setVisibility(View.VISIBLE);
                    iSendDataListener.openSelect();
                    viewPager2.setCurrentItem(position, false);
                    if (position == 0) {
                        lol = 1;
                    }
                    long dateAdded1 = listItemInAlbum.get(position).getDateAdded();
                    String date1 = String.valueOf(DateFormat.getDateInstance(DateFormat.LONG).format(dateAdded1));
                    String time = String.valueOf(new SimpleDateFormat("hh:mm aa").format(dateAdded1));
                    dateAdded.setText(date1);
                    txtTime.setText(time);
                    setFavorite(listItemInAlbum.get(position));
                } else {
                    Item item = listItemInAlbum.get(position);
                    if (listItemInAlbum.get(position).isItemSelected() == false) {
                        listItemSelected.add(item);
                        viewDelete.setColorFilter(Color.parseColor("#0b8bf4"));
                        viewShare.setColorFilter(Color.parseColor("#0b8bf4"));
                    } else {
                        listItemSelected.remove(item);
                    }
                    if (listItemSelected.size() == 0) {
                        viewCountItemSelected.setText(R.string.select_item);
                        viewShare.setColorFilter(Color.parseColor("#535759"));
                        viewDelete.setColorFilter(Color.parseColor("#535759"));
                    } else {
                        setCountItem(listItemSelected, 1);
//                        viewCountItemSelected.setText(listItemSelected.size() + " " + getString(R.string.selected));
                    }
                }
            }
        });
    }

    private void openPopupAlbum(int position, Activity activity) {
//        asffdsag
        LinearLayout viewGroup = (LinearLayout) activity.findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popup_layout_album, viewGroup);
        layout.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.show_popup_window));

        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(activity);
        popup.setContentView(layout);
        popup.setFocusable(true);


        float OFFSET_X = dpToPx(20);
        float OFFSET_Y = dpToPx(20);
        int[] location = new int[2];
        imgMenuPopup.getLocationOnScreen(location);

        popup.setBackgroundDrawable(new BitmapDrawable());

        popup.showAtLocation(layout, Gravity.TOP | Gravity.RIGHT, (int) OFFSET_X, (int) (location[1] + OFFSET_Y));

        RelativeLayout layoutAddPhotos = layout.findViewById(R.id.layoutAddPhotos);
        RelativeLayout layoutRenameAlbum = layout.findViewById(R.id.layoutRenameAlbum);
        ImageView imgClosePopup = layout.findViewById(R.id.imgClosePopup);
        TextView txtDate = layout.findViewById(R.id.txtDate);
        TextView txtAlbumName = layout.findViewById(R.id.txtAlbumName);
        ImageView imgAlbum = layout.findViewById(R.id.imgAlbum);

        Butket butket = buckets.get(position);
        Glide.with(imgAlbum.getContext()).load(butket.getFirstImageContainedPath())
                .override(100,100)
                .dontAnimate()
                .centerCrop()
                .into(imgAlbum);
        txtAlbumName.setText(butket.getName());

        imgClosePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });

        layoutAddPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
                showBottomSheetAlbum(butket.getName(),false,position);
            }
        });

        layoutRenameAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
                showDialogRename(position);
            }
        });
    }

    private void showDialogRename(int position) {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_dialog_create_album);
        dialog.getWindow().setLayout(getWindowWidth() - 50, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView txtCancel = dialog.findViewById(R.id.txtCancel);
        TextView txtSave = dialog.findViewById(R.id.txtSave);
        TextView txtDuplicateName = dialog.findViewById(R.id.txtDuplicateName);
        EditText edtTitleAlbum = dialog.findViewById(R.id.edtTitleAlbum);
        TextView txtNewAlbum = dialog.findViewById(R.id.txtNewAlbum);
        txtNewAlbum.setText(getString(R.string.rename_album));

        edtTitleAlbum.requestFocus();
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        edtTitleAlbum.setFilters(new InputFilter[]{inputFilter});

        edtTitleAlbum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) {
                    txtSave.setTextColor(getResources().getColor(R.color.gray6));
                } else {
                    txtSave.setTextColor(getResources().getColor(R.color.button));
                }
            }
        });

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtTitleAlbum.getText().toString();
                if (!name.equals("")) {
                    for (Butket butket : buckets) {
                        if (butket.getName() != null) {
                            if (butket.getName().toLowerCase().equals(name.toLowerCase())) {
                                txtDuplicateName.setVisibility(View.VISIBLE);
                                YoYo.with(Techniques.Bounce).duration(100).repeat(1).playOn(txtDuplicateName);
                                return;
                            }
                        }
                    }
                    dialog.dismiss();
                    renameAlbum(name, position);
                } else {
                    Toast.makeText(getContext(), getString(R.string.please_enter_a_name_for_new_album), Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
        new Handler(Looper.getMainLooper()).postDelayed(() -> showKeyboard(edtTitleAlbum, imm), 200);
    }

    private void renameAlbum(String name, int position) {
        database.queryData("update Album set AlbumName='"+name+"' where AlbumName='"+buckets.get(position).getName()+"'");

        butketAlbumAdapter.setData(getImageButket(getContext()));
        butketAdapter.setData(getImageButket(getContext()));
        rcvAlbum.scrollToPosition(buckets.size() - 1);

        listItemInAlbum = getListItemAlbum(name);
        itemInAlbumAdapter.setData(listItemInAlbum);
        imageFragmentAdapterTest.setData(listItemInAlbum);
        listItemLittle = getListTittle(position);
        littleItemAdapter.setData(listItemLittle);
        rcvItemInAlbum.scrollToPosition(listItemInAlbum.size());
        txtTitle.setText(name);
//        dsagsadgahg
    }

    private void setSizeListItemInAlbum(List<Item> listItemInAlbum) {
        if (listItemInAlbum.size()==0){
            txtAdd.setVisibility(View.VISIBLE);
            txtSelect.setVisibility(View.GONE);
            imgMenuPopup.setVisibility(View.GONE);
            txtNoItem.setVisibility(View.VISIBLE);
            rcvItemInAlbum.setVisibility(View.GONE);
            txtCountItem.setVisibility(View.INVISIBLE);
        }else {
            txtAdd.setVisibility(View.GONE);
            txtSelect.setVisibility(View.VISIBLE);
            imgMenuPopup.setVisibility(View.VISIBLE);
            txtNoItem.setVisibility(View.GONE);
            rcvItemInAlbum.setVisibility(View.VISIBLE);
            txtCountItem.setVisibility(View.VISIBLE);
        }
    }

    private void setFavorite(Item item) {
        if (isFavorite(item.getPathOfItem())) {
            viewFavoriteBlue.setVisibility(View.GONE);
            viewFavoriteBlue1.setVisibility(View.VISIBLE);
        } else {
            viewFavoriteBlue.setVisibility(View.VISIBLE);
            viewFavoriteBlue1.setVisibility(View.GONE);
        }
    }

    private boolean isFavorite(String pathOfItem) {
        if (listFavoriteString != null) {
            for (int i = 0; i < listFavoriteString.size(); i++) {
                if (listFavoriteString.get(i).equalsIgnoreCase(pathOfItem)) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<Item> getListTittle(int position) {
        List<Item> list = getListItemAlbum(buckets.get(position).getName());
        for (int i = 0; i < 5; i++) {
            list.add(0, new Item("", 0, 0, false, "", null));
        }
        for (int i = 0; i < 5; i++) {
            list.add(new Item("", 0, 0, false, "", null));
        }
        return list;
    }

    private void cancel() {
        txtSelect.setVisibility(View.VISIBLE);
        txtCancel.setVisibility(View.GONE);
        clickBackMyAlbum1.setVisibility(View.VISIBLE);
        txtSelectAll.setVisibility(View.GONE);
        iSendDataListener.closeSelect();
        viewCountItemSelected.setText(R.string.select_item);
        viewShare.setColorFilter(Color.parseColor("#535759"));
        viewDelete.setColorFilter(Color.parseColor("#535759"));
        itemInAlbumAdapter.unSelectAll();
        listItemSelected.clear();
        itemInAlbumAdapter.setSelect(false);
    }


    private void clickShare(View v) {
        if (listItemSelected.size() == 0) {
            Toast.makeText(getContext(), R.string.no_item_selected_1, Toast.LENGTH_SHORT).show();
            return;
        }
        shareItemsSelected();
    }

    private void shareItemsSelected() {
        ArrayList<Uri> list = new ArrayList<>();
        for (int i = 0; i < listItemSelected.size(); i++) {
            list.add(FileProvider.getUriForFile(getActivity(), "lol1.contentprovider", new File(listItemSelected.get(i).getPathOfItem())));
        }

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, list);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setType("image/*");
        shareIntent.setType("video/*");
        startActivity(Intent.createChooser(shareIntent, "Share"));
    }

    private void clickDelete(View v) {
        if (listItemSelected.size() == 0) {
            Toast.makeText(getContext(), R.string.no_item_selected_1, Toast.LENGTH_SHORT).show();
            return;
        }
        showBottomSheetDeleteItemInAlbum();
    }

    private void showBottomSheetDeleteItemInAlbum() {
        View view1 = getLayoutInflater().inflate(R.layout.custom_delete_item_album_dialog, null);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(view1);
        bottomSheetDialog.show();

        TextView deleteItem = view1.findViewById(R.id.deleteItem);
        TextView btnCancel = view1.findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lol = (String) txtTitle.getText();
                int position = -1;
                for (int i = 0; i < listItemSelected.size(); i++) {
                    database.queryData("delete from Album where AlbumName='" + lol + "' and idMedia='" + listItemSelected.get(i).getId() + "'");
                }
                itemInAlbumAdapter.setData(getListItemAlbum(lol));
                butketAlbumAdapter.setData(getImageButket(getContext()));
                butketAdapter.setData(getImageButket(getContext()));
                cancel();
                bottomSheetDialog.dismiss();
                for (int i = 0; i < buckets.size(); i++) {
                    if (buckets.get(i).getName().equals(lol)) {
                        position = i;
                        break;
                    }
                }
                listItemInAlbum = getListItemAlbum(buckets.get(position).getName());
                itemInAlbumAdapter.setData(listItemInAlbum);
                imageFragmentAdapterTest.setData(listItemInAlbum);
                listItemLittle = getListTittle(position);
                littleItemAdapter.setData(listItemLittle);
                rcvItemInAlbum.scrollToPosition(listItemInAlbum.size());
                setCountItem(listItemInAlbum,2);
//                txtCountItem.setText(String.valueOf(listItemInAlbum.size()) + " " + getString(R.string.items));
                Toast.makeText(getContext(), getString(R.string.delete_successfully), Toast.LENGTH_SHORT).show();
                setSizeListItemInAlbum(listItemInAlbum);
//                ádfsadgagadsfs
            }
        });
    }

    private List<Item> getListItemAlbum(String name) {
        List<Item> list = new ArrayList<>();
        Cursor data = database.getData("SELECT * FROM Album WHERE AlbumName='" + name + "'");
        if (data != null) {
            while (data.moveToNext()) {
                String lol = data.getString(2);
                if (lol != null) {
                    list.add(new Item(data.getString(3), data.getLong(4), data.getLong(5), false, lol, data.getString(1)));
                }
            }
        }
        return list;
    }

    private int getTotalPhoto() {
        int count = 0;
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {"_id"};
        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                count++;
            }
            return count;
        }
        return 0;
    }

    private int getTotalVideo() {
        int count = 0;
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {"_id"};
        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                count++;
            }
            return count;
        }
        return 0;
    }

    private List<String> getListFavoriteString() {
        Cursor data = database.getData("SELECT * FROM Favorite");
        List<String> list = new ArrayList<>();
        if (data!=null){
            while (data.moveToNext()){
                String path = data.getString(1);
                list.add(path);
            }
        }
        return list;
    }

    private void sendDataToFragmen(String name) {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.fragmentView = view;
        viewCountItemSelected = view.findViewById(R.id.txtCountItemSelected);
        viewDelete = view.findViewById(R.id.clickDelete);
        viewShare = view.findViewById(R.id.clickShare);

        viewFavoriteBlue = view.findViewById(R.id.clickFavoriteBlue);
        viewDeleteBlue = view.findViewById(R.id.clickDeleteBlue);
        viewShareBlue = view.findViewById(R.id.clickShareBlue);
        viewFavoriteBlue1 = view.findViewById(R.id.clickFavoriteBlue1);

        viewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickDelete(v);
            }
        });

        viewShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickShare(v);
            }
        });

        viewFavoriteBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickFavorite(v);
            }
        });

        viewDeleteBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickDeleteBlue(v);
            }
        });

        viewShareBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickShareBlue(v);
            }
        });

        viewFavoriteBlue1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickFavorite1(v);
            }
        });

        viewAdd = view.findViewById(R.id.clickAdd);
        viewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickAdd(v);
            }
        });
    }

    private void clickFavorite1(View v) {
        viewFavoriteBlue1.setVisibility(View.GONE);
        viewFavoriteBlue.setVisibility(View.VISIBLE);
        String path = listItemInAlbum.get(viewPager2.getCurrentItem()).getPathOfItem();
        database.queryData("delete from Favorite where Path='"+path+"'");
        Toast.makeText(getActivity(), R.string.unfavorite, Toast.LENGTH_SHORT).show();
//        listFavorite.remove(listItemInAlbum.get(viewPager2.getCurrentItem()).getPathOfItem());
        listFavoriteString.remove(listItemInAlbum.get(viewPager2.getCurrentItem()).getPathOfItem());
        SharedPreferences preferences = getContext().getSharedPreferences(MainActivity.getKeyShareReload(),MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putInt("RELOAD",1);
        editor.commit();
    }

    private void clickShareBlue(View v) {
        Uri uri = FileProvider.getUriForFile(getActivity(), "lol1.contentprovider", new File(listItemInAlbum.get(viewPager2.getCurrentItem()).getPathOfItem()));
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM,uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setType("image/*");
        shareIntent.setType("video/*");
        startActivity(Intent.createChooser(shareIntent, "Share"));
    }

    private void clickDeleteBlue(View v) {
        showBottomSheetDeleteItemInAlbumBlue();
//        adsgasscustom
    }

    private void showBottomSheetDeleteItemInAlbumBlue() {
        View view1 = getLayoutInflater().inflate(R.layout.custom_delete_item_album_dialog, null);
        String lol = (String) txtTitle.getText();

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(view1);
        bottomSheetDialog.show();

        TextView deleteItem = view1.findViewById(R.id.deleteItem);
        TextView btnCancel = view1.findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = -1;
                int positionViewPager = viewPager2.getCurrentItem();
                Item item = listItemInAlbum.get(viewPager2.getCurrentItem());
                database.queryData("delete from Album where AlbumName='" + lol + "' and idMedia='" + item.getId() + "'");
                itemInAlbumAdapter.setData(getListItemAlbum(lol));
                butketAlbumAdapter.setData(getImageButket(getContext()));
                butketAdapter.setData(getImageButket(getContext()));
                for (int i = 0; i < buckets.size(); i++) {
                    if (buckets.get(i).getName().equalsIgnoreCase(lol)) {
                        position = i;
                        break;
                    }
                }

                listItemInAlbum = getListItemAlbum(buckets.get(position).getName());
                itemInAlbumAdapter.setData(listItemInAlbum);
                imageFragmentAdapterTest.setData(listItemInAlbum);
                viewPager2.setAdapter(imageFragmentAdapterTest);
                listItemLittle = getListTittle(position);
                littleItemAdapter.setData(listItemLittle);
                rcvItemInAlbum.scrollToPosition(listItemInAlbum.size());

                if (positionViewPager>listItemInAlbum.size()){
                    viewPager2.setCurrentItem(positionViewPager-1);
                }else {
                    viewPager2.setCurrentItem(positionViewPager);
                }
                Toast.makeText(getContext(), getString(R.string.delete_successfully), Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
                setCountItem(listItemInAlbum,2);
//                txtCountItem.setText(String.valueOf(listItemInAlbum.size()) + " " + getString(R.string.items));
            }
        });
    }

    private void clickFavorite(View v) {
        viewFavoriteBlue1.setVisibility(View.VISIBLE);
        viewFavoriteBlue.setVisibility(View.GONE);
        Item item = listItemInAlbum.get(viewPager2.getCurrentItem());
        String path = item.getPathOfItem();
        int dateAdded = (int) (item.getDateAdded()/1000);
        int duration = (int) item.getDurationVideo();
        String id = item.getId();
        database.queryData("insert into Favorite values(null,'"+path+"',"+dateAdded+","+duration+",'"+id+"')");
        Toast.makeText(getActivity(), R.string.favorite, Toast.LENGTH_SHORT).show();
        listFavoriteString.add(item.getPathOfItem());
        SharedPreferences preferences = getContext().getSharedPreferences(MainActivity.getKeyShareReload(),MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putInt("RELOAD",1);
        editor.commit();
    }


    private ArrayList<Butket> getImageButket(Context context) {
        ArrayList<Butket> butkets1 = new ArrayList<>();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.MediaColumns.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA, MediaStore.MediaColumns.DATE_TAKEN, "_id"};

        Uri uri1 = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection1 = {MediaStore.MediaColumns.BUCKET_DISPLAY_NAME, MediaStore.Video.Media.DATA, MediaStore.MediaColumns.DATE_TAKEN, "_id", "duration"};

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, MediaStore.MediaColumns.DATE_TAKEN);
        ArrayList<String> listPath = new ArrayList<>();
        if (cursor != null) {
            File file;
            while (cursor.moveToNext()) {
                String bucketPath = cursor.getString(cursor.getColumnIndex(projection[0]));
                String fisrtImage = cursor.getString(cursor.getColumnIndex(projection[1]));
                long dateAdded = cursor.getLong(cursor.getColumnIndex(projection[2]));
                String id = cursor.getString(cursor.getColumnIndex(projection[3]));
                file = new File(fisrtImage);
                if (file.exists()) {
                    if (listPath.contains(bucketPath)) {
                        for (int i = 0; i < butkets1.size(); i++) {
                            if (butkets1.get(i).getName() != null && butkets1.get(i).getName().equals(bucketPath)) {
                                int a = butkets1.get(i).getTotalItem();
                                butkets1.remove(i);
                                butkets1.add(new Butket(1, bucketPath, fisrtImage, a + 1, dateAdded, 0, id));
                                break;
                            }
                        }
                    } else {
                        butkets1.add(new Butket(1, bucketPath, fisrtImage, 1, dateAdded, 0, id));
                        listPath.add(bucketPath);
                    }
//                    buckets.add(new Butket(bucketPath, fisrtImage));
//                    listPath.add(bucketPath);
                }
            }
            cursor.close();
        }

        Cursor cursor1 = context.getContentResolver().query(uri1, projection1, null, null, null);
        if (cursor1 != null) {
            File file;
            while (cursor1.moveToNext()) {
                String bucketPath = cursor1.getString(cursor1.getColumnIndex(projection1[0]));
                String fisrtImage = cursor1.getString(cursor1.getColumnIndex(projection1[1]));
                long dateAdded = cursor1.getLong(cursor1.getColumnIndex(projection1[2]));
                String id = cursor1.getString(cursor1.getColumnIndex(projection1[3]));
                long duration = cursor1.getLong(cursor1.getColumnIndex(projection1[4]));
                file = new File(fisrtImage);
                if (file.exists()) {
                    if (listPath.contains(bucketPath)) {
                        for (int i = 0; i < butkets1.size(); i++) {
                            if (butkets1.get(i).getName() != null && butkets1.get(i).getName().equals(bucketPath)) {
                                if (butkets1.get(i).getDateAdded() > dateAdded) {
                                    int a = butkets1.get(i).getTotalItem();
                                    butkets1.get(i).setTotalItem(a + 1);
                                    break;
                                } else {
                                    int a = butkets1.get(i).getTotalItem();
                                    butkets1.remove(i);
                                    butkets1.add(new Butket(1, bucketPath, fisrtImage, a + 1, dateAdded, duration, id));
                                    break;
                                }
                            }
                        }
                    } else {
                        butkets1.add(new Butket(1, bucketPath, fisrtImage, 1, dateAdded, duration, id));
                        listPath.add(bucketPath);
                    }
                }
            }
            cursor.close();
        }

        if (butkets1.isEmpty()) {
            Log.d("TAG", "savedInstanceState is null");
        }
        Collections.sort(butkets1, new Comparator<Butket>() {
            @Override
            public int compare(Butket o1, Butket o2) {
                if (o1.getDateAdded() > o2.getDateAdded()) {
                    return -1;
                } else if (o1.getDateAdded() < o2.getDateAdded()) {
                    return 1;
                }
                return 0;
            }
        });

        if (!butkets1.isEmpty()) {
            if (listFavoriteString.size() > 0) {
//                Favorite favorite = listFavorite.get(0);
                butkets1.add(1, new Butket(1, "Favorite", listFavoriteString.get(0), listFavoriteString.size(), 0, 0, null));
            } else {
                butkets1.add(1, new Butket(1, "Favorite", null, 0, 0, 0, null));
            }
        }

        List<Butket> listLol = new ArrayList<>();
        List<String> listName = new ArrayList<>();
        Cursor data = database.getData("SELECT * FROM Album");
        if (data != null) {
            while (data.moveToNext()) {
                String lol = data.getString(1);
                Butket butket = new Butket(2, data.getString(1), data.getString(3), 0, data.getLong(4), data.getLong(5), data.getString(2));

                if (listName.contains(lol)) {
                    for (int i = 0; i < listLol.size(); i++) {
                        if (listLol.get(i).getName().equals(lol)) {
                            int count = listLol.get(i).getTotalItem() + 1;
                            listLol.remove(i);
                            listLol.add(new Butket(2, data.getString(1), data.getString(3), count, data.getLong(4), data.getLong(5), data.getString(2)));
                        }
                    }
                } else {
                    listLol.add(new Butket(2, data.getString(1), data.getString(3), 0, data.getLong(4), data.getLong(5), data.getString(2)));
                    listName.add(lol);
                }
            }
        }
        butkets1.addAll(listLol);
        return butkets1;
    }

    public ArrayList<Butket> reverse(ArrayList<Butket> list) {
        if (list.size() > 1) {
            Butket value = list.remove(0);
            reverse(list);
            list.add(value);
        }
        return list;
    }

    public void clickAdd(View view) {
        TranslateAnimation animate = new TranslateAnimation(nesAlbum.getWidth(), 0, 0, 0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        layoutAllAlbums.startAnimation(animate);
        layoutAllAlbums.setVisibility(View.VISIBLE);
        showDialogAlbum();
    }

    private void showDialogFolder() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_dialog_create_folder);
        dialog.getWindow().setLayout(getWindowWidth() - 50, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView txtCancel = dialog.findViewById(R.id.txtCancel);
        TextView txtSave = dialog.findViewById(R.id.txtSave);
        EditText edtTitleFolder = dialog.findViewById(R.id.edtTitleFolder);

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtTitleFolder.getText().equals("")) {
                    Toast.makeText(getContext(), getString(R.string.please_enter_a_name_for_new_folder), Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();
                }

            }
        });

        dialog.show();
        TranslateAnimation animate = new TranslateAnimation(nesAlbum.getWidth(), 0, 0, 0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        layoutAllAlbums.startAnimation(animate);
        layoutAllAlbums.setVisibility(View.VISIBLE);
    }

    private void showDialogAlbum() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_dialog_create_album);
        dialog.getWindow().setLayout(getWindowWidth() - 50, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView txtCancel = dialog.findViewById(R.id.txtCancel);
        TextView txtSave = dialog.findViewById(R.id.txtSave);
        TextView txtDuplicateName = dialog.findViewById(R.id.txtDuplicateName);
        EditText edtTitleAlbum = dialog.findViewById(R.id.edtTitleAlbum);

        edtTitleAlbum.requestFocus();
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        edtTitleAlbum.setFilters(new InputFilter[]{inputFilter});

        edtTitleAlbum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) {
                    txtSave.setTextColor(getResources().getColor(R.color.gray6));
                } else {
                    txtSave.setTextColor(getResources().getColor(R.color.button));
                }
            }
        });

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtTitleAlbum.getText().toString();
                if (!name.equals("")) {
                    for (Butket butket : buckets) {
                        if (butket.getName() != null) {
                            if (butket.getName().toLowerCase().equals(name.toLowerCase())) {
                                txtDuplicateName.setVisibility(View.VISIBLE);
                                YoYo.with(Techniques.Bounce).duration(100).repeat(1).playOn(txtDuplicateName);
                                return;
                            }
                        }
                    }
                    dialog.dismiss();
                    showBottomSheetAlbum(name, false, -1);
                } else {
                    Toast.makeText(getContext(), getString(R.string.please_enter_a_name_for_new_album), Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
        new Handler(Looper.getMainLooper()).postDelayed(() -> showKeyboard(edtTitleAlbum, imm), 200);
    }

    private void showBottomSheetAlbum(String name, boolean type, int position) {
        View viewLol = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_album, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(viewLol);
        bottomSheetDialog.show();
//        bottomSheetDialog.setCancelable(false);

        TextView txtTitle = bottomSheetDialog.findViewById(R.id.txtTitle);
        TextView txtCancel = bottomSheetDialog.findViewById(R.id.txtCancel);
        TextView txtDone = bottomSheetDialog.findViewById(R.id.txtDone);
        RecyclerView rcvAllItems = bottomSheetDialog.findViewById(R.id.rcvAllItems);
        LinearLayout layoutBottom = bottomSheetDialog.findViewById(R.id.layoutBottom);

        txtTitle.setText(getString(R.string.add_item_to_album) + " '" + name + "'");
        if (type == false) {
            listItem = PhotosFragment.setRecyclerView(getContext(), null);
        } else {
            listItem = PhotosFragment.setRecyclerView(getContext(), listItemInAlbum);
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (itemAdapter.getItemViewType(position) == 0) {
                    return 1;
                } else {
                    return 3;
                }
            }
        });
        rcvAllItems.setLayoutManager(gridLayoutManager);
        itemAdapter = new ItemAdapter(getContext());
        itemAdapter.setData(listItem);
        itemAdapter.setSpanCount(3);
        rcvAllItems.setAdapter(itemAdapter);
        itemAdapter.setItemCount(2);
        rcvAllItems.scrollToPosition(listItem.size());
        rcvAllItems.setHasFixedSize(true);
        List<Item> list = new ArrayList<>();
//        database.queryData("insert into Album values(null,'"+name+"','empty')");

        itemAdapter.setOnItemClickListener(new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Item item = listItem.get(position);
                if (item.isItemSelected() == false) {
                    list.add(item);
                } else {
                    list.remove(item);
                }
            }
        });

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        txtDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!type) {
                    database.queryData("insert into Album values(null,'" + name + "',null,null,0,0)");
                }
                if (list.size() != 0) {
                    for (int i = 0; i < list.size(); i++) {
                        Item item = list.get(i);
                        database.queryData("insert into Album values(null,'" + name + "','" + item.getId() + "','" + item.getPathOfItem() + "'," + item.getDateAdded() + "," + item.getDurationVideo() + ")");
                        Log.e("~~~", name);
                    }
                }
                bottomSheetDialog.dismiss();
                butketAlbumAdapter.setData(getImageButket(getContext()));
                butketAdapter.setData(getImageButket(getContext()));
                rcvAlbum.scrollToPosition(buckets.size() - 1);
                if (position != -1) {
                    listItemInAlbum = getListItemAlbum(buckets.get(position).getName());
                    itemInAlbumAdapter.setData(listItemInAlbum);
                    imageFragmentAdapterTest.setData(listItemInAlbum);
                    listItemLittle = getListTittle(position);
                    littleItemAdapter.setData(listItemLittle);
                    rcvItemInAlbum.scrollToPosition(listItemInAlbum.size());
                    setCountItem(listItemInAlbum,2);
//                    txtCountItem.setText(String.valueOf(listItemInAlbum.size()) + " " + getString(R.string.items));
                }

                if (layoutItemInAlbum.getVisibility() == View.VISIBLE) {
                    listItemInAlbum = getListItemAlbum(name);
                    itemInAlbumAdapter.setData(listItemInAlbum);
                    setSizeListItemInAlbum(listItemInAlbum);
                    setCountItem(listItemInAlbum,2);
                }
            }
        });

        BottomSheetBehavior bottomSheetBehavior = bottomSheetDialog.getBehavior();

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public void clickEdit(View view) {
        Toast.makeText(getActivity(), "Edit", Toast.LENGTH_SHORT).show();
    }

    public void resetRecyclerViewBucket(String name) {
        butketAdapter.setData(getImageButket(getContext()));
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void reloadData() {
        SharedPreferences preferences = getContext().getSharedPreferences(keyShareReload, MODE_PRIVATE);
        int reload = preferences.getInt("RELOAD", 0);
//        if (reload==1){
//            listFavoriteString = getListFavoriteString();
//            butketAdapter.setData(getImageButket(getContext()));
//            txtCountXoa.setText(String.valueOf(getCountItemBin()));
//            txtCountXoa.setText(String.valueOf(getCountItemBin()));
//            txtCountVideo.setText(String.valueOf(getTotalVideo()));
//            txtCountPhoto.setText(String.valueOf(getTotalPhoto()));
//        }

//        MyTask myTask = new MyTask();
//        if (myTask.isCancelled()){
//            myTask.execute();
//        }
        listFavoriteString = getListFavoriteString();
        butketAdapter.setData(getImageButket(getContext()));
        txtCountXoa.setText(String.valueOf(getCountItemBin()));
        txtCountXoa.setText(String.valueOf(getCountItemBin()));
        txtCountVideo.setText(String.valueOf(getTotalVideo()));
        txtCountPhoto.setText(String.valueOf(getTotalPhoto()));
    }

    private int getCountItemBin() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            int count = 0;
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {MediaStore.MediaColumns.IS_TRASHED};

            Uri uri1 = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            String[] projection1 = {MediaStore.MediaColumns.IS_TRASHED};

            Bundle bundle = new Bundle();
            bundle.putInt("android:query-arg-match-trashed", 1);
            bundle.putString("android:query-arg-sql-selection = ?",
                    "${MediaStore.MediaColumns.IS_TRASHED} = 1");
            bundle.putString("android:query-arg-sql-sort-order = ?",
                    "${MediaStore.MediaColumns.DATE_MODIFIED} DESC");
            Cursor cursor = getContext().getContentResolver().query(uri, projection, bundle, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int lol = cursor.getInt(cursor.getColumnIndex(projection[0]));
                    if (lol == 1) {
                        count++;
                    }
                }
            }

            Cursor cursor1 = getContext().getContentResolver().query(uri1, projection1, bundle, null);

            if (cursor1 != null) {
                while (cursor1.moveToNext()) {
                    int lol = cursor1.getInt(cursor1.getColumnIndex(projection1[0]));
                    if (lol == 1) {
                        count++;
                    }
                }
            }
            return count;
        }
        List<ItemBin> list1 = new ArrayList<>();
        File file = new File(MainActivity.getStore(getContext()) + "/Bin");
        if (file.isDirectory()) {
            File[] listFile = file.listFiles();
            for (int i = 0; i < listFile.length; i++) {
                String path = listFile[i].getAbsolutePath();
                long date = listFile[i].lastModified();
                list1.add(new ItemBin(path, null, date, false));
            }
            return list1.size();
        }
        return 0;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        int[] location = new int[2];

        // Get the x, y location and store it in the location[] array
        // location[0] = x, location[1] = y.
        viewAdd.getLocationOnScreen(location);

        //Initialize the Point with x, and y positions
        p = new Point();
        p.x = location[0];
        p.y = location[1];
    }

    private int getWindowWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    public static void showKeyboard(EditText edt, InputMethodManager imm) {
        edt.requestFocus();
        imm.showSoftInput(edt, InputMethodManager.SHOW_IMPLICIT);
    }

    public boolean onBackInFragment() {
        if (onTouch == 0) {
            if (layoutItemInAlbum.getVisibility() == View.VISIBLE) {
                if (layoutContainerItemSelected.getVisibility() == View.VISIBLE) {
                    layoutContainerItemSelected.setVisibility(View.GONE);
                    layoutAllItemInAlbum.setVisibility(View.VISIBLE);
                    iSendDataListener.closeSelect();
                    return true;
                }
                if (txtSelect.getVisibility() == View.GONE && txtAdd.getVisibility()==View.GONE) {
                    cancel();
                    return true;
                }
                TranslateAnimation animate = new TranslateAnimation(0, nesAlbum.getWidth(), 0, 0);
                animate.setDuration(500);
                animate.setFillAfter(false);
                layoutItemInAlbum.startAnimation(animate);
                layoutItemInAlbum.setVisibility(View.GONE);
                return true;
            }
            if (layoutAllAlbums.getVisibility() == View.VISIBLE) {
                if (butketAlbumAdapter.getDelete() == true) {
                    butketAlbumAdapter.setDelete(false);
                    return true;
                }
                TranslateAnimation animate = new TranslateAnimation(0, nesAlbum.getWidth(), 0, 0);
                animate.setDuration(500);
                animate.setFillAfter(false);
                layoutAllAlbums.startAnimation(animate);
                layoutAllAlbums.setVisibility(View.GONE);
                return true;
            }
        }
        return false;
    }

    public void setOnTouch() {
        onTouch = 0;
    }

    public int getTouch() {
        return onTouch;
    }

    public static float dpToPx(float dp) {
        return TypedValue.applyDimension(1, dp, Resources.getSystem().getDisplayMetrics());
    }

    public static float pxToDp(float px) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return px / ((float)metrics.densityDpi / 160.0F);
    }

    class MyTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            iSendDataListener.openSelect();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Do your request
            listFavoriteString = getListFavoriteString();
            getImageButket(getContext());
            publishProgress();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            butketAdapter.setData(buckets);
            txtCountXoa.setText(String.valueOf(getCountItemBin()));
            txtCountXoa.setText(String.valueOf(getCountItemBin()));
            txtCountVideo.setText(String.valueOf(getTotalVideo()));
            txtCountPhoto.setText(String.valueOf(getTotalPhoto()));
//            iSendDataListener.closeSelect();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    class MyTask2 extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            layoutProgressBar.setVisibility(View.VISIBLE);
            nesAlbum.setVisibility(View.GONE);
//            iSendDataListener.openSelect();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Do your request
            buckets = getImageButket(getContext());
            itemBin = String.valueOf(getCountItemBin());
            totalPhoto = String.valueOf(getTotalPhoto());
            totalVideo = String.valueOf(getTotalVideo());
            publishProgress();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            layoutProgressBar.setVisibility(View.GONE);
            nesAlbum.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            butketAdapter.setData(buckets);
            butketAlbumAdapter.setData(buckets);
            txtCountXoa.setText(itemBin);
            txtCountVideo.setText(totalVideo);
            txtCountPhoto.setText(totalPhoto);
        }
    }
}
