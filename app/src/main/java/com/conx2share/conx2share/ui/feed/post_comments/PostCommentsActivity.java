package com.conx2share.conx2share.ui.feed.post_comments;

import com.conx2share.conx2share.model.Business;
import com.conx2share.conx2share.model.Group;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.ui.base.BaseActionBarActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuItem;

public class PostCommentsActivity extends BaseActionBarActivity {

    private static final String TAG = PostCommentsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Group group = null;
            Business business = null;

            // TODO - make this use parcelables
            if (getIntent().hasExtra(Group.EXTRA)) {
                Log.d(TAG, "has group extra");
                try {
                    group = NetworkClient.getGson().fromJson(getIntent().getStringExtra(Group.EXTRA), Group.class);
                } catch (Exception e) {
                    Log.d(TAG, "error obtaining group from extras");
                }
            } else if (getIntent().hasExtra(Business.EXTRA)) {
                Log.d(TAG, "has business extra");
                try {
                    business = NetworkClient.getGson().fromJson(getIntent().getStringExtra(Business.EXTRA), Business.class);
                } catch (Exception e) {
                    Log.d(TAG, "error obtaining business from extras");
                }
            }

            Fragment fragment = PostCommentsFragment.newInstance(group, business);
            String profileId = getIntent().getStringExtra(PostCommentsFragment.EXTRA_POST_ID);
            int postPosition = getIntent().getIntExtra(PostCommentsFragment.EXTRA_POST_POSITION, 0);
            Bundle bundle = new Bundle();
            bundle.putString(PostCommentsFragment.EXTRA_POST_ID, profileId);
            bundle.putInt(PostCommentsFragment.EXTRA_POST_POSITION, postPosition);
            fragment.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(android.R.id.content, fragment);
            ft.commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
