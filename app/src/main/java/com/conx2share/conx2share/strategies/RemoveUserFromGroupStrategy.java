package com.conx2share.conx2share.strategies;


import com.conx2share.conx2share.R;
import com.conx2share.conx2share.async.BlockUserAsync;
import com.conx2share.conx2share.async.RemoveUserFromGroupAsync;
import com.conx2share.conx2share.model.Group;
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

public class RemoveUserFromGroupStrategy {

    public static final String TAG = RemoveUserFromGroupStrategy.class.getSimpleName();

    @Inject
    protected SnackbarUtil mSnackbarUtil;

    private Activity mActivity;

    private RemoveUserFromGroupAsync mRemoveUserFromGroupAsync;

    private int mUserId;

    private Group mGroup;

    private BlockUserAsync mBlockUserAsync;

    public RemoveUserFromGroupStrategy(Activity activity, int userId, Group group) {
        mUserId = userId;
        mActivity = activity;
        mGroup = group;
        RoboGuice.injectMembers(activity.getApplicationContext(), this);
    }

    public void launchRemoveUserFromGroupConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(mActivity.getString(R.string.remove_user_from_group))
                .setMessage(mActivity.getString(R.string.are_you_sure_you_want_to_remove_this_user_from_the_group))
                .setPositiveButton(mActivity.getString(R.string.yes),
                        (dialog, id) -> {
                            dialog.cancel();
                            removeUserFromGroup();
                        })
                .setNegativeButton(mActivity.getString(R.string.no), (dialog, id) -> {
                    dialog.cancel();
                });
        builder.show();
    }

    protected void removeUserFromGroup() {
        if (mRemoveUserFromGroupAsync != null) {
            Log.w(TAG, "Request to remove user from group already iin progress, new request will be ignored");
            return;
        }

        mRemoveUserFromGroupAsync = new RemoveUserFromGroupAsync(mActivity, mGroup.getId()) {

            @Override
            protected void onSuccess(Result<ResponseMessage> result) {
                mSnackbarUtil.showSnackBarWithoutAction(mActivity, R.string.user_has_been_removed_from_group);
                EventBusUtil.getEventBus().post(new LoadRemoveUserFromGroupSuccessEvent(mGroup.getId(), mUserId));
                if (mGroup.getGroupType() == Group.DISCUSSION_KEY) {
                    Log.d(TAG, "Group was a discussion group, also need to send a block request");
                    UserIdWrapper userIdWrapper = new UserIdWrapper(mUserId);
                    blockUser(userIdWrapper);
                }
                mRemoveUserFromGroupAsync = null;
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.e(TAG, "Error while trying to remove user from group", error);

                mSnackbarUtil.showSnackBarWithAction(mActivity, R.string.unable_to_remove_user_from_group, R.string.retry, snackbar -> {
                    removeUserFromGroup();
                    SnackbarManager.dismiss();
                });

                mRemoveUserFromGroupAsync = null;
            }

        }.executeInParallel(String.valueOf(mUserId));
    }

    public void blockUser(final UserIdWrapper userIdWrapper) {
        if (mBlockUserAsync != null) {
            Log.w(TAG, "Block user request already in progress, new block user request will be ignored");
            return;
        }

        mBlockUserAsync = new BlockUserAsync(mActivity, mGroup.getId()) {
            @Override
            protected void onSuccess(Result<ResponseMessage> result) {
                mBlockUserAsync = null;
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.e(TAG, "Could not block user", error);
                mBlockUserAsync = null;
            }
        }.executeInParallel(userIdWrapper);
    }

    public class LoadRemoveUserFromGroupSuccessEvent {

        private int userId;

        private int groupId;

        public LoadRemoveUserFromGroupSuccessEvent(int groupId, int userId) {
            this.userId = userId;
            this.groupId = groupId;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getGroupId() {
            return groupId;
        }

        public void setGroupId(int groupId) {
            this.groupId = groupId;
        }
    }
}
