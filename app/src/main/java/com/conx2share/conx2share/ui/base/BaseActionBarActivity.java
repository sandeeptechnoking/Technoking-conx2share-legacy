package com.conx2share.conx2share.ui.base;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.conx2share.conx2share.util.ForegroundUtil;
import com.google.inject.Key;

import java.util.HashMap;
import java.util.Map;

import roboguice.RoboGuice;
import roboguice.activity.event.OnActivityResultEvent;
import roboguice.activity.event.OnConfigurationChangedEvent;
import roboguice.activity.event.OnContentChangedEvent;
import roboguice.activity.event.OnCreateEvent;
import roboguice.activity.event.OnDestroyEvent;
import roboguice.activity.event.OnNewIntentEvent;
import roboguice.activity.event.OnPauseEvent;
import roboguice.activity.event.OnRestartEvent;
import roboguice.activity.event.OnResumeEvent;
import roboguice.activity.event.OnStartEvent;
import roboguice.activity.event.OnStopEvent;
import roboguice.event.EventManager;
import roboguice.inject.RoboInjector;
import roboguice.util.RoboContext;

/**
 * use {@link BaseAppCompatActivity} instead
 */
@Deprecated
public abstract class BaseActionBarActivity extends AppCompatActivity implements RoboContext {

    public static final int PERMISSION_CAMERA_RESULT = 1000;

    protected EventManager eventManager;

    protected HashMap<Key<?>, Object> scopedObjects = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final RoboInjector injector = RoboGuice.getInjector(this);
        eventManager = injector.getInstance(EventManager.class);
        injector.injectMembersWithoutViews(this);

        super.onCreate(savedInstanceState);
        eventManager.fire(new OnCreateEvent(savedInstanceState));

        // TODO: this code is necessary to eliminate the possibility of having an invalid authUser in preferences, but it borks all the tests.
//        AuthUser authUser = mPreferencesUtil.getAuthUser();
//        if (authUser == null || !authUser.isValid()) {
//            EmergencyUtil.emergencyLogoutWithNotification(this, mPreferencesUtil);
//        }
    }

    @Override
    protected void onRestart() {

        super.onRestart();
        eventManager.fire(new OnRestartEvent());
    }

    @Override
    protected void onStart() {

        super.onStart();
        eventManager.fire(new OnStartEvent());
    }

    @Override
    protected void onResume() {

        super.onResume();
        eventManager.fire(new OnResumeEvent());
        ForegroundUtil.setAppInForeground(true);
    }

    @Override
    protected void onPause() {

        super.onPause();
        eventManager.fire(new OnPauseEvent());
        ForegroundUtil.setAppInForeground(false);
    }

    @Override
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);
        eventManager.fire(new OnNewIntentEvent());
    }

    @Override
    protected void onStop() {

        try {
            eventManager.fire(new OnStopEvent());
        } finally {
            super.onStop();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            eventManager.fire(new OnDestroyEvent());
        } finally {
            try {
                RoboGuice.destroyInjector(this);
            } finally {
                super.onDestroy();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        final Configuration currentConfig = getResources().getConfiguration();
        super.onConfigurationChanged(newConfig);
        eventManager.fire(new OnConfigurationChangedEvent(currentConfig, newConfig));
    }

    @Override
    public void onSupportContentChanged() {
        super.onSupportContentChanged();
        RoboGuice.getInjector(this).injectViewMembers(this);
        eventManager.fire(new OnContentChangedEvent());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        eventManager.fire(new OnActivityResultEvent(requestCode, resultCode, data));
    }

    public boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_CAMERA_RESULT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public Map<Key<?>, Object> getScopedObjectMap() {
        return scopedObjects;
    }
}
