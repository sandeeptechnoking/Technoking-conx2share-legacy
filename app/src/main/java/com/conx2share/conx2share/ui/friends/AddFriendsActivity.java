package com.conx2share.conx2share.ui.friends;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.ui.base.BaseActionBarActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import butterknife.ButterKnife;
//import butterknife.InjectView;
import roboguice.inject.InjectView;


public class AddFriendsActivity extends BaseActionBarActivity {

    private static final String TAG = AddFriendsActivity.class.getSimpleName();

    public static void start(Context mContext) {
        mContext.startActivity(new Intent(mContext, AddFriendsActivity.class));
    }
    @InjectView(R.id.add_friends_toolbar)
    Toolbar mAddFriendsToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        ButterKnife.bind(this);
        overridePendingTransition(R.anim.enter_from_bottom, R.anim.exit_to_top);
        setSupportActionBar(mAddFriendsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("");

        View addFriendsBackButton = findViewById(R.id.add_friends_back_button);
        addFriendsBackButton.setOnClickListener(v -> {
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
        View addFriendsBackButton = findViewById(R.id.add_friends_back_button);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(addFriendsBackButton.getWindowToken(), 0);
    }
}
