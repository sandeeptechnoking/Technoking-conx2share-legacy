package com.conx2share.conx2share.strategies;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.async.DeletePostCommentAsync;
import com.conx2share.conx2share.model.Comment;
import com.conx2share.conx2share.model.CommentHolder;
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

public class DeletePostCommentStrategy {

    public static final String TAG = DeletePostStrategy.class.getSimpleName();

    @Inject
    protected SnackbarUtil mSnackbarUtil;

    @Inject
    private PreferencesUtil mPreferencesUtil;

    private DeletePostCommentAsync mDeletePostCommentAsync;

    private Activity mActivity;

    private Comment mComment;

    public DeletePostCommentStrategy(Activity activity, Comment comment) {
        mActivity = activity;
        mComment = comment;
        RoboGuice.injectMembers(activity.getApplicationContext(), this);
    }

    public void launchDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(mActivity.getString(R.string.delete_comment))
                .setMessage(mActivity.getString(R.string.are_you_sure_you_want_to_delete_this_comment))
                .setPositiveButton(mActivity.getString(R.string.yes),
                        (dialog, id) -> {
                            deletePostComment(mComment);
                        })
                .setNegativeButton(mActivity.getString(R.string.no), (dialog, id) -> {
                    dialog.cancel();
                });
        builder.show();
    }

    protected void deletePostComment(final Comment comment) {
        if (mDeletePostCommentAsync != null) {
            Log.w(TAG, "Delete Post in progress, new delete post request ignored");
            // TODO: queue delete comment requests?
            return;
        }

        mDeletePostCommentAsync = new DeletePostCommentAsync(mActivity) {
            @Override
            protected void onSuccess(Result<CommentHolder> result) {
                mSnackbarUtil.showSnackBarWithoutAction(mActivity, R.string.comment_deleted);
                EventBusUtil.getEventBus().post(new LoadDeleteCommentSuccessEvent(comment));
                mDeletePostCommentAsync = null;
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.e(TAG, "Could not delete comment", error);

                mSnackbarUtil.showSnackBarWithAction(mActivity, R.string.unable_to_delete_comment, R.string.retry, snackbar -> {
                    deletePostComment(comment);
                    SnackbarManager.dismiss();
                });
                mDeletePostCommentAsync = null;
            }
        }.executeInParallel(String.valueOf(comment.getId()));
    }

    public class LoadDeleteCommentSuccessEvent {

        private Comment comment;

        public LoadDeleteCommentSuccessEvent(Comment comment) {
            this.comment = comment;
        }

        public Comment getComment() {
            return comment;
        }
    }
}
