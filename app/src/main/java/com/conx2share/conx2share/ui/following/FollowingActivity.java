package com.conx2share.conx2share.ui.following;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.ui.base.BaseActionBarActivity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import butterknife.ButterKnife;
//import butterknife.InjectView;
import roboguice.inject.InjectView;

public class FollowingActivity extends BaseActionBarActivity {

    @InjectView(R.id.following_toolbar)
    Toolbar mFollowersToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);
        ButterKnife.bind(this);

        setSupportActionBar(mFollowersToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("");

        View followingBackButton = findViewById(R.id.following_back);
        followingBackButton.setOnClickListener(v -> finish());
    }
}
