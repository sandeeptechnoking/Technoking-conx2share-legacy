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

public class DeleteGroupStrategy {

    public static final String TAG = DeleteGroupStrategy.class.getSimpleName();

    @Inject
    protected SnackbarUtil mSnackbarUtil;

    @Inject
    private PreferencesUtil mPreferencesUtil;

    private Activity mActivity;

    private LeaveGroupAsync mDeleteGroupAsync; // Deleting a group is caused by an owner making a leave group request

    public DeleteGroupStrategy(Activity activity) {
        mActivity = activity;
        RoboGuice.injectMembers(mActivity.getApplicationContext(), this);
    }

    public void launchDeleteGroupConfirmation(final Group group) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setTitle(mActivity.getString(R.string.delete_group_text));
        alertDialogBuilder
                .setMessage(mActivity.getString(R.string.are_you_sure_you_want_to_delete_the_group))
                .setCancelable(true)
                .setPositiveButton(mActivity.getString(R.string.yes),
                        (dialog, id) -> {
                            dialog.cancel();
                            deleteGroup(group);
                        })
                .setNegativeButton(mActivity.getString(R.string.no),
                        (dialog, id) -> {
                            dialog.cancel();
                        });
        alertDialogBuilder.show();
    }

    public void deleteGroup(final Group group) {
        if (mDeleteGroupAsync != null) {
            Log.w(TAG, "Request to delete group already in progress, new request will be ignored");
            return;
        }

        mDeleteGroupAsync = new LeaveGroupAsync(mActivity) {
            @Override
            protected void onSuccess(Result<GroupLeaveResponse> result) {
                EventBusUtil.getEventBus().post(new DeleteGroupSuccessEvent());
                mDeleteGroupAsync = null;
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.e(TAG, "Could not delete group", error);
                mSnackbarUtil.showSnackBarWithAction(mActivity, R.string.unable_to_delete_group, R.string.retry, snackbar -> {
                    deleteGroup(group);
                    SnackbarManager.dismiss();
                });
                mDeleteGroupAsync = null;
            }
        }.executeInParallel(new GroupLeaveParams(group.getId()));
    }

    public class DeleteGroupSuccessEvent {

    }
}
