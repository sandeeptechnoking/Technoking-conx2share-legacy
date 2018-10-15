package com.conx2share.conx2share.ui.following;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.adapter.FollowingAndFollowersAdapter;
import com.conx2share.conx2share.async.GetFollowingUsersAsync;
import com.conx2share.conx2share.model.Users;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.User;
import com.conx2share.conx2share.ui.base.BaseFragment;
import com.conx2share.conx2share.ui.profile.ProfileActivity;
import com.conx2share.conx2share.util.SnackbarUtil;
import com.nispok.snackbar.SnackbarManager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import javax.inject.Inject;

//import butterknife.InjectView;
import retrofit.RetrofitError;
import roboguice.inject.InjectView;

public class FollowingFragment extends BaseFragment implements FollowingAndFollowersAdapter.FollowingAndFollowersAdapterCallbacks {

    public static final String TAG = FollowingFragment.class.getSimpleName();

    public static final String PROFILEID_KEY = "profileId";

    @InjectView(R.id.following_list_view)
    ListView mFollowingListView;

    @Inject
    SnackbarUtil mSnackbarUtil;

    @InjectView(R.id.following_progress_bar)
    ProgressBar mFollowingProgressBar;

    private ArrayList<User> mFollowingUsers;

    private int mProfileUserId;

    private FollowingAndFollowersAdapter mFollowingAdapter;

    private int mCurrentPageNumber;

    private boolean mAtEndOfSearch;

    private GetFollowingUsersAsync mGetFollowingUsersAsync;

    public static FollowingFragment newInstance() {
        return new FollowingFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_following, container, false);
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

        mFollowingListView.setOnItemClickListener((parent, view1, position, id) -> {
            Adapter adapter = mFollowingListView.getAdapter();
            User user = (User) adapter.getItem(position);

            Intent profileActivityIntent = new Intent(getActivity(), ProfileActivity.class);
            profileActivityIntent.putExtra(PROFILEID_KEY, String.valueOf(user.getId()));
            startActivity(profileActivityIntent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mFollowingListView.setVisibility(View.GONE);
        mFollowingProgressBar.setVisibility(View.VISIBLE);
        getFollowingUsers(mCurrentPageNumber);
    }

    private void collectAndSortUsers(ArrayList<User> newUsers) {
        if (mFollowingUsers == null) {
            mFollowingUsers = new ArrayList<>();
        }

        for (User newUser : newUsers) {
            if (!mFollowingUsers.contains(newUser)) {
                mFollowingUsers.add(newUser);
            }
        }

        setupFollowingAdapter();
    }

    private void setupFollowingAdapter() {
        if (mFollowingListView.getAdapter() == null) {
            mFollowingAdapter = new FollowingAndFollowersAdapter(getActivity(), this, mFollowingUsers);
            mFollowingListView.setAdapter(mFollowingAdapter);
        } else {
            mFollowingAdapter.notifyDataSetChanged();
        }
    }

    protected void getFollowingUsers(int page) {
        if (mGetFollowingUsersAsync != null) {
            Log.w(TAG, "Already getting users who are following, new request will be ignored");
            return;
        }

        mGetFollowingUsersAsync = new GetFollowingUsersAsync(getActivity()) {
            @Override
            protected void onSuccess(Result<Users> result) {
                mFollowingListView.setVisibility(View.VISIBLE);
                mFollowingProgressBar.setVisibility(View.GONE);
                if (result != null && result.getResource() != null && result.getResource().getUsers() != null && result.getResource().getUsers().size() > 0) {
                    collectAndSortUsers(result.getResource().getUsers());
                    if (result.getResource().getUsers().size() < 20) {
                        Log.w(TAG, "Page with less than 20 entries returned, at end of search");
                        mAtEndOfSearch = true;
                    }
                } else {
                    Log.w(TAG, "No following users returned, at end of search");
                    mAtEndOfSearch = true;
                }
                mGetFollowingUsersAsync = null;
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.e(TAG, "Error getting following users", error);
                mFollowingListView.setVisibility(View.VISIBLE);
                mFollowingProgressBar.setVisibility(View.GONE);
                mSnackbarUtil.showSnackBarWithAction(getActivity(), R.string.unable_to_get_following_users, R.string.retry, snackbar -> {
                    getFollowingUsers(mCurrentPageNumber);
                    SnackbarManager.dismiss();
                });
                mGetFollowingUsersAsync = null;
            }
        }.executeInParallel(mProfileUserId, page);
    }

    @Override
    public void onNearingEndOfList() {
        if (!mAtEndOfSearch) {
            Log.d(TAG, "Grabbing page " + (mCurrentPageNumber + 1));
            getFollowingUsers(mCurrentPageNumber++);
        }
    }
}