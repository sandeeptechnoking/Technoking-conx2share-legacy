package com.conx2share.conx2share.strategies;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.async.FlagPostAsync;
import com.conx2share.conx2share.model.Flag;
import com.conx2share.conx2share.model.FlagHolder;
import com.conx2share.conx2share.model.Post;
import com.conx2share.conx2share.model.ResponseMessage;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.util.PreferencesUtil;
import com.conx2share.conx2share.util.SnackbarUtil;
import com.nispok.snackbar.SnackbarManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;

import javax.inject.Inject;

import retrofit.RetrofitError;
import roboguice.RoboGuice;

public class FlagPostStrategy {

    public static final String TAG = FlagPostStrategy.class.getSimpleName();

    @Inject
    protected SnackbarUtil mSnackbarUtil;

    @Inject
    private PreferencesUtil mPreferencesUtil;

    private FlagPostAsync mFlagPostAsync;

    private Activity mActivity;

    private Post mPost;

    public FlagPostStrategy(Activity activity, Post post) {
        mActivity = activity;
        mPost = post;
        RoboGuice.injectMembers(activity.getApplicationContext(), this);
    }

    public void launchFlagDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(mActivity.getString(R.string.flag_post));
        builder.setMessage(mActivity.getString(R.string.flag_content_as_inappropriate_prompt));
        builder
                .setPositiveButton(mActivity.getString(R.string.yes), (dialog, id) -> {
                    flagPost(mPost);
                    dialog.cancel();
                })
                .setNegativeButton(mActivity.getString(R.string.no),
                        (dialog, id) -> {
                            dialog.cancel();
                        });
        builder.show();
    }

    private void launchErrorAlreadyFlaggedPost() {
        mSnackbarUtil.showSnackBarWithoutAction(mActivity, R.string.post_already_flagged);
    }

    private void launchErrorUnableToFlagPost(final Post post) {
        mSnackbarUtil.showSnackBarWithAction(mActivity, R.string.unable_to_flag_post_text,
                R.string.retry, snackbar -> {
                    flagPost(post);
                    SnackbarManager.dismiss();
                });
    }

    protected void flagPost(final Post post) {
        if (mFlagPostAsync != null) {
            Log.w(TAG, "Flag in progress, new flag request ignored");
            // TODO: queue flag requests?
            return;
        }

        mFlagPostAsync = new FlagPostAsync(mActivity) {
            @Override
            protected void onSuccess(Result<ResponseMessage> result) {
                mSnackbarUtil.showSnackBarWithoutAction(mActivity, R.string.post_flagged);
                mFlagPostAsync = null;
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.e(TAG, "Could not flag post", error);
                if (error.getResponse() != null && error.getResponse().getStatus() == 400) {
                    launchErrorAlreadyFlaggedPost();
                } else {
                    launchErrorUnableToFlagPost(post);
                }
                mFlagPostAsync = null;
            }
        }.executeInParallel(new FlagHolder(new Flag(String.valueOf(mPreferencesUtil.getAuthUser().getId()), String.valueOf(post.getId()))));
    }
}
