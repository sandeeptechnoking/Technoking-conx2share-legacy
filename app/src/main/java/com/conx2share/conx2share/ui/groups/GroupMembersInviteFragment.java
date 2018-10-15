package com.conx2share.conx2share.ui.groups;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.adapter.GroupMembersInviteAdapter;
import com.conx2share.conx2share.async.FollowSearchAsync;
import com.conx2share.conx2share.model.Users;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.User;
import com.conx2share.conx2share.network.models.param.GroupInviteParams;
import com.conx2share.conx2share.ui.base.BaseFragment;
import com.conx2share.conx2share.ui.profile.ProfileActivity;
import com.conx2share.conx2share.util.PreferencesUtil;
import com.conx2share.conx2share.util.SnackbarUtil;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

import javax.inject.Inject;

//import butterknife.InjectView;
import retrofit.RetrofitError;
import roboguice.inject.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GroupMembersInviteFragment extends BaseFragment implements GroupMembersInviteAdapter.GroupMembersInviteAdapterCallbacks {

    public static final String TAG = GroupMembersInviteFragment.class.getSimpleName();

    @Inject
    NetworkClient mNetworkClient;

    @InjectView(R.id.user_search_toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.group_members_invite_edit_text)
    EditText mGroupMembersInviteEditText;

    @InjectView(R.id.group_members_invite_listview)
    ListView mGroupMembersListView;

    @Inject
    SnackbarUtil mSnackbarUtil;

    @Inject
    PreferencesUtil mPreferencesUtil;

    private ArrayList<User> mUsersFromSearch;

    private int mGroupId;

    private GroupMembersInviteAdapter mGroupMembersInviteAdapter;

    private FollowSearchAsync mFollowSearchAsync;

    private int mUserPosition;

    private GroupInviteParams mGroupInviteParams;

    public static GroupMembersInviteFragment newInstance() {
        return new GroupMembersInviteFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGroupId = getArguments().getInt("GroupId");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_members_invite, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() != null) {
            ImageButton groupMembersBackButton = (ImageButton) getActivity().findViewById(R.id.group_members_back_button);

            if (groupMembersBackButton != null) {
                groupMembersBackButton.setOnClickListener(v -> {
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                });
            }
        }

        mGroupMembersInviteEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchForUsers();
            }
        });
    }

    // TODO - Maybe use the User object instead of the position...the only crucial part the position is being used for is to set the group status upon completion of the Async task, the reset we know
    @Override
    public void onInviteCheckBoxClicked(int position) {
        if (mUsersFromSearch.size() > 0) {
            mGroupInviteParams = new GroupInviteParams(mGroupId, mUsersFromSearch.get(position).getId());
            mUserPosition = position;
            if (mUsersFromSearch.get(position).getGroupStatus() != null) {
                Log.d(TAG, mUsersFromSearch.get(position).getFirstName() + " " + mUsersFromSearch.get(position).getLastName() + " has a group status of " + mUsersFromSearch.get(position)
                        .getGroupStatus());
                if (!mUsersFromSearch.get(position).getGroupStatus().equals("pending") && !mUsersFromSearch.get(position).getGroupStatus().equals("accepted")) {
                    inviteUserToGroupAsyncTask(mGroupInviteParams);
                } else {
                    Log.d(TAG, "In a pending or accepted status, not sending invite");
                }
            }
        } else {
            Log.w(TAG, "No users from search");
        }
    }

    // TODO - Make this take into account pagination (GroupFollowersInviteFragment uses very similar logic)
    protected void searchForUsers() {

        mFollowSearchAsync = new FollowSearchAsync(getActivity()) {
            @Override
            protected void onSuccess(Result<Users> result) {
                if (mGroupMembersInviteEditText.getText().length() > 0) {
                    mUsersFromSearch = result.getResource().getUsers();
                    setupListView();
                } else {
                    mUsersFromSearch = new ArrayList<>();
                    setupListView();
                }
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.d(TAG, "Error searching for users", error);
            }

        }.executeInParallel(mGroupMembersInviteEditText.getText().toString(), String.valueOf(mGroupId), "1");
    }

    private void setupListView() {
        mGroupMembersInviteAdapter = new GroupMembersInviteAdapter(getActivity(), this, mUsersFromSearch, mPreferencesUtil.getAuthUser().getId());
        mGroupMembersListView.setAdapter(mGroupMembersInviteAdapter);
        mGroupMembersListView.setOnItemClickListener((parent, view, position, id) -> {
            Adapter adapter = mGroupMembersListView.getAdapter();
            User user = (User) adapter.getItem(position);
            Log.d(TAG, "user.getId: " + user.getId());
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            intent.putExtra("profileId", String.valueOf(user.getId()));
            startActivity(intent);
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mGroupMembersInviteEditText.getWindowToken(), 0);
    }

    private void inviteUserToGroupAsyncTask(GroupInviteParams params) {

        addSubscription(mNetworkClient.inviteUserToGroup(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(groupInviteResponse -> {
                    Log.d(TAG, "Invite user success");
                    mUsersFromSearch.get(mUserPosition).setGroupStatus("pending");
                    hideKeyboard();
                    setupListView();
                }, throwable -> {
                    hideKeyboard();
                    mUsersFromSearch.get(mUserPosition).setGroupStatus("not invited");
                    mGroupMembersInviteAdapter.notifyDataSetChanged();
                    Log.d(TAG, "error inviting user to group");
                    SnackbarManager.show(
                            Snackbar.with(getActivity().getApplicationContext())
                                    .type(SnackbarType.MULTI_LINE)
                                    .text(getString(R.string.unable_to_invite_user_to_group_text))
                                    .actionLabel(getString(R.string.retry))
                                    .actionListener(snackbar -> {
                                        inviteUserToGroupAsyncTask(mGroupInviteParams);
                                        SnackbarManager.dismiss();
                                    })
                            , getActivity());
                    setupListView();

                }));
    }
}
