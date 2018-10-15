package com.conx2share.conx2share.ui.notifications;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.KeyEvent;
import android.view.Menu;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.ui.base.BaseDrawerActivity;
import com.conx2share.conx2share.ui.feed.FeedActivity;
import com.conx2share.conx2share.ui.sayno.SayNoFlowInteractor;

import javax.inject.Inject;

public class NotificationsActivity extends BaseDrawerActivity {
    @Inject
    SayNoFlowInteractor sayNoFlowInteractor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.notifications);
    }

    @Override
    public Fragment initializeFragment() {
        return NotificationsFragment.newInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notification_menu, menu);
        menu.findItem(R.id.say_no_item)
                .getActionView()
                .setOnClickListener(v -> sayNoFlowInteractor.startSayNo(NotificationsActivity.this));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isTaskRoot()) {
            Intent upIntent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                upIntent = NavUtils.getParentActivityIntent(NotificationsActivity.this);
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
