package com.example.igallery;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Space extends RecyclerView.ItemDecoration {
    int mSpace,mSpace1;

    public Space(int space, int space1){
        this.mSpace=space;
        this.mSpace1=space1;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.left = mSpace/* set your margin here */;
        }else if (parent.getChildAdapterPosition(view)==parent.getAdapter().getItemCount()-1){
            outRect.right = mSpace1;
        }
    }
}
