package com.conx2share.conx2share.ui.news;


import com.conx2share.conx2share.R;
import com.conx2share.conx2share.ui.base.BaseActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import butterknife.ButterKnife;
//import butterknife.InjectView;
import roboguice.inject.InjectView;

public class NewsActivity extends BaseActionBarActivity {

    @InjectView(R.id.news_toolbar)
    Toolbar mNewsToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ButterKnife.bind(this);

        setSupportActionBar(mNewsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("");

        View followingBackButton = findViewById(R.id.news_back_button);
        followingBackButton.setOnClickListener(v -> finish());
    }
}