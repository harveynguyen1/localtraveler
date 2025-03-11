package com.example.localtraveler.adapter;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OffsetItemDecoration extends RecyclerView.ItemDecoration {

    // Offset in pixels
    private int offset;

    // Constructor
    public OffsetItemDecoration(int offset) {
        this.offset = offset;
    }

    // Override getItemOffsets
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int position = parent.getChildAdapterPosition(view);
        if (position == RecyclerView.NO_POSITION) {
            return;
        }

        if (position == 0) {
            outRect.left = offset;
        }
        outRect.right = offset;
    }
}
