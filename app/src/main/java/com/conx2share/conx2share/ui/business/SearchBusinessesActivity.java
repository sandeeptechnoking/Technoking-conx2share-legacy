package com.conx2share.conx2share.ui.business;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.ui.base.BaseActionBarActivity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import butterknife.BindView;
import butterknife.ButterKnife;
//import butterknife.InjectView;

public class SearchBusinessesActivity extends BaseActionBarActivity {

    public static final String TAG = SearchBusinessesActivity.class.getSimpleName();

    @BindView(R.id.search_businesses_toolbar)
    Toolbar mSearchBusinessesToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_businesses);
        ButterKnife.bind(this);
        overridePendingTransition(R.anim.enter_from_bottom, R.anim.exit_to_top);
        setSupportActionBar(mSearchBusinessesToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("");

        View searchBusinessesBackButton = findViewById(R.id.search_businesses_back_button);
        searchBusinessesBackButton.setOnClickListener(v -> {
            hideKeyboard();
            finish();
            overridePendingTransition(R.anim.enter_from_top, R.anim.exit_to_bottom);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_top, R.anim.exit_to_bottom);
    }

    private void hideKeyboard() {
        View searchBusinessBackButton = findViewById(R.id.search_businesses_back_button);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchBusinessBackButton.getWindowToken(), 0);
    }
}
