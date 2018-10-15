package com.conx2share.conx2share.ui.sayno.intro;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.model.InvitationState;
import com.conx2share.conx2share.ui.base.BaseAppCompatActivity;
import com.conx2share.conx2share.ui.sayno.SayNoChatActivity;
import com.conx2share.conx2share.ui.sayno.SayNoFlowInteractor;
import com.conx2share.conx2share.ui.sayno.SayNoSignInActivity;
import com.conx2share.conx2share.ui.sayno.dialog.SayNoConfirmationDialogFragment;
import com.conx2share.conx2share.ui.sayno.dialog.SayNoNotificationDialogFragment;
import com.conx2share.conx2share.util.PreferencesUtil;
import com.rd.PageIndicatorView;

import javax.inject.Inject;

import butterknife.ButterKnife;
//import butterknife.InjectView;
import butterknife.OnClick;
import roboguice.inject.InjectView;

public class SayNoIntroActivity extends BaseAppCompatActivity {

    private static final String TAG = SayNoFlowInteractor.class.getName();

    private static final int FINAL_PAGE_POSITION = 2;

    @InjectView(R.id.intro_view_pager)
    ViewPager introPager;

    @InjectView(R.id.circleIndicator)
    PageIndicatorView indicator;

    @InjectView(R.id.say_no_into_skip_btn)
    Button skipBtn;

    @InjectView(R.id.say_no_into_next_btn)
    Button confirmButton;

    @Inject
    SayNoFlowInteractor sayNoFlowInteractor;

    @Inject
    PreferencesUtil preferencesUtil;

    public static void start(Context context) {
        context.startActivity(new Intent(context, SayNoIntroActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_say_no_into);
        ButterKnife.bind(this);

        ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                final boolean isFinal = position == FINAL_PAGE_POSITION;
                preparePage(position, isFinal);

                confirmButton.setOnClickListener(v -> {
                    if (isFinal) {
                        goChat();
                    } else {
                        introPager.setCurrentItem(position + 1, true);
                    }
                });
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        };

        introPager.setAdapter(new PageSlidePagerAdapter(getSupportFragmentManager()));
        introPager.addOnPageChangeListener(pageChangeListener);
        pageChangeListener.onPageSelected(0); //fire viewpager listener manually
    }

    @OnClick(R.id.say_no_into_skip_btn)
    void onSkipClicked() {
        sayNoFlowInteractor.dontShowIntroAgain();
        goChat();
    }

    private void preparePage(final int position, boolean isFinal) {
        skipBtn.setVisibility(isFinal ? View.GONE : View.VISIBLE);
        confirmButton.setText(isFinal ? R.string.say_no_got_it : R.string.say_no_next);

        int primaryColor;
        int buttonDrawable;
        switch (position) {
            case 0:
                primaryColor = ContextCompat.getColor(this, R.color.say_no_intro_page_1_primary);
                buttonDrawable = R.drawable.button_into_1_rounded_shape;
                break;
            case 1:
                primaryColor = ContextCompat.getColor(this, R.color.say_no_intro_page_2_primary);
                buttonDrawable = R.drawable.button_into_2_rounded_shape;
                break;
            case 2:
                primaryColor = ContextCompat.getColor(this, R.color.say_no_intro_page_3_primary);
                buttonDrawable = R.drawable.button_into_3_rounded_shape;
                break;
            default:
                throw new IllegalStateException("Unknown intro page");
        }

        confirmButton.setBackgroundResource(buttonDrawable);
        indicator.setSelectedColor(primaryColor);
        setStatusBarColorIfPossible(primaryColor);
    }

    private void goChat() {
        InvitationState state = preferencesUtil.getSayNoInvitationState();

        if (state == null) {
            SayNoSignInActivity.start(this);
            finish();
            return;
        }

        switch (state) {
            case PENDING:
                SayNoNotificationDialogFragment
                        .newInstance(R.string.say_no_invitation_sent)
                        .show(getSupportFragmentManager(), TAG);
                break;
            case DECLINED:
                SayNoConfirmationDialogFragment
                        .newInstance(R.string.say_no_invitation_declined, R.string.say_no_invitation_send_another_request)
                        .setConfirmationDialogInteraction(new SayNoConfirmationDialogFragment.ConfirmationDialogInteraction() {
                            @Override
                            public void onPositiveButtonClicked() {
                                SayNoSignInActivity.start(SayNoIntroActivity.this);
                            }

                            @Override
                            public void onNegativeButtonClicked() {
                            }
                        })
                        .show(getSupportFragmentManager(), TAG);
                break;
            case ACCEPTED:
                SayNoChatActivity.start(this);
                break;
        }

        finish();
    }

    private class PageSlidePagerAdapter extends FragmentStatePagerAdapter {
        PageSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return SayNoIntroPageFragment.newInstance(R.drawable.intro_image_1,
                            R.string.say_no_intro_1_title, R.string.say_no_intro_1_text);
                case 1:
                    return SayNoIntroPageFragment.newInstance(R.drawable.intro_image_2,
                            R.string.say_no_intro_2_title, R.string.say_no_intro_2_text);
                case 2:
                    return SayNoIntroPageFragment.newInstance(R.drawable.intro_image_3,
                            R.string.say_no_intro_3_title, R.string.say_no_intro_3_text);
                default:
                    throw new IllegalStateException("Unknown intro page");
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    private void setStatusBarColorIfPossible(@ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }
}