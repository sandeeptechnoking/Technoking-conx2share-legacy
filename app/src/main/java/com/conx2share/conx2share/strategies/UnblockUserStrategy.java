package com.conx2share.conx2share.strategies;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.async.UnblockUserAsync;
import com.conx2share.conx2share.model.ResponseMessage;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.util.EventBusUtil;
import com.conx2share.conx2share.util.SnackbarUtil;
import com.nispok.snackbar.SnackbarManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;

import javax.inject.Inject;

import retrofit.RetrofitError;
import roboguice.RoboGuice;

public class UnblockUserStrategy {

    public static final String TAG = UnblockUserStrategy.class.getSimpleName();

    @Inject
    protected SnackbarUtil mSnackbarUtil;

    private UnblockUserAsync mUnblockUserAsync;

    private Activity mActivity;

    private int mGroupId;

    public UnblockUserStrategy(Activity activity, int groupId) {
        mActivity = activity;
        mGroupId = groupId;
        RoboGuice.injectMembers(activity.getApplicationContext(), this);
    }

    public void launchUnblockUserConfirmationDialog(final String userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(mActivity.getString(R.string.unblock_user))
                .setMessage(mActivity.getString(R.string.are_you_sure_you_want_to_unblock_this_user))
                .setPositiveButton(mActivity.getString(R.string.yes),
                        (dialog, id) -> {
                            dialog.cancel();
                            unblockUser(userId);
                        })
                .setNegativeButton(mActivity.getString(R.string.no),
                        (dialog, id) -> {
                            dialog.cancel();
                        });
        builder.show();
    }

    public void unblockUser(final String userId) {
        if (mUnblockUserAsync != null) {
            Log.w(TAG, "Unblock request already in progress, new unblock request will be ignored");
            return;
        }

        mUnblockUserAsync = new UnblockUserAsync(mActivity, mGroupId) {
            @Override
            protected void onSuccess(Result<ResponseMessage> result) {
                mSnackbarUtil.showSnackBarWithoutAction(mActivity, R.string.you_have_unblocked_this_user_from_the_group);
                EventBusUtil.getEventBus().post(new LoadUnblockUserSuccessEvent());
                mUnblockUserAsync = null;
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.w(TAG, "Error unblocking user", error);
                mSnackbarUtil.showSnackBarWithAction(mActivity, R.string.unable_to_unblock_user, R.string.retry, snackbar -> {
                    unblockUser(userId);
                    SnackbarManager.dismiss();
                });
                mUnblockUserAsync = null;
            }
        }.executeInParallel(userId);
    }

    public class LoadUnblockUserSuccessEvent {

    }
}
