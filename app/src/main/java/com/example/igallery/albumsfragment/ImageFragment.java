package com.example.igallery.albumsfragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.igallery.FileUltils;
import com.example.igallery.R;
import com.example.igallery.model.Item;
import com.example.igallery.model.ItemBin;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ImageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ImageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ImageFragment newInstance(String param1, String param2) {
        ImageFragment fragment = new ImageFragment();
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
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        // Inflate the layout for this fragment
        PhotoView imgItem = view.findViewById(R.id.imgItem);
        ImageView imgItemVideo = view.findViewById(R.id.imgItemVideo);
        ImageView btnPlayVideo = view.findViewById(R.id.btnPlayVideo);

        Item item = (Item) getArguments().getSerializable("IMAGE");
        ItemBin itemBin = (ItemBin) getArguments().getSerializable("IMAGE_BIN");
        String path = "";
        if (item!=null){
            path = item.getPathOfItem();
        }else {
            path = itemBin.getPath();
        }
        if (path.endsWith(".mp4") || path.endsWith(".avi") || path.endsWith(".mov") || path.endsWith(".flv") || path.endsWith(".wmv")){
            if (path.contains("trash")){
                Glide.with(imgItemVideo.getContext()).load(itemBin.getUri())
                        .override(1000,1200)
                        .dontAnimate()
                        .into(imgItemVideo);
                btnPlayVideo.setVisibility(View.VISIBLE);
                imgItemVideo.setVisibility(View.VISIBLE);
                imgItem.setVisibility(View.GONE);
            }else {
//                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(path,   MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
//                Matrix matrix = new Matrix();
//                Bitmap bmThumbnail = Bitmap.createBitmap(thumb, 0, 0,
//                        thumb.getWidth(), thumb.getHeight(), matrix, true);
//                imgItemVideo.setImageBitmap(bmThumbnail);
                Uri uri = getUriFromId(getContext(),item.getId());
                Glide.with(imgItemVideo.getContext()).load(uri)
                        .override(1000,1200)
                        .dontAnimate()
                        .into(imgItemVideo);
                btnPlayVideo.setVisibility(View.VISIBLE);
                imgItemVideo.setVisibility(View.VISIBLE);
                imgItem.setVisibility(View.GONE);
            }
        }else {
//            Bitmap bitmap = BitmapFactory.decodeFile(path);
//            imgItem.setImageBitmap(FileUltils.modifyOrientation(getContext(),bitmap,path));
            if (item!=null){
                Glide.with(imgItem.getContext()).load(path)
                        .override(1000,1200)
                        .dontAnimate()
                        .into(imgItem);
            }else if (Build.VERSION.SDK_INT<Build.VERSION_CODES.R){
                    Glide.with(imgItem.getContext()).load(path)
                        .override(1000,1200)
                        .dontAnimate()
                        .into(imgItem);
                }else {
                    Glide.with(imgItem.getContext()).load(itemBin.getUri())
                        .override(1000,1200)
                        .dontAnimate()
                        .into(imgItem);
                }

        }


        btnPlayVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lol;
                if (item!=null){
                    lol=item.getPathOfItem();
                }else {
                    lol=itemBin.getPath();
                }
                Uri uriForFile = FileProvider.getUriForFile(getContext(), "lol1.contentprovider", new File(lol));
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

        return view;
    }

    private Uri getUriFromId(Context context,String id) {
        Uri uri = Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,id);
        return uri;
    }
}