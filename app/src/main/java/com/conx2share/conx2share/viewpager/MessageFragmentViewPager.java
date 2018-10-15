package com.conx2share.conx2share.viewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class MessageFragmentViewPager extends ViewPager {

    public static String TAG = MessageFragmentViewPager.class.getSimpleName();

    public static boolean sEnabled;

    float mStartDragX;

    OnSwipeOutListener mListener;

    Context mContext;

    public MessageFragmentViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        sEnabled = false;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //view pager scrolling enable if true
        return sEnabled && super.onTouchEvent(event);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (sEnabled) {
            return super.onInterceptTouchEvent(ev);
        } else  // view pager disable scrolling
        {
            float x = ev.getX();
            Log.i(TAG, "EV: " + ev.toString());
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.i(TAG, "Action Down");
                    mStartDragX = x;

                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.i(TAG, "Left Scroll");
                    if (mStartDragX < x - 100)//100 value velocity
                    {

                        //Left scroll
                        return super.onInterceptTouchEvent(ev);
                    } else if (mStartDragX > x + 100) {
                        //Right scroll
//                        return super.onInterceptTouchEvent(ev);
                    }
                    break;
            }
        }

        return false;

    }


    public void setOnSwipeOutListener(OnSwipeOutListener listener) {
        mListener = listener;
    }

    public interface OnSwipeOutListener {

        void onSwipeOutAtStart();

        void onSwipeOutAtEnd();
    }
}
