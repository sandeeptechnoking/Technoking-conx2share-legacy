package com.conx2share.conx2share.ui.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class MarginItemDecorator extends RecyclerView.ItemDecoration {

    private int margin;

    private boolean excludeFirst;

    public MarginItemDecorator(Context context,
                               @DimenRes int dimenId,
                               boolean excludeFirst) {
        this.margin = context.getResources().getDimensionPixelSize(dimenId);
        this.excludeFirst = excludeFirst;
    }

    public MarginItemDecorator(Context context, @DimenRes int margin) {
        this(context, margin, false);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        if (position == 0 && excludeFirst) {
            return;
        }

        outRect.top = margin;
    }
}