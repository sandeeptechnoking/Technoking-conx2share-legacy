/**
 *  ControlButton.java

 */
package com.conx2share.conx2share.ui.wowza;

import android.app.Activity;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import com.conx2share.conx2share.R.color;

/**
 * A utility class for a multi-state toggle button
 */
public class ControlButton {

    private ImageButton imageButton;
    private Drawable onIcon;
    private Drawable offIcon;

    private boolean stateOn;

    private final int pressedColor;

    public ControlButton(Activity activity, int resourceId, boolean enabled) {

        pressedColor = activity.getResources().getColor(color.controlButtonPressed);
        imageButton = (ImageButton) activity.findViewById(resourceId);
        imageButton.setClickable(enabled);

        imageButton.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (!ControlButton.this.imageButton.isClickable()) return false;

                ImageButton btn = (ImageButton) v;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.getDrawable().setColorFilter(ControlButton.this.pressedColor, Mode.SRC_IN);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.getDrawable().clearColorFilter();
                }

                return false;
            }
        });

        this.setEnabled(enabled);
    }

    public ControlButton(Activity activity, int resourceId, boolean enabled, boolean stateOn, int onIconId, int offIconId) {
        this(activity, resourceId, enabled);

        this.stateOn = stateOn;
        onIcon = activity.getResources().getDrawable(onIconId);
        offIcon = activity.getResources().getDrawable(offIconId);

        this.setStateOn(stateOn);
    }

    public boolean isEnabled() {
        return this.imageButton.isClickable();
    }

    public void setEnabled(boolean enabled) {
        this.imageButton.setClickable(enabled);
        this.imageButton.setImageAlpha(this.isEnabled() ? 255 : 125);
    }

    public boolean isVisible() {
        return this.imageButton.getVisibility() == View.VISIBLE;
    }

    public void setVisible(boolean visible) {
        this.imageButton.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    public boolean toggleState() {
        if (onIcon == null) return false;

        stateOn = !stateOn;
        this.setStateOn(stateOn);

        return stateOn;
    }

    public boolean isStateOn() {
        return this.stateOn;
    }

    public void setStateOn(boolean stateOn) {
        if (onIcon == null) return;

        this.stateOn = stateOn;
        this.imageButton.setImageDrawable(this.stateOn ? onIcon : offIcon);
    }
}
