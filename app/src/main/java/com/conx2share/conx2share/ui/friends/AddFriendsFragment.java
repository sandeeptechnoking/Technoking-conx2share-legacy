package com.conx2share.conx2share.ui.friends;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.adapter.UsersAdapter;
import com.conx2share.conx2share.model.Users;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.User;
import com.conx2share.conx2share.network.models.param.SearchParams;
import com.conx2share.conx2share.ui.base.BaseFragment;
import com.conx2share.conx2share.ui.profile.ProfileActivity;
import com.conx2share.conx2share.util.PreferencesUtil;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import javax.inject.Inject;

//import butterknife.InjectView;
import retrofit.RetrofitError;
import roboguice.inject.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class AddFriendsFragment extends BaseFragment implements UsersAdapter.UserAdapterCallbacks {

    public static final String TAG = AddFriendsFragment.class.getSimpleName();

    @InjectView(R.id.search_for_friends_edit_text)
    EditText mSearchForFriendsEditText;

    @InjectView(R.id.search_users_listview)
    ListView mSearchUsersListView;

    @Inject
    NetworkClient mNetworkClient;

    @Inject
    PreferencesUtil mPreferencesUtil;

    private Integer mCurrentUserSearchPage;

    private String mSearchTerms;

    private ArrayList<User> mUsers;

    private UsersAdapter mUsersAdapter;

    private boolean mAtEndOfSearch;

    public static AddFriendsFragment newInstance() {
        return new AddFriendsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_friends, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSearchUsersListView.setOnItemClickListener((parent, view1, position, id) -> {
            Adapter adapter = mSearchUsersListView.getAdapter();
            User user = (User) adapter.getItem(position);
            Log.d(TAG, "user.getId: " + user.getId());
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            intent.putExtra("profileId", String.valueOf(user.getId()));
            startActivity(intent);
        });

        mSearchForFriendsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // NO OP
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // NO OP
            }

            @Override
            public void afterTextChanged(Editable s) {
                mSearchTerms = s.toString();
                if (mUsers != null) {
                    mUsers.clear();
                } else {
                    mUsers = new ArrayList<>();
                }
                mAtEndOfSearch = false;
                setupUsersListViewAdapter();
                mCurrentUserSearchPage = 1;
                if (getActivity() != null) {
                    searchUsers(mSearchTerms, mCurrentUserSearchPage);
                }
            }
        });
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
        if (mSearchUsersListView.getAdapter() == null) {
            mUsersAdapter = new UsersAdapter(getActivity(), this, mNetworkClient, new Users(mUsers), mPreferencesUtil.getAuthUser().getId());
            mSearchUsersListView.setAdapter(mUsersAdapter);
        } else {
            mUsersAdapter.notifyDataSetChanged();
        }
    }

    private void searchUsers(String query, int page) {

        addSubscription(mNetworkClient.searchUsers(query, page, null)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(users -> {
                            if (users != null && users.getUsers() != null
                                    && users.getUsers().size() > 0) {
                                collectAndSortUsers(users.getUsers());
                                if (users.getUsers().size() < 20) {
                                    Log.w(TAG, "Page with less than 20 entries returned, at end of search");
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
}