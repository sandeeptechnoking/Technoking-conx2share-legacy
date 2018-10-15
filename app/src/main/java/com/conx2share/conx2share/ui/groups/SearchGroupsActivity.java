package com.conx2share.conx2share.ui.groups;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.ui.base.BaseActionBarActivity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;

import butterknife.ButterKnife;
//import butterknife.InjectView;
import roboguice.inject.InjectView;

public class SearchGroupsActivity extends BaseActionBarActivity {

    public static final String TAG = SearchGroupsActivity.class.getSimpleName();

    @InjectView(R.id.search_groups_toolbar)
    Toolbar mSearchGroupsToolbar;

    @InjectView(R.id.search_groups_back_button)
    ImageButton mSearchGroupsBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_groups);
        ButterKnife.bind(this);
        setSupportActionBar(mSearchGroupsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("");

        mSearchGroupsBackButton.setOnClickListener(v -> {
            hideKeyboard();
            finish();
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSearchGroupsBackButton.getWindowToken(), 0);
    }
}
