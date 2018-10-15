/**
 * WOWZA MEDIA SYSTEMS, LLC SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. ALL CODE PROVIDED HEREUNDER IS PROVIDED "AS IS".
 * WOWZA MEDIA SYSTEMS, LLC HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 * <p>
 * Copyright © 2015 Wowza Media Systems, LLC. All rights reserved.
 * <p>
 * created by Michelle Cannon 2/2017 based on Wowza delivered configuration class
 */

package com.conx2share.conx2share.streaming;

import android.Manifest;
import android.R.id;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.WindowManager.LayoutParams;

import com.conx2share.conx2share.BuildConfig;
import com.conx2share.conx2share.R;
import com.conx2share.conx2share.util.Statics;
import com.crashlytics.android.Crashlytics;
import com.newrelic.agent.android.NewRelic;
import com.wowza.gocoder.sdk.api.WowzaGoCoder;
import com.wowza.gocoder.sdk.api.broadcast.WZBroadcast;
import com.wowza.gocoder.sdk.api.broadcast.WZBroadcastConfig;
import com.wowza.gocoder.sdk.api.configuration.WZMediaConfig;
import com.wowza.gocoder.sdk.api.configuration.WowzaConfig;
import com.wowza.gocoder.sdk.api.devices.WZCameraView;
import com.wowza.gocoder.sdk.api.errors.WZStreamingError;
import com.wowza.gocoder.sdk.api.h264.WZProfileLevel;
import com.wowza.gocoder.sdk.api.logging.WZLog;
import com.wowza.gocoder.sdk.api.status.WZStatus;
import com.wowza.gocoder.sdk.api.status.WZStatusCallback;

import java.util.Arrays;

import io.fabric.sdk.android.Fabric;


public abstract class GoCoderSDKActivityBase extends Activity
        implements WZStatusCallback {

    private static final String TAG = GoCoderSDKActivityBase.class.getSimpleName();

    private static final String SDK_APP_LICENSE_KEY = "GOSK-1243-0100-E2DF-D6AB-C358";
    private static final String SDK_APP_LICENSE_KEY_STAGING = "GOSK-A143-0103-F7DC-A550-D5AB";
    private static final int PERMISSIONS_REQUEST_CODE = 0x1;

    private static final int DEFAULT_ASPECT_VIDEO_WIDTH = 640;
    private static final int DEFAULT_ASPECT_VIDEO_HEIGHT = 480;

    protected String[] mRequiredPermissions = { Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO };

    private static final Object sBroadcastLock = new Object();
    private static boolean sBroadcastEnded = true;

    // indicates whether this is a full screen activity or not
    protected static boolean sFullScreenActivity = true;

    // GoCoder SDK top level interface
    protected static WowzaGoCoder sGoCoderSDK;

    /**
     * Build an array of WZMediaConfigs from the frame sizes supported by the active camera
     * @param goCoderCameraView the camera view
     * @return an array of WZMediaConfigs from the frame sizes supported by the active camera
     */
    protected static WZMediaConfig[] getVideoConfigs(WZCameraView goCoderCameraView) {
        WZMediaConfig configs[] = WowzaConfig.PRESET_CONFIGS;

        if (goCoderCameraView != null && goCoderCameraView.getCamera() != null) {
            WZMediaConfig cameraConfigs[] = goCoderCameraView.getCamera().getSupportedConfigs();
            Arrays.sort(cameraConfigs);
            configs = cameraConfigs;
        }

        return configs;
    }

    protected boolean mPermissionsGranted;

    protected WZBroadcast mWZBroadcast = null;

    public WZBroadcast getBroadcast() {
        return this.mWZBroadcast;
    }

    protected WZBroadcastConfig mWZBroadcastConfig;

    public WZBroadcastConfig getBroadcastConfig() {
        return this.mWZBroadcastConfig;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (sGoCoderSDK == null) {
            // Enable detailed logging from the GoCoder SDK
            WZLog.LOGGING_ENABLED = true;

            // Initialize the GoCoder SDK
            if (BuildConfig.FLAVOR.equals("staging")) {
                sGoCoderSDK = WowzaGoCoder.init(this, SDK_APP_LICENSE_KEY_STAGING);
            } else {
                sGoCoderSDK = WowzaGoCoder.init(this, SDK_APP_LICENSE_KEY);
            }

            if (sGoCoderSDK == null) {
                WZLog.error(TAG, WowzaGoCoder.getLastError());
            }
        }
        if (sGoCoderSDK != null) {
            // Create a GoCoder broadcaster and an associated broadcast configuration
            mWZBroadcast = new WZBroadcast();
            mWZBroadcastConfig = new WZBroadcastConfig(sGoCoderSDK.getConfig());
            mWZBroadcastConfig.setLogLevel(WZLog.LOG_LEVEL_DEBUG);
        }
    }

    /**
     * Android Activity lifecycle methods
     */
    @Override
    protected void onStart() {
        super.onStart();

        mPermissionsGranted = true;

        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            mPermissionsGranted = mRequiredPermissions.length <= 0 || WowzaGoCoder.hasPermissions(this, mRequiredPermissions);
            if (!mPermissionsGranted) {
                requestPermissions();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWZBroadcast != null && mPermissionsGranted) {
            initConfigurationSettings();
        }
    }

    private void initConfigurationSettings() {
        if (mWZBroadcastConfig != null) {

            // connection settings
            if (!BuildConfig.DEBUG) {
                mWZBroadcastConfig.setHostAddress(getString(R.string.wz_live_host_address));
            } else {
                mWZBroadcastConfig.setHostAddress(getString(R.string.wz_staging_host_address));
            }
            if (BuildConfig.FLAVOR.equals("staging")) {
                mWZBroadcastConfig.setHostAddress(getString(R.string.wz_staging_host_address));
            }

            mWZBroadcastConfig.setPortNumber(WowzaConfig.DEFAULT_PORT);
            mWZBroadcastConfig.setApplicationName(WowzaConfig.DEFAULT_APP);
            mWZBroadcastConfig.setStreamName(WowzaConfig.DEFAULT_STREAM);
            mWZBroadcastConfig.setUsername(null);
            mWZBroadcastConfig.setPassword(null);

            // video settings
            WZMediaConfig wzMediaConfig = mWZBroadcastConfig;
            wzMediaConfig.setVideoEnabled(true);
            wzMediaConfig.setVideoFrameWidth(DEFAULT_ASPECT_VIDEO_WIDTH);
            wzMediaConfig.setVideoFrameHeight(DEFAULT_ASPECT_VIDEO_HEIGHT);
            wzMediaConfig.setVideoFramerate(WZMediaConfig.DEFAULT_VIDEO_FRAME_RATE);
            wzMediaConfig.setVideoKeyFrameInterval(WZMediaConfig.DEFAULT_VIDEO_KEYFRAME_INTERVAL);
            wzMediaConfig.setVideoBitRate(WZMediaConfig.DEFAULT_VIDEO_BITRATE);
            wzMediaConfig.setABREnabled(true);
            WZProfileLevel profileLevel = new WZProfileLevel(WZProfileLevel.PROFILE_BASELINE, WZProfileLevel.PROFILE_LEVEL1);
            if (profileLevel.validate()) {
                wzMediaConfig.setVideoProfileLevel(profileLevel);
            } else {
                wzMediaConfig.setVideoProfileLevel(null);
            }

            // audio settings
            wzMediaConfig.setAudioEnabled(true);
            wzMediaConfig.setAudioSampleRate(WZMediaConfig.DEFAULT_AUDIO_SAMPLE_RATE);
            wzMediaConfig.setAudioChannels(WZMediaConfig.AUDIO_CHANNELS_STEREO);
            wzMediaConfig.setAudioBitRate(WZMediaConfig.DEFAULT_AUDIO_BITRATE);
        }
    }

    @Override
    protected void onPause() {
        // Stop any active live stream
        if (mWZBroadcast != null && mWZBroadcast.getStatus().isRunning()) {
            endBroadcast(true);
        }
        super.onPause();
    }

    protected void requestPermissions() {
        ActivityCompat.requestPermissions(this, mRequiredPermissions, GoCoderSDKActivityBase.PERMISSIONS_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            mPermissionsGranted = true;

            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    mPermissionsGranted = false;
                    break;
                }
            }
            if (mPermissionsGranted) {
                initConfigurationSettings();
            }
        }
    }

    /**
     * Enable Android's sticky immersive full-screen mode
     * See http://developer.android.com/training/system-ui/immersive.html#sticky
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (sFullScreenActivity && hasFocus) {
            View rootView = getWindow().getDecorView().findViewById(id.content);
            if (rootView != null)
                rootView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    /**
     * WZStatusCallback interface methods
     */
    @Override
    public void onWZStatus(final WZStatus goCoderStatus) {
        new Handler(Looper.getMainLooper()).post(() -> {
            if (goCoderStatus.isReady()) {
                // Keep the screen on while the broadcast is active
                getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);

                // Since we have successfully opened up the server connection, store the connection info for auto complete
                //  ConfigPrefs.storeAutoCompleteHostConfig(PreferenceManager.getDefaultSharedPreferences(GoCoderSDKActivityBase.this), mWZBroadcastConfig);
            } else if (goCoderStatus.isIdle())
                // Clear the "keep screen on" flag
                getWindow().clearFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);

            WZLog.debug(TAG, goCoderStatus.toString());
        });
    }

    @Override
    public void onWZError(final WZStatus goCoderStatus) {
        new Handler(Looper.getMainLooper()).post(() -> WZLog.error(TAG, goCoderStatus.getLastError()));
    }

    protected synchronized WZStreamingError startBroadcast() {
        WZStreamingError configValidationError = null;

        if (mWZBroadcast.getStatus().isIdle()) {
            WZLog.info(TAG, "=============== Broadcast Configuration ===============\n"
                    + mWZBroadcastConfig
                    + "\n=======================================================");

            configValidationError = mWZBroadcastConfig.validateForBroadcast();
            if (configValidationError == null)
                mWZBroadcast.startBroadcast(mWZBroadcastConfig, this);
        } else {
            WZLog.error(TAG, "startBroadcast() called while another broadcast is active");
        }
        return configValidationError;
    }

    protected synchronized void endBroadcast(boolean appPausing) {
        if (!mWZBroadcast.getStatus().isIdle()) {
            if (appPausing) {
                // Stop any active live stream
                sBroadcastEnded = false;
                mWZBroadcast.endBroadcast(new WZStatusCallback() {
                    @Override
                    public void onWZStatus(WZStatus wzStatus) {
                        synchronized (sBroadcastLock) {
                            sBroadcastEnded = true;
                            sBroadcastLock.notifyAll();
                        }
                    }

                    @Override
                    public void onWZError(WZStatus wzStatus) {
                        WZLog.error(TAG, wzStatus.getLastError());
                        synchronized (sBroadcastLock) {
                            sBroadcastEnded = true;
                            sBroadcastLock.notifyAll();
                        }
                    }
                });

                while (!sBroadcastEnded) {
                    try {
                        sBroadcastLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                mWZBroadcast.endBroadcast(this);
            }
        } else {
            WZLog.error(TAG, "endBroadcast() called without an active broadcast");
        }
    }

    protected synchronized void endBroadcast() {
        endBroadcast(false);
    }
}
