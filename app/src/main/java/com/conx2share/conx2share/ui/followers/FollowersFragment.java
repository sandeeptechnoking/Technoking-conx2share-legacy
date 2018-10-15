package com.conx2share.conx2share.ui.followers;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.adapter.FollowingAndFollowersAdapter;
import com.conx2share.conx2share.async.GetFollowersAsync;
import com.conx2share.conx2share.model.Users;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.User;
import com.conx2share.conx2share.ui.base.BaseFragment;
import com.conx2share.conx2share.ui.profile.ProfileActivity;
import com.conx2share.conx2share.util.SnackbarUtil;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;

import java.util.ArrayList;

import javax.inject.Inject;

//import butterknife.InjectView;
import retrofit.RetrofitError;
import roboguice.inject.InjectView;

public class FollowersFragment extends BaseFragment implements FollowingAndFollowersAdapter
        .FollowingAndFollowersAdapterCallbacks {

    public static final String TAG = FollowersFragment.class.getSimpleName();

    public static final String PROFILEID_KEY = "profileId";

    @InjectView(R.id.followers_list_view)
    ListView mFollowersListView;

    @Inject
    SnackbarUtil mSnackbarUtil;

    @Inject
    NetworkClient mNetworkClient;

    @InjectView(R.id.followers_progress_bar)
    ProgressBar mFollowersProgressBar;

    private ArrayList<User> mFollowers;

    private int mProfileUserId;

    private GetFollowersAsync mGetFollowersAsync;

    private FollowingAndFollowersAdapter mFollowersAdapter;

    private boolean mAtEndOfSearch;

    private int mCurrentPageNumber;

    public static FollowersFragment newInstance() {
        return new FollowersFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        return inflater.inflate(R.layout.fragment_followers, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity().getIntent().hasExtra(PROFILEID_KEY)) {
            mProfileUserId = getActivity().getIntent().getIntExtra(PROFILEID_KEY, 0);
        } else {
            Log.w(TAG, "Failed to get profile user's ID from extra");
        }

        mCurrentPageNumber = 1;

        mFollowersListView.setOnItemClickListener((parent, view1, position, id) -> {
            Adapter adapter = mFollowersListView.getAdapter();
            User user = (User) adapter.getItem(position);

            Intent profileActivityIntent = new Intent(getActivity(), ProfileActivity.class);
            profileActivityIntent.putExtra(PROFILEID_KEY, String.valueOf(user.getId()));
            startActivity(profileActivityIntent);
        });

        mFollowersListView.setOnItemLongClickListener((parent, view12, position, id) -> {
            showUnfollowDialog(mFollowers.get(position).getUsername(), mFollowers.get(position).getId());
            return true;
        });
    }

    private void showUnfollowDialog(String username, int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(Html.fromHtml(String.format(getString(R.string.unfollow_dialog_text), boldText(username)))).
                setPositiveButton(R.string.unfollow_positive_button, (dialog, which) -> {
                    new UnfollowMeAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(id));
                }).setNegativeButton(R.string.cancel, (dialog, which) -> {
            dialog.dismiss();
        }).
                setOnCancelListener(dialog -> dialog.dismiss());
        builder.show();
    }

    private String boldText(String text) {
        return "<b>" + text + "</b>";
    }

    @Override
    public void onResume() {
        super.onResume();
        mFollowersListView.setVisibility(View.GONE);
        mFollowersProgressBar.setVisibility(View.VISIBLE);
        getFollowers(mCurrentPageNumber);
    }

    private void collectAndSortUsers(ArrayList<User> newUsers) {
        if (mFollowers == null) {
            mFollowers = new ArrayList<>();
        }

        for (User newUser : newUsers) {
            if (!mFollowers.contains(newUser)) {
                mFollowers.add(newUser);
            }
        }

        setupFollowersAdapter();
    }

    public void setupFollowersAdapter() {
        if (mFollowersListView.getAdapter() == null) {
            mFollowersAdapter = new FollowingAndFollowersAdapter(getActivity(), this, mFollowers);
            mFollowersListView.setAdapter(mFollowersAdapter);
        } else {
            mFollowersAdapter.notifyDataSetChanged();
        }
    }

    protected void getFollowers(int page) {
        if (mGetFollowersAsync != null) {
            Log.w(TAG, "Request to get followers already in progress, new request will be ignored");
            return;
        }

        mGetFollowersAsync = new GetFollowersAsync(getActivity()) {
            @Override
            protected void onSuccess(Result<Users> result) {
                mFollowersListView.setVisibility(View.VISIBLE);
                mFollowersProgressBar.setVisibility(View.GONE);
                if (result != null && result.getResource() != null && result.getResource().getUsers() != null &&
                        result.getResource().getUsers().size() > 0) {
                    collectAndSortUsers(result.getResource().getUsers());
                    if (result.getResource().getUsers().size() < 20) {
                        Log.w(TAG, "Page with less than 20 entries returned, at end of search");
                        mAtEndOfSearch = true;
                    }
                } else {
                    Log.w(TAG, "No followers returned, at end of search");
                    mAtEndOfSearch = true;
                }
                mGetFollowersAsync = null;
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.e(TAG, "Error getting followers", error);
                mFollowersListView.setVisibility(View.VISIBLE);
                mFollowersProgressBar.setVisibility(View.GONE);
                mSnackbarUtil.showSnackBarWithAction(getActivity(), R.string.unable_to_get_followers, R.string.retry,
                        snackbar -> {
                    getFollowers(mCurrentPageNumber);
                    SnackbarManager.dismiss();
                });
                mGetFollowersAsync = null;
            }
        }.executeInParallel(mProfileUserId, page);
    }

    @Override
    public void onNearingEndOfList() {
        if (!mAtEndOfSearch) {
            Log.d(TAG, "Grabbing page " + (mCurrentPageNumber + 1));
            getFollowers(mCurrentPageNumber++);
        }
    }

    private class UnfollowMeAsync extends AsyncTask<String, Void, Boolean> {

        String userId;

        @Override
        protected Boolean doInBackground(String... params) {
            userId = params[0];
            return mNetworkClient.unfollowMe(params[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mFollowersProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (getActivity() != null) {
                mFollowersProgressBar.setVisibility(View.GONE);
                if (aBoolean) {
                    mCurrentPageNumber = 1;
                    mFollowers.clear();
                    getFollowers(mCurrentPageNumber);
                } else {
                    SnackbarManager.show(Snackbar.with(getActivity().getApplicationContext()).
                            type(SnackbarType.MULTI_LINE).
                            text(getString(R.string.blurbGenericErrorMessage)).
                            actionLabel(getString(R.string.retry)).
                            actionListener(snackbar -> {
                                SnackbarManager.dismiss();
                                new UnfollowMeAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String
                                        .valueOf(userId));
                            }), getActivity());
                }
            }
        }
    }
}