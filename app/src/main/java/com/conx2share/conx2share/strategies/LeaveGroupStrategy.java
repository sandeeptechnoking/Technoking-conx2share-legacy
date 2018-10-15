package com.conx2share.conx2share.strategies;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.async.LeaveGroupAsync;
import com.conx2share.conx2share.model.Group;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.param.GroupLeaveParams;
import com.conx2share.conx2share.network.models.response.GroupLeaveResponse;
import com.conx2share.conx2share.util.EventBusUtil;
import com.conx2share.conx2share.util.PreferencesUtil;
import com.conx2share.conx2share.util.SnackbarUtil;
import com.nispok.snackbar.SnackbarManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;

import javax.inject.Inject;

import retrofit.RetrofitError;
import roboguice.RoboGuice;


public class LeaveGroupStrategy {

    public static final String TAG = LeaveGroupStrategy.class.getSimpleName();

    @Inject
    protected SnackbarUtil mSnackbarUtil;

    @Inject
    private PreferencesUtil mPreferencesUtil;

    private Activity mActivity;

    private LeaveGroupAsync mLeaveGroupAsync;

    public LeaveGroupStrategy(Activity activity) {
        mActivity = activity;
        RoboGuice.injectMembers(mActivity, this);
    }

    public void launchLeaveGroupConfirmation(final Group group) {

        String message;
        String title;
        if (group.getGroupType() == Group.DISCUSSION_KEY) {
            title = mActivity.getString(R.string.unfollow_group);
            message = mActivity.getString(R.string.are_you_sure_you_want_to_unfollow_the_group);
        } else {
            title = mActivity.getString(R.string.leave_group_text);
            message = mActivity.getString(R.string.are_you_sure_you_want_to_leave_the_group);
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton(mActivity.getString(R.string.yes),
                        (dialog, id) -> {
                            dialog.cancel();
                            leaveGroup(group);
                        })
                .setNegativeButton(mActivity.getString(R.string.no),
                        (dialog, id) -> {
                            dialog.cancel();
                        });
        alertDialogBuilder.show();
    }

    public void leaveGroup(final Group group) {
        if (mLeaveGroupAsync != null) {
            Log.w(TAG, "Request to leave group already in progress, new request will be ignored");
            return;
        }

        mLeaveGroupAsync = new LeaveGroupAsync(mActivity) {
            @Override
            protected void onSuccess(Result<GroupLeaveResponse> result) {
                EventBusUtil.getEventBus().post(new LeaveGroupSuccessEvent());
                mLeaveGroupAsync = null;
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.e(TAG, "Could not leave group", error);
                if (mActivity != null) {
                    if (group.getGroupType() == Group.DISCUSSION_KEY) {
                        mSnackbarUtil.showSnackBarWithAction(mActivity, R.string.unable_to_unfollow_group, R.string.retry, snackbar -> {
                            leaveGroup(group);
                            SnackbarManager.dismiss();
                        });
                    } else {
                        mSnackbarUtil.showSnackBarWithAction(mActivity, R.string.unable_to_leave_group, R.string.retry, snackbar -> {
                            leaveGroup(group);
                            SnackbarManager.dismiss();
                        });
                    }
                }
                mLeaveGroupAsync = null;
            }
        }.executeInParallel(new GroupLeaveParams(group.getId()));
    }

    public class LeaveGroupSuccessEvent {

    }
}
