package com.conx2share.conx2share.ui.contact;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.ui.base.BaseDrawerActivity;
import com.conx2share.conx2share.ui.feed.FeedActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.KeyEvent;
import android.view.Menu;

public class ContactActivity extends BaseDrawerActivity {

    @Override
    public Fragment initializeFragment() {
        return ContactFragment.newInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.invite);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_support, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isTaskRoot()) {
            Intent upIntent;
            upIntent = NavUtils.getParentActivityIntent(ContactActivity.this);
            startActivity(upIntent);
            finish();
        } else if (keyCode == KeyEvent.KEYCODE_BACK){
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
