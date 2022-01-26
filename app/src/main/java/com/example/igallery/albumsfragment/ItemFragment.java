package com.example.igallery.albumsfragment;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.Option;
import com.example.igallery.FileUltils;
import com.example.igallery.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemFragment extends Fragment {

    private ImageView imgShare, imgFavorite, imgDelete, btnPlayVideo, imgItemVideo;
    private PhotoView imgItem;
    private LinearLayout clickBack;
    private TextView clickEdit, dateAdded;

    private IListener mIListener;

    public interface IListener{
        void resetData(String name);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
//        if (context instanceof IListener){
//            mIListener = (IListener) getActivity();
//        }else {
//            throw new RuntimeException(context.toString()+" must implement IListener");
//        }
        mIListener = (IListener) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mIListener=null;
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private int mParam2;

    public ItemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ItemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ItemFragment newInstance(String param1, int param2) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getInt(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);
        // Inflate the layout for this fragment
        addControls(view);

        if (mParam1.endsWith(".mp4") || mParam1.endsWith(".avi") || mParam1.endsWith(".mov") || mParam1.endsWith(".flv") || mParam1.endsWith(".wmv")){
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(mParam1,   MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
            Matrix matrix = new Matrix();
            Bitmap bmThumbnail = Bitmap.createBitmap(thumb, 0, 0,
                    thumb.getWidth(), thumb.getHeight(), matrix, true);
            imgItemVideo.setImageBitmap(bmThumbnail);
            btnPlayVideo.setVisibility(View.VISIBLE);
            imgItemVideo.setVisibility(View.VISIBLE);
            imgItem.setVisibility(View.GONE);
            clickEdit.setVisibility(View.INVISIBLE);
        }else {
            Bitmap bitmap = BitmapFactory.decodeFile(mParam1);
            imgItem.setImageBitmap(FileUltils.modifyOrientation(getContext(),bitmap,mParam1));
        }

        clickBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
//                        mIListener.resetData(mParam1);
                        delete(bottomSheetDialog);
                    }
                });
            }
        });

        imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareOnePhoto();
            }
        });

        btnPlayVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri path = FileProvider.getUriForFile(getContext(), "lol1.contentprovider", new File(mParam1));
//            intent.setData(path);
//            startActivity(intent);
                Intent intent = ShareCompat.IntentBuilder.from(getActivity())
                        .setStream(path) // uri from FileProvider
                        .setType("text/html")
                        .getIntent()
                        .setAction(Intent.ACTION_VIEW) //Change if needed
                        .setDataAndType(path, "video/*")
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(intent);
            }
        });
        return view;
    }

    private void delete(BottomSheetDialog bottomSheetDialog) {
        ContentResolver contentResolver = getActivity().getContentResolver();
//        File file = new File(mParam1.substring(0,mParam1.lastIndexOf("/")));
        int result = contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,MediaStore.Images.Media.DATA + "= ?",new String[]{mParam1});
        if (result>0){
            bottomSheetDialog.dismiss();
            Toast.makeText(getActivity(),"Delete successfully",Toast.LENGTH_SHORT).show();
//            mIListener.resetData(file.getName());
            getFragmentManager().beginTransaction().remove(ItemFragment.this).commit();
            getActivity().onBackPressed();
        }else {
            bottomSheetDialog.dismiss();
            Toast.makeText(getActivity(),"Delete unsuccessfully",Toast.LENGTH_SHORT).show();
        }
    }

    private void addControls(View view) {
        imgShare = view.findViewById(R.id.imgShare);
        imgDelete = view.findViewById(R.id.imgDelete);
        imgFavorite = view.findViewById(R.id.imgFavorite);
        imgItem = view.findViewById(R.id.imgItem);
        clickBack = view.findViewById(R.id.clickBack);
        clickEdit = view.findViewById(R.id.clickEdit);
        dateAdded = view.findViewById(R.id.dateAdded);
        imgItemVideo = view.findViewById(R.id.imgItemVideo);
        btnPlayVideo = view.findViewById(R.id.btnPlayVideo);
    }

    private void shareOnePhoto() {
        Uri path = FileProvider.getUriForFile(getActivity(), "lol1.contentprovider", new File(mParam1));

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, path);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (mParam1.endsWith(".mp4") || mParam1.endsWith(".avi") || mParam1.endsWith(".mov") || mParam1.endsWith(".flv") || mParam1.endsWith(".wmv")){
            shareIntent.setType("video/*");
        }else {
            shareIntent.setType("image/*");
        }
        startActivity(Intent.createChooser(shareIntent, "Share"));
    }
}