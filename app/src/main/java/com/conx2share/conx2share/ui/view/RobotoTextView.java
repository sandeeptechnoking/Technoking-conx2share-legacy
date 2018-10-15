package com.conx2share.conx2share.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by zacharyhensley on 12/17/14.
 *
 * This is necessary as using sans-serif-light or thin requires newer APIs than what is the min for this project
 */
public class RobotoTextView extends android.support.v7.widget.AppCompatTextView {

    private Context mContext;


    public RobotoTextView(Context context) {
        super(context);

        mContext = context;
        setCustomTypeFace();
    }

    public RobotoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        setCustomTypeFace();
    }

    public RobotoTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;
        setCustomTypeFace();
    }

    private void setCustomTypeFace() {

        if (isInEditMode()) {

            return;
        }

        Typeface font = Typeface.createFromAsset(mContext.getResources().getAssets(), "Roboto-Light.ttf");
        setTypeface(font);
    }
}
