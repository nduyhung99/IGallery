package com.example.igallery.albumsfragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

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
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.igallery.CustomViewPagerItem;
import com.example.igallery.FileUltils;
import com.example.igallery.MainActivity;
import com.example.igallery.R;
import com.example.igallery.ZoomCenterLinearLayoutManager;
import com.example.igallery.adapter.AndroidXI;
import com.example.igallery.adapter.ImageFragmentAdapter;
import com.example.igallery.adapter.ImageFragmentAdapterTest;
import com.example.igallery.adapter.ItemAdapter;
import com.example.igallery.adapter.LittleItemAdapter;
import com.example.igallery.database.Database;
import com.example.igallery.model.Butket;
import com.example.igallery.model.Item;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ItemInAlbumsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemInAlbumsFragment extends Fragment {
    private ItemAdapter itemAdapter;
    private List<Item> listItems = new ArrayList<Item>();
    private RecyclerView rcvItemInAlbums, littleRcv;
    private TextView folderName, clickSelect, clickCloseSelect, clickSelectAll,viewCountItemSelected, txtTitle, txtTime;
    private LinearLayout clickBack;
    private String folderName1;
    private List<Item> listItemSelected = new ArrayList<Item>();
    private ImageView menuPopup,viewDelete, viewShare;
    private ImageView viewFavoriteBlue, viewDeleteBlue, viewShareBlue, viewFavoriteBlue1;
    private List<String> listFavorite = new ArrayList<>();

    private RelativeLayout layoutContainerAllItem,layoutContainerItemSelected;
    private LinearLayout clickBackSelected;
    private TextView dateAdded,clickEdit;
    private int type;
    private List<Butket> listReceive;

    private ViewPager2 viewPager2;
    private ImageFragmentAdapterTest imageFragmentAdapterTest;
    int currentPosition=0,lol=0, lol2=0;

    Database database;

    private LittleItemAdapter littleItemAdapter;
    private List<Item> listItemLittle = new ArrayList<>();

    View fragmentItemInAlbumView;

    private ISendDataListener mISendDataListener;
    public static final String nameFragment = ItemInAlbumsFragment.class.getName();

    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher, activityResultLauncher1, activityResultLauncherBlue, activityResultLauncherBlue1;

    public interface ISendDataListener{
        void sendData(String name, int dateAdded);
        void openSelect();
        void closeSelect();
        void reloadOrtherFragment(int number);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mISendDataListener = (ISendDataListener) getActivity();
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ItemInAlbumsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ItemInAlbumsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ItemInAlbumsFragment newInstance(String param1, String param2) {
        ItemInAlbumsFragment fragment = new ItemInAlbumsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragment_item_in_albums, container, false);
        addControls(view);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                int resultCode=result.getResultCode();
                if (resultCode== Activity.RESULT_OK) {
                    for (int i = 0; i < listItemSelected.size(); i++) {
                        String path = listItemSelected.get(i).getPathOfItem();
                        listItemLittle.remove(listItemSelected.get(i));
                        listItems.remove(listItemSelected.get(i));
                        database.queryData("delete from Favorite where Path='" + path + "'");
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
                        listItems.remove(listItemSelected.get(i));
                        listItemLittle.remove(listItemSelected.get(i));
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
                int resultCode = result.getResultCode();
                if (resultCode==Activity.RESULT_OK){
                    Item item = listItems.get(viewPager2.getCurrentItem());
                    String path = item.getPathOfItem();
                    int position = viewPager2.getCurrentItem();
                    reloadAfterDeleteBlue(path,position,false);
                }
            }
        });

        activityResultLauncherBlue1 = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                int resultCode = result.getResultCode();
                if (resultCode==Activity.RESULT_OK){
                    Item item = listItems.get(viewPager2.getCurrentItem());
                    String path = item.getPathOfItem();
                    int position = viewPager2.getCurrentItem();
                    reloadAfterDeleteBlue(path,position,true);
                }
            }
        });

        littleItemAdapter.setOnItemLittleClick(new LittleItemAdapter.OnItemLittleClick() {
            @Override
            public void onClick(int position) {
                lol=1;
                viewPager2.setCurrentItem(position-5);
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
                        viewPager2.setCurrentItem(0,false);
                    }else if (position==1&&lol==0){
                        viewPager2.setCurrentItem(position,false);
                    }else {
                        lol2=1;
                        viewPager2.setCurrentItem(position+1,false);
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

        itemAdapter.setOnItemClickListener(new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (clickSelect.getVisibility()==View.VISIBLE){
                    Item item = listItems.get(position);
                    listItems.get(position).setItemSelected(true);
                    layoutContainerAllItem.setVisibility(View.GONE);
                    layoutContainerItemSelected.setVisibility(View.VISIBLE);
                    mISendDataListener.openSelect();
                    viewPager2.setCurrentItem(position,false);
                    if (position==0){
                        lol=1;
                    }
                    long dateAdded1 = listItems.get(position).getDateAdded();
                    String date1 = String.valueOf(DateFormat.getDateInstance(DateFormat.LONG).format(dateAdded1));
                    String time = String.valueOf(new SimpleDateFormat("hh:mm aa").format(dateAdded1));
                    dateAdded.setText(date1);
                    txtTime.setText(time);
                    setFavorite(item);
                }else {
                    Item item = listItems.get(position);
                    if (listItems.get(position).isItemSelected() == false){
                        listItemSelected.add(item);
                        viewDelete.setColorFilter(Color.parseColor("#0b8bf4"));
                        viewShare.setColorFilter(Color.parseColor("#0b8bf4"));
                    }else {
                        for (int i = 0; i<listItemSelected.size(); i++){
                            if (listItemSelected.get(i).getPathOfItem().equalsIgnoreCase(item.getPathOfItem())){
                                listItemSelected.remove(i);
                                break;
                            }
                        }
                    }
                    if (listItemSelected.size()==0){
                        viewCountItemSelected.setText(R.string.select_item);
                        viewShare.setColorFilter(Color.parseColor("#535759"));
                        viewDelete.setColorFilter(Color.parseColor("#535759"));
                    }else {
                        setCountItem(listItemSelected);
//                        viewCountItemSelected.setText(listItemSelected.size()+" "+getString(R.string.selected));
                    }
                }
            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Item item = listItems.get(position);
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
        });

//        viewPager2.


        clickBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        clickSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSelect.setVisibility(View.GONE);
                clickBack.setVisibility(View.GONE);
                clickCloseSelect.setVisibility(View.VISIBLE);
                clickSelectAll.setVisibility(View.VISIBLE);
//                menuPopup.setVisibility(View.GONE);
                folderName.setVisibility(View.GONE);
                mISendDataListener.openSelect();
            }
        });

        clickCloseSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
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
//                    viewCountItemSelected.setText(listItemSelected.size()+" "+getString(R.string.selected));
                }else if (listItemSelected.size()<listItems.size()){
                    itemAdapter.selectAll();
                    listItemSelected = itemAdapter.getSelected();
                    setCountItem(listItemSelected);
//                    viewCountItemSelected.setText(listItemSelected.size()+" "+getString(R.string.selected));
                }else {
                    itemAdapter.unSelectAll();
                    listItemSelected = itemAdapter.getSelected();
                    viewCountItemSelected.setText("Select item");
                    viewShare.setColorFilter(Color.parseColor("#535759"));
                    viewDelete.setColorFilter(Color.parseColor("#535759"));
                }
            }
        });

        clickBackSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listItemSelected.clear();
                layoutContainerAllItem.setVisibility(View.VISIBLE);
                layoutContainerItemSelected.setVisibility(View.GONE);
                mISendDataListener.closeSelect();
            }
        });

        clickEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Edit", Toast.LENGTH_SHORT).show();
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

    private void cancel() {
        clickSelect.setVisibility(View.VISIBLE);
        clickBack.setVisibility(View.VISIBLE);
        clickCloseSelect.setVisibility(View.GONE);
        clickSelectAll.setVisibility(View.GONE);
//        menuPopup.setVisibility(View.VISIBLE);
        folderName.setVisibility(View.VISIBLE);
        viewCountItemSelected.setText(R.string.select_item);
        viewShare.setColorFilter(Color.parseColor("#535759"));
        viewDelete.setColorFilter(Color.parseColor("#535759"));
        itemAdapter.unSelectAll();
        listItemSelected = itemAdapter.getSelected();
        mISendDataListener.closeSelect();
    }

    private void addControls(View view) {
        rcvItemInAlbums = view.findViewById(R.id.rcvItemInAlbum);
        folderName = view.findViewById(R.id.folderName);
        clickBack = view.findViewById(R.id.clickBack);
        clickSelect = view.findViewById(R.id.clickSelect);
        clickCloseSelect = view.findViewById(R.id.clickCloseSelect);
        clickSelectAll = view.findViewById(R.id.clickSelectAll);

        layoutContainerAllItem = view.findViewById(R.id.layoutContainerAllItem);
        layoutContainerItemSelected = view.findViewById(R.id.layoutContainerItemSelected);
        clickBackSelected = view.findViewById(R.id.clickBackSelected);
        dateAdded = view.findViewById(R.id.dateAdded);
        clickEdit = view.findViewById(R.id.clickEdit);
        menuPopup = view.findViewById(R.id.menuPopup);
        littleRcv = view.findViewById(R.id.littleRcv);
        txtTitle = view.findViewById(R.id.txtTitle);
        txtTime = view.findViewById(R.id.txtTime);

        database = new Database(getActivity());

        listFavorite = getListFavorite();

        Bundle bundle = this.getArguments();
        folderName1 = bundle.getString("NAME_FOLDER");
        type = bundle.getInt("TYPE");
        listReceive = (List<Butket>) bundle.getSerializable("LIST_BUTKET");
        folderName.setText(folderName1);
        if (type!=0){
            if (type==7){
                txtTitle.setText(R.string.date);
            }else {
                txtTitle.setText(R.string.action_search);
            }
        }

        if (type!=3){
            listItems = setRecyclerView(folderName1);
        }else {
            listItems = getListLocation();
        }
        listItemLittle = getListTittle();
        if (type==4){
            folderName.setText( getString(R.string.photo_and_video)+" ("+listItems.size()+")");
        }


        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(getContext(),4);
        gridLayoutManager1.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (itemAdapter.getItemViewType(position)==0){
                    return 1;
                }else {
                    return 4;
                }
            }
        });
        rcvItemInAlbums.setLayoutManager(gridLayoutManager1);
        itemAdapter = new ItemAdapter(getContext());
        itemAdapter.setData(listItems);
        rcvItemInAlbums.setAdapter(itemAdapter);
        rcvItemInAlbums.setHasFixedSize(true);
        rcvItemInAlbums.scrollToPosition(listItems.size());

        viewPager2 = view.findViewById(R.id.viewPager2);
        imageFragmentAdapterTest = new ImageFragmentAdapterTest(getActivity());
        imageFragmentAdapterTest.setData(listItems);
        viewPager2.setAdapter(imageFragmentAdapterTest);
        viewPager2.setOffscreenPageLimit(2);

        littleRcv.setLayoutManager(new ZoomCenterLinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false));
        littleRcv.setHasFixedSize(true);
        littleItemAdapter = new LittleItemAdapter(getActivity());
        littleItemAdapter.setData(listItemLittle);
        littleRcv.setAdapter(littleItemAdapter);
        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(littleRcv);
    }

    private List<Item> getListTittle() {
        List<Item> list = setRecyclerView(folderName1);
        for (int i=0; i<5; i++){
            list.add(0,new Item("",0,0,false,"",null));
        }
        for (int i=0; i<5; i++){
            list.add(new Item("",0,0,false,"",null));
        }
        return list;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.fragmentItemInAlbumView = view;

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

    private void clickShareBlue(View v) {
        Uri uri = FileProvider.getUriForFile(getActivity(), "lol1.contentprovider", new File(listItems.get(viewPager2.getCurrentItem()).getPathOfItem()));
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM,uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setType("image/*");
        shareIntent.setType("video/*");
        startActivity(Intent.createChooser(shareIntent, "Share"));
    }

    private void clickDeleteBlue(View v) {
        View view1 = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_delete,null);

        BottomSheetDialog bottomSheetDialog =new BottomSheetDialog(getActivity(),R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(view1);
        bottomSheetDialog.show();

        TextView deleteItem = view1.findViewById(R.id.deleteItem);
        TextView deleteItemtoBin = view1.findViewById(R.id.deleteItemToBin);
        TextView btnCancel = view1.findViewById(R.id.btnCancel);
        int position = viewPager2.getCurrentItem();

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
        SharedPreferences preferences = getContext().getSharedPreferences(MainActivity.getKeyShareReload(),MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putInt("RELOAD",1);
        editor.commit();
        database.queryData("delete from Favorite where Path='"+path+"'");
        database.queryData("delete from Album where path='" + path + "'");
        listFavorite = getListFavorite();
        listItems.remove(position);
        listItemLittle.remove(position+5);
        littleItemAdapter.setData(listItemLittle);
        itemAdapter.setData(listItems);
        imageFragmentAdapterTest.setData(listItems);
        viewPager2.setAdapter(imageFragmentAdapterTest);

        if (position>listItems.size()){
            viewPager2.setCurrentItem(position-1);
        }else {
            viewPager2.setCurrentItem(position);
        }
    }

    private void clickShare(View v) {
        if (listItemSelected.size()==0){
            Toast.makeText(getContext(), R.string.no_item_selected_1, Toast.LENGTH_SHORT).show();
            return;
        }
        shareItemsSelected();
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

    private void clickFavorite(View v) {
        viewFavoriteBlue1.setVisibility(View.VISIBLE);
        viewFavoriteBlue.setVisibility(View.GONE);
        Item item = listItems.get(viewPager2.getCurrentItem());
        String path = item.getPathOfItem();
        int dateAdded = (int) (item.getDateAdded()/1000);
        int duration = (int) item.getDurationVideo();
        String id = item.getId();
        database.queryData("insert into Favorite values(null,'"+path+"',"+dateAdded+","+duration+",'"+id+"')");
        Toast.makeText(getActivity(), R.string.favorite, Toast.LENGTH_SHORT).show();
        listFavorite.add(listItems.get(viewPager2.getCurrentItem()).getPathOfItem());
        SharedPreferences preferences = getContext().getSharedPreferences(MainActivity.getKeyShareReload(),MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putInt("RELOAD",1);
        editor.commit();
    }

    private void clickFavorite1(View v) {
        viewFavoriteBlue1.setVisibility(View.GONE);
        viewFavoriteBlue.setVisibility(View.VISIBLE);
        String path = listItems.get(viewPager2.getCurrentItem()).getPathOfItem();
        database.queryData("delete from Favorite where Path='"+path+"'");
        Toast.makeText(getActivity(), R.string.unfavorite, Toast.LENGTH_SHORT).show();
        listFavorite.remove(listItems.get(viewPager2.getCurrentItem()).getPathOfItem());
        SharedPreferences preferences = getContext().getSharedPreferences(MainActivity.getKeyShareReload(),MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putInt("RELOAD",1);
        editor.commit();
    }

    private List<Item> setRecyclerView(String folderName) {
        List<Item> listItem1 = new ArrayList<>();
        switch (type){
            case 0:
                getListAlbum(listItem1, folderName, getContext());
                break;
            case 1:
                getListAlbum(listItem1, folderName, getContext());
                break;
            case 2:
                getListYear(listItem1, folderName, getContext());
                break;
            case 4:
                getListSearch(listItem1,getContext());
                break;
            case 5:
                getListVideo(listItem1,folderName,getContext());
                break;
            case 6:
                getListPhoto(listItem1,folderName,getContext());
                break;
            case 7:
                getListItemOfDay(listItem1,folderName,getContext());
                break;
            default:
                break;
        }
        return listItem1;
    }

    private void getListItemOfDay(List<Item> listItem1, String folderName, Context context) {
        Uri uri1 = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection1 = {MediaStore.Images.Media.DATA,MediaStore.Images.ImageColumns.DATE_TAKEN,"_id"};

        Uri uri2 = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection2 = {MediaStore.Video.Media.DATA,MediaStore.Video.VideoColumns.DURATION,MediaStore.Video.VideoColumns.DATE_TAKEN,"_id"};

        Cursor cursor = context.getContentResolver().query(uri1, projection1,null,null,null);
        if (cursor!=null){
            while (cursor.moveToNext()){
                @SuppressLint("Range") long date = cursor.getLong(cursor.getColumnIndex(projection1[1]));
                String year = DateFormat.getDateInstance(DateFormat.LONG).format(date);
                if (year.equals(folderName)) {
                    @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(projection1[0]));
                    @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(projection1[2]));
                    listItem1.add(new Item(path,date,0,false,id,null));
                }
            }
            cursor.close();
        }

        Cursor cursor1 = context.getContentResolver().query(uri2,projection2,null,null,null);
        if (cursor1!=null){
            while (cursor1.moveToNext()){
                @SuppressLint("Range") long duration = cursor1.getLong(cursor1.getColumnIndex(projection2[1]));
                @SuppressLint("Range") long date = cursor1.getLong(cursor1.getColumnIndex(projection2[2]));
                String year = DateFormat.getDateInstance(DateFormat.LONG).format(date);
                if (year.equals(folderName)) {
                    @SuppressLint("Range") String path = cursor1.getString(cursor1.getColumnIndex(projection2[0]));
                    @SuppressLint("Range") String id = cursor1.getString(cursor1.getColumnIndex(projection2[3]));
                    listItem1.add(new Item(path,date,duration,false,id,null));
                }
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
    }

    private void getListPhoto(List<Item> listItem1, String folderName, Context context) {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {"_data","datetaken","_id"};

        Cursor cursor = context.getContentResolver().query(uri,projection,null,null,"datetaken");
        if (cursor!=null){
            while (cursor.moveToNext()){
                @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(projection[0]));
                @SuppressLint("Range") long date = cursor.getLong(cursor.getColumnIndex(projection[1]));
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(projection[2]));
                listItem1.add(new Item(path,date,0,false,id,null));
            }
        }
    }

    private void getListVideo(List<Item> listItem1, String folderName, Context context) {
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {"_data","datetaken","_id","duration"};

        Cursor cursor = context.getContentResolver().query(uri,projection,null,null,"datetaken");
        if (cursor!=null){
            while (cursor.moveToNext()){
                @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(projection[0]));
                @SuppressLint("Range") long date = cursor.getLong(cursor.getColumnIndex(projection[1]));
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(projection[2]));
                @SuppressLint("Range") long duration = cursor.getLong(cursor.getColumnIndex(projection[3]));
                listItem1.add(new Item(path,date,duration,false,id,null));
            }
        }
    }

    private void getListSearch(List<Item> listItem1, Context context) {
        if (listReceive!=null){
            String arrayId = null;
            for (int i=0; i<listReceive.size(); i++){
                arrayId =listReceive.get(i).getId();
                Uri uri1 = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                String[] projection1 = {MediaStore.Images.Media.DATA,MediaStore.Images.ImageColumns.DATE_TAKEN,"_id"};

                Uri uri2 = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] projection2 = {MediaStore.Video.Media.DATA,MediaStore.Video.VideoColumns.DURATION,MediaStore.Video.VideoColumns.DATE_TAKEN,"_id"};

                Cursor cursor = context.getContentResolver().query(uri1, projection1,"_id"+"=?", new String[]{arrayId},null);
                if (cursor!=null){
                    while (cursor.moveToNext()){
                        @SuppressLint("Range") String fisrtImage = cursor.getString(cursor.getColumnIndex(projection1[0]));
                        File file = new File(fisrtImage);
                        if (file.exists()){
                            @SuppressLint("Range") long date = cursor.getLong(cursor.getColumnIndex(projection1[1]));
                            @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(projection1[2]));
                            listItem1.add(new Item(fisrtImage, date, 0, false, id,null));
                            Log.e("cc", fisrtImage);
                        }
                    }
                    cursor.close();
                }

                Cursor cursor1 = context.getContentResolver().query(uri2,projection2,"_id"+"=?",new String[]{arrayId},null);
                if (cursor1!=null){
                    while (cursor1.moveToNext()){
                        @SuppressLint("Range") String fisrtImage = cursor1.getString(cursor1.getColumnIndex(projection2[0]));
                        File file = new File(fisrtImage);
                        if (file.exists()){
                            @SuppressLint("Range") long date = cursor1.getLong(cursor1.getColumnIndex(projection2[2]));
                            @SuppressLint("Range") String id = cursor1.getString(cursor1.getColumnIndex(projection2[3]));
                            @SuppressLint("Range") long duration = cursor1.getLong(cursor1.getColumnIndex(projection2[1]));
                            listItem1.add(new Item(fisrtImage,date,duration,false,id,null));
                            Log.e("cc",fisrtImage);
                        }
                    }
                    cursor1.close();
                }
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
        }
    }

    public List<Item> getListLocation() {
//        Uri uri1 = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//        String[] projection1 = {MediaStore.Images.Media.DATA,MediaStore.Images.ImageColumns.DATE_TAKEN,"_id"};
//
//        Uri uri2 = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//        String[] projection2 = {MediaStore.Video.Media.DATA,MediaStore.Video.VideoColumns.DURATION,MediaStore.Video.VideoColumns.DATE_TAKEN,"_id"};
//
//        Cursor cursor = context.getContentResolver().query(uri1, projection1,null,null,null);
//        if (cursor!=null){
//            while (cursor.moveToNext()){
//                String fisrtImage = cursor.getString(cursor.getColumnIndex(projection1[0]));
//                File file = new File(fisrtImage);
//                if (file.exists()){
//                    try {
//                        ExifInterface exifInterface = new ExifInterface(file);
//                        if (exifInterface.getLatLong()!=null){
//                            String locationString = getCompleteAddressString(exifInterface.getLatLong()[0],exifInterface.getLatLong()[1]);
//                            if (locationString.equals(folderName)){
//                                long date = cursor.getLong(cursor.getColumnIndex(projection1[1]));
//                                String id = cursor.getString(cursor.getColumnIndex(projection1[2]));
//                                listItem1.add(new Item(fisrtImage,date,0,false,id,null));
//                            }
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//            cursor.close();
//
//        }
//
//        Cursor cursor1 = context.getContentResolver().query(uri2,projection2,null,null,null);
//        if (cursor1!=null){
//            while (cursor1.moveToNext()){
//                String fisrtImage = cursor1.getString(cursor1.getColumnIndex(projection2[0]));
//                File file = new File(fisrtImage);
//                if (file.exists()){
//                    try {
//                        ExifInterface exifInterface = new ExifInterface(file);
//                        if (exifInterface.getLatLong()!=null){
//                            String locationString = getCompleteAddressString(exifInterface.getLatLong()[0],exifInterface.getLatLong()[1]);
//                            if (locationString.equals(folderName)){
//                                long date = cursor1.getLong(cursor.getColumnIndex(projection2[2]));
//                                String id = cursor1.getString(cursor.getColumnIndex(projection2[3]));
//                                long duration = cursor1.getLong(cursor1.getColumnIndex(projection2[1]));
//                                listItem1.add(new Item(fisrtImage,date,duration,false,id,null));
//                            }
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//            cursor1.close();
//        }
//
//        if (listItem1.isEmpty()){
//            Log.d("TAG", "savedInstanceState is null");
//        }
        Bundle bundle = this.getArguments();
        List<Item> list1 = (List<Item>) bundle.getSerializable("LIST_LOCATION1");

        Collections.sort(list1, new Comparator<Item>() {
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
        return list1;
    }

    public static void getListYear(List<Item> listItem1, String folderName, Context context) {
        Uri uri1 = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection1 = {MediaStore.Images.Media.DATA,MediaStore.Images.ImageColumns.DATE_TAKEN,"_id"};

        Uri uri2 = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection2 = {MediaStore.Video.Media.DATA,MediaStore.Video.VideoColumns.DURATION,MediaStore.Video.VideoColumns.DATE_TAKEN,"_id"};

        Cursor cursor = context.getContentResolver().query(uri1, projection1,null,null,null);
        if (cursor!=null){
            while (cursor.moveToNext()){
                @SuppressLint("Range") long date = cursor.getLong(cursor.getColumnIndex(projection1[1]));
                String year = String.valueOf(new SimpleDateFormat("MM/yyyy").format(date));
                if (year.equals(folderName)) {
                    @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(projection1[0]));
                    @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(projection1[2]));
                    listItem1.add(new Item(path,date,0,false,id,null));
                }
            }
            cursor.close();
        }

        Cursor cursor1 = context.getContentResolver().query(uri2,projection2,null,null,null);
        if (cursor1!=null){
            while (cursor1.moveToNext()){
                @SuppressLint("Range") long duration = cursor1.getLong(cursor1.getColumnIndex(projection2[1]));
                @SuppressLint("Range") long date = cursor1.getLong(cursor1.getColumnIndex(projection2[2]));
                String year = String.valueOf(new SimpleDateFormat("MM/yyyy").format(date));
                if (year.equals(folderName)) {
                    @SuppressLint("Range") String path = cursor1.getString(cursor1.getColumnIndex(projection2[0]));
                    @SuppressLint("Range") String id = cursor1.getString(cursor1.getColumnIndex(projection2[3]));
                    listItem1.add(new Item(path,date,duration,false,id,null));
                }
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
    }

    public static void getListAlbum(List<Item> listItem1, String folderName, Context context) {
        Uri uri1 = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection1 = {MediaStore.Images.Media.DATA,MediaStore.Images.ImageColumns.DATE_TAKEN,"_id"};

        Uri uri2 = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection2 = {MediaStore.Video.Media.DATA,MediaStore.Video.VideoColumns.DURATION,MediaStore.Video.VideoColumns.DATE_TAKEN,"_id"};
        Cursor cursor = context.getContentResolver().query(uri1, projection1,MediaStore.MediaColumns.BUCKET_DISPLAY_NAME+" = ?",new String[]{folderName},null);
        if (cursor!=null){
            while (cursor.moveToNext()){
                @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(projection1[0]));
                @SuppressLint("Range") long date = cursor.getLong(cursor.getColumnIndex(projection1[1]));
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(projection1[2]));
                listItem1.add(new Item(path,date,0,false,id,null));
            }
            cursor.close();
        }

        Cursor cursor1 = context.getContentResolver().query(uri2,projection2,MediaStore.MediaColumns.BUCKET_DISPLAY_NAME+" = ?",new String[]{folderName},null);
        if (cursor1!=null){
            while (cursor1.moveToNext()){
                @SuppressLint("Range") String path = cursor1.getString(cursor1.getColumnIndex(projection2[0]));
                @SuppressLint("Range") long date = cursor1.getLong(cursor1.getColumnIndex(projection2[2]));
                @SuppressLint("Range") long duration = cursor1.getLong(cursor1.getColumnIndex(projection2[1]));
                @SuppressLint("Range") String id = cursor1.getString(cursor1.getColumnIndex(projection2[3]));
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
    }

    public void receiveDataFromAlbumsFragment(String name){
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void resetRecyclerViewAlbum(String name, Context context){
        setRecyclerView(name);
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
        listFavorite = getListFavorite();
        if (type==false){
            Toast.makeText(getActivity(),R.string.delete_done,Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getActivity(),R.string.item_moved_to_trash,Toast.LENGTH_SHORT).show();
        }
        SharedPreferences preferences = getContext().getSharedPreferences(MainActivity.getKeyShareReload(),MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putInt("RELOAD",1);
        editor.commit();
        cancel();
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

    private void shareItemsSelected(){
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

    public boolean onBackInFragment(){
        if (layoutContainerItemSelected.getVisibility()==View.VISIBLE){
            listItemSelected.clear();
            layoutContainerAllItem.setVisibility(View.VISIBLE);
            layoutContainerItemSelected.setVisibility(View.GONE);
            mISendDataListener.closeSelect();
            return true;
        }else if (clickCloseSelect.getVisibility()==View.VISIBLE){
            cancel();
            return true;
        }else {
            return false;
        }
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

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
//                Log.w("My Current loction address", strReturnedAddress.toString());
            } else {
//                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
//            Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd.substring(strAdd.indexOf(",")+2);
    }

    private void showDeleteDialog(int style, Integer position){
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
        Item item = listItems.get(viewPager2.getCurrentItem());
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
            copyFile(path);
            dialog.dismiss();
            contentResolver.delete(collection,column+"=?",new String[]{id});
            reloadAfterDeleteBlue(path, position,true);
        }else {
//            copyFile(path);
            dialog.dismiss();
            Uri uri = Uri.withAppendedPath(collection, item.getId());
            AndroidXI androidXI = new AndroidXI(getActivity());
            androidXI.trash(activityResultLauncherBlue1,uri,true);
        }
    }

    private void deleteItemBlue(Dialog dialog, Integer position) {
        Item item = listItems.get(viewPager2.getCurrentItem());
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
            dialog.dismiss();
            reloadAfterDeleteBlue(path, position,false);
        }else {
            dialog.dismiss();
            Uri uri = Uri.withAppendedPath(collection, item.getId());
            AndroidXI androidXI = new AndroidXI(getActivity());
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
                    listItemLittle.remove(listItemSelected.get(i));
                    listItems.remove(listItemSelected.get(i));
                    database.queryData("delete from Favorite where Path='"+path+"'");
                    database.queryData("delete from Album where path='" + path + "'");
                }else {
//                    copyFile(path);
                    Uri uri = Uri.withAppendedPath(collection, listItemSelected.get(i).getId());
                    list.add(uri);
                }
            }

            dialog.dismiss();

            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
                AndroidXI androidXI = new AndroidXI(getContext());
                androidXI.trashList(activityResultLauncher1,list,true);
            }else {
                reloadAfterDelete(true);
            }
        }
    }

    private void deleteItem(Dialog dialog) {
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
                    listItemLittle.remove(listItemSelected.get(i));
                    listItems.remove(listItemSelected.get(i));
                    database.queryData("delete from Favorite where Path='"+path+"'");
                    database.queryData("delete from Album where path='" + path + "'");
                }else {
                    Uri uri = Uri.withAppendedPath(collection, listItemSelected.get(i).getId());
                    list.add(uri);
                }
            }

            dialog.dismiss();

            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
                AndroidXI androidXI = new AndroidXI(getContext());
                androidXI.deleteList(activityResultLauncher,list);
            }else {
                reloadAfterDelete(false);
            }
        }
    }

    private int getWindowWidth() {
        DisplayMetrics metrics=new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }
}