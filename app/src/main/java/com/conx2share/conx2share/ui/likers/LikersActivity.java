package com.conx2share.conx2share.ui.likers;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.ui.base.BaseActionBarActivity;

import butterknife.ButterKnife;
//import butterknife.InjectView;
import roboguice.inject.InjectView;

public class LikersActivity extends BaseActionBarActivity {

    public static final String EXTRA_POST_ID = "post_id";
    @InjectView(R.id.likers_toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likers);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("");

        View followingBackButton = findViewById(R.id.liker_back);
        followingBackButton.setOnClickListener(v -> finish());
    }
}
