package com.example.igallery;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ZoomCenterLinearLayoutManager extends LinearLayoutManager {
    public ZoomCenterLinearLayoutManager(Context context) {
        super(context);
    }

    public ZoomCenterLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public ZoomCenterLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
        lp.width = getWidth() / 12; //Độ rộng của mỗi item sẽ bằng 1/3 độ rộng của RecyclerView
        return true;
    }

    @Override
    public void onLayoutCompleted(RecyclerView.State state) {
        super.onLayoutCompleted(state);
        scaleMiddle();
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        scaleMiddle();
        return super.scrollHorizontallyBy(dx, recycler, state);
    }

    public void scaleMiddle() {
        float midpoint = getWidth() / 11.f;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            float childMidpoint = (getDecoratedRight(child) + getDecoratedLeft(child)) / 11.f; // tính hoành độ của item
            float d = Math.abs(midpoint - childMidpoint); // khoảng cách giữa item và điểm trung tâm của recyclerView
            float scale = 1f - 0.35f * d / midpoint; //tính độ scale của item (0/35: Số này càng lớn thì các item 2 bên sẽ càng nhỏ
            child.setScaleX(scale);
            child.setScaleY(scale);
        }
    }
}
