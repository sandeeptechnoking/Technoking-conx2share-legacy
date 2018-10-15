package com.conx2share.conx2share.ui.groups;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.ui.base.BaseDrawerActivity;
import com.conx2share.conx2share.ui.feed.FeedActivity;
import com.conx2share.conx2share.util.LogUtil;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class GroupsIndexActivity extends BaseDrawerActivity {

    public static final String TAG = GroupsIndexActivity.class.getSimpleName();

    @Override
    public Fragment initializeFragment() {

        return GroupsIndexFragment.newInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout baseDrawerToolbarPlaceHolderLayout = (FrameLayout) findViewById(R.id.base_drawer_toolbar_placeholder);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, 0);
        baseDrawerToolbarPlaceHolderLayout.setLayoutParams(layoutParams);

        setTitle(R.string.groups);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getToolbar().getMenu().clear();
        getToolbar().inflateMenu(R.menu.group_menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_for_groups_button:
                launchSearchGroupsActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isTaskRoot()) {
            Intent upIntent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                upIntent = NavUtils.getParentActivityIntent(GroupsIndexActivity.this);
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

    private void launchSearchGroupsActivity() {
        if (LogUtil.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "search groups selected");
        }
        Intent intent = new Intent(this, SearchGroupsActivity.class);
        startActivity(intent);
    }
}
