package com.conx2share.conx2share.strategies;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.async.UnfollowGroupAsync;
import com.conx2share.conx2share.model.Group;
import com.conx2share.conx2share.model.GroupResponse;
import com.conx2share.conx2share.network.Result;
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

public class UnfollowGroupStrategy {

    public static final String TAG = UnfollowGroupStrategy.class.getSimpleName();

    @Inject
    protected SnackbarUtil mSnackbarUtil;

    @Inject
    private PreferencesUtil mPreferencesUtil;

    private Activity mActivity;

    private UnfollowGroupAsync mUnfollowGroupAsync;

    public UnfollowGroupStrategy(Activity activity) {
        mActivity = activity;
        RoboGuice.injectMembers(mActivity, this);
    }

    public void launchUnfollowConfirmationDialog(final Group group) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setTitle(mActivity.getString(R.string.unfollow_group));
        alertDialogBuilder
                .setMessage(mActivity.getString(R.string.are_you_sure_you_want_to_unfollow_the_group))
                .setCancelable(true)
                .setPositiveButton(mActivity.getString(R.string.yes),
                        (dialog, id) -> {
                            dialog.cancel();
                            unfollowGroup(group);
                        })
                .setNegativeButton(mActivity.getString(R.string.no),
                        (dialog, id) -> {
                            dialog.cancel();
                        });
        alertDialogBuilder.show();
    }

    public void unfollowGroup(final Group group) {
        if (mUnfollowGroupAsync != null) {
            Log.w(TAG,
                    "Group unfollow already in progress, new group unfollow request will be ignored");
            // TODO - queue group unfollow request
            return;
        }

        mUnfollowGroupAsync = new UnfollowGroupAsync(mActivity) {
            @Override
            protected void onSuccess(Result<GroupResponse> result) {
                EventBusUtil.getEventBus().post(new UnfollowGroupSuccessEvent());
                mUnfollowGroupAsync = null;
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.e(TAG, "Could not unfollow group", error);
                if (mActivity != null) {
                    mSnackbarUtil.showSnackBarWithAction(mActivity, R.string.unable_to_unfollow_group, R.string.retry, snackbar -> {
                        unfollowGroup(group);
                        SnackbarManager.dismiss();
                    });
                }
                mUnfollowGroupAsync = null;
            }
        }.executeInParallel(group.getId());
    }

    public class UnfollowGroupSuccessEvent {

    }
}
