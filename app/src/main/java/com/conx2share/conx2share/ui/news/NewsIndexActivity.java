package com.conx2share.conx2share.ui.news;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.ui.base.BaseDrawerActivity;
import com.conx2share.conx2share.ui.feed.FeedActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.KeyEvent;

public class NewsIndexActivity extends BaseDrawerActivity{

    @Override
    public Fragment initializeFragment() {
        return NewsIndexFragment.newInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleWithExpandedPlaceholder(R.string.news);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isTaskRoot()) {
            Intent upIntent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                upIntent = NavUtils.getParentActivityIntent(NewsIndexActivity.this);
            } else {
                upIntent = new Intent(getApplicationContext(), FeedActivity.class);
            }
            startActivity(upIntent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
