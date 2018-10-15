package com.conx2share.conx2share.ui.profile_settings;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import com.conx2share.conx2share.R;
import com.conx2share.conx2share.ui.base.BaseActionBarActivity;
import com.conx2share.conx2share.ui.sayno.SayNoFlowInteractor;
import com.conx2share.conx2share.util.MediaUploadUtil;

import javax.inject.Inject;

import butterknife.ButterKnife;
//import butterknife.InjectView;
import roboguice.inject.InjectView;

public class ProfileSettingsActivity extends BaseActionBarActivity {
    @InjectView(R.id.profile_toolbar)
    Toolbar toolbar;

    @Inject
    SayNoFlowInteractor sayNoFlowInteractor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        overridePendingTransition(R.anim.enter_from_bottom, R.anim.exit_to_top);
        if (savedInstanceState == null) {

            Fragment fragment = ProfileSettingsFragment.newInstance();
            int profileId = getIntent().getIntExtra(ProfileSettingsFragment.EXTRA_PROFILE_ID, -1);
            Bundle bundle = new Bundle();
            bundle.putInt(ProfileSettingsFragment.EXTRA_PROFILE_ID, profileId);
            fragment.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.container, fragment);
            ft.commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(MediaUploadUtil.EXTRA_PHOTO_URI, MediaUploadUtil.getPhotoUri());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Uri photoUri = savedInstanceState.getParcelable(MediaUploadUtil.EXTRA_PHOTO_URI);
        MediaUploadUtil.setPhotoUri(photoUri);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_setting_menu, menu);
        menu.findItem(R.id.say_no_item)
                .getActionView()
                .setOnClickListener(v -> sayNoFlowInteractor.startSayNo(ProfileSettingsActivity.this));

        return super.onCreateOptionsMenu(menu);
    }
}