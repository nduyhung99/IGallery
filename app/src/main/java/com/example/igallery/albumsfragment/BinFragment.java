package com.example.igallery.albumsfragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
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
import android.media.MediaMetadataRetriever;
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
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
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
import com.example.igallery.IntroduceActivity;
import com.example.igallery.MainActivity;
import com.example.igallery.R;
import com.example.igallery.ZoomCenterLinearLayoutManager;
import com.example.igallery.adapter.AndroidXI;
import com.example.igallery.adapter.BinAdapter;
import com.example.igallery.adapter.ImageBinFragmentAdapter;
import com.example.igallery.adapter.ImageBinFragmentAdapterTest;
import com.example.igallery.adapter.ImageFragmentAdapter;
import com.example.igallery.adapter.ItemAdapter;
import com.example.igallery.adapter.LittleItemAdapter;
import com.example.igallery.adapter.LittleItemBinAdapter;
import com.example.igallery.model.Butket;
import com.example.igallery.model.Item;
import com.example.igallery.model.ItemBin;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BinFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BinFragment extends Fragment {
    public static final String nameFragment = BinFragment.class.getName();
    private LinearLayout clickBack;
    private TextView txtTitleBin, clickSelect, txtDeleteAll, txtRecoverAll, clickCloseSelect, txtDelete, txtRecover, dateAdded, txtDescription, clickSelectAll, txtTime;
    private RecyclerView rcvBin, littleRcv;
    private RelativeLayout layoutAll,layoutSelectBin, layoutViewItemBin, toolbarRecently;
    private IControlFromBin iControlFromBin;
    private BinAdapter binAdapter;
    private List<ItemBin> listItemBin = new ArrayList<>();
    private List<ItemBin> listItemBinSelected = new ArrayList<>();
    private LinearLayout clickBackSelected;
    private ViewPager2 viewPagerItemBin;
    private ImageBinFragmentAdapterTest imageBinFragmentAdapter;

    private LittleItemBinAdapter littleItemBinAdapter;
    private List<ItemBin> listItemLittle = new ArrayList<>();
    int currentPosition=0,lol=0,lol2=0;

    private ItemInAlbumsFragment.ISendDataListener mISendDataListener;

    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher, activityResultLauncher1, activityResultLauncherDelete, activityResultLauncherDelete1;

    public interface IControlFromBin{
        void closeSelect();
        void openSelect();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        iControlFromBin = (IControlFromBin) getActivity();
        mISendDataListener = (ItemInAlbumsFragment.ISendDataListener) getActivity();
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BinFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BinFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BinFragment newInstance(String param1, String param2) {
        BinFragment fragment = new BinFragment();
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
        View view = inflater.inflate(R.layout.fragment_bin, container, false);
        addControls(view);
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.R){
            deleteItemInBin();
        }
        // Inflate the layout for this fragment

        txtDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                return;
            }
        });

        clickSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listItemBinSelected.isEmpty()){
                    binAdapter.selectAll();
                    listItemBinSelected = binAdapter.getSelected();
                    setCountItemBin(listItemBinSelected);
//                    txtTitleBin.setText(listItemBinSelected.size()+" "+getString(R.string.selected));
                }else if (listItemBinSelected.size()<listItemBin.size()){
                    binAdapter.selectAll();
                    listItemBinSelected = binAdapter.getSelected();
                    setCountItemBin(listItemBinSelected);
//                    txtTitleBin.setText(listItemBinSelected.size()+" "+getString(R.string.selected));
                }else {
                    binAdapter.unSelectAll();
                    listItemBinSelected = binAdapter.getSelected();
                    txtTitleBin.setText(R.string.no_item_selected);
                }
            }
        });

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                int resultCode = result.getResultCode();
                if (resultCode== Activity.RESULT_OK){
                    resetAfterRecoverAll();
                }
            }
        });

        activityResultLauncher1 = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                int resultCode = result.getResultCode();
                if (resultCode==Activity.RESULT_OK){
                    int position = viewPagerItemBin.getCurrentItem();
                    reloadAfterRecover(position);
                }
            }
        });

        activityResultLauncherDelete = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                int resultCode = result.getResultCode();
                if (resultCode==Activity.RESULT_OK){
                    reloadAfterDeleteAll();
                }
            }
        });

        activityResultLauncherDelete1 = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                int resultCode = result.getResultCode();
                if (resultCode==Activity.RESULT_OK){
                    int position = viewPagerItemBin.getCurrentItem();
                    reloadAfterDelete(position);
                }
            }
        });

        viewPagerItemBin.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                long dateAdded1 = listItemBin.get(position).getDate();
                String date1 = String.valueOf(DateFormat.getDateInstance(DateFormat.LONG).format(dateAdded1));
                String time = String.valueOf(new SimpleDateFormat("hh:mm aa").format(dateAdded1));
                dateAdded.setText(date1);
                txtTime.setText(time);

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

        littleItemBinAdapter.setOnItemLittleClick(new LittleItemBinAdapter.OnItemLittleClick() {
            @Override
            public void onClick(int position) {
                lol=1;
                viewPagerItemBin.setCurrentItem(position+5);
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
                        viewPagerItemBin.setCurrentItem(0,false);
                    }else if (position==1&&lol==0){
                        viewPagerItemBin.setCurrentItem(position,false);
                    }else {
                        lol2=1;
                        viewPagerItemBin.setCurrentItem(position+1,false);
                    }
                }
            }
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                LinearLayoutManager lol = (LinearLayoutManager) littleRcv.getLayoutManager();
//                if (dx!=0){
//                    int position = lol.findFirstVisibleItemPosition();
//                    viewPagerItemBin.setCurrentItem(position+1);
//                }
            }
        });

        clickBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (getFragmentManager()!=null){
//                    getFragmentManager().popBackStack();
//                }
                getActivity().onBackPressed();
            }
        });
        clickSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSelect.setVisibility(View.GONE);
                clickCloseSelect.setVisibility(View.VISIBLE);
                clickBack.setVisibility(View.INVISIBLE);
                clickSelectAll.setVisibility(View.VISIBLE);
                txtTitleBin.setText(R.string.no_item_selected);
                iControlFromBin.openSelect();
            }
        });

        clickCloseSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSelect.setVisibility(View.VISIBLE);
                clickCloseSelect.setVisibility(View.GONE);
                clickBack.setVisibility(View.VISIBLE);
                clickSelectAll.setVisibility(View.GONE);
                txtTitleBin.setText(R.string.recently_deleted);
                binAdapter.unSelectAll();
                iControlFromBin.closeSelect();
            }
        });

        binAdapter.setOnItemClickListener(new BinAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ItemBin itemBin = listItemBin.get(position);
                if (clickSelect.getVisibility()==View.VISIBLE){
                    listItemBin.get(position).setSelected(true);
                    toolbarRecently.setVisibility(View.GONE);
                    layoutSelectBin.setVisibility(View.GONE);
                    layoutViewItemBin.setVisibility(View.VISIBLE);
                    listItemBinSelected.add(listItemBin.get(position));
                    viewPagerItemBin.setCurrentItem(position,false);
                    iControlFromBin.openSelect();
                    if (position==0){
                        lol=1;
                    }
                    long dateAdded1 = listItemBin.get(position).getDate();
                    String date1 = String.valueOf(DateFormat.getDateInstance(DateFormat.LONG).format(dateAdded1));
                    String time = String.valueOf(new SimpleDateFormat("hh:mm aa").format(dateAdded1));
                    dateAdded.setText(date1);
                    txtTime.setText(time);
                }else {
                    if (listItemBin.get(position).isSelected() == false){
                        listItemBinSelected.add(itemBin);
                    }else {
                        for (int i = 0; i<listItemBinSelected.size(); i++){
                            if (listItemBinSelected.get(i).getPath().equalsIgnoreCase(itemBin.getPath())){
                                listItemBinSelected.remove(i);
                                break;
                            }
                        }
                    }
                    setCountItemBin(listItemBinSelected);
//                    txtTitleBin.setText(listItemBinSelected.size()+" "+getString(R.string.selected));
                }
            }
        });

        txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewPagerItemBin.getCurrentItem();
                showDeleteDialog(2,position);
            }
        });

        txtRecover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewPagerItemBin.getCurrentItem();
                showRecoverDialog(2,position);
            }
        });

        txtDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog(1,null);
            }
        });

        txtRecoverAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecoverDialog(1,null);
            }
        });

        clickBackSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbarRecently.setVisibility(View.VISIBLE);
                layoutSelectBin.setVisibility(View.VISIBLE);
                layoutViewItemBin.setVisibility(View.GONE);
                listItemBinSelected.clear();
                iControlFromBin.closeSelect();
            }
        });
        return view;
    }

    private void setCountItemBin(List<ItemBin> listItemBinSelected) {
        int countPhoto = 0;
        int countVideo = 0;
        String photo, video;
        for (ItemBin itemBin : listItemBinSelected) {
            String path = itemBin.getPath();
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
            txtTitleBin.setText(video);
        }else if (countVideo==0){
            txtTitleBin.setText(photo);
        }else {
            txtTitleBin.setText(photo+", "+video);
        }
    }

    private void reloadAfterDeleteAll() {
        SharedPreferences preferences = getContext().getSharedPreferences("ReloadData",MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putInt("RELOAD",1);
        Toast.makeText(getActivity(),R.string.delete_done,Toast.LENGTH_SHORT).show();
        clickSelect.setVisibility(View.VISIBLE);
        clickBack.setVisibility(View.VISIBLE);
        clickCloseSelect.setVisibility(View.GONE);
        clickSelectAll.setVisibility(View.GONE);
        txtTitleBin.setText(R.string.recently_deleted);
        binAdapter.unSelectAll();
        listItemBinSelected = binAdapter.getSelected();
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.R){
            binAdapter.setData(setRecylerView());
        }else {
            binAdapter.setData(getListFromTrash(getContext()));
        }

        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.R){
            listItemBin = setRecylerView();
        }else {
            listItemBin = getListFromTrash(getContext());
        }
        listItemLittle = getListTittle();
        littleItemBinAdapter.setData(getListTittle());
        imageBinFragmentAdapter.setData(listItemBin);
        iControlFromBin.closeSelect();
    }

    private void reloadAfterDelete(int position) {
        SharedPreferences preferences = getContext().getSharedPreferences("ReloadData",MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putInt("RELOAD",1);
        Toast.makeText(getActivity(),R.string.delete_done,Toast.LENGTH_SHORT).show();
        listItemBin.remove(position);
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.R){
            binAdapter.setData(setRecylerView());
        }else {
            binAdapter.setData(getListFromTrash(getContext()));
        }
        imageBinFragmentAdapter.setData(listItemBin);
        viewPagerItemBin.setAdapter(imageBinFragmentAdapter);
        if (position>listItemBin.size()){
            viewPagerItemBin.setCurrentItem(position-1,false);
        }else {
            viewPagerItemBin.setCurrentItem(position,false);
        }
    }

    private void reloadAfterRecover(int position) {
        Toast.makeText(getActivity(),R.string.recover_done,Toast.LENGTH_SHORT).show();
        SharedPreferences preferences = getContext().getSharedPreferences("ReloadData",MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putInt("RELOAD",1);
        listItemBin.remove(position);
        listItemLittle.remove(position+5);
        littleItemBinAdapter.setData(listItemLittle);
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.R){
            binAdapter.setData(setRecylerView());
        }else {
            binAdapter.setData(getListFromTrash(getContext()));
        }
        imageBinFragmentAdapter.setData(listItemBin);
        viewPagerItemBin.setAdapter(imageBinFragmentAdapter);
        if (position>listItemBin.size()){
            viewPagerItemBin.setCurrentItem(position-1,false);
        }else {
            viewPagerItemBin.setCurrentItem(position,false);
        }
        if (listItemBin.size()==0){
            toolbarRecently.setVisibility(View.VISIBLE);
            layoutSelectBin.setVisibility(View.VISIBLE);
            layoutViewItemBin.setVisibility(View.GONE);
            listItemBinSelected.clear();
            iControlFromBin.closeSelect();
        }

        mISendDataListener.reloadOrtherFragment(2);
    }

    private void resetAfterRecoverAll() {
        Toast.makeText(getActivity(),R.string.recover_done,Toast.LENGTH_SHORT).show();
        SharedPreferences preferences = getContext().getSharedPreferences("ReloadData",MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putInt("RELOAD",1);
        clickSelect.setVisibility(View.VISIBLE);
        clickBack.setVisibility(View.VISIBLE);
        clickSelectAll.setVisibility(View.GONE);
        clickCloseSelect.setVisibility(View.GONE);
        txtTitleBin.setText(R.string.recently_deleted);
        binAdapter.unSelectAll();
        listItemBinSelected = binAdapter.getSelected();
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.R){
            binAdapter.setData(setRecylerView());
        }else {
            binAdapter.setData(getListFromTrash(getContext()));
        }

        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.R){
            listItemBin = setRecylerView();
        }else {
            listItemBin = getListFromTrash(getContext());
        }
        listItemLittle = getListTittle();
        littleItemBinAdapter.setData(getListTittle());
        imageBinFragmentAdapter.setData(listItemBin);

        iControlFromBin.closeSelect();
        mISendDataListener.reloadOrtherFragment(2);
    }

    private void copyFile(String path) {
        String pathOfItem = path.substring(path.lastIndexOf("/")+1).replaceAll("%","/");
        File file = new File(pathOfItem);
        try {
            FileInputStream inputStream = new FileInputStream(path);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            MainActivity.copyStream(inputStream,fileOutputStream);
            fileOutputStream.close();
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
    }

    private void addControls(View view) {
        clickBack = view.findViewById(R.id.clickBack);
        txtDeleteAll = view.findViewById(R.id.txtDeleteAll);
        txtRecoverAll = view.findViewById(R.id.txtRecoverAll);
        txtTitleBin = view.findViewById(R.id.txtTitleBin);
        clickSelect = view.findViewById(R.id.clickSelect);
        rcvBin = view.findViewById(R.id.rcvBin);
        layoutAll = view.findViewById(R.id.layoutAll);
        clickCloseSelect = view.findViewById(R.id.clickCloseSelect);
        toolbarRecently = view.findViewById(R.id.toolbarRecently);
        dateAdded = view.findViewById(R.id.dateAdded);
        littleRcv = view.findViewById(R.id.littleRcv);
        clickSelectAll = view.findViewById(R.id.clickSelectAll);

        layoutSelectBin = view.findViewById(R.id.layoutSelectBin);
        txtDelete = view.findViewById(R.id.txtDelete);
        txtRecover = view.findViewById(R.id.txtRecover);
        layoutViewItemBin = view.findViewById(R.id.layoutViewItemBin);
        clickBackSelected = view.findViewById(R.id.clickBackSelected);
        viewPagerItemBin = view.findViewById(R.id.viewPagerItemBin);
        txtDescription = view.findViewById(R.id.description);
        txtTime = view.findViewById(R.id.txtTime);

        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.R){
            txtDescription.setText(R.string.fifteen);
        }else {
            txtDescription.setText(R.string.thirdty);
        }

        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.R){
            listItemBin = setRecylerView();
        }else {
            listItemBin = getListFromTrash(getContext());
        }
        listItemLittle = getListTittle();

        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(getContext(),4);
        rcvBin.setLayoutManager(gridLayoutManager1);
        binAdapter = new BinAdapter(getContext());
        binAdapter.setData(listItemBin);
        rcvBin.setAdapter(binAdapter);
        rcvBin.setHasFixedSize(true);

        imageBinFragmentAdapter= new ImageBinFragmentAdapterTest(getActivity());
        imageBinFragmentAdapter.setData(listItemBin);
        viewPagerItemBin.setAdapter(imageBinFragmentAdapter);
        viewPagerItemBin.setOffscreenPageLimit(2);

        littleRcv.setLayoutManager(new ZoomCenterLinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false));
        littleRcv.setHasFixedSize(true);
        littleItemBinAdapter = new LittleItemBinAdapter(getActivity());
        littleItemBinAdapter.setData(listItemLittle);
        littleRcv.setAdapter(littleItemBinAdapter);
        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(littleRcv);

    }

    private List<ItemBin> getListFromTrash(Context context) {
        List<ItemBin> list = new ArrayList<>();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.SIZE,
                MediaStore.MediaColumns.MIME_TYPE,
                MediaStore.MediaColumns.WIDTH,
                MediaStore.MediaColumns.HEIGHT,
                MediaStore.MediaColumns.DATE_MODIFIED,
                MediaStore.MediaColumns.IS_FAVORITE,
                MediaStore.MediaColumns.IS_TRASHED,
                MediaStore.MediaColumns.DATE_EXPIRES};

        Uri uri1 = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection1 = {MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.SIZE,
                MediaStore.MediaColumns.MIME_TYPE,
                MediaStore.MediaColumns.WIDTH,
                MediaStore.MediaColumns.HEIGHT,
                MediaStore.MediaColumns.DATE_MODIFIED,
                MediaStore.MediaColumns.IS_FAVORITE,
                MediaStore.MediaColumns.IS_TRASHED,
                MediaStore.MediaColumns.DATE_EXPIRES};

//        Cursor cursor = context.getContentResolver().query(uri,projection, MediaStore.MediaColumns.IS_TRASHED+"=?",new String[]{"1"},null);
//        if (cursor!=null){
//            while (cursor.moveToNext()){
//                String  path = cursor.getString(cursor.getColumnIndex(projection[1]));
//                long dateExpires = cursor.getLong(cursor.getColumnIndex(projection[10]));
//                list.add(new ItemBin(path,null,dateExpires,false));
//
//            }
//        }
//
//        Cursor cursor1 = context.getContentResolver().query(uri1,projection1,null,null,null);
//        if (cursor1!=null){
//            while (cursor1.moveToNext()){
//                String  path = cursor1.getString(cursor1.getColumnIndex(projection1[1]));
//                long dateExpires = cursor1.getLong(cursor1.getColumnIndex(projection1[10]));
//                list.add(new ItemBin(path,dateExpires,false));
//            }
//        }

        Bundle bundle = new Bundle();
        bundle.putInt("android:query-arg-match-trashed", 1);
        bundle.putString("android:query-arg-sql-selection = ?",
                "${MediaStore.MediaColumns.IS_TRASHED} = 1");
        bundle.putString("android:query-arg-sql-sort-order = ?",
                "${MediaStore.MediaColumns.DATE_MODIFIED} DESC");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Cursor cursor = context.getContentResolver().query(uri,projection,bundle,null);

            if (cursor!=null){
                while (cursor.moveToNext()){
                    @SuppressLint("Range") int lol = cursor.getInt(cursor.getColumnIndex(projection[9]));
                    if (lol==1){
                        @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(projection[1]));
                        @SuppressLint("Range") long dateExpires = cursor.getLong(cursor.getColumnIndex(projection[10]))*1000;
                        @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(projection[0]));
                        Uri uri2 = Uri.withAppendedPath(uri,id);
                        list.add(new ItemBin(path,uri2,dateExpires,false));
                    }
                }
            }

            Cursor cursor1 = context.getContentResolver().query(uri1,projection1,bundle,null);

            if (cursor1!=null){
                while (cursor1.moveToNext()){
                    @SuppressLint("Range") int lol = cursor1.getInt(cursor1.getColumnIndex(projection1[9]));
                    if (lol==1){
                        @SuppressLint("Range") String path = cursor1.getString(cursor1.getColumnIndex(projection1[1]));
                        @SuppressLint("Range") long dateExpires = cursor1.getLong(cursor1.getColumnIndex(projection1[10]))*1000;
                        @SuppressLint("Range") String id = cursor1.getString(cursor1.getColumnIndex(projection1[0]));
                        Uri uri2 = Uri.withAppendedPath(uri1,id);
                        list.add(new ItemBin(path,uri2,dateExpires,false));
                    }
                }
            }
        }

        return list;
    }

    private List<ItemBin> setRecylerView() {
        List<ItemBin> list1 = new ArrayList<>();
        File file = new File(MainActivity.getStore(getContext())+"/Bin");
        if (file.isDirectory()){
            File[] listFile = file.listFiles();
            for (int i=0; i<listFile.length; i++){
                String path = listFile[i].getAbsolutePath();
                long date = listFile[i].lastModified();
                list1.add(new ItemBin(path,null,date,false));
//                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//                retriever.setDataSource(getContext(), Uri.fromFile(file));
//                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//                long timeInMillisec = Long.parseLong(time );
//                retriever.release();
            }
        }
        return list1;
    }

    public void deleteItemInBin(){
        File file = new File(MainActivity.getStore(getActivity())+"/Bin");
        if (file.isDirectory()){
            File[] list = file.listFiles();
            if (list!=null){
                for (int i=0; i<list.length; i++){
                    if ((list[i].lastModified()+(86400000*15)-System.currentTimeMillis())<=0){
                        list[i].delete();
                    }
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        deleteItemInBin();
    }

    public boolean onBackInFragment(){
        if (layoutViewItemBin.getVisibility()==View.VISIBLE){
            toolbarRecently.setVisibility(View.VISIBLE);
            layoutSelectBin.setVisibility(View.VISIBLE);
            layoutViewItemBin.setVisibility(View.GONE);
            listItemBinSelected.clear();
            iControlFromBin.closeSelect();
            return true;
        }else {
            return false;
        }
    }

    private List<ItemBin> getListTittle() {
        List<ItemBin> list;
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.R){
            list = setRecylerView();
        }else {
            list = getListFromTrash(getContext());
        }
        for (int i=0; i<5; i++){
            list.add(0,new ItemBin("",null,0,false));
        }
        for (int i=0; i<5; i++){
            list.add(new ItemBin("",null,0,false));
        }
        return list;
    }

    private void showDeleteDialog(int style, Integer position){
        Dialog dialog=new Dialog(getContext());
        dialog.setContentView(R.layout.custom_delete_dialog);
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
                        deleteSelected(dialog);
                        break;
                    case 2:
                        deleteCurrent(dialog,position);
                        break;
                    default:
                        dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    private void deleteCurrent(Dialog dialog, Integer position) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
            File file = new File(listItemBin.get(viewPagerItemBin.getCurrentItem()).getPath());
            file.delete();
            dialog.dismiss();
            reloadAfterDelete(position);
        }else {
            dialog.dismiss();
            Uri uri = listItemBin.get(viewPagerItemBin.getCurrentItem()).getUri();
            AndroidXI androidXI = new AndroidXI(getContext());
            androidXI.delete(activityResultLauncherDelete1,uri);
        }
    }

    private void deleteSelected(Dialog dialog) {
        if (!listItemBinSelected.isEmpty()){
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
                for (int i = 0; i<listItemBinSelected.size(); i++){
                    File file = new File(listItemBinSelected.get(i).getPath());
                    file.delete();
                }
                dialog.dismiss();
                reloadAfterDeleteAll();
            }else {
                ArrayList<Uri> listUri = new ArrayList<>();
                for (int i=0; i<listItemBinSelected.size(); i++){
                    listUri.add(listItemBinSelected.get(i).getUri());
                }
                dialog.dismiss();
                AndroidXI androidXI = new AndroidXI(getContext());
                androidXI.deleteList(activityResultLauncherDelete,listUri);
            }
//                    Toast.makeText(getContext(), listItemBinSelected.get(0).getUri().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showRecoverDialog(int style, Integer position){
        Dialog dialog=new Dialog(getContext());
        dialog.setContentView(R.layout.custom_recover_dialog);
        dialog.getWindow().setLayout(getWindowWidth()-50, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.recoverItem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (style){
                    case 1:
                        recoverSelected(dialog);
                        break;
                    case 2:
                        recoverCurrent(dialog,position);
                        break;
                    default:
                        dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    private void recoverCurrent(Dialog dialog, Integer position) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
            String path = listItemBin.get(viewPagerItemBin.getCurrentItem()).getPath();
            copyFile(path);
            File file = new File(path);
            file.delete();
            dialog.dismiss();
            reloadAfterRecover(position);
        }else {
            Uri uri = listItemBin.get(viewPagerItemBin.getCurrentItem()).getUri();
            AndroidXI androidXI = new AndroidXI(getContext());
            dialog.dismiss();
            androidXI.trash(activityResultLauncher1,uri,false);
        }
    }

    private void recoverSelected(Dialog dialog) {
        if (!listItemBinSelected.isEmpty()){
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
                for (int i = 0; i<listItemBinSelected.size(); i++){
                    String path = listItemBinSelected.get(i).getPath();
                    copyFile(path);
                    File file = new File(listItemBinSelected.get(i).getPath());
                    file.delete();
                }
                dialog.dismiss();
                resetAfterRecoverAll();
            }else {
                ArrayList<Uri> listUri = new ArrayList<>();
                for (int i=0; i<listItemBinSelected.size();i++){
                    Uri uri = listItemBinSelected.get(i).getUri();
                    listUri.add(uri);
                }
                dialog.dismiss();
                AndroidXI androidXI = new AndroidXI(getContext());
                androidXI.trashList(activityResultLauncher,listUri,false);
            }
        }
    }

    private int getWindowWidth() {
        DisplayMetrics metrics=new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }
}