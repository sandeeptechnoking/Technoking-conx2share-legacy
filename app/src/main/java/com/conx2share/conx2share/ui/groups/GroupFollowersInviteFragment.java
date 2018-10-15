package com.conx2share.conx2share.ui.groups;


import com.conx2share.conx2share.R;
import com.conx2share.conx2share.adapter.GroupFollowersInviteAdapter;
import com.conx2share.conx2share.async.FollowSearchAsync;
import com.conx2share.conx2share.async.InviteToFollowGroupAsync;
import com.conx2share.conx2share.model.ResponseMessage;
import com.conx2share.conx2share.model.UserIdWrapper;
import com.conx2share.conx2share.model.Users;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.User;
import com.conx2share.conx2share.ui.base.BaseFragment;
import com.conx2share.conx2share.ui.profile.ProfileActivity;
import com.conx2share.conx2share.util.PreferencesUtil;
import com.conx2share.conx2share.util.SnackbarUtil;
import com.nispok.snackbar.SnackbarManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

public class GroupFollowersInviteFragment extends BaseFragment implements GroupFollowersInviteAdapter.GroupFollowersInviteCallbacks {

    public static final String TAG = GroupFollowersInviteFragment.class.getSimpleName();

    @InjectView(R.id.group_followers_invite_edit_text)
    EditText mGroupFollowersInviteEditText;

    @InjectView(R.id.group_followers_invite_back_button)
    ImageButton mGroupFollowersInviteBackButton;

    @InjectView(R.id.group_followers_invite_listview)
    ListView mGroupFollowersListView;

    @Inject
    SnackbarUtil mSnackbarUtil;

    @Inject
    PreferencesUtil mPreferencesUtil;

    private ArrayList<User> mUsersFromSearch;

    private int mGroupId;

    private int mUserPosition;

    private FollowSearchAsync mFollowSearchAsync;

    private GroupFollowersInviteAdapter mGroupFollowersInviteAdapter;

    private InviteToFollowGroupAsync mInviteToFollowGroupAsync;

    public static GroupFollowersInviteFragment newInstance() {
        return new GroupFollowersInviteFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGroupId = getArguments().getInt("GroupId");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_followers_invite, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mGroupFollowersInviteBackButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        mGroupFollowersInviteEditText.addTextChangedListener(new TextWatcher() {
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

    private void setupListView() {
        mGroupFollowersInviteAdapter = new GroupFollowersInviteAdapter(getActivity(), this, mUsersFromSearch, mPreferencesUtil.getAuthUser().getId());
        mGroupFollowersListView.setAdapter(mGroupFollowersInviteAdapter);
        mGroupFollowersListView.setOnItemClickListener((parent, view, position, id) -> {
            Adapter adapter = mGroupFollowersListView.getAdapter();
            User user = (User) adapter.getItem(position);
            Log.d(TAG, "user.getId: " + user.getId());
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            intent.putExtra("profileId", String.valueOf(user.getId()));
            startActivity(intent);
        });
    }

    @Override
    public void onCheckboxToInviteUserToFollowGroupClicked(int position) {
        if (mUsersFromSearch.size() > 0) {
            UserIdWrapper userIdWrapper = new UserIdWrapper(mUsersFromSearch.get(position).getId());
            mUserPosition = position;
            inviteToFollowGroup(userIdWrapper);
        } else {
            Log.w(TAG, "No users from search");
        }
    }

    protected void inviteToFollowGroup(final UserIdWrapper userIdWrapper) {
        if (mInviteToFollowGroupAsync != null) {
            Log.w(TAG, "Invite to follow group already sent, new request to send invite will be ignored");
            return;
        }

        mInviteToFollowGroupAsync = new InviteToFollowGroupAsync(getActivity(), mGroupId) {

            @Override
            protected void onSuccess(Result<ResponseMessage> result) {
                hideKeyboard();
                mUsersFromSearch.get(mUserPosition).setGroupFollowStatus(true);
                mGroupFollowersInviteAdapter.notifyDataSetChanged();
                mInviteToFollowGroupAsync = null;
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.e(TAG, "Could not send invite to follow group", error);
                hideKeyboard();
                mUsersFromSearch.get(mUserPosition).setGroupFollowStatus(false);
                mGroupFollowersInviteAdapter.notifyDataSetChanged();
                if (getActivity() != null) {
                    mSnackbarUtil.showSnackBarWithAction(getActivity(), R.string.unable_to_send_invite_to_follow_group, R.string.retry, snackbar -> {
                        inviteToFollowGroup(userIdWrapper);
                        SnackbarManager.dismiss();
                    });
                }
                mInviteToFollowGroupAsync = null;
            }

        }.executeInParallel(userIdWrapper);
    }

    // TODO - Make this take into account pagination (GroupMembersInviteFragment uses very similar logic)
    protected void searchForUsers() {

        mFollowSearchAsync = new FollowSearchAsync(getActivity()) {
            @Override
            protected void onSuccess(Result<Users> result) {
                if (mGroupFollowersInviteEditText.getText().length() > 0) {
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

        }.executeInParallel(mGroupFollowersInviteEditText.getText().toString(), String.valueOf(mGroupId), "1");
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mGroupFollowersInviteEditText.getWindowToken(), 0);
    }
}