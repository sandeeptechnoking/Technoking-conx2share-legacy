/**
 *  Copyright © 2015 Wowza Media Systems, LLC. All rights reserved.
 *  modified by Michelle Cannon 2/2017
 */

package com.conx2share.conx2share.ui.wowza;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import com.conx2share.conx2share.R;

/**
 * A utility class for a multi-state toggle button
 */
public class MultiStateButton extends ImageView {

    private Drawable    mOnDrawable;
    private Drawable    mOffDrawable;
    private boolean     mIsOn;
    private int         mPressedColor;
    private int         mDisabledColor;

    public MultiStateButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        mDisabledColor = context.getResources().getColor(R.color.multiStateButtonDisabled);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.MultiStateButton,
                0, 0);

        try {
            boolean isOn = a.getBoolean(R.styleable.MultiStateButton_isOn, true);
            Drawable offDrawable = a.getDrawable(R.styleable.MultiStateButton_offSrc);
            int pressedColor = a.getColor(R.styleable.MultiStateButton_pressedColor, context.getResources().getColor(R.color.multiStateButtonPressed));

            init(isOn, offDrawable, pressedColor);

        } finally {
            a.recycle();
        }
    }

    public MultiStateButton(Context context) {
        super(context);
        init(mIsOn, null, mPressedColor);
    }

    private void init(boolean isOn, Drawable offDrawable, int pressedColor) {
        mOnDrawable = getDrawable();
        mOffDrawable = offDrawable == null ? mOnDrawable : offDrawable;
        mPressedColor = pressedColor;


        setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                ImageView imgView = (ImageView) v;
                if (!imgView.isClickable()) return false;

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    imgView.getDrawable().setColorFilter(mPressedColor, PorterDuff.Mode.SRC_IN);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    imgView.getDrawable().clearColorFilter();
                }

                return false;
            }
        });


        //setClickable(isEnabled());
        setState(isOn);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setClickable(enabled);

        if (enabled) {
            getDrawable().clearColorFilter();
        } else {
            getDrawable().setColorFilter(mDisabledColor, PorterDuff.Mode.SRC_IN);
        }

        invalidate();
        requestLayout();
    }

    public boolean isOn() {
        return mIsOn;
    }

    public void setState(boolean isOn) {
        mIsOn = isOn;

        setImageDrawable(mIsOn ? mOnDrawable : mOffDrawable);
        if (isEnabled()) {
            getDrawable().clearColorFilter();
        } else {
            getDrawable().setColorFilter(mDisabledColor, PorterDuff.Mode.SRC_IN);
        }

        invalidate();
        requestLayout();
    }

    public boolean toggleState() {
        setState(!mIsOn);
        return mIsOn;
    }
}
