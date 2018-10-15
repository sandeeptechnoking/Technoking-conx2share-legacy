package com.conx2share.conx2share.model;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;


public class NewsSourceGridItem extends ImageView {

    public NewsSourceGridItem(Context context) {
        super(context);
    }

    public NewsSourceGridItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NewsSourceGridItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec); // This is the key that will make the height equivalent to its width
    }
}
