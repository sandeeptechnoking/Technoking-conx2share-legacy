package com.conx2share.conx2share.strategies;


import com.conx2share.conx2share.R;
import com.conx2share.conx2share.async.BlockUserAsync;
import com.conx2share.conx2share.model.ResponseMessage;
import com.conx2share.conx2share.model.UserIdWrapper;
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

public class BlockUserStrategy {

    public static final String TAG = BlockUserStrategy.class.getSimpleName();

    @Inject
    protected SnackbarUtil mSnackbarUtil;

    private Activity mActivity;

    private BlockUserAsync mBlockUserAsync;

    private int mGroupId;

    public BlockUserStrategy(Activity activity, int groupId) {
        mActivity = activity;
        mGroupId = groupId;
        RoboGuice.injectMembers(activity.getApplicationContext(), this);
    }

    public void launchBlockUserConfirmationDialog(final UserIdWrapper userIdWrapper) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(mActivity.getString(R.string.block_user))
                .setMessage(mActivity.getString(R.string.are_you_sure_you_want_to_block_this_user))
                .setPositiveButton(mActivity.getString(R.string.yes),
                        (dialog, id) -> {
                            dialog.cancel();
                            blockUser(userIdWrapper);
                        })
                .setNegativeButton(mActivity.getString(R.string.no),
                        (dialog, id) -> {
                            dialog.cancel();
                        });
        builder.show();
    }

    public void blockUser(final UserIdWrapper userIdWrapper) {
        if (mBlockUserAsync != null) {
            Log.w(TAG, "Block user request already in progress, new block user request will be ignored");
            return;
        }

        mBlockUserAsync = new BlockUserAsync(mActivity, mGroupId) {
            @Override
            protected void onSuccess(Result<ResponseMessage> result) {
                mSnackbarUtil.showSnackBarWithoutAction(mActivity, R.string.you_have_blocked_this_user_from_the_group);
                EventBusUtil.getEventBus().post(new LoadBlockUserSuccessEvent());
                mBlockUserAsync = null;
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.e(TAG, "Could not block user", error);

                mSnackbarUtil.showSnackBarWithAction(mActivity, R.string.unable_to_block_user_from_group, R.string.retry, snackbar -> {
                    blockUser(userIdWrapper);
                    SnackbarManager.dismiss();
                });
                mBlockUserAsync = null;
            }
        }.executeInParallel(userIdWrapper);
    }

    public class LoadBlockUserSuccessEvent {

    }
}