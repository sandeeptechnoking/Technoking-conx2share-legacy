package com.conx2share.conx2share.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.async.UnregisterDeviceAsync;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.ui.base.BaseActivity;
import com.conx2share.conx2share.ui.groups.AddGroupActivity;
import com.conx2share.conx2share.ui.profile_settings.ProfileSettingsActivity;
import com.conx2share.conx2share.ui.profile_settings.ProfileSettingsFragment;
import com.conx2share.conx2share.util.EmergencyUtil;
import com.conx2share.conx2share.util.PreferencesUtil;
import com.conx2share.conx2share.util.RoboActionBarActivity;
import com.google.inject.Key;

import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
//import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.activity.RoboActivity;
//import roboguice.inject.InjectView;

public class SettingsActivity extends RoboActionBarActivity {

    @Inject
    PreferencesUtil mPreferencesUtil;

    @Inject
    NetworkClient networkClient;

    @BindView(R.id.settings_toolbar)
    public Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);


        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @OnClick(R.id.setting_edit_profile_button)
    public void editProfileClicked() {
        Intent intent = new Intent(this, ProfileSettingsActivity.class);
        intent.putExtra(ProfileSettingsFragment.EXTRA_PROFILE_ID, getIntent().getIntExtra(ProfileSettingsFragment.EXTRA_PROFILE_ID, 0));
        startActivity(intent);
    }

    @OnClick(R.id.setting_create_group_button)
    public void createGroupClicked() {
        Intent intent = new Intent(this, AddGroupActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.setting_invite_friends_button)
    public void inviteFriendsClicked() {

    }

    @OnClick(R.id.setting_contact_support_button)
    public void contactSupportClicked() {
        Intent intent = new Intent(this, ContactSupportActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.setting_logout_button)
    public void logoutClicked() {
        startUnsubscribe(mPreferencesUtil.getRegisteredDeviceId());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void startUnsubscribe(int deviceId) {
        new UnregisterDeviceAsync(this) {
            @Override
            protected void onSuccess(Result<Response> result) {
                EmergencyUtil.logout(SettingsActivity.this, mPreferencesUtil);
            }

            @Override
            protected void onFailure(RetrofitError error) {
                EmergencyUtil.logout(SettingsActivity.this, mPreferencesUtil);
            }
        }.executeInParallel(deviceId);
    }

}
