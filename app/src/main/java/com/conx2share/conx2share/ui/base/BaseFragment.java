package com.conx2share.conx2share.ui.base;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.conx2share.conx2share.model.Subscription;
import com.conx2share.conx2share.model.notification.NewBusinessPostPushEvent;
import com.conx2share.conx2share.model.notification.NewCommentPushEvent;
import com.conx2share.conx2share.model.notification.NewFollowPushEvent;
import com.conx2share.conx2share.model.notification.NewGroupInvitePushEvent;
import com.conx2share.conx2share.model.notification.NewGroupPostPushEvent;
import com.conx2share.conx2share.model.notification.NewInviteToFollowGroupEvent;
import com.conx2share.conx2share.model.notification.NewMessagePushEvent;
import com.conx2share.conx2share.model.notification.NewUserPostPushEvent;
import com.conx2share.conx2share.model.notification.UserTaggedYouPushEvent;
import com.conx2share.conx2share.util.EventBusUtil;
import com.conx2share.conx2share.util.ForegroundUtil;
import com.conx2share.conx2share.util.PreferencesUtil;
import com.conx2share.conx2share.util.SnackbarUtil;

import javax.inject.Inject;

import butterknife.ButterKnife;
import roboguice.fragment.RoboFragment;
import rx.subscriptions.CompositeSubscription;

public abstract class BaseFragment extends RoboFragment {

    public static final int PERMISSION_CAMERA_RESULT = 1000;
    public static final int PERMISSION_CONTACT_RESULT = 1001;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Inject
    protected SnackbarUtil mSnackbarUtil;

    @Inject
    PreferencesUtil mPreferencesUtil;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBusUtil.getEventBus().isRegistered(this)) {
            EventBusUtil.getEventBus().register(this);
        } else {
            Log.d(this.getClass().getSimpleName(), "onResume: EventBus already registered");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeSubscription.clear();
        if (EventBusUtil.getEventBus().isRegistered(this)) {
            EventBusUtil.getEventBus().unregister(this);
        } else {
            Log.d(this.getClass().getSimpleName(), "onPause: EventBus not registered");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void addSubscription(rx.Subscription subscription){
        compositeSubscription.add(subscription);
    }

    public boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestCameraPermission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_CAMERA_RESULT);
    }

    public boolean hasContactPermission() {
        return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestContactPermission() {
        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                PERMISSION_CONTACT_RESULT);
    }

    public void onEventMainThread(NewMessagePushEvent newMessagePush) {
        if (ForegroundUtil.getAppInForeground() && getActivity() != null) {
            mSnackbarUtil.displaySnackBar(this.getActivity(), newMessagePush.getAlertText());
        }
    }

    public void onEventMainThread(NewCommentPushEvent newCommentPush) {
        if (ForegroundUtil.getAppInForeground() && getActivity() != null) {
            mSnackbarUtil.displaySnackBar(this.getActivity(), newCommentPush.getAlertText());
        }
    }

    public void onEventMainThread(NewFollowPushEvent newFollowPush) {
        if (ForegroundUtil.getAppInForeground() && getActivity() != null) {
            mSnackbarUtil.displaySnackBar(this.getActivity(), newFollowPush.getAlertText());
        }
    }

    public void onEventMainThread(NewGroupInvitePushEvent newGroupInvitePush) {
        if (ForegroundUtil.getAppInForeground() && getActivity() != null) {
            mSnackbarUtil.displaySnackBar(this.getActivity(), newGroupInvitePush.getAlertText());
        }
    }

    public void onEventMainThread(NewUserPostPushEvent newUserPostPush) {
        if (ForegroundUtil.getAppInForeground() && getActivity() != null) {
            mSnackbarUtil.displaySnackBar(this.getActivity(), newUserPostPush.getAlertText());
        }
    }

    public void onEventMainThread(NewGroupPostPushEvent newGroupPostPush) {
        if (ForegroundUtil.getAppInForeground() && getActivity() != null) {
            mSnackbarUtil.displaySnackBar(this.getActivity(), newGroupPostPush.getAlertText());
        }
    }

    public void onEventMainThread(NewInviteToFollowGroupEvent newInviteToFollowGroupEvent) {
        if (ForegroundUtil.getAppInForeground() && getActivity() != null) {
            mSnackbarUtil.displaySnackBar(this.getActivity(), newInviteToFollowGroupEvent.getAlertText());
        }
    }

    public void onEventMainThread(UserTaggedYouPushEvent userTaggedYouPushEvent) {
        if (ForegroundUtil.getAppInForeground() && getActivity() != null) {
            mSnackbarUtil.displaySnackBar(this.getActivity(), userTaggedYouPushEvent.getAlertText());
        }
    }

    public void onEventMainThread(NewBusinessPostPushEvent newBusinessPostPushEvent) {
        if (ForegroundUtil.getAppInForeground() && getActivity() != null) {
            mSnackbarUtil.displaySnackBar(this.getActivity(), newBusinessPostPushEvent.getAlertText());
        }
    }
}
