package com.conx2share.conx2share.strategies;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.async.DeletePostAsync;
import com.conx2share.conx2share.model.Post;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.response.GetPostsResponse;
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

public class DeletePostStrategy {

    public static final String TAG = DeletePostStrategy.class.getSimpleName();

    @Inject
    protected SnackbarUtil mSnackbarUtil;

    @Inject
    private PreferencesUtil mPreferencesUtil;

    private DeletePostAsync mDeletePostAsync;

    private Activity mActivity;

    private Post mPost;

    public DeletePostStrategy(Activity activity, Post post) {
        mActivity = activity;
        mPost = post;
        RoboGuice.injectMembers(activity.getApplicationContext(), this);
    }

    public void launchDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(mActivity.getString(R.string.delete_post));
        builder.setMessage(mActivity.getString(R.string.are_you_sure_you_want_to_delete_this_post));
        builder
                .setPositiveButton(mActivity.getString(R.string.yes),
                        (dialog, id) -> {
                            deletePost(mPost);
                        })
                .setNegativeButton(mActivity.getString(R.string.no), (dialog, id) -> {
                    dialog.cancel();
                });
        builder.show();
    }

    protected void deletePost(final Post post) {
        if (mDeletePostAsync != null) {
            Log.w(TAG, "Delete Post in progress, new delete post request ignored");
            // TODO: queue delete post requests?
            return;
        }

        mDeletePostAsync = new DeletePostAsync(mActivity) {
            @Override
            protected void onSuccess(Result<GetPostsResponse> result) {
                mSnackbarUtil.showSnackBarWithoutAction(mActivity, R.string.post_deleted);
                EventBusUtil.getEventBus().post(new LoadDeletePostSuccessEvent(post));
                mDeletePostAsync = null;
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.e(TAG, "Could not delete post", error);

                mSnackbarUtil.showSnackBarWithAction(mActivity, R.string.unable_to_delete_post, R.string.retry, snackbar -> {
                    deletePost(post);
                    SnackbarManager.dismiss();
                });
                mDeletePostAsync = null;
            }
        }.executeInParallel(String.valueOf(post.getId()));
    }

    public class LoadDeletePostSuccessEvent {

        private Post post;

        public LoadDeletePostSuccessEvent(Post post) {
            this.post = post;
        }

        public Post getPost() {
            return post;
        }
    }
}
