package com.conx2share.conx2share.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class AvenirNextTextView extends android.support.v7.widget.AppCompatTextView {

    public AvenirNextTextView(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
        //init();
    }

    public AvenirNextTextView(Context context, AttributeSet attrs) {

        super(context, attrs);
        //init();
    }


    public AvenirNextTextView(Context context) {

        super(context);
//        init();
    }

//    private void init() {
//
//        if (!isInEditMode()) {
//            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), getTypefacePath());
//            setTypeface(tf);
//        }
//    }

    protected String getTypefacePath() {
        return "fonts/Avenir-Next.ttc";
    }
}