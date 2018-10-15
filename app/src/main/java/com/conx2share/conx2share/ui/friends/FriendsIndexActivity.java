package com.conx2share.conx2share.ui.friends;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.ui.base.BaseActionBarActivity;
import com.conx2share.conx2share.ui.feed.FeedActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;

import butterknife.ButterKnife;
//import butterknife.InjectView;
import roboguice.inject.InjectView;

public class FriendsIndexActivity extends BaseActionBarActivity {

    @InjectView(R.id.friends_index_toolbar)
    Toolbar mFriendsIndexToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_index);
        ButterKnife.bind(this);

        setSupportActionBar(mFriendsIndexToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("");

        View friendsIndexBack = findViewById(R.id.friends_index_back);
        friendsIndexBack.setOnClickListener(v -> {
            Intent upIntent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                upIntent = NavUtils.getParentActivityIntent(FriendsIndexActivity.this);
            } else {
                upIntent = new Intent(getApplicationContext(), FeedActivity.class);
            }
            startActivity(upIntent);
            finish();
        });

        View friendsIndexAddFriends = findViewById(R.id.friends_index_add_friends);
        friendsIndexAddFriends.setOnClickListener(v -> {
            Intent addFriendsIntent = new Intent(getApplicationContext(), AddFriendsActivity.class);
            startActivity(addFriendsIntent);
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isTaskRoot()) {
            Intent upIntent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                upIntent = NavUtils.getParentActivityIntent(FriendsIndexActivity.this);
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
