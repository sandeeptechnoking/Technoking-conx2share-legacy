/**
 michelle Cannon 2/2017 derived from Wowza camera code
 */

package com.conx2share.conx2share.streaming;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.MotionEvent;
import android.view.View;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.ui.wowza.AutoFocusListener;
import com.conx2share.conx2share.ui.wowza.MultiStateButton;
import com.conx2share.conx2share.ui.wowza.TimerView;
import com.wowza.gocoder.sdk.api.devices.WZCamera;

public class CameraActivity extends CameraActivityBase {

    private static final String EXTRA_USER_ID = "userId";
    private static final String EXTRA_GROUP_ID = "groupId";
    private static final String EXTRA_EVENT_ID = "extra_event_id";

    public static void startUserStream(Context context, int userId) {
        Intent intent = new Intent(context, CameraActivity.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        context.startActivity(intent);
    }

    public static void startGroupStream(Context context, int groupId) {
        Intent intent = new Intent(context, CameraActivity.class);
        intent.putExtra(EXTRA_GROUP_ID, groupId);
        context.startActivity(intent);
    }

    public static void startEventStream(Context context, int eventId) {
        Intent intent = new Intent(context, CameraActivity.class);
        intent.putExtra(EXTRA_EVENT_ID, eventId);
        context.startActivity(intent);
    }

    private static final String TAG = CameraActivity.class.getSimpleName();

    private int mEventId;
    private int mGroupId;
    private int mUserId;
    // UI controls
    protected MultiStateButton      mBtnSwitchCamera;
    protected MultiStateButton      mBtnTorch;
    protected TimerView             mTimerView;

    // Gestures are used to toggle the focus modes
    protected GestureDetectorCompat mAutoFocusDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mEventId = this.getIntent().getIntExtra(EXTRA_EVENT_ID, 0);
        mGroupId = this.getIntent().getIntExtra(EXTRA_GROUP_ID, 0);
        mUserId = this.getIntent().getIntExtra(EXTRA_USER_ID, 0);

        mRequiredPermissions = new String[] {
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
        };


        // Initialize the UI controls
        mBtnTorch = (MultiStateButton) findViewById(R.id.ic_torch);
        mBtnSwitchCamera = (MultiStateButton) findViewById(R.id.ic_switch_camera);
        mTimerView = (TimerView) findViewById(R.id.txtTimer);
    }

    /**
     * Android Activity lifecycle methods
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (GoCoderSDKActivityBase.sGoCoderSDK != null && mWZCameraView != null) {
          //  String eventStreamName = (String.format("event_\\%d", mEventId));

            if (mUserId != 0 ) {
                mWZBroadcastConfig.setStreamName(String.format("user_%d", mUserId));
            } else {
                if (mGroupId != 0) {
                    mWZBroadcastConfig.setStreamName(String.format("group_%d", mGroupId));
                } else {
                    mWZBroadcastConfig.setStreamName(String.format("event_%d", mEventId));
                }
            }
            if (mAutoFocusDetector == null)
                mAutoFocusDetector = new GestureDetectorCompat(this, new AutoFocusListener(this, mWZCameraView));

            WZCamera activeCamera = mWZCameraView.getCamera();
            if (activeCamera != null && activeCamera.hasCapability(WZCamera.FOCUS_MODE_CONTINUOUS))
                activeCamera.setFocusMode(WZCamera.FOCUS_MODE_CONTINUOUS);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Click handler for the switch camera button
     */
    public void onSwitchCamera(View v) {
        if (mWZCameraView == null) return;

        mBtnTorch.setState(false);
        mBtnTorch.setEnabled(false);

        WZCamera newCamera = mWZCameraView.switchCamera();
        if (newCamera != null) {
            if (newCamera.hasCapability(WZCamera.FOCUS_MODE_CONTINUOUS))
                newCamera.setFocusMode(WZCamera.FOCUS_MODE_CONTINUOUS);

            boolean hasTorch = newCamera.hasCapability(WZCamera.TORCH);
            if (hasTorch) {
                mBtnTorch.setState(newCamera.isTorchOn());
                mBtnTorch.setEnabled(true);
            }
        }
    }

    /**
     * Click handler for the torch/flashlight button
     */
    public void onToggleTorch(View v) {
        if (mWZCameraView == null) return;

        WZCamera activeCamera = mWZCameraView.getCamera();
        activeCamera.setTorchOn(mBtnTorch.toggleState());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mAutoFocusDetector != null)
            mAutoFocusDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    /**
     * Update the state of the UI controls
     */
    @Override
    protected boolean syncUIControlState() {
        boolean disableControls = super.syncUIControlState();

        if (disableControls) {
            mBtnSwitchCamera.setEnabled(false);
            mBtnTorch.setEnabled(false);
        } else {
            boolean isDisplayingVideo = getBroadcastConfig().isVideoEnabled() && mWZCameraView.getCameras().length > 0;
            boolean isStreaming = getBroadcast().getStatus().isRunning();

            if (isDisplayingVideo) {
                WZCamera activeCamera = mWZCameraView.getCamera();

                boolean hasTorch = activeCamera != null && activeCamera.hasCapability(WZCamera.TORCH);
                mBtnTorch.setEnabled(hasTorch);
                if (hasTorch) {
                    mBtnTorch.setState(activeCamera.isTorchOn());
                }

                mBtnSwitchCamera.setEnabled(mWZCameraView.getCameras().length > 0);
                //mBtnSwitchCamera.setEnabled(mWZCameraView.isSwitchCameraAvailable());
            } else {
                mBtnSwitchCamera.setEnabled(false);
                mBtnTorch.setEnabled(false);
            }

            if (isStreaming && !mTimerView.isRunning()) {
                mTimerView.startTimer();
            } else if (getBroadcast().getStatus().isIdle() && mTimerView.isRunning()) {
                mTimerView.stopTimer();
            } else if (!isStreaming) {
                mTimerView.setVisibility(View.GONE);
            }
        }

        return disableControls;
    }
}
