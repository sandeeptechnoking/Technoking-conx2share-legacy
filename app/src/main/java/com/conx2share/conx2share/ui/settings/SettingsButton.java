package com.conx2share.conx2share.ui.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.conx2share.conx2share.R;

public class SettingsButton extends LinearLayout {

    private ImageView iconImageView;
    private TextView titleTextView;

    public SettingsButton(Context context) {
        super(context);
        init(null, 0);
    }

    public SettingsButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public SettingsButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        LayoutInflater.from(getContext()).inflate(R.layout.button_settings, this);
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.SettingsButton, defStyle, 0);

        String title = a.getString(R.styleable.SettingsButton_sb_title);

        Drawable icon = null;
        if (a.hasValue(R.styleable.SettingsButton_sb_icon)) {
            icon = a.getDrawable(R.styleable.SettingsButton_sb_icon);
        }

        a.recycle();

        iconImageView = (ImageView) findViewById(R.id.settings_button_icon);
        titleTextView = (TextView) findViewById(R.id.settings_button_textview);

        if (!isInEditMode()) {
            if (icon != null) {
                iconImageView.setImageDrawable(icon);
            }
            titleTextView.setText(title);
        }

    }
}
