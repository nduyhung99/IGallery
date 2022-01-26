package com.example.igallery.albumsfragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
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
import com.example.igallery.MainActivity;
import com.example.igallery.R;
import com.example.igallery.ZoomCenterLinearLayoutManager;
import com.example.igallery.adapter.AndroidXI;
import com.example.igallery.adapter.ImageFragmentAdapter;
import com.example.igallery.adapter.ImageFragmentAdapterTest;
import com.example.igallery.adapter.ItemAdapter;
import com.example.igallery.adapter.LittleItemAdapter;
import com.example.igallery.database.Database;
import com.example.igallery.model.Item;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoriteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoriteFragment extends Fragment {
    public static final String nameFragment = FavoriteFragment.class.getName();
    private RelativeLayout layoutContainerAllItem,layoutContainerItemSelected;
    private RecyclerView rcvItemFavorite, littleRcv;
    private TextView noItemFavorite, clickSelectAll, folderName, clickSelect, clickCloseSelect, dateAdded, clickEdit,viewCountItemSelected, txtTime;
    private LinearLayout clickBack, clickBackSelected;
    private ImageView menuPopup,viewDelete, viewShare;
    private NestedScrollView nesItemFavorite;
    private ImageView viewFavoriteBlue, viewDeleteBlue, viewShareBlue, viewFavoriteBlue1;
    private List<Item> listItem = new ArrayList<>();
    private List<Item> listFavorite = new ArrayList<>();
    private List<Item> listItemSelected = new ArrayList<>();

    private LittleItemAdapter littleItemAdapter;
    private List<Item> listItemLittle = new ArrayList<>();
    int currentPosition=0,lol=0, lol2=0;

    Database database;
    View fragmentFavorite;
    private ItemAdapter itemAdapter;
    private ItemInAlbumsFragment.ISendDataListener mISendDataListener;

    private ImageFragmentAdapterTest imageFragmentAdapterTest;
    private ViewPager2 viewPager2;

    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher,activityResultLauncher1, activityResultLauncherBlue, activityResultLauncherBlue1;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mISendDataListener = (ItemInAlbumsFragment.ISendDataListener) getActivity();
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavoriteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoriteFragment newInstance(String param1, String param2) {
        FavoriteFragment fragment = new FavoriteFragment();
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
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        // Inflate the layout for this fragment
        addControls(view);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                int ok = result.getResultCode();
                Log.e("aaaa",ok+"");
                if (ok==Activity.RESULT_OK){
                    for (int i=0; i<listItemSelected.size();i++){
                        String path = listItemSelected.get(i).getPathOfItem();
                        listItem.remove(listItemSelected.get(i));
                        listItemLittle.remove(listItemSelected.get(i));
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
                int ok = result.getResultCode();
                if (ok==Activity.RESULT_OK){
                    Item item = listItem.get(viewPager2.getCurrentItem());
                    String path = item.getPathOfItem();
                    int position = viewPager2.getCurrentItem();
                    reloadAfterDeleteBlue(path,position,false);
                }
            }
        });

        activityResultLauncherBlue1 = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                int ok = result.getResultCode();
                if (ok==Activity.RESULT_OK){
                    Item item = listItem.get(viewPager2.getCurrentItem());
                    String path = item.getPathOfItem();
                    copyFile(path);
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
            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
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
        });

        itemAdapter.setOnItemClickListener(new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (clickSelect.getVisibility()==View.VISIBLE){
                    Item item = listItem.get(position);
                    listItem.get(position).setItemSelected(true);
                    layoutContainerAllItem.setVisibility(View.GONE);
                    layoutContainerItemSelected.setVisibility(View.VISIBLE);
                    mISendDataListener.openSelect();
                    viewPager2.setCurrentItem(position,false);
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
                }else if (listItemSelected.size()<listItem.size()){
                    itemAdapter.selectAll();
                    listItemSelected = itemAdapter.getSelected();
                    setCountItem(listItemSelected);
//                    viewCountItemSelected.setText(listItemSelected.size()+" "+getString(R.string.selected));
                }else {
                    itemAdapter.unSelectAll();
                    listItemSelected = itemAdapter.getSelected();
                    viewCountItemSelected.setText(R.string.select_item);
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.fragmentFavorite = view;

        viewCountItemSelected = view.findViewById(R.id.txtCountItemSelected);
        viewDelete = view.findViewById(R.id.clickDelete);
        viewShare = view.findViewById(R.id.clickShare);

        viewFavoriteBlue = view.findViewById(R.id.clickFavoriteBlue);
        viewDeleteBlue = view.findViewById(R.id.clickDeleteBlue);
        viewShareBlue = view.findViewById(R.id.clickShareBlue);
        viewFavoriteBlue1 = view.findViewById(R.id.clickFavoriteBlue1);

//        viewCountItemSelected.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });

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
        Item item = listItem.get(viewPager2.getCurrentItem());
        String path = item.getPathOfItem();
        int dateAdded = (int) (item.getDateAdded()/1000);
        int duration = (int) item.getDurationVideo();
        database.queryData("insert into Favorite values(null,'"+path+"',"+dateAdded+","+duration+")");
        Toast.makeText(getActivity(), R.string.favorite, Toast.LENGTH_SHORT).show();
        listFavorite.remove(listItem.get(viewPager2.getCurrentItem()));
        SharedPreferences preferences = getContext().getSharedPreferences(MainActivity.getKeyShareReload(),MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putInt("RELOAD",1);
        editor.commit();
    }

    private void clickFavorite1(View v) {
        viewFavoriteBlue1.setVisibility(View.GONE);
        viewFavoriteBlue.setVisibility(View.VISIBLE);
        String path = listItem.get(viewPager2.getCurrentItem()).getPathOfItem();
        database.queryData("delete from Favorite where Path='"+path+"'");
        Toast.makeText(getActivity(), R.string.unfavorite, Toast.LENGTH_SHORT).show();
        listFavorite.add(listItem.get(viewPager2.getCurrentItem()));
        SharedPreferences preferences = getContext().getSharedPreferences(MainActivity.getKeyShareReload(),MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putInt("RELOAD",1);
        editor.commit();
    }

    private void clickShare(View v) {
        shareItemsSelected();
    }

    private void clickShareBlue(View v) {
        Uri uri = FileProvider.getUriForFile(getActivity(), "lol1.contentprovider", new File(listItem.get(viewPager2.getCurrentItem()).getPathOfItem()));
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
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                deleteItemsSelectedBlue(bottomSheetDialog,position);
            }
        });

        deleteItemtoBin.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                deleteToBinBlue(bottomSheetDialog,position);
            }
        });
    }

    private void addControls(View view) {
        layoutContainerAllItem = view.findViewById(R.id.layoutContainerAllItem);
        layoutContainerItemSelected = view.findViewById(R.id.layoutContainerItemSelected);
        rcvItemFavorite = view.findViewById(R.id.rcvItemFavorite);
        noItemFavorite = view.findViewById(R.id.noItemFavorite);
        clickSelectAll = view.findViewById(R.id.clickSelectAll);
        folderName = view.findViewById(R.id.folderName);
        clickSelect = view.findViewById(R.id.clickSelect);
        clickCloseSelect = view.findViewById(R.id.clickCloseSelect);
        clickBack = view.findViewById(R.id.clickBack);
        menuPopup = view.findViewById(R.id.menuPopup);

        dateAdded = view.findViewById(R.id.dateAdded);
        clickEdit = view.findViewById(R.id.clickEdit);
        clickBackSelected = view.findViewById(R.id.clickBackSelected);
        nesItemFavorite = view.findViewById(R.id.nesItemFavorite);
        littleRcv = view.findViewById(R.id.littleRcv);
        txtTime = view.findViewById(R.id.txtTime);

        database = new Database(getActivity());

        listItem=getListFavorite();
        listItemLittle = getListTittle();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),4);
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
        rcvItemFavorite.setLayoutManager(gridLayoutManager);
        itemAdapter = new ItemAdapter(getActivity());
        itemAdapter.setData(listItem);
        rcvItemFavorite.setAdapter(itemAdapter);
        rcvItemFavorite.setHasFixedSize(true);

        viewPager2 = view.findViewById(R.id.viewPager2);
        imageFragmentAdapterTest = new ImageFragmentAdapterTest(getActivity());
        imageFragmentAdapterTest.setData(listItem);
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
        List<Item> list = getListFavorite();
        for (int i=0; i<5; i++){
            list.add(0,new Item("",0,0,false,"",null));
        }
        for (int i=0; i<5; i++){
            list.add(new Item("",0,0,false,"",null));
        }
        return list;
    }

    public List<Item> getListFavorite() {
        Cursor data = database.getData("SELECT * FROM Favorite ORDER BY Id");
        List<Item> list = new ArrayList<>();
        if (data!=null){
            while (data.moveToNext()){
                String path = data.getString(1);
                int dateAdded = data.getInt(2);
                int duration = data.getInt(3);
                String idMedia = data.getString(4);
                list.add(new Item(path,dateAdded,duration,false,idMedia,null));
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
        if (listFavorite.size()>0){
            for (int i=0; i<listFavorite.size(); i++){
                if (listFavorite.get(i).getPathOfItem().equalsIgnoreCase(pathOfItem)){
                    return false;
                }
            }
        }
        return true;
    }

    private void shareItemsSelected(){
        ArrayList<Uri> list = new ArrayList<>();
        if (list.size()==0){
            Toast.makeText(getContext(), getString(R.string.no_item_selected), Toast.LENGTH_SHORT).show();
            return;
        }
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

    private void deleteItemsSelected(BottomSheetDialog bottomSheetDialog) {
        bottomSheetDialog.dismiss();
        showDeleteDialog(1,null);

    }

    private void reloadAfterDelete(boolean type) {
        itemAdapter.setData(listItem);
        imageFragmentAdapterTest.setData(listItem);
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

    private void deleteToBin(BottomSheetDialog bottomSheetDialog) {
        bottomSheetDialog.dismiss();
        showDeleteDialog(2,null);
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
        listItem.remove(position);
        listItemLittle.remove(position+5);
        littleItemAdapter.setData(listItemLittle);
        itemAdapter.setData(listItem);
        imageFragmentAdapterTest.setData(listItem);
        viewPager2.setAdapter(imageFragmentAdapterTest);
        if (position>listItem.size()){
            viewPager2.setCurrentItem(position-1);
        }else {
            viewPager2.setCurrentItem(position);
        }
    }

    public boolean onBackInFragment(){
        if (layoutContainerItemSelected.getVisibility()==View.VISIBLE){
            listItemSelected.clear();
            layoutContainerAllItem.setVisibility(View.VISIBLE);
            layoutContainerItemSelected.setVisibility(View.GONE);
            if (listFavorite.size()>0){
                removeFavorite();
                SharedPreferences preferences = getContext().getSharedPreferences(MainActivity.getKeyShareReload(),MODE_PRIVATE);
                SharedPreferences.Editor editor=preferences.edit();
                editor.putInt("RELOAD",1);
                editor.commit();
            }
            listFavorite.clear();
            mISendDataListener.closeSelect();
            return true;
        }else {
            return false;
        }
    }

    private void removeFavorite() {
        for (int i=0; i<listFavorite.size(); i++){
            listItem.remove(listFavorite.get(i));
        }
        itemAdapter.setData(listItem);
        imageFragmentAdapterTest.setData(listItem);
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
        Item item = listItem.get(viewPager2.getCurrentItem());
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
        Item item = listItem.get(viewPager2.getCurrentItem());
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
                    listItem.remove(listItemSelected.get(i));
                    listItemLittle.remove(listItemSelected.get(i));
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
                    listItem.remove(listItemSelected.get(i));
                    listItemLittle.remove(listItemSelected.get(i));
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

    private int getWindowWidth() {
        DisplayMetrics metrics=new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }
}