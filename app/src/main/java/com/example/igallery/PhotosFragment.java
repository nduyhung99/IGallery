package com.example.igallery;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.igallery.adapter.AndroidXI;
import com.example.igallery.adapter.ImageFragmentAdapter;
import com.example.igallery.adapter.ImageFragmentAdapterTest;
import com.example.igallery.adapter.ItemAdapter;
import com.example.igallery.adapter.ItemDayAdapter;
import com.example.igallery.adapter.ItemMonthAdapter;
import com.example.igallery.adapter.ItemYearAdapter;
import com.example.igallery.adapter.LittleItemAdapter;
import com.example.igallery.albumsfragment.ItemInAlbumsFragment;
import com.example.igallery.database.Database;
import com.example.igallery.model.Item;
import com.example.igallery.model.ItemPhoto;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class PhotosFragment extends Fragment {
    private TextView btnDay, btnMonth, btnYear, btnAllPhoto, txtDate, txtSelect, txtCancel, dateAdded, clickEdit, txtDate2, clickSelectAll, txtTime;
    private RecyclerView rcvPhotos, rcvPhotosMonth, rcvPhotosYear, rcvAllPhotos, littleRcv;
    private LinearLayout clickBackSelected;
    private RelativeLayout layoutDate,layoutContainerAllItem,layoutContainerItemSelected, toolbarPhotos, layoutProgressBar;
    private List<ItemPhoto> listItemPhoto = new ArrayList<>();
    private List<ItemPhoto> listItemPhotoDay = new ArrayList<>();
    private List<Item> listItem = new ArrayList<>();
    private List<String> listTitle = new ArrayList<>();
    View fragmentPhotosView;
    private ImageView viewFavoriteBlue, viewDeleteBlue, viewShareBlue, viewFavoriteBlue1;
    private TextView viewCountItemSelected;
    private ImageView viewDelete, viewShare, menuPopup;
    private ItemInAlbumsFragment.ISendDataListener iSendDataListener;
    private List<Item> listItemSelected = new ArrayList<>();
    private ItemAdapter itemAdapter;
    private List<Item> listItems = new ArrayList<Item>();
    private List<String> listFavorite = new ArrayList<>();
    private List<Item> listLol = new ArrayList<>();

    private LittleItemAdapter littleItemAdapter;

    private ItemYearAdapter itemYearAdapter;
    private ItemMonthAdapter itemMonthAdapter;
    private ItemDayAdapter itemDayAdapter;

    private ViewPager2 itemViewPager;
    private ImageFragmentAdapterTest imageFragmentAdapter;

    private LinearLayoutManager linearLayoutManager,linearLayoutManager1,linearLayoutManager2, linearLayoutManager3;
    private GridLayoutManager gridLayoutManager;

    Database database;
    int currentPosition=0,lol=0, lol2=0, zoom=0, scaleType=0, positionOfRcvAllPhotos;
    TextView txtTest;
    long onClickTime = 0;

    private static final String keyShareReload = "ReloadData_1";

    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher, activityResultLauncher1, activityResultLauncherBlue, activityResultLauncherBlue1;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        iSendDataListener = (ItemInAlbumsFragment.ISendDataListener) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos,container,false);
        addControls(view);

        itemAdapter.setOnItemClickListener_1(new ItemAdapter.OnItemClickListener_1() {
            @Override
            public void onItemClick_1(int position) {
                showItemDialog(position);
            }
        });

        menuPopup.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                Context wrapper = new ContextThemeWrapper(getContext(), R.style.AppTheme);
                PopupMenu popupMenu = new PopupMenu(wrapper,v);
                popupMenu.getMenuInflater().inflate(R.menu.popup_in_photos,popupMenu.getMenu());
                popupMenu.setGravity(Gravity.END);

                if (zoom==-1){
                    popupMenu.getMenu().findItem(R.id.zoomIn).setVisible(false);
                }else if (zoom==1){
                    popupMenu.getMenu().findItem(R.id.zoomOut).setVisible(false);
                }

                if (scaleType==0){
                    popupMenu.getMenu().findItem(R.id.square).setVisible(false);
                }else {
                    popupMenu.getMenu().findItem(R.id.aspect).setVisible(false);
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.zoomIn:
                                if (zoom==0){
                                    itemAdapter.setSpanCount(1);
                                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),1);
                                    gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                        @Override
                                        public int getSpanSize(int position) {
                                            if (itemAdapter.getItemViewType(position)==0){
                                                return 1;
                                            }else {
                                                return 1;
                                            }
                                        }
                                    });
                                    rcvAllPhotos.setLayoutManager(gridLayoutManager);
                                    rcvAllPhotos.setAdapter(itemAdapter);
                                    rcvAllPhotos.scrollToPosition(positionOfRcvAllPhotos);
                                    zoom = -1;
                                }else {
                                    itemAdapter.setSpanCount(4);
                                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),4);
                                    gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                        @Override
                                        public int getSpanSize(int position) {
                                            if (itemAdapter.getItemViewType(position)==0){
                                                return 1;
                                            }else {
                                                return 4;
                                            }
                                        }
                                    });
                                    rcvAllPhotos.setLayoutManager(gridLayoutManager);
                                    rcvAllPhotos.setAdapter(itemAdapter);
                                    rcvAllPhotos.scrollToPosition(positionOfRcvAllPhotos);
                                    zoom = 0;
                                }
                                break;
                            case R.id.zoomOut:
                                if (zoom==-1){
                                    itemAdapter.setSpanCount(4);
                                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),4);
                                    gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                        @Override
                                        public int getSpanSize(int position) {
                                            if (itemAdapter.getItemViewType(position)==0){
                                                return 1;
                                            }else {
                                                return 4;
                                            }
                                        }
                                    });
                                    rcvAllPhotos.setLayoutManager(gridLayoutManager);
                                    rcvAllPhotos.setAdapter(itemAdapter);
                                    rcvAllPhotos.scrollToPosition(positionOfRcvAllPhotos);
                                    zoom = 0;
                                }else {
                                    itemAdapter.setSpanCount(5);
                                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),5);
                                    gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                        @Override
                                        public int getSpanSize(int position) {
                                            if (itemAdapter.getItemViewType(position)==0){
                                                return 1;
                                            }else {
                                                return 5;
                                            }
                                        }
                                    });
                                    rcvAllPhotos.setLayoutManager(gridLayoutManager);
                                    rcvAllPhotos.setAdapter(itemAdapter);
                                    rcvAllPhotos.scrollToPosition(positionOfRcvAllPhotos);
                                    zoom = 1;
                                }
                                break;
                            case R.id.aspect:
                                scaleType = 1;
                                itemAdapter.setCenterCrop(false);
                                break;
                            case R.id.square:
                                scaleType = 0;
                                itemAdapter.setCenterCrop(true);
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });
                setForceShowIcon(popupMenu);
                popupMenu.show();
            }
        });



        clickSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listItemSelected.isEmpty()){
                    itemAdapter.selectAll();
                    listItemSelected = itemAdapter.getSelected();
                    viewDelete.setColorFilter(Color.parseColor("#0b8bf4"));
                    viewShare.setColorFilter(Color.parseColor("#0b8bf4"));
                    setCountItem(listItemSelected);
                }else if (listItemSelected.size()<listItem.size()){
                    itemAdapter.selectAll();
                    listItemSelected = itemAdapter.getSelected();
                    setCountItem(listItemSelected);
                }else {
                    itemAdapter.unSelectAll();
                    listItemSelected = itemAdapter.getSelected();
                    viewCountItemSelected.setText("Select item");
                    viewShare.setColorFilter(Color.parseColor("#535759"));
                    viewDelete.setColorFilter(Color.parseColor("#535759"));
                }
            }
        });

        itemDayAdapter.setOnItemDayClickListener(new ItemDayAdapter.OnItemDayClickListener() {
            @Override
            public void onClick(int position) {
                if (onClickTime==0 || onClickTime+1000 < System.currentTimeMillis()){
                    ItemPhoto itemPhoto = listItemPhotoDay.get(position);
                    Bundle bundle = new Bundle();
                    bundle.putString("NAME_FOLDER",itemPhoto.getTitle());
                    bundle.putInt("TYPE",7);
//                bundle.putSerializable("LIST_BUTKET",(Serializable) listItemPhotoDay.get(position).getListItem());

                    ItemInAlbumsFragment itemInAlbumsFragment = new ItemInAlbumsFragment();
                    itemInAlbumsFragment.setArguments(bundle);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_right,R.anim.enter_from_right,R.anim.exit_to_right);
                    fragmentTransaction.addToBackStack(ItemInAlbumsFragment.nameFragment);
                    fragmentTransaction.add(R.id.fragmentContainerPhotos, itemInAlbumsFragment,ItemInAlbumsFragment.nameFragment).commit();
                    onClickTime=System.currentTimeMillis();
                }
            }
        });

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                int ok = result.getResultCode();
                Log.e("aaaa",ok+"");
                if (ok==Activity.RESULT_OK){
                    for (int i=0; i<listItemSelected.size();i++){
                        String path = listItemSelected.get(i).getPathOfItem();
//                        listItem.remove(listItemSelected.get(i));
//                        listItems.remove(listItemSelected.get(i));
                        listLol = getListReverse(setRecyclerView(getContext(),null));
                        database.queryData("delete from Favorite where Path='"+path+"'");
                        database.queryData("delete from Album where path='" + path + "'");
                    }
                    reloadAfterDelete(false);
                }
            }
        });

        activityResultLauncher1 = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                int ok = result.getResultCode();
                if (ok==Activity.RESULT_OK){
                    for (int i=0; i<listItemSelected.size();i++){
                        String path = listItemSelected.get(i).getPathOfItem();
                        copyFile(path);
                        listItem.remove(listItemSelected.get(i));
                        listItems.remove(listItemSelected.get(i));
                        listLol = getListReverse(setRecyclerView(getContext(),null));
                        database.queryData("delete from Favorite where Path='"+path+"'");
                        database.queryData("delete from Album where path='" + path + "'");
                    }
                    reloadAfterDelete(true);
                }
            }
        });

        activityResultLauncherBlue = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                int ok = result.getResultCode();
                if (ok==Activity.RESULT_OK){
                    Item item = listItem.get(itemViewPager.getCurrentItem());
                    String path = item.getPathOfItem();
                    int position = itemViewPager.getCurrentItem();
                    reloadAfterDeleteBlue(path,position,false);
                }
            }
        });

        activityResultLauncherBlue1 = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                int ok = result.getResultCode();
                if (ok==Activity.RESULT_OK){
                    Item item = listItem.get(itemViewPager.getCurrentItem());
                    String path = item.getPathOfItem();
                    copyFile(path);
                    int position = itemViewPager.getCurrentItem();
                    reloadAfterDeleteBlue(path,position,true);
                }
            }
        });

        littleItemAdapter.setOnItemLittleClick(new LittleItemAdapter.OnItemLittleClick() {
            @Override
            public void onClick(int position) {
                lol=1;
                itemViewPager.setCurrentItem(position-5);
                currentPosition=position-5;
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
                    if (position==0){
                        itemViewPager.setCurrentItem(0,false);
                    }else if (position==1&&lol==0){
                        itemViewPager.setCurrentItem(position,false);
                    }else {
                        lol2=1;
                        itemViewPager.setCurrentItem(position+1,false);
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                LinearLayoutManager lol = (LinearLayoutManager) littleRcv.getLayoutManager();
//                if (dx!=0){
//                    int position = lol.findFirstVisibleItemPosition();
//                    itemViewPager.setCurrentItem(position+1);
//                }
            }
        });

        itemViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Item item = listItem.get(position);
                long dateAdded1 = item.getDateAdded();
                String date1 = String.valueOf(DateFormat.getDateInstance(DateFormat.LONG).format(dateAdded1));
                String time = String.valueOf(new SimpleDateFormat("hh:mm aa").format(dateAdded1));
                dateAdded.setText(date1);
                txtTime.setText(time);
                setFavorite(item);
                if (currentPosition>position){
                    littleRcv.scrollToPosition(position);
                }else if (currentPosition<position){
                    if (lol==0){
                        lol=1;
                        littleRcv.scrollToPosition(position);
                    }else {
                        if (lol2==0){
                            littleRcv.scrollToPosition(position+10);
                        }
                        lol2=0;
                    }
                }
                currentPosition=position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

        btnDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackgroudButton(1);
                setCurrentRcv(1);
                toolbarPhotos.setVisibility(View.GONE);
//                txtDate2.setVisibility(View.VISIBLE);
//                txtDate.setVisibility(View.GONE);
            }
        });

        btnMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackgroudButton(2);
                setCurrentRcv(2);
                toolbarPhotos.setVisibility(View.GONE);
            }
        });

        btnYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackgroudButton(3);
                setCurrentRcv(3);
                toolbarPhotos.setVisibility(View.GONE);
            }
        });

        btnAllPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackgroudButton(4);
                setCurrentRcv(4);
                toolbarPhotos.setVisibility(View.VISIBLE);
                txtDate2.setVisibility(View.GONE);
                txtDate.setVisibility(View.VISIBLE);
            }
        });

        itemAdapter.setOnItemClickListener(new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (txtSelect.getVisibility()==View.VISIBLE){
                    Item item = listItem.get(position);
                    listItem.get(position).setItemSelected(true);
                    layoutContainerItemSelected.setVisibility(View.VISIBLE);
                    layoutContainerAllItem.setVisibility(View.GONE);
                    iSendDataListener.openSelect();
                    itemViewPager.setCurrentItem(position,false);
                    if (position==0){
                        lol=1;
                    }
                    long dateAdded1 = listItem.get(position).getDateAdded();
                    String date1 = String.valueOf(DateFormat.getDateInstance(DateFormat.LONG).format(dateAdded1));
                    String time = String.valueOf(new SimpleDateFormat("hh:mm aa").format(dateAdded1));
                    dateAdded.setText(date1);
                    txtTime.setText(time);
                    setFavorite(item);
                }else {
                    Item item = listItem.get(position);
                    if (listItem.get(position).isItemSelected() == false){
                        listItemSelected.add(item);
                        viewDelete.setColorFilter(Color.parseColor("#0b8bf4"));
                        viewShare.setColorFilter(Color.parseColor("#0b8bf4"));
                    }else {
                        listItemSelected.remove(item);
                    }
                    if (listItemSelected.size()==0){
                        viewCountItemSelected.setText(R.string.select_item);
                        viewShare.setColorFilter(Color.parseColor("#535759"));
                        viewDelete.setColorFilter(Color.parseColor("#535759"));
                    }else {
                        setCountItem(listItemSelected);
                    }
                }
            }
        });

        clickBackSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        clickEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Edit", Toast.LENGTH_SHORT).show();
            }
        });

        txtSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtSelect.setVisibility(View.GONE);
                txtCancel.setVisibility(View.VISIBLE);
                txtDate.setVisibility(View.GONE);
                menuPopup.setVisibility(View.GONE);
                clickSelectAll.setVisibility(View.VISIBLE);
                layoutDate.setVisibility(View.INVISIBLE);
                iSendDataListener.openSelect();
            }
        });

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        itemYearAdapter.setOnItemClickListener(new ItemYearAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {
                String date1 = String.valueOf(new SimpleDateFormat("MM/yyyy").format(item.getDateAdded()));
                List<String> list = getTitleThang(listItem);
                int position=0;
                for (int i=0; i<list.size(); i++){
                    if (date1.equals(list.get(i))){
                        position=i;
                        break;
                    }
                }
                setCurrentRcv(2);
                setBackgroudButton(2);
//                linearLayoutManager1.scrollToPositionWithOffset(position,0);
                rcvPhotosMonth.scrollToPosition(position);
            }
        });

        itemMonthAdapter.setOnItemClickListener(new ItemMonthAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {
                String date1 = DateFormat.getDateInstance(DateFormat.LONG).format(item.getDateAdded());
                List<String> list = getTitleNgay(listItem);
//                reverse(list);
                int position=0;
                for (int i=0; i<list.size(); i++){
                    if (date1.equalsIgnoreCase(list.get(i))){
                        position=i;
                        break;
                    }
                }
                setCurrentRcv(1);
                setBackgroudButton(1);
                rcvPhotos.scrollToPosition(position);
//                linearLayoutManager.scrollToPositionWithOffset(position,0);
                txtSelect.setVisibility(View.VISIBLE);
            }
        });


        rcvAllPhotos.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                GridLayoutManager gridLayoutManager = (GridLayoutManager) rcvAllPhotos.getLayoutManager();
                if (gridLayoutManager!=null){
                    int position = gridLayoutManager.findLastVisibleItemPosition()-1;
                    if (!listItem.isEmpty()){
                        Item item = listItem.get(position);
                        String stringDate = DateFormat.getDateInstance(DateFormat.LONG).format(item.getDateAdded());
                        txtDate.setText(stringDate);
                    }
                    positionOfRcvAllPhotos = position;
                }
            }
        });
        return view;
    }

    private void setCountItem(List<Item> listItemSelected) {
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
            viewCountItemSelected.setText(video);
        }else if (countVideo==0){
            viewCountItemSelected.setText(photo);
        }else {
            viewCountItemSelected.setText(photo+", "+video);
        }
    }

    private void showItemDialog(int position) {
        Dialog dialog=new Dialog(getContext());
        dialog.setContentView(R.layout.custom_item_dialog);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        RoundedImageView imgPlay,imgItem,imgItemVideo;
        RelativeLayout layoutShare, layoutFavorite, layoutUnFavorite, layoutDelete;
        imgItem  = dialog.findViewById(R.id.imgItem);
        imgPlay  = dialog.findViewById(R.id.imgPlay);
        layoutShare  = dialog.findViewById(R.id.layoutShare);
        layoutFavorite  = dialog.findViewById(R.id.layoutFavorite);
        layoutUnFavorite  = dialog.findViewById(R.id.layoutUnFavorite);
        layoutDelete  = dialog.findViewById(R.id.layoutDelete);
        imgItemVideo = dialog.findViewById(R.id.imgItemVideo);

        Item item = listItem.get(position);
        String path = item.getPathOfItem();
        if (listFavorite.contains(path)){
            layoutFavorite.setVisibility(View.GONE);
            layoutUnFavorite.setVisibility(View.VISIBLE);
        }
        if (path.endsWith(".mp4") || path.endsWith(".avi") || path.endsWith(".mov") || path.endsWith(".flv") || path.endsWith(".wmv")){
            imgPlay.setVisibility(View.VISIBLE);
            imgItem.setVisibility(View.GONE);
            imgItemVideo.setVisibility(View.VISIBLE);
            Glide.with(imgItemVideo.getContext()).load(item.getPathOfItem())
                    .override(800,1000)
                    .dontAnimate()
                    .into(imgItemVideo);
        }else {
            Glide.with(imgItem.getContext()).load(item.getPathOfItem())
                    .override(800,1000)
                    .dontAnimate()
                    .into(imgItem);
        }

        imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uriForFile = FileProvider.getUriForFile(getContext(), "lol1.contentprovider", new File(path));
                Intent intent = ShareCompat.IntentBuilder.from(getActivity())
                        .setStream(uriForFile) // uri from FileProvider
                        .setType("text/html")
                        .getIntent()
                        .setAction(Intent.ACTION_VIEW) //Change if needed
                        .setDataAndType(uriForFile, "video/*")
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(intent);
            }
        });

        layoutDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogDeleteBlue(position);
                dialog.dismiss();
            }
        });

        layoutFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int dateAdded = (int) (item.getDateAdded()/1000);
                int duration = (int) item.getDurationVideo();
                String id = item.getId();
                database.queryData("insert into Favorite values(null,'"+path+"',"+dateAdded+","+duration+",'"+id+"')");
                Toast.makeText(getActivity(), R.string.favorite, Toast.LENGTH_SHORT).show();
                listFavorite.add(path);
                dialog.dismiss();
            }
        });

        layoutUnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.queryData("delete from Favorite where Path='"+path+"'");
                Toast.makeText(getActivity(), R.string.unfavorite, Toast.LENGTH_SHORT).show();
                listFavorite.remove(path);
            }
        });

        layoutShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(), "lol1.contentprovider", new File(path));
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM,uri);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.setType("image/*");
                shareIntent.setType("video/*");
                startActivity(Intent.createChooser(shareIntent, "Share"));
            }
        });

        dialog.show();
    }

    private void setFavorite(Item item) {
        if (isFavorite(item.getPathOfItem())){
            viewFavoriteBlue.setVisibility(View.GONE);
            viewFavoriteBlue1.setVisibility(View.VISIBLE);
        }else {
            viewFavoriteBlue.setVisibility(View.VISIBLE);
            viewFavoriteBlue1.setVisibility(View.GONE);
        }
    }

    private boolean isFavorite(String pathOfItem) {
        if (listFavorite!=null){
            for (int i=0; i<listFavorite.size(); i++){
                if (listFavorite.get(i).equalsIgnoreCase(pathOfItem)){
                    return true;
                }
            }
        }
        return false;
    }

    public List<String> getListFavorite() {
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.fragmentPhotosView = view;

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
    }

    private void clickFavorite(View v) {
        viewFavoriteBlue1.setVisibility(View.VISIBLE);
        viewFavoriteBlue.setVisibility(View.GONE);
        Item item = listItem.get(itemViewPager.getCurrentItem());
        String path = item.getPathOfItem();
        int dateAdded = (int) (item.getDateAdded()/1000);
        int duration = (int) item.getDurationVideo();
        String id = item.getId();
        database.queryData("insert into Favorite values(null,'"+path+"',"+dateAdded+","+duration+",'"+id+"')");
        Toast.makeText(getActivity(), R.string.favorite, Toast.LENGTH_SHORT).show();
        listFavorite.add(listItem.get(itemViewPager.getCurrentItem()).getPathOfItem());
    }

    private void clickFavorite1(View v) {
        viewFavoriteBlue1.setVisibility(View.GONE);
        viewFavoriteBlue.setVisibility(View.VISIBLE);
        String path = listItem.get(itemViewPager.getCurrentItem()).getPathOfItem();
        database.queryData("delete from Favorite where Path='"+path+"'");
        Toast.makeText(getActivity(), R.string.unfavorite, Toast.LENGTH_SHORT).show();
        listFavorite.remove(listItem.get(itemViewPager.getCurrentItem()).getPathOfItem());
    }

    private void clickDeleteBlue(View v) {
        int position = itemViewPager.getCurrentItem();
        showDialogDeleteBlue(position);
    }

    private void showDialogDeleteBlue(int position) {
        Log.e("lol", String.valueOf(position) );
        View view1 = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_delete,null);

        BottomSheetDialog bottomSheetDialog =new BottomSheetDialog(getActivity(),R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(view1);
        bottomSheetDialog.show();

        TextView deleteItem = view1.findViewById(R.id.deleteItem);
        TextView deleteItemtoBin = view1.findViewById(R.id.deleteItemToBin);
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
                deleteItemsSelectedBlue(bottomSheetDialog,position);
            }
        });

        deleteItemtoBin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteToBinBlue(bottomSheetDialog,position);
            }
        });
    }

    private void deleteToBinBlue(BottomSheetDialog bottomSheetDialog, int position) {
        Log.e("lol", String.valueOf(position) );
        bottomSheetDialog.dismiss();
        showDeleteDialog(4,position);
    }

    private void deleteItemsSelectedBlue(BottomSheetDialog bottomSheetDialog, int position) {
        bottomSheetDialog.dismiss();
        showDeleteDialog(3,position);
    }

    private void reloadAfterDeleteBlue(String path, int position, boolean type) {
        if (type==false){
           Toast.makeText(getActivity(),R.string.delete_done,Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getActivity(),R.string.item_moved_to_trash,Toast.LENGTH_SHORT).show();
        }
        database.queryData("delete from Favorite where Path='"+path+"'");
        database.queryData("delete from Album where path='" + path + "'");
        listFavorite = getListFavorite();
        listItem.remove(position);
        listItems.remove(position+5);
        littleItemAdapter.setData(listItems);
        itemAdapter.setData(listItem);
        listLol = getListReverse(setRecyclerView(getContext(),null));
        imageFragmentAdapter.setData(listItem);
        itemViewPager.setAdapter(imageFragmentAdapter);
        MyTask1 myTask1 = new MyTask1();
        myTask1.execute();
        if (position>listItem.size()){
            itemViewPager.setCurrentItem(position-1);
        }else {
            itemViewPager.setCurrentItem(position);
        }
        iSendDataListener.reloadOrtherFragment(0);
    }

    private void clickShareBlue(View v) {
        Uri uri = FileProvider.getUriForFile(getActivity(), "lol1.contentprovider", new File(listItem.get(itemViewPager.getCurrentItem()).getPathOfItem()));
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM,uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setType("image/*");
        shareIntent.setType("video/*");
        startActivity(Intent.createChooser(shareIntent, "Share"));
    }

    private void clickDelete(View v) {
        if (listItemSelected.size()==0){
            Toast.makeText(getContext(), R.string.no_item_selected_1, Toast.LENGTH_SHORT).show();
            return;
        }
        View view1 = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_delete,null);

        BottomSheetDialog bottomSheetDialog =new BottomSheetDialog(getActivity(),R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(view1);
        bottomSheetDialog.show();

        TextView deleteItem = view1.findViewById(R.id.deleteItem);
        TextView deleteItemtoBin = view1.findViewById(R.id.deleteItemToBin);
        TextView btnCancel = view1.findViewById(R.id.btnCancel);

        deleteItem.setText(getString(R.string.delete_item)+" ("+listItemSelected.size()+")");
        deleteItemtoBin.setText(getString(R.string.move_to_trash)+" ("+listItemSelected.size()+")");

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItemsSelected(bottomSheetDialog);
            }
        });

        deleteItemtoBin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteToBin(bottomSheetDialog);
            }
        });
    }

    private void clickShare(View v) {
        if (listItemSelected.size()==0){
            Toast.makeText(getContext(), R.string.no_item_selected_1, Toast.LENGTH_SHORT).show();
            return;
        }
        shareItemsSelected();
    }

    private void shareItemsSelected() {
        ArrayList<Uri> list = new ArrayList<>();
        for (int i=0; i<listItemSelected.size(); i++){
            list.add(FileProvider.getUriForFile(getActivity(), "lol1.contentprovider", new File(listItemSelected.get(i).getPathOfItem())));
        }

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,list);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setType("image/*");
        shareIntent.setType("video/*");
        startActivity(Intent.createChooser(shareIntent, "Share"));
    }

    private void addControls(View view) {
        rcvPhotos = view.findViewById(R.id.rcvPhotos);
        rcvPhotosMonth = view.findViewById(R.id.rcvPhotosMonth);
        rcvPhotosYear = view.findViewById(R.id.rcvPhotosYear);
        rcvAllPhotos = view.findViewById(R.id.rcvAllPhotos);
        btnDay = view.findViewById(R.id.btnDay);
        btnMonth = view.findViewById(R.id.btnMonth);
        btnYear = view.findViewById(R.id.btnYear);
        btnAllPhoto = view.findViewById(R.id.btnAllPhoto);
        txtCancel = view.findViewById(R.id.txtCancel);
        txtDate = view.findViewById(R.id.txtDate);
        txtSelect = view.findViewById(R.id.txtSelect);
        layoutDate = view.findViewById(R.id.layoutDate);
        toolbarPhotos = view.findViewById(R.id.toolbarPhotos);
        layoutProgressBar = view.findViewById(R.id.layoutProgressBar);
        txtDate2 = view.findViewById(R.id.txtDate2);
        menuPopup = view.findViewById(R.id.menuPopup);
        clickSelectAll = view.findViewById(R.id.clickSelectAll);

        dateAdded = view.findViewById(R.id.dateAdded);
        clickEdit = view.findViewById(R.id.clickEdit);
        clickBackSelected = view.findViewById(R.id.clickBackSelected);
        layoutContainerAllItem = view.findViewById(R.id.layoutContainerAllItem);
        layoutContainerItemSelected = view.findViewById(R.id.layoutContainerItemSelected);
        itemViewPager = view.findViewById(R.id.itemViewPager);
        littleRcv = view.findViewById(R.id.littleRcv);
        txtTime = view.findViewById(R.id.txtTime);

        txtTest = view.findViewById(R.id.txtTest);

        database = new Database(getActivity());

        listFavorite = getListFavorite();


//        listItem = setRecyclerView();
//        listItemPhoto = getListThang(listItem);
//        listItemPhotoDay = getListNgay(listItem);

        linearLayoutManager = new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false);
        rcvPhotos.setLayoutManager(linearLayoutManager);
        rcvPhotos.setHasFixedSize(true);
        itemDayAdapter = new ItemDayAdapter(getContext());
        itemDayAdapter.setData(listItemPhotoDay);
        rcvPhotos.setAdapter(itemDayAdapter);

        linearLayoutManager1 = new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false);
        rcvPhotosMonth.setLayoutManager(linearLayoutManager1);
        rcvPhotosMonth.setHasFixedSize(true);
        itemMonthAdapter = new ItemMonthAdapter(getContext());
        itemMonthAdapter.setData(listItemPhoto);
        rcvPhotosMonth.setAdapter(itemMonthAdapter);

        linearLayoutManager2 = new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false);
        rcvPhotosYear.setLayoutManager(linearLayoutManager2);
        rcvPhotosYear.setHasFixedSize(true);
        itemYearAdapter = new ItemYearAdapter(getContext());
        itemYearAdapter.setData(getTitleNam(listItem));
        rcvPhotosYear.setAdapter(itemYearAdapter);

        listItem = setRecyclerView(getContext(),null);
        listItems = getListTitle1();
        listLol = getListReverse(setRecyclerView(getContext(),null));


        gridLayoutManager = new GridLayoutManager(getActivity(),4);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (itemAdapter.getItemViewType(position)==0){
                    return 1;
                }else {
                    return 4;
                }
            }
        });
        rcvAllPhotos.setLayoutManager(gridLayoutManager);
        rcvAllPhotos.setHasFixedSize(true);
        itemAdapter = new ItemAdapter(getContext());
        itemAdapter.setData(listItem);
        rcvAllPhotos.setAdapter(itemAdapter);
        rcvAllPhotos.scrollToPosition(listItem.size());

        linearLayoutManager3 = new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false);
        littleRcv.setLayoutManager(new ZoomCenterLinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        littleRcv.setHasFixedSize(true);
        littleItemAdapter = new LittleItemAdapter(getActivity());
//        littleItemAdapter.setData(listItem);
        littleItemAdapter.setData(listItems);
        littleRcv.setAdapter(littleItemAdapter);
        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(littleRcv);

//        littleRcv.addItemDecoration(new Space(660,650));

        setBackgroudButton(4);

        imageFragmentAdapter= new ImageFragmentAdapterTest(getActivity());
        imageFragmentAdapter.setData(listItem);
        itemViewPager.setAdapter(imageFragmentAdapter);
        itemViewPager.setOffscreenPageLimit(6);


        MyTask myTask = new MyTask();
        myTask.execute();
    }

    private List<Item> getListReverse(List<Item> setRecyclerView) {
        Collections.sort(setRecyclerView, new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                if (o1.getDateAdded()>o2.getDateAdded()){
                    return 1;
                }else if (o1.getDateAdded()<o2.getDateAdded()){
                    return -1;
                }
                return 0;
            }
        });
        return setRecyclerView;
    }

    private List<Item> getListTitle1() {
        List<Item> list = setRecyclerView(getContext(),null);
        for (int i=0; i<5; i++){
            list.add(0,new Item("",0,0,false,"",null));
        }
        for (int i=0; i<5; i++){
            list.add(new Item("",0,0,false,"",null));
        }
        return list;
    }

    private List<Item> getListItem() {
        List<Item> list = new ArrayList<>();
        list = setRecyclerView(getContext(),null);
        for (int i=0; i<5; i++){
            list.add(0,new Item("",0,0,false,"",null));
        }
        for (int i=0; i<5; i++){
            list.add(new Item("",0,0,false,"",null));
        }
        return list;
    }

    private List<ItemPhoto> getListNgay(List<Item> listItem) {
        List<String> list = getTitleNgay(listItem);
        List<ItemPhoto> itemPhotos= new ArrayList<>();
        for (int i=0; i<list.size(); i++){
            List<Item> itemList = new ArrayList<>();
            String countItem = "";
            for (int j=0; j<listItem.size(); j++){
                String date1 = DateFormat.getDateInstance(DateFormat.LONG).format(listItem.get(j).getDateAdded());
                if (list.get(i).equalsIgnoreCase(date1)){
                    itemList.add(listItem.get(j));
                }
            }
            if (itemList.size()>3){
                countItem = "  + "+(itemList.size()-3)+"  ";
            }
            itemPhotos.add(new ItemPhoto(list.get(i),itemList,countItem));
        }
        itemPhotos.add(new ItemPhoto("",null,""));
        return itemPhotos;
    }

    private List<ItemPhoto> getListThang(List<Item> listItem) {
        List<String> list = getTitleThang(listItem);
        List<ItemPhoto> itemPhotos= new ArrayList<>();
        for (int i=0; i<list.size(); i++){
            List<Item> itemList = new ArrayList<>();
            for (int j=0; j<listItem.size(); j++){
                String date1 = String.valueOf(new SimpleDateFormat("MM/yyyy").format(listItem.get(j).getDateAdded()));
                if (list.get(i).equalsIgnoreCase(date1)){
                    itemList.add(listItem.get(j));
                }
            }
            itemPhotos.add(new ItemPhoto(list.get(i),itemList,""));
        }
        itemPhotos.add(new ItemPhoto("",null,""));
        return itemPhotos;
    }

    private List<String> getTitleNgay(List<Item> listItem) {
        List<String> listTitle = new ArrayList<>();
        for (int i=0; i<listItem.size(); i++){
            String date1 = DateFormat.getDateInstance(DateFormat.LONG).format(listItem.get(i).getDateAdded());
            if (!listTitle.contains(date1)){
                listTitle.add(date1);
            }
        }
        return listTitle;
    }

    private List<String> getTitleThang(List<Item> listItem) {
        List<String> listTitle = new ArrayList<>();
        for (int i=0; i<listItem.size(); i++){
            String date1 = String.valueOf(new SimpleDateFormat("MM/yyyy").format(listItem.get(i).getDateAdded()));
            if (!listTitle.contains(date1)){
                listTitle.add(date1);
            }
        }
        return listTitle;
    }

    private List<Item> getTitleNam(List<Item> listItem) {
        List<String> listTitle = new ArrayList<>();
        List<Item> list = new ArrayList<>();
        for (int i=0; i<listItem.size(); i++){
            String date1 = String.valueOf(new SimpleDateFormat("yyyy").format(listItem.get(i).getDateAdded()));
            if (!listTitle.contains(date1)){
                listTitle.add(date1);
                list.add(listItem.get(i));
            }
        }
        list.add(new Item("",0,0,false,"",null));
        return list;
    }

    public static List<Item> setRecyclerView(Context context, List<Item> list) {
        List<Item> listItem1 = new ArrayList<>();
        Uri uri1 = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection1 = {MediaStore.Images.Media.DATA,MediaStore.Images.ImageColumns.DATE_TAKEN,MediaStore.Images.ImageColumns._ID};

        Uri uri2 = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection2 = {MediaStore.Video.Media.DATA,MediaStore.Video.VideoColumns.DURATION,MediaStore.Video.VideoColumns.DATE_TAKEN,MediaStore.Video.VideoColumns._ID};

        Cursor cursor = context.getContentResolver().query(uri1, projection1,null,null,null);
        if (cursor!=null){
            while (cursor.moveToNext()){
                String path = cursor.getString(cursor.getColumnIndex(projection1[0]));
                if (path.contains("storage")){
                    long date = cursor.getLong(cursor.getColumnIndex(projection1[1]));
                    String id = cursor.getString(cursor.getColumnIndex(projection1[2]));
                    listItem1.add(new Item(path,date,0,false,id,null));
                }
            }
            cursor.close();
        }

        Cursor cursor1 = context.getContentResolver().query(uri2,projection2,null,null,null);
        if (cursor1!=null){
            while (cursor1.moveToNext()){
                String path = cursor1.getString(cursor1.getColumnIndex(projection2[0]));
                long date = cursor1.getLong(cursor1.getColumnIndex(projection2[2]));
                long duration = cursor1.getLong(cursor1.getColumnIndex(projection2[1]));
                String id = cursor1.getString(cursor1.getColumnIndex(projection2[3]));
                listItem1.add(new Item(path,date,duration,false,id,null));
            }
            cursor1.close();
        }

        if (listItem1.isEmpty()){
            Log.d("TAG", "savedInstanceState is null");
        }
        Collections.sort(listItem1, new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                if (o1.getDateAdded()>o2.getDateAdded()){
                    return 1;
                }else if (o1.getDateAdded()<o2.getDateAdded()){
                    return -1;
                }
                return 0;
            }
        });

        if (list!=null){
            for (int i = 0; i < list.size(); i++) {
                String itemId = list.get(i).getId();
                for (int j = 0; j < listItem1.size(); j++) {
                    if (itemId.equals(listItem1.get(j).getId())){
                        listItem1.remove(j);
                    }
                }
            }
        }
        return listItem1;
    }

    public void reloadData(){
//        SharedPreferences preferences = getContext().getSharedPreferences(keyShareReload, MODE_PRIVATE);
//        int reload = preferences.getInt("RELOAD", 0);
//        if (reload==1){
//            listFavorite = getListFavorite();
//            listItem = setRecyclerView(getContext(),null);
//            itemAdapter.setData(listItem);
//            listItems = getListTitle1();
//            listLol = getListReverse(setRecyclerView(getContext(),null));
//            littleItemAdapter.setData(listItems);
//            MyTask1 myTask = new MyTask1();
//            myTask.execute();
//        }

        listFavorite = getListFavorite();
        listItem = setRecyclerView(getContext(),null);
        itemAdapter.setData(listItem);
        listItems = getListTitle1();
        listLol = getListReverse(setRecyclerView(getContext(),null));
        littleItemAdapter.setData(listItems);
        MyTask1 myTask = new MyTask1();
        myTask.execute();
    }

    private void setBackgroudButton(int i) {
        switch (i){
            case 1:
                btnDay.setBackgroundResource(R.drawable.shape_date_button);
                btnMonth.setBackgroundResource(0);
                btnYear.setBackgroundResource(0);
                btnAllPhoto.setBackgroundResource(0);
                btnDay.setTextColor(getResources().getColor(R.color.white));
                btnMonth.setTextColor(Color.parseColor("#706F6F"));
                btnYear.setTextColor(Color.parseColor("#706F6F"));
                btnAllPhoto.setTextColor(Color.parseColor("#706F6F"));
                break;
            case 2:
                btnMonth.setBackgroundResource(R.drawable.shape_date_button);
                btnDay.setBackgroundResource(0);
                btnYear.setBackgroundResource(0);
                btnAllPhoto.setBackgroundResource(0);
                btnMonth.setTextColor(getResources().getColor(R.color.white));
                btnDay.setTextColor(Color.parseColor("#706F6F"));
                btnYear.setTextColor(Color.parseColor("#706F6F"));
                btnAllPhoto.setTextColor(Color.parseColor("#706F6F"));
                break;
            case 3:
                btnYear.setBackgroundResource(R.drawable.shape_date_button);
                btnMonth.setBackgroundResource(0);
                btnDay.setBackgroundResource(0);
                btnAllPhoto.setBackgroundResource(0);
                btnYear.setTextColor(getResources().getColor(R.color.white));
                btnMonth.setTextColor(Color.parseColor("#706F6F"));
                btnDay.setTextColor(Color.parseColor("#706F6F"));
                btnAllPhoto.setTextColor(Color.parseColor("#706F6F"));
                break;
            case 4:
                btnAllPhoto.setBackgroundResource(R.drawable.shape_date_button);
                btnMonth.setBackgroundResource(0);
                btnYear.setBackgroundResource(0);
                btnDay.setBackgroundResource(0);
                btnAllPhoto.setTextColor(getResources().getColor(R.color.white));
                btnMonth.setTextColor(Color.parseColor("#706F6F"));
                btnYear.setTextColor(Color.parseColor("#706F6F"));
                btnDay.setTextColor(Color.parseColor("#706F6F"));
                break;
        }
    }
    private void setCurrentRcv(int i) {
        switch (i){
            case 1:
                rcvPhotos.setVisibility(View.VISIBLE);
                rcvPhotosMonth.setVisibility(View.GONE);
                rcvPhotosYear.setVisibility(View.GONE);
                rcvAllPhotos.setVisibility(View.GONE);
                break;
            case 2:
                rcvPhotos.setVisibility(View.GONE);
                rcvPhotosMonth.setVisibility(View.VISIBLE);
                rcvPhotosYear.setVisibility(View.GONE);
                rcvAllPhotos.setVisibility(View.GONE);
                break;
            case 3:
                rcvPhotos.setVisibility(View.GONE);
                rcvPhotosMonth.setVisibility(View.GONE);
                rcvPhotosYear.setVisibility(View.VISIBLE);
                rcvAllPhotos.setVisibility(View.GONE);
                break;
            case 4:
                rcvPhotos.setVisibility(View.GONE);
                rcvPhotosMonth.setVisibility(View.GONE);
                rcvPhotosYear.setVisibility(View.GONE);
                rcvAllPhotos.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void deleteItemsSelected(BottomSheetDialog bottomSheetDialog) {
        bottomSheetDialog.dismiss();
        showDeleteDialog(1,null);
    }

    private void deleteToBin(BottomSheetDialog bottomSheetDialog) {
        bottomSheetDialog.dismiss();
        showDeleteDialog(2,null);
    }

    private void reloadAfterDelete(boolean type) {
//        SharedPreferences preferences = getContext().getSharedPreferences(MainActivity.getKeyShareReload(),MODE_PRIVATE);
//        SharedPreferences.Editor editor=preferences.edit();
//        editor.putInt("RELOAD",1);
//        editor.commit();
        iSendDataListener.reloadOrtherFragment(0);

        listItem = setRecyclerView(getContext(),null);
        listItems = getListTitle1();

        listFavorite = getListFavorite();
        itemAdapter.setData(listItem);
        imageFragmentAdapter.setData(listItem);
        itemViewPager.setAdapter(imageFragmentAdapter);
        littleItemAdapter.setData(listItems);
        littleRcv.setAdapter(littleItemAdapter);
        MyTask1 myTask1 = new MyTask1();
        myTask1.execute();
        if (type==false){
            Toast.makeText(getActivity(),R.string.delete_done,Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getActivity(),R.string.item_moved_to_trash,Toast.LENGTH_SHORT).show();
        }
        cancel();
    }


    private void cancel(){
        txtCancel.setVisibility(View.GONE);
        txtSelect.setVisibility(View.VISIBLE);
        menuPopup.setVisibility(View.VISIBLE);
        txtDate.setVisibility(View.VISIBLE);
        layoutDate.setVisibility(View.VISIBLE);
        clickSelectAll.setVisibility(View.GONE);
        viewCountItemSelected.setText(R.string.select_item);
        viewShare.setColorFilter(Color.parseColor("#535759"));
        viewDelete.setColorFilter(Color.parseColor("#535759"));
        itemAdapter.unSelectAll();
        listItemSelected.clear();
        iSendDataListener.closeSelect();
    }

    private void copyFile(String pathOfItem) {
        String name = pathOfItem.replaceAll("/","%");
        String path = MainActivity.getStore(getContext())+"/Bin";
        File file = new File(path,name);
        Uri uri = Uri.fromFile(new File(pathOfItem));
        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            MainActivity.copyStream(inputStream,fileOutputStream);
            fileOutputStream.close();
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean onBackInFragment(){
        if (layoutContainerItemSelected.getVisibility()==View.VISIBLE){
            listItemSelected.clear();
            layoutContainerAllItem.setVisibility(View.VISIBLE);
            layoutContainerItemSelected.setVisibility(View.GONE);
            iSendDataListener.closeSelect();
            return true;
        }else if (txtCancel.getVisibility()==View.VISIBLE){
            cancel();
            return true;
        }else {
            return false;
        }
    }

    class MyTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            layoutProgressBar.setVisibility(View.VISIBLE);
//            iSendDataListener.openSelect();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Do your request
            listItemPhoto = getListThang(listLol);
            listItemPhotoDay = getListNgay(listLol);
            publishProgress();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            layoutProgressBar.setVisibility(View.GONE);
            itemDayAdapter.setData(listItemPhotoDay);
            itemMonthAdapter.setData(listItemPhoto);
            itemYearAdapter.setData(getTitleNam(listLol));
            rcvPhotos.scrollToPosition(rcvPhotos.getAdapter().getItemCount()-1);
            rcvPhotosMonth.scrollToPosition(rcvPhotosMonth.getAdapter().getItemCount()-1);
            rcvPhotosYear.scrollToPosition(rcvPhotosYear.getAdapter().getItemCount()-1);
//            iSendDataListener.closeSelect();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    class MyTask1 extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Do your request
            listItemPhoto = getListThang(listLol);
            listItemPhotoDay = getListNgay(listLol);
            publishProgress();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            itemDayAdapter.setData(listItemPhotoDay);
            itemMonthAdapter.setData(listItemPhoto);
            itemYearAdapter.setData(getTitleNam(listLol));
            super.onProgressUpdate(values);
        }
    }

    public void deleteListImage(ActivityResultLauncher<IntentSenderRequest> launcher, ArrayList<Uri> uriList){
        ContentResolver contentResolver = getContext().getContentResolver();

        try {

            //delete object using resolver
            for (int i=0; i<uriList.size(); i++){
                Uri uri = uriList.get(i);
                contentResolver.delete(uri, null, null);
            }


        } catch (SecurityException e) {

            PendingIntent pendingIntent = null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                ArrayList<Uri> collection = uriList;
                pendingIntent = MediaStore.createDeleteRequest(contentResolver, collection);

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                //if exception is recoverable then again send delete request using intent
                if (e instanceof RecoverableSecurityException) {
                    RecoverableSecurityException exception = (RecoverableSecurityException) e;
                    pendingIntent = exception.getUserAction().getActionIntent();
                }
            }

            if (pendingIntent != null) {
                IntentSender sender = pendingIntent.getIntentSender();
                IntentSenderRequest request = new IntentSenderRequest.Builder(sender).build();
                launcher.launch(request);
            }
        }
    }
    public static void setForceShowIcon(PopupMenu popupMenu) {
        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper
                            .getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void showDeleteDialog(int style, Integer position){
        Log.e("lol", String.valueOf(position) );
        Dialog dialog=new Dialog(getContext());
        if (style==2 || style==4){
            dialog.setContentView(R.layout.custom_move_to_trash_dialog);
        }else {
            dialog.setContentView(R.layout.custom_delete_dialog);
        }
        dialog.getWindow().setLayout(getWindowWidth()-50, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.deleteItem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (style){
                    case 1:
                        deleteItem(dialog);
                        break;
                    case 2:
                        deleteItemToBin(dialog);
                        break;
                    case 3:
                        deleteItemBlue(dialog,position);
                        break;
                    case 4:
                        deleteItemToBinBlue(dialog,position);
                        break;
                    default:
                        dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    private void deleteItemToBinBlue(Dialog dialog, Integer position) {
        Log.e("lol", String.valueOf(position) );
//        Item item = listItem.get(itemViewPager.getCurrentItem());
        Item item = listItem.get(position);
        String path = item.getPathOfItem();
        String id = item.getId();
        ContentResolver contentResolver = getContext().getContentResolver();
        Uri collection;
        String column;
//        Uri uriTest = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//        Cursor cursor  = contentResolver.query(uriTest,new String[]{MediaStore.Images.ImageColumns.IS_TRASHED,MediaStore.Images.ImageColumns._ID},null,null,null);
//        if (cursor!=null){
//            while (cursor.moveToNext()){
//                String lol = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID));
//            }
//        }
        if (path.endsWith(".mp4") || path.endsWith(".avi") || path.endsWith(".mov") || path.endsWith(".flv") || path.endsWith(".wmv")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
            } else {
                collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            }
            column = MediaStore.Video.VideoColumns._ID;
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
            } else {
                collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            }
            column = MediaStore.Images.ImageColumns._ID;
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
            copyFile(path);
            contentResolver.delete(collection,column+"=?",new String[]{id});
            SharedPreferences preferences = getContext().getSharedPreferences(MainActivity.getKeyShareReload(),MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putInt("RELOAD",1);
            editor.commit();
            dialog.dismiss();
            reloadAfterDeleteBlue(path, position,true);
        }else {
//            copyFile(path);
            dialog.dismiss();
            Uri uri = Uri.withAppendedPath(collection, item.getId());
            AndroidXI androidXI = new AndroidXI(getContext());
            androidXI.trash(activityResultLauncherBlue1,uri,true);
        }
    }

    private void deleteItemBlue(Dialog dialog, Integer position) {
        Item item = listItem.get(itemViewPager.getCurrentItem());
        String path = item.getPathOfItem();
        String id = item.getId();
        ContentResolver contentResolver = getContext().getContentResolver();
        Uri collection;
        String column;
        if (path.endsWith(".mp4") || path.endsWith(".avi") || path.endsWith(".mov") || path.endsWith(".flv") || path.endsWith(".wmv")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
            } else {
                collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            }
            column = MediaStore.Video.VideoColumns._ID;
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
            } else {
                collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            }
            column = MediaStore.Images.ImageColumns._ID;
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
            contentResolver.delete(collection,column+"=?",new String[]{id});
            SharedPreferences preferences = getContext().getSharedPreferences(MainActivity.getKeyShareReload(),MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putInt("RELOAD",1);
            editor.commit();
            dialog.dismiss();
            reloadAfterDeleteBlue(path, position,false);
        }else {
            dialog.dismiss();
            Uri uri = Uri.withAppendedPath(collection, item.getId());
            AndroidXI androidXI = new AndroidXI(getContext());
            androidXI.delete(activityResultLauncherBlue,uri);
        }
    }

    private void deleteItemToBin(Dialog dialog) {
        if (!listItemSelected.isEmpty()){
            ArrayList<Uri> list = new ArrayList<>();
            ContentResolver contentResolver = getContext().getContentResolver();
            for (int i = 0; i<listItemSelected.size(); i++){
                String path = listItemSelected.get(i).getPathOfItem();
                Uri collection;
                String column;
                if (path.endsWith(".mp4") || path.endsWith(".avi") || path.endsWith(".mov") || path.endsWith(".flv") || path.endsWith(".wmv")){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
                    } else {
                        collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    }
                    column = MediaStore.Video.VideoColumns._ID;
                }else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
                    } else {
                        collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    }
                    column = MediaStore.Images.ImageColumns._ID;
                }

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
                    copyFile(path);
                    contentResolver.delete(collection,column+"=?",new String[]{listItemSelected.get(i).getId()});
//                    listItem.remove(listItemSelected.get(i));
//                    listItems.remove(listItemSelected.get(i));
                    listLol = getListReverse(setRecyclerView(getContext(),null));
                    database.queryData("delete from Favorite where Path='"+path+"'");
                    database.queryData("delete from Album where path='" + path + "'");
                }else {
//                    copyFile(path);
                    Uri uri = Uri.withAppendedPath(collection, listItemSelected.get(i).getId());
                    list.add(uri);
                }
            }

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
                reloadAfterDelete(true);
                dialog.dismiss();
            }else {
                dialog.dismiss();
//                deleteListImage(activityResultLauncher1,list);
                AndroidXI androidXI = new AndroidXI(getContext());
                androidXI.trashList(activityResultLauncher1,list,true);
            }
        }
    }

    private int getWindowWidth() {
        DisplayMetrics metrics=new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    private void deleteItem(Dialog dialog){
        if (!listItemSelected.isEmpty()){
            ArrayList<Uri> list = new ArrayList<>();
            ContentResolver contentResolver = getContext().getContentResolver();
            for (int i = 0; i<listItemSelected.size(); i++){
                String path = listItemSelected.get(i).getPathOfItem();
                Uri collection;
                String column;
                if (path.endsWith(".mp4") || path.endsWith(".avi") || path.endsWith(".mov") || path.endsWith(".flv") || path.endsWith(".wmv")){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
                    } else {
                        collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    }
                    column = MediaStore.Video.VideoColumns._ID;
                }else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
                    } else {
                        collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    }
                    column = MediaStore.Images.ImageColumns._ID;
                }

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
                    contentResolver.delete(collection,column+"=?",new String[]{listItemSelected.get(i).getId()});
                    listItem.remove(listItemSelected.get(i));
                    listItems.remove(listItemSelected.get(i));
                    listLol = getListReverse(setRecyclerView(getContext(),null));
                    database.queryData("delete from Favorite where Path='"+path+"'");
                    database.queryData("delete from Album where path='" + path + "'");
                }else {
                    Uri uri = Uri.withAppendedPath(collection, listItemSelected.get(i).getId());
                    list.add(uri);
                }
            }

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
                reloadAfterDelete(false);
                dialog.dismiss();
            }else {
                dialog.dismiss();
                AndroidXI androidXI = new AndroidXI(getContext());
                androidXI.deleteList(activityResultLauncher,list);
            }
        }
    }

    public List<String> reverse(List<String> list) {
        for(int i = 0, j = list.size() - 1; i < j; i++) {
            list.add(i, list.remove(j));
        }
        return list;
    }
}
