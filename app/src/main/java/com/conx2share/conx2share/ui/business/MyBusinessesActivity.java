package com.conx2share.conx2share.ui.business;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.async.GetBusinessPageBusinessesAsync;
import com.conx2share.conx2share.async.RestResultCallback;
import com.conx2share.conx2share.model.Business;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.response.BusinessesResponse;
import com.conx2share.conx2share.ui.base.BaseActionBarActivity;
import com.conx2share.conx2share.ui.feed.FeedActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;

import java.util.ArrayList;

import butterknife.ButterKnife;
//import butterknife.InjectView;
import retrofit.RetrofitError;
import roboguice.inject.InjectView;

public class MyBusinessesActivity extends BaseActionBarActivity implements MyBusinessesFragment.MyBusinessesCallback {

    public static final String TAG = MyBusinessesActivity.class.getSimpleName();

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    private GetBusinessPageBusinessesAsync mGetRelevantBusinessesAsync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_business_index);

        ButterKnife.bind(this);

        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_right);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("");

        View toolbarBackButton = findViewById(R.id.toolbar_back);
        toolbarBackButton.setOnClickListener(v -> {
            Intent upIntent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                upIntent = NavUtils.getParentActivityIntent(MyBusinessesActivity.this);
            } else {
                upIntent = new Intent(getApplicationContext(), FeedActivity.class);
            }
            startActivity(upIntent);
            finish();
        });

        View toolbarAddButton = findViewById(R.id.toolbar_add);
        toolbarAddButton.setOnClickListener(v -> {
            Intent addFriendsIntent = new Intent(getApplicationContext(), SearchBusinessesActivity.class);
            startActivity(addFriendsIntent);
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isTaskRoot()) {
            Intent upIntent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                upIntent = NavUtils.getParentActivityIntent(MyBusinessesActivity.this);
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

    @Override
    public void getBusinesses(final RestResultCallback<ArrayList<Business>> callback) {
        if (mGetRelevantBusinessesAsync != null) {
            mGetRelevantBusinessesAsync.cancel(true);
        }

        mGetRelevantBusinessesAsync = new GetBusinessPageBusinessesAsync(this) {
            @Override
            protected void onSuccess(Result<BusinessesResponse> result) {
                callback.onSuccess(result.getResource().getBusinesses());
            }

            @Override
            protected void onFailure(RetrofitError error) {
                callback.onFailure(error);
            }
        }.executeInParallel();
    }
}
