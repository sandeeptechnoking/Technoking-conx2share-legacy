package com.conx2share.conx2share.ui.base;

import com.conx2share.conx2share.model.notification.NewBusinessPostPushEvent;
import com.conx2share.conx2share.model.notification.NewCommentPushEvent;
import com.conx2share.conx2share.model.notification.NewFollowPushEvent;
import com.conx2share.conx2share.model.notification.NewGroupInvitePushEvent;
import com.conx2share.conx2share.model.notification.NewGroupPostPushEvent;
import com.conx2share.conx2share.model.notification.NewInviteToFollowGroupEvent;
import com.conx2share.conx2share.model.notification.NewMessagePushEvent;
import com.conx2share.conx2share.model.notification.NewUserPostPushEvent;
import com.conx2share.conx2share.model.notification.UserTaggedYouPushEvent;
import com.conx2share.conx2share.util.ForegroundUtil;
import com.conx2share.conx2share.util.SnackbarUtil;
import com.metova.slim.Slim;

import android.os.Bundle;
import android.view.View;

import javax.inject.Inject;

import butterknife.ButterKnife;
import roboguice.activity.RoboFragmentActivity;

public class BaseActivity extends RoboFragmentActivity {

    @Inject
    SnackbarUtil mSnackbarUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View layout = Slim.createLayout(this, this);
        if (layout != null) {
            setContentView(layout);
            ButterKnife.bind(this);
        }

        Slim.injectExtras(getIntent().getExtras(), this);

        // TODO: check for valid authUser in preferences before continuing.  see BaseActionBarActivity
    }

    @Override
    protected void onResume() {
        super.onResume();
        ForegroundUtil.setAppInForeground(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ForegroundUtil.setAppInForeground(false);
    }

    public void onEventMainThread(NewMessagePushEvent newMessagePush) {
        if (ForegroundUtil.getAppInForeground()) {
            mSnackbarUtil.displaySnackBar(this, newMessagePush.getAlertText());
        }
    }

    public void onEventMainThread(NewCommentPushEvent newCommentPush) {
        if (ForegroundUtil.getAppInForeground()) {
            mSnackbarUtil.displaySnackBar(this, newCommentPush.getAlertText());
        }
    }

    public void onEventMainThread(NewFollowPushEvent newFollowPush) {
        if (ForegroundUtil.getAppInForeground()) {
            mSnackbarUtil.displaySnackBar(this, newFollowPush.getAlertText());
        }
    }

    public void onEventMainThread(NewGroupInvitePushEvent newGroupInvitePush) {
        if (ForegroundUtil.getAppInForeground()) {
            mSnackbarUtil.displaySnackBar(this, newGroupInvitePush.getAlertText());
        }
    }

    public void onEventMainThread(NewUserPostPushEvent newUserPostPush) {
        if (ForegroundUtil.getAppInForeground()) {
            mSnackbarUtil.displaySnackBar(this, newUserPostPush.getAlertText());
        }
    }

    public void onEventMainThread(NewGroupPostPushEvent newGroupPostPush) {
        if (ForegroundUtil.getAppInForeground()) {
            mSnackbarUtil.displaySnackBar(this, newGroupPostPush.getAlertText());
        }
    }

    public void onEventMainThread(NewInviteToFollowGroupEvent newInviteToFollowGroupEvent) {
        if (ForegroundUtil.getAppInForeground()) {
            mSnackbarUtil.displaySnackBar(this, newInviteToFollowGroupEvent.getAlertText());
        }
    }

    public void onEventMainThread(UserTaggedYouPushEvent newUserTaggedYouEvent) {
        if (ForegroundUtil.getAppInForeground()) {
            mSnackbarUtil.displaySnackBar(this, newUserTaggedYouEvent.getAlertText());
        }
    }

    public void onEventMainThread(NewBusinessPostPushEvent newBusinessPostPushEvent) {
        if (ForegroundUtil.getAppInForeground()) {
            mSnackbarUtil.displaySnackBar(this, newBusinessPostPushEvent.getAlertText());
        }
    }
}
