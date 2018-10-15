package com.conx2share.conx2share.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.ui.base.BaseAppCompatActivity;
import com.conx2share.conx2share.ui.dialog.ShareDialogFragment;
import com.conx2share.conx2share.ui.sayno.SayNoFlowInteractor;

import javax.inject.Inject;

public class ProfileActivity extends BaseAppCompatActivity {
    private static final String PROFILE_ID_KEY = "profileId";

    @Inject
    SayNoFlowInteractor sayNoFlowInteractor;

    public static final String EXTRA_USER_STREAM = "extra_user_stream";

    public static void start(Context context, int friendId) {
        context.startActivity(new Intent(context, ProfileActivity.class)
                .putExtra(PROFILE_ID_KEY, String.valueOf(friendId)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter_from_bottom, R.anim.exit_to_top);
        if (savedInstanceState == null) {
            Fragment fragment = ProfileFragment.newInstance();
            String profileId = getIntent().getStringExtra(PROFILE_ID_KEY);
            Bundle bundle = new Bundle();
            bundle.putString(ProfileFragment.PROFILEID_KEY, profileId);
            fragment.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(android.R.id.content, fragment);
            ft.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        menu.findItem(R.id.say_no_item)
                .getActionView()
                .setOnClickListener(v -> sayNoFlowInteractor.startSayNo(ProfileActivity.this));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ShareDialogFragment dialogFragment = (ShareDialogFragment) getSupportFragmentManager().findFragmentByTag(ShareDialogFragment.TAG);
        if (dialogFragment != null) {
            dialogFragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}