package com.conx2share.conx2share.ui.sayno.choose;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.conx2share.conx2share.R;
import com.conx2share.conx2share.ui.base.BaseAppCompatActivity;
import com.conx2share.conx2share.util.PreferencesUtil;
import com.makeramen.roundedimageview.RoundedImageView;

import javax.inject.Inject;

import butterknife.ButterKnife;
//import butterknife.InjectView;
import butterknife.OnClick;
import roboguice.inject.InjectView;

public class SayNoAnonymityChooseActivity extends BaseAppCompatActivity {

    private static final int REQUEST_CODE = 102;

    private static final String EXTRA_IS_ANONYMOUS = "is-anonymous";

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.say_no_anonymity_chooser_user_avatar)
    RoundedImageView userAvatarIv;

    @Inject
    PreferencesUtil preferencesUtil;

    public static void startForResult(Activity activity) {
        activity.startActivityForResult(new Intent(activity, SayNoAnonymityChooseActivity.class), REQUEST_CODE);
    }

    public static boolean canHandle(int requestCode) {
        return REQUEST_CODE == requestCode;
    }

    public static boolean isAnonymous(Intent data) {
        return data.getBooleanExtra(EXTRA_IS_ANONYMOUS, false);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_say_no_anonymity_mode_chooser);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Glide.with(this)
                .load(preferencesUtil.getAuthUser().getAvatar().getAvatar().getUrl())
                .dontAnimate()
                .centerCrop()
                .placeholder(R.drawable.friend_placeholder)
                .into(userAvatarIv);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return true;
    }

    @OnClick(R.id.say_no_anonymity_chooser_stay_anonymous)
    void onChooseStayAnonymousClicked() {
        selectAnonymityMode(true);
    }

    @OnClick(R.id.say_no_anonymity_chooser_non_anonymous)
    void onChooseNonAnonymousClicked() {
        selectAnonymityMode(false);
    }

    private void selectAnonymityMode(boolean isAnonymous) {
        setResult(RESULT_OK, new Intent()
                .putExtra(EXTRA_IS_ANONYMOUS, isAnonymous));
        finish();
    }
}