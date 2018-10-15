package com.conx2share.conx2share.ui.livestream.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;


public class DividerItemTypeDecoration extends RecyclerView.ItemDecoration {

    private Paint paint;
    private int offsetDp;
    private int dividerHeight;

    public DividerItemTypeDecoration(Context context) {
        this(context, Color.argb((int) (255 * 0.2), 0, 0, 0), 40f, 1f);
    }

    private DividerItemTypeDecoration(Context context, int color, float offset, float dividerHeight) {
        this.paint = new Paint();
        this.paint.setStyle(Paint.Style.FILL);
        this.paint.setColor(color);
        this.offsetDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, offset, context.getResources().getDisplayMetrics());
        this.dividerHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerHeight, context.getResources().getDisplayMetrics());
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (hasDividerOnBottom(view, parent, state)) {
            outRect.set(0, 0, 0, offsetDp);
        } else {
            outRect.setEmpty();
        }
    }

    private boolean hasDividerOnBottom(View view, RecyclerView parent, RecyclerView.State state) {
        int position = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewAdapterPosition();
        return position < state.getItemCount() - 1 && ((parent.getAdapter().getItemViewType(position) == StreamAsRecyclerAdapter.USER_TYPE
                && parent.getAdapter().getItemViewType(position + 1) == StreamAsRecyclerAdapter.GROUP_TYPE)
                || (parent.getAdapter().getItemViewType(position) == StreamAsRecyclerAdapter.GROUP_TYPE
                        && parent.getAdapter().getItemViewType(position + 1) == StreamAsRecyclerAdapter.BUSINESS_TYPE));
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int heightDiff = offsetDp / 2;

        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (hasDividerOnBottom(child, parent, state)) {
                int top = child.getBottom() + heightDiff;
                int bottom = top + dividerHeight;
                c.drawRect(child.getLeft(), top, child.getRight(), bottom, paint);
            }
        }
    }
}
