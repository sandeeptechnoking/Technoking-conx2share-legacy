package com.conx2share.conx2share.ui.sayno;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.model.Group;
import com.conx2share.conx2share.model.InvitationState;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.ui.sayno.dialog.SayNoConfirmationDialogFragment;
import com.conx2share.conx2share.ui.sayno.dialog.SayNoNotificationDialogFragment;
import com.conx2share.conx2share.ui.sayno.intro.SayNoIntroActivity;
import com.conx2share.conx2share.util.PreferencesUtil;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Singleton
public final class SayNoFlowInteractor {

    private static final String TAG = SayNoFlowInteractor.class.getName();

    private PreferencesUtil preferencesUtil;

    private NetworkClient networkClient;

    @Inject
    public SayNoFlowInteractor(PreferencesUtil preferencesUtil,
                               NetworkClient networkClient) {
        this.preferencesUtil = preferencesUtil;
        this.networkClient = networkClient;
    }

    public Subscription requestGroup() {
        return networkClient.getSayNoGroup()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(groups -> {
                    if (groups == null || groups.isEmpty()) {
                        return;
                    }

                    Group sayNoGroup = groups.get(0);
                    InvitationState state = sayNoGroup.getSayNoInvitationState();

                    if (state != null) {
                        setInvitationState(state);
                    }
                    setGroupId(sayNoGroup.getId());
                }, throwable -> Log.e(TAG, "GROUP", throwable));
    }

    public void setGroupId(int groupId) {
        preferencesUtil.setSayNoGroupId(groupId);
    }

    public void setInvitationState(InvitationState state) {
        preferencesUtil.setSayNoInvitationState(state);
    }

    public void dontShowIntroAgain() {
        preferencesUtil.setSayNoDontShowAgain(true);
    }

    public void startSayNo(AppCompatActivity activity) {

        boolean dontShowIntroAgain = preferencesUtil.isSayNoDontShowAgain();

        if (!dontShowIntroAgain) {
            SayNoIntroActivity.start(activity);
            return;
        }

        InvitationState state = preferencesUtil.getSayNoInvitationState();
        if (state == null) {
            SayNoSignInActivity.start(activity);
            return;
        }

        switch (state) {
            case PENDING:
                SayNoNotificationDialogFragment
                        .newInstance(R.string.say_no_invitation_sent)
                        .show(activity.getSupportFragmentManager(), TAG);
                break;
            case DECLINED:
                SayNoConfirmationDialogFragment
                        .newInstance(R.string.say_no_invitation_declined, R.string.say_no_invitation_send_another_request)
                        .setConfirmationDialogInteraction(new SayNoConfirmationDialogFragment.ConfirmationDialogInteraction() {
                            @Override
                            public void onPositiveButtonClicked() {
                                SayNoSignInActivity.start(activity);
                            }

                            @Override
                            public void onNegativeButtonClicked() {
                            }
                        })
                        .show(activity.getSupportFragmentManager(), TAG);
                break;
            case ACCEPTED:
                SayNoChatActivity.start(activity);
                break;
            default:
                Log.d(TAG, "Status skipped: " + state.name());
        }
    }
}