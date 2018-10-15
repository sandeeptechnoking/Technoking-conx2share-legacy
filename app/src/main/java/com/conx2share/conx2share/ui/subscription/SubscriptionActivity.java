package com.conx2share.conx2share.ui.subscription;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.ui.base.BaseDrawerActivity;
import com.conx2share.conx2share.ui.feed.FeedActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.KeyEvent;

public class SubscriptionActivity extends BaseDrawerActivity {

    private SubscriptionFragment mSubscriptionFragment;

    @Override
    public Fragment initializeFragment() {
        mSubscriptionFragment = SubscriptionFragment.newInstance();
        return mSubscriptionFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.subscriptions, 22);
        displayRestore();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mSubscriptionFragment != null) {
            mSubscriptionFragment.onSubscriptionReturn(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isTaskRoot()) {
            Intent upIntent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                upIntent = NavUtils.getParentActivityIntent(SubscriptionActivity.this);
            } else {
                upIntent = new Intent(getApplicationContext(), FeedActivity.class);
            }
            startActivity(upIntent);
            finish();
        } else {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}
