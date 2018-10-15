package com.conx2share.conx2share;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.appsflyer.AppsFlyerLib;
import com.conx2share.conx2share.model.GoogleAnalyticsTrackerName;
import com.conx2share.conx2share.util.Statics;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.localytics.android.LocalyticsActivityLifecycleCallbacks;
import com.newrelic.agent.android.NewRelic;

import java.util.HashMap;

import io.fabric.sdk.android.Fabric;

public class Conx2ShareApplication extends MultiDexApplication {

    public static final String TAG = Conx2ShareApplication.class.getSimpleName();

    private static Conx2ShareApplication sInstance;

    HashMap<GoogleAnalyticsTrackerName, Tracker> mTrackers = new HashMap<>();

    public Conx2ShareApplication() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;

        if (!BuildConfig.DEBUG) {
            registerActivityLifecycleCallbacks(new LocalyticsActivityLifecycleCallbacks(this));
            AppsFlyerLib.setAppsFlyerKey("tmtFz9NDhtA3Kk66y2ifKa");
            AppsFlyerLib.sendTracking(getApplicationContext());
        }

        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
            NewRelic.withApplicationToken(BuildConfig.NEW_RELIC_APP_TOKEN).start(this);
            Statics.BASE_URL = "https://conx2share.com";
        } else {
            Statics.BASE_URL = "https://staging.conx2share.com";
        }

//        Statics.BASE_URL = "https://conx2share.com";
        if (BuildConfig.FLAVOR.equals("staging")) {
            Statics.BASE_URL = "https://staging.conx2share.com";
        }
    }

    public static synchronized Conx2ShareApplication getInstance() {
        if (sInstance == null) {
            sInstance = new Conx2ShareApplication();
        }
        return sInstance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public synchronized Tracker getTracker(GoogleAnalyticsTrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);

            if (trackerId == GoogleAnalyticsTrackerName.APP_TRACKER_PRODUCTION) {
                Tracker tracker = analytics.newTracker(R.xml.production_tracker);
                mTrackers.put(trackerId, tracker);
            } else if (trackerId == GoogleAnalyticsTrackerName.APP_TRACKER_STAGING) {
                Tracker tracker = analytics.newTracker(R.xml.staging_tracker);
                mTrackers.put(trackerId, tracker);
            }
        }
        return mTrackers.get(trackerId);
    }
}
