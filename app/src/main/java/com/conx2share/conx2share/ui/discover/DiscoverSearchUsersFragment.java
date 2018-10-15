package com.conx2share.conx2share.ui.discover;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.adapter.DiscoverUsersAdapter;
import com.conx2share.conx2share.model.Post;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.network.models.User;
import com.conx2share.conx2share.ui.base.BaseFragment;
import com.conx2share.conx2share.ui.feed.post_comments.PostCommentsActivity;
import com.conx2share.conx2share.ui.feed.post_comments.PostCommentsFragment;

import java.util.ArrayList;

import javax.inject.Inject;

import roboguice.inject.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DiscoverSearchUsersFragment extends BaseFragment implements DiscoverUsersAdapter.DiscoverUsersAdapterCallbacks {

    public static final String TAG = DiscoverSearchUsersFragment.class.getSimpleName();

    @InjectView(R.id.discover_users_list_view)
    RecyclerView mDiscoverUsersListView;

    @InjectView(R.id.discover_users_progress_bar)
    ProgressBar mDiscoverUserProgressBar;

    private Integer mCurrentUserSearchPage;
    private String mSearchTerms;

    private ArrayList<User> mUsers;
    private DiscoverUsersAdapter mDiscoverUsersAdapter;

    private boolean mAtEndOfSearch;

    @Inject
    NetworkClient networkClient;

    public static Fragment newInstance() {
        return new DiscoverSearchUsersFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discover_search_users, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCurrentUserSearchPage = 1;
        mUsers = new ArrayList<>();
        searchUsers(null, mCurrentUserSearchPage);
    }

    public void onEventMainThread(DiscoverFragment.LoadDiscoverSearchEvent event) {
        mDiscoverUserProgressBar.setVisibility(View.VISIBLE);
        mDiscoverUsersListView.setVisibility(View.GONE);
        mSearchTerms = event.getSearchTerms();
        Log.d(TAG, "Received a discover search event. Search terms: " + mSearchTerms);
        if (mUsers != null) {
            mUsers.clear();
        }
        mAtEndOfSearch = false;
        setupUsersListViewAdapter();
        mCurrentUserSearchPage = 1;
        if (getActivity() != null) {
            searchUsers(mSearchTerms, mCurrentUserSearchPage);
        }
    }

    private void collectAndSortUsers(ArrayList<User> newUsers) {
        for (User newUser : newUsers) {
            if (!mUsers.contains(newUser)) {
                mUsers.add(newUser);
            }
        }

        setupUsersListViewAdapter();
    }

    private void setupUsersListViewAdapter() {
        if (mDiscoverUsersListView.getAdapter() == null) {
            mDiscoverUsersAdapter = new DiscoverUsersAdapter(this, mUsers);
            mDiscoverUsersListView.setAdapter(mDiscoverUsersAdapter);
        } else {
            mDiscoverUsersAdapter.notifyDataSetChanged();
        }
    }

    protected void searchUsers(String query, int page) {

        addSubscription(networkClient.searchUsers(query, page, "posts")
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(() -> {
                    mDiscoverUserProgressBar.setVisibility(View.GONE);
                    mDiscoverUsersListView.setVisibility(View.VISIBLE);
                })
                .subscribe(users -> {
                            if (users != null && users.getUsers() != null
                                    && users.getUsers().size() > 0) {
                                collectAndSortUsers(users.getUsers());
                                if (users.getUsers().size() < 20) {
                                    Log.w(TAG, "A page with less than 20 entries was returned, at end of search");
                                    mAtEndOfSearch = true;
                                }
                            } else {
                                mAtEndOfSearch = true;
                                Log.w(TAG, "No users returned in search, at end of search");
                            }
                        },
                        throwable -> {
                            Log.e(TAG, "Searching for users failed", throwable);
                            mCurrentUserSearchPage--;
                        }));
    }

    @Override
    public void onNearingEndOfList() {
        if (!mAtEndOfSearch) {
            Log.d(TAG, "Grabbing page " + (mCurrentUserSearchPage + 1));
            searchUsers(mSearchTerms, mCurrentUserSearchPage++);
        }
    }

    @Override
    public void onFollowUserClick(User user) {
        addSubscription(networkClient.followUserRequest(String.valueOf(user.getId()), true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseMessage -> {
                    mSnackbarUtil.displaySnackBar(getActivity(), getString(R.string.invite_was_sent));
                }, throwable -> {
                    mSnackbarUtil.displaySnackBar(getActivity(), throwable.getMessage());
                }));
    }

    @Override
    public void onPostClick(Post post) {
        getActivity().startActivity(new Intent(getActivity(), PostCommentsActivity.class)
                .putExtra(PostCommentsFragment.EXTRA_POST_ID, String.valueOf(post.getId())));
    }

}