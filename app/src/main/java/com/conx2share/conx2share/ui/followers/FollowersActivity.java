package com.conx2share.conx2share.ui.followers;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.ui.base.BaseActionBarActivity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import butterknife.ButterKnife;
//import butterknife.InjectView;
import roboguice.inject.InjectView;

public class FollowersActivity extends BaseActionBarActivity {

    @InjectView(R.id.followers_toolbar)
    Toolbar mFollowersToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);
        ButterKnife.bind(this);

        setSupportActionBar(mFollowersToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("");

        View followingBackButton = findViewById(R.id.followers_back);
        followingBackButton.setOnClickListener(v -> finish());
    }
}
