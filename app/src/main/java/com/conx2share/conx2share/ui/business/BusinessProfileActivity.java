package com.conx2share.conx2share.ui.business;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.ui.base.BaseActionBarActivity;
import com.conx2share.conx2share.ui.feed.FeedActivity;
import com.google.common.base.Strings;

import android.content.Intent;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.KeyEvent;

import java.net.URL;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class BusinessProfileActivity extends BaseActionBarActivity {

    public static final String TAG = BusinessProfileActivity.class.getSimpleName();

    public static final String CONX2SHARE_SCHEME = "conx2share";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            int businessId = 0;
            boolean loadStore = false;
            Intent intent = getIntent();
            if (intent != null) {
                if (intent.hasExtra(BusinessProfileFragment.EXTRA_BUSINESS_ID)) {
                    Log.d(TAG, "Using intent extra for business id");
                    businessId = intent.getIntExtra(BusinessProfileFragment.EXTRA_BUSINESS_ID, 0);
                } else {
                    Log.d(TAG, "Using intent data for business id");
                    if (intent.getData() != null) {
                        Uri intentData = intent.getData();
                        Log.d(TAG, "intentData: " + intentData);
                        if (intentData.getScheme().equals(CONX2SHARE_SCHEME)) {
                            try {
                                businessId = Integer.parseInt(intentData.getQueryParameter("business_id"));
                                if (intentData.getQueryParameter("load_store") != null) {
                                    loadStore = intentData.getQueryParameter("load_store").equals("true");
                                }
                            } catch (NumberFormatException e) {
                                Log.e(TAG, "uri does not contain a numeric id: " + intentData);
                            }
                        }
                    }
                }
            }
            Log.d(TAG, "businessId: " + businessId);
            BusinessProfileFragment fragment = BusinessProfileFragment.newInstance(businessId, loadStore);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(android.R.id.content, fragment);
            ft.commit();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isTaskRoot()) {
            Intent upIntent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                upIntent = NavUtils.getParentActivityIntent(BusinessProfileActivity.this);
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
