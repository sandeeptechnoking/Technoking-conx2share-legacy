package com.conx2share.conx2share.strategies;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.async.GetBusinessAsyncTask;
import com.conx2share.conx2share.async.GetGroupAsync;
import com.conx2share.conx2share.model.AuthUser;
import com.conx2share.conx2share.model.Business;
import com.conx2share.conx2share.model.Group;
import com.conx2share.conx2share.model.GroupResponse;
import com.conx2share.conx2share.model.Post;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.response.BusinessResponse;
import com.conx2share.conx2share.ui.feed.post.PostActivity;
import com.conx2share.conx2share.ui.feed.post.PostFragment;
import com.conx2share.conx2share.util.PreferencesUtil;
import com.conx2share.conx2share.util.SnackbarUtil;
import com.nispok.snackbar.SnackbarManager;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import javax.inject.Inject;

import retrofit.RetrofitError;
import roboguice.RoboGuice;


public class EditPostStrategy {

    public static final String TAG = EditPostStrategy.class.getSimpleName();

    @Inject
    protected SnackbarUtil mSnackbarUtil;

    @Inject
    private PreferencesUtil mPreferencesUtil;

    private Activity mActivity;

    private Post mPost;

    private Business mBusiness;

    private Group mGroup;

    private GetBusinessAsyncTask mGetBusinessAsyncTask;

    private GetGroupAsync mGetGroupAsync;

    public EditPostStrategy(Activity activity, Post post) {
        mActivity = activity;
        mPost = post;
        RoboGuice.injectMembers(activity.getApplicationContext(), this);
    }

    public void checkIfUserAllowedToEdit() {
        AuthUser user = mPreferencesUtil.getAuthUser();

        if (user != null && user.getId() != null && user.getId().equals(mPost.getUserId())) {
            determineHowToLaunchActivity();
        } else {
            launchYouDontOwnThisPostSnackbar();
        }
    }

    private void launchYouDontOwnThisPostSnackbar() {
        mSnackbarUtil.showSnackBarWithoutAction(mActivity, R.string.you_dont_own_this_post_to_edit_it);
    }

    private void determineHowToLaunchActivity() {
        if (mPost.getBusinessName() != null) {
            getBusiness();
        } else if (mPost.getGroupName() != null) {
            getGroup();
        } else {
            launchPostFragmentWithPostExtra();
        }
    }

    protected void getBusiness() {
        if (mGetBusinessAsyncTask != null) {
            Log.w(TAG, "Get business in progress, new get business request ignored");
            // TODO: queue get business requests?
            return;
        }

        mGetBusinessAsyncTask = new GetBusinessAsyncTask(mActivity, mPost.getBusinessId()) {
            @Override
            protected void onSuccess(Result<BusinessResponse> result) {
                mBusiness = result.getResource().getBusiness();
                launchPostFragmentWithPostAndBusinessExtra();
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.e(TAG, "Could not delete post", error);

                mSnackbarUtil.showSnackBarWithAction(mActivity, R.string.unable_to_get_business_info, R.string.retry, snackbar -> {
                    getBusiness();
                    SnackbarManager.dismiss();
                });
                mGetBusinessAsyncTask = null;
            }
        }.executeInParallel();
    }

    protected void getGroup() {
        if (mGetGroupAsync != null) {
            Log.w(TAG, "Get group in progress, new get business request ignored");
            // TODO: queue get group requests?
            return;
        }

        mGetGroupAsync = new GetGroupAsync(mActivity) {
            @Override
            protected void onSuccess(Result<GroupResponse> result) {
                mGroup = result.getResource().getGroup();
                launchPostFragmentWithPostAndGroupExtra();
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.e(TAG, "Could not get group", error);

                mSnackbarUtil.showSnackBarWithAction(mActivity, R.string.unable_to_get_group_text, R.string.retry, snackbar -> {
                    getGroup();
                    SnackbarManager.dismiss();
                });
                mGetGroupAsync = null;
            }
        }.executeInParallel(mPost.getGroupId());
    }

    private void launchPostFragmentWithPostExtra() {
        Intent postActivityIntent = new Intent(mActivity, PostActivity.class);
        postActivityIntent.putExtra(PostFragment.EXTRA_POST, mPost);
        mActivity.startActivity(postActivityIntent);
    }

    private void launchPostFragmentWithPostAndGroupExtra() {
        Intent postActivityIntent = new Intent(mActivity, PostActivity.class);
        postActivityIntent.putExtra(PostFragment.EXTRA_POST, mPost);
        postActivityIntent.putExtra(Group.EXTRA, mGroup);
        mActivity.startActivity(postActivityIntent);
    }

    private void launchPostFragmentWithPostAndBusinessExtra() {
        Intent postActivityIntent = new Intent(mActivity, PostActivity.class);
        postActivityIntent.putExtra(PostFragment.EXTRA_POST, mPost);
        postActivityIntent.putExtra(Business.EXTRA, mBusiness);
        mActivity.startActivity(postActivityIntent);
    }
}
