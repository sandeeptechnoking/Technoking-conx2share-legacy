package com.conx2share.conx2share.strategies;


import com.conx2share.conx2share.R;
import com.conx2share.conx2share.async.EditPostCommentAsync;
import com.conx2share.conx2share.model.AuthUser;
import com.conx2share.conx2share.model.Comment;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.response.GetPostCommentsResponse;
import com.conx2share.conx2share.ui.feed.post_comments.EditPostCommentActivity;
import com.conx2share.conx2share.ui.feed.post_comments.EditPostCommentFragment;
import com.conx2share.conx2share.util.EventBusUtil;
import com.conx2share.conx2share.util.PreferencesUtil;
import com.conx2share.conx2share.util.SnackbarUtil;
import com.nispok.snackbar.SnackbarManager;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import javax.inject.Inject;

import retrofit.RetrofitError;
import roboguice.RoboGuice;

public class EditPostCommentStrategy {

    public static final String TAG = EditPostCommentStrategy.class.getSimpleName();

    @Inject
    protected SnackbarUtil mSnackbarUtil;

    @Inject
    private PreferencesUtil mPreferencesUtil;

    private Activity mActivity;

    private Comment mComment;

    private EditPostCommentAsync mEditPostCommentAsync;

    public EditPostCommentStrategy(Activity activity, Comment initialComment) {
        mActivity = activity;
        mComment = initialComment;
        RoboGuice.injectMembers(activity.getApplicationContext(), this);
    }

    public void checkIfUserAllowedToEdit() {
        AuthUser user = mPreferencesUtil.getAuthUser();

        if (user != null && user.getId() != null && user.getId().equals(mComment.getCommenterId())) {
            launchEditPostCommentActivity();
        } else {
            launchYouDontOwnThisCommentSnackbar();
        }
    }

    private void launchEditPostCommentActivity() {
        Intent editPostCommentActivityIntent = new Intent(mActivity, EditPostCommentActivity.class);
        editPostCommentActivityIntent.putExtra(EditPostCommentFragment.EXTRA_COMMENT, NetworkClient.getGson().toJson(mComment, Comment.class));
        mActivity.startActivity(editPostCommentActivityIntent);
    }

    private void launchYouDontOwnThisCommentSnackbar() {
        mSnackbarUtil.showSnackBarWithoutAction(mActivity, R.string.you_dont_own_this_comment_to_edit_it);
    }

    public void editPostComment(final Comment updatedComment) {
        if (mEditPostCommentAsync != null) {
            Log.w(TAG, "Edit post comment in progress, new edit post comment request ignored");
            // TODO: queue edit post comment requests?
            return;
        }

        mEditPostCommentAsync = new EditPostCommentAsync(mActivity, String.valueOf(updatedComment.getId())) {
            @Override
            protected void onSuccess(Result<GetPostCommentsResponse> result) {
                EventBusUtil.getEventBus().post(new LoadEditCommentSuccessEvent(updatedComment));
                mEditPostCommentAsync = null;
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.e(TAG, "Could not update post comment", error);
                if (error.getResponse() != null && error.getResponse().getStatus() == 403) {
                    mSnackbarUtil.showSnackBarWithoutAction(mActivity, R.string.unable_to_update_comment_blocked);
                } else {
                    mSnackbarUtil.showSnackBarWithAction(mActivity, R.string.unable_to_update_post_comment, R.string.retry, snackbar -> {
                        editPostComment(updatedComment);
                        SnackbarManager.dismiss();
                    });
                }
                EventBusUtil.getEventBus().post(new LoadEditCommentFailureEvent(updatedComment));
                mEditPostCommentAsync = null;
            }
        }.executeInParallel(updatedComment);
    }

    public class LoadEditCommentSuccessEvent {

        private Comment comment;

        public LoadEditCommentSuccessEvent(Comment comment) {
            this.comment = comment;
        }

        public Comment getComment() {
            return comment;
        }
    }

    public class LoadEditCommentFailureEvent {

        private Comment comment;

        public LoadEditCommentFailureEvent(Comment comment) {
            this.comment = comment;
        }

        public Comment getComment() {
            return comment;
        }
    }
}
