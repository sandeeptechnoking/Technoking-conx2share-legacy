package com.conx2share.conx2share.util;

import com.conx2share.conx2share.R;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class AboutFrameUtil {

    public static final String TAG = AboutFrameUtil.class.getSimpleName();

    private static final int ANIMATION_DURATION = 300;

    private final int mPanelHeight;

    private View mSlideUpPanel;

    private View mProfileAboutLink;

    private ImageButton mCloseAboutButton;

    private ValueAnimator mAnimator;

    private TextView mAboutTextView;

    private Animator.AnimatorListener mUpAnimatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            mSlideUpPanel.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            // NO OP
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            // NO OP
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
            // NO OP
        }
    };

    private Animator.AnimatorListener mDownAnimatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            // NO OP
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mSlideUpPanel.setVisibility(View.GONE);
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            // NO OP
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
            // NO OP
        }
    };

    /**
     * Parent view should include the about frame layout and include a view with the id profile_about_link.
     * Best place for this object to be constructed is in the onViewCreated of a Fragment or the onCreate of
     * an activity that has the about panel in its layout.
     *
     * Code for your layout to include the about panel:
     * <include layout="@layout/partial_about_slideup_panel"/>
     */
    public AboutFrameUtil(Activity activity, View parent) {
        mCloseAboutButton = (ImageButton) parent.findViewById(R.id.close_about_screen);
        mSlideUpPanel = parent.findViewById(R.id.slide_up_about_panel);
        mProfileAboutLink = parent.findViewById(R.id.about_link);
        mAboutTextView = (TextView) parent.findViewById(R.id.about_text);

        mProfileAboutLink.setOnClickListener(v -> slidePanelUp());

        mCloseAboutButton.setOnClickListener(v -> slidePanelDown());

        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mPanelHeight = displayMetrics.heightPixels;
    }

    public final void slidePanelUp() {
        resetAnimator(true);
        startAnimator();
    }

    protected final void slidePanelDown() {
        resetAnimator(false);
        startAnimator();
    }

    private void resetAnimator(boolean up) {
        if (mAnimator != null) {
            mAnimator.removeAllUpdateListeners();
            mAnimator.removeAllListeners();
            mAnimator.cancel();
        }

        if (up) {
            mAnimator = ValueAnimator.ofFloat(mPanelHeight, 0);
            mAnimator.addListener(mUpAnimatorListener);
        } else {
            mAnimator = ValueAnimator.ofFloat(0, mPanelHeight);
            mAnimator.addListener(mDownAnimatorListener);
        }

        mAnimator.setDuration(ANIMATION_DURATION);
    }

    private void startAnimator() {
        mAnimator.addUpdateListener(animation -> mSlideUpPanel.setY((float) animation.getAnimatedValue()));

        mAnimator.start();
    }

    public final void setAboutText(CharSequence aboutText) {
        mAboutTextView.setText(aboutText);
    }

}