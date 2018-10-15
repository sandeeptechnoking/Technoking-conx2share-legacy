/**
 *  This is sample code provided by Wowza Media Systems, LLC.  All sample code is intended to be a reference for the
 *  purpose of educating developers, and is not intended to be used in any production environment.
 *
 *  IN NO EVENT SHALL WOWZA MEDIA SYSTEMS, LLC BE LIABLE TO YOU OR ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL,
 *  OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION,
 *  EVEN IF WOWZA MEDIA SYSTEMS, LLC HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  WOWZA MEDIA SYSTEMS, LLC SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. ALL CODE PROVIDED HEREUNDER IS PROVIDED "AS IS".
 *  WOWZA MEDIA SYSTEMS, LLC HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 *
 *  Copyright © 2015 Wowza Media Systems, LLC. All rights reserved.
 */

package com.conx2share.conx2share.streaming;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.ui.wowza.MultiStateButton;
import com.conx2share.conx2share.ui.wowza.StatusView;
import com.wowza.gocoder.sdk.api.WowzaGoCoder;
import com.wowza.gocoder.sdk.api.configuration.WZMediaConfig;
import com.wowza.gocoder.sdk.api.devices.WZAudioDevice;
import com.wowza.gocoder.sdk.api.devices.WZCamera;
import com.wowza.gocoder.sdk.api.devices.WZCameraView;
import com.wowza.gocoder.sdk.api.errors.WZError;
import com.wowza.gocoder.sdk.api.errors.WZStreamingError;
import com.wowza.gocoder.sdk.api.geometry.WZSize;
import com.wowza.gocoder.sdk.api.graphics.WZColor;
import com.wowza.gocoder.sdk.api.status.WZStatus;

public abstract class CameraActivityBase extends GoCoderSDKActivityBase
    implements WZCameraView.PreviewStatusListener{

    // UI controls
    protected MultiStateButton mBtnBroadcast;
    protected MultiStateButton mBtnClose;
    protected StatusView       mStatusView;

    // The GoCoder SDK camera preview display view
    protected WZCameraView mWZCameraView;
    protected WZAudioDevice mWZAudioDevice;

    private boolean mDevicesInitialized;
    private boolean mUIInitialized;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Android Activity lifecycle methods
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (!mUIInitialized)
            initUIControls();
        if (!mDevicesInitialized)
            initGoCoderDevices();

        if (sGoCoderSDK != null && mPermissionsGranted) {
            mWZCameraView.setCameraConfig(getBroadcastConfig());
            mWZCameraView.setScaleMode(WZMediaConfig.RESIZE_TO_ASPECT);
            mWZCameraView.setVideoBackgroundColor(WZColor.DARKGREY);

            if (mWZBroadcastConfig.isVideoEnabled()) {
                if (mWZCameraView.isPreviewPaused())
                    mWZCameraView.onResume();
                else
                    mWZCameraView.startPreview();
            }

            // Briefly display the video frame size from config
            Toast.makeText(this, getBroadcastConfig().getLabel(true, true, false, true), Toast.LENGTH_LONG).show();
        }

        syncUIControlState();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mWZCameraView != null) {
            mWZCameraView.onPause();
        }
    }

    /**
     * WZStatusCallback interface methods
     */
    @Override
    public void onWZStatus(final WZStatus goCoderStatus) {
        new Handler(Looper.getMainLooper()).post(() -> {
            if (goCoderStatus.isRunning()) {
                // Keep the screen on while we are broadcasting
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                // Since we have successfully opened up the server connection, store the connection info for auto complete
              //  ConfigPrefs.storeAutoCompleteHostConfig(PreferenceManager.getDefaultSharedPreferences(CameraActivityBase.this), mWZBroadcastConfig);
            } else if (goCoderStatus.isIdle()) {
                // Clear the "keep screen on" flag
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }

            if (mStatusView != null) mStatusView.setStatus(goCoderStatus);
            syncUIControlState();
        });
    }

    @Override
    public void onWZError(final WZStatus goCoderStatus) {
        new Handler(Looper.getMainLooper()).post(() -> {
            if (mStatusView != null) mStatusView.setStatus(goCoderStatus);
            syncUIControlState();
        });
    }

    /**
     * Click handler for the broadcast button
     */
    public void onToggleBroadcast(View v) {
        if (getBroadcast() == null) return;

        if (!mPermissionsGranted) {
            requestPermissions();
            return;
        }

        if (getBroadcast().getStatus().isIdle()) {
            WZStreamingError configError = startBroadcast();
            if (configError != null) {
                if (mStatusView != null) mStatusView.setErrorMessage(configError.getErrorDescription());
            } else {
                int orientation = getResources().getConfiguration().orientation;
                setRequestedOrientation(orientation == Configuration.ORIENTATION_PORTRAIT
                        ? ActivityInfo.SCREEN_ORIENTATION_LOCKED: ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }
        } else {
            endBroadcast();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    /**
     * Click handler for the settings button
     */
    public void onSettings(View v) {
        endBroadcast();
        finish();

    }

    protected void initGoCoderDevices() {
        if (sGoCoderSDK != null && mPermissionsGranted) {

            // Initialize the camera preview
            if (mWZCameraView != null) {
                WZCamera availableCameras[] = mWZCameraView.getCameras();
                // Ensure we can access to at least one camera
                if (availableCameras.length > 0) {
                    // Set the video broadcaster in the broadcast config
                    getBroadcastConfig().setVideoBroadcaster(mWZCameraView);
                } else {
                    mStatusView.setErrorMessage("Could not detect or gain access to any cameras on this device");
                    getBroadcastConfig().setVideoEnabled(false);
                }
            } else {
                getBroadcastConfig().setVideoEnabled(false);
            }

            // Initialize the audio input device interface
            mWZAudioDevice = new WZAudioDevice();

            // Set the audio broadcaster in the broadcast config
            getBroadcastConfig().setAudioBroadcaster(mWZAudioDevice);

            mDevicesInitialized = true;
        }
    }

    @Override
    public void onWZCameraPreviewStarted(WZCamera wzCamera, WZSize wzSize, int i) {
    }

    @Override
    public void onWZCameraPreviewStopped(int cameraId) {
    }

    @Override
    public void onWZCameraPreviewError(WZCamera wzCamera, WZError wzError) {
    }

    protected void initUIControls() {
        // Initialize the UI controls
        mBtnBroadcast = (MultiStateButton) findViewById(R.id.ic_broadcast);
        mBtnClose = (MultiStateButton) findViewById(R.id.ic_close);
        mStatusView = (StatusView) findViewById(R.id.statusView);

        // The GoCoder SDK camera view
        mWZCameraView = (WZCameraView) findViewById(R.id.cameraPreview);
        mWZCameraView.setPreviewReadyListener(this);

        mUIInitialized = true;

        if (sGoCoderSDK == null && mStatusView != null)
            mStatusView.setErrorMessage(WowzaGoCoder.getLastError().getErrorDescription());
    }

    protected boolean syncUIControlState() {
        boolean disableControls = getBroadcast() == null ||
                !(getBroadcast().getStatus().isIdle() ||
                        getBroadcast().getStatus().isRunning());
        boolean isStreaming = getBroadcast() != null && getBroadcast().getStatus().isRunning();

        if (disableControls) {
            if (mBtnBroadcast != null) mBtnBroadcast.setEnabled(false);
            if (mBtnClose != null) {
                mBtnClose.setEnabled(false);
                mBtnClose.setVisibility(View.INVISIBLE);
            }
        } else {
            if (mBtnBroadcast != null) {
                mBtnBroadcast.setState(isStreaming);
                mBtnBroadcast.setEnabled(true);
            }
            if (mBtnClose != null)
                mBtnClose.setEnabled(!isStreaming);
                if (mBtnClose.isEnabled()) {
                    mBtnClose.setVisibility(View.VISIBLE);
                }else {
                    mBtnClose.setVisibility(View.INVISIBLE);
                }
        }

        return disableControls;
    }
}
