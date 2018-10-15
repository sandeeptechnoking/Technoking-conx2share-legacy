package com.conx2share.conx2share.ui.feed;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import com.conx2share.conx2share.BuildConfig;
import com.conx2share.conx2share.Conx2ShareApplication;
import com.conx2share.conx2share.R;
import com.conx2share.conx2share.model.GoogleAnalyticsTrackerName;
import com.conx2share.conx2share.ui.base.BaseDrawerActivity;
import com.conx2share.conx2share.ui.sayno.SayNoFlowInteractor;
import com.google.android.gms.analytics.GoogleAnalytics;

import javax.inject.Inject;

import rx.Subscription;

public class FeedActivity extends BaseDrawerActivity {

    @Inject
    SayNoFlowInteractor sayNoFlow;

    private Subscription subscription;

    public static void start(Context context) {
        context.startActivity(new Intent(context, FeedActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() != null) {
            overridePendingTransition(R.anim.enter_from_bottom, R.anim.exit_to_top);
        }
        setTitle(R.string.home);
        if (BuildConfig.FLAVOR.equals("production")) {
            ((Conx2ShareApplication) getApplication()).getTracker(GoogleAnalyticsTrackerName.APP_TRACKER_PRODUCTION);
        } else {
            ((Conx2ShareApplication) getApplication()).getTracker(GoogleAnalyticsTrackerName.APP_TRACKER_STAGING);
        }

        subscription = sayNoFlow.requestGroup();
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    @Override
    public Fragment initializeFragment() {
        return FeedFragment.newInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getToolbar().getMenu().clear();
        getToolbar().inflateMenu(R.menu.feed_menu);

        menu.findItem(R.id.say_no_item).getActionView().setOnClickListener(v -> sayNoFlow.startSayNo(FeedActivity.this));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.say_no_item:
                sayNoFlow.startSayNo(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
