package com.conx2share.conx2share.ui.groups;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.adapter.GroupMembersAdapter;
import com.conx2share.conx2share.async.GetGroupMembersAsync;
import com.conx2share.conx2share.model.AuthUser;
import com.conx2share.conx2share.model.Group;
import com.conx2share.conx2share.model.Users;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.User;
import com.conx2share.conx2share.strategies.RemoveUserFromGroupStrategy;
import com.conx2share.conx2share.ui.base.BaseFragment;
import com.conx2share.conx2share.ui.profile.ProfileActivity;
import com.conx2share.conx2share.util.LogUtil;
import com.conx2share.conx2share.util.PreferencesUtil;
import com.conx2share.conx2share.util.SnackbarUtil;
import com.nispok.snackbar.SnackbarManager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
//import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

import javax.inject.Inject;

import retrofit.RetrofitError;
import roboguice.inject.InjectView;

public class GroupMembersIndexFragment extends BaseFragment implements GroupMembersAdapter.GroupMembersAdapterCallbacks {

    private static final String TAG = GroupMembersIndexFragment.class.getSimpleName();

    @Inject
    PreferencesUtil mPreferencesUtil;

    @InjectView(R.id.group_members_listview)
    ListView mGroupMembersListView;

    @InjectView(R.id.invite_new_members_button)
    ImageButton mInviteMembers;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.group_members_back_button)
    ImageButton mGroupMemberBackButton;

    @Inject
    SnackbarUtil mSnackbarUtil;

    private Group mGroup;

    private String mGroupId;

    private ArrayList<User> mCurrentGroupMembers;

    private GetGroupMembersAsync mGetGroupMembersAsync;

    public static GroupMembersIndexFragment newInstance() {
        return new GroupMembersIndexFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGroup = getArguments().getParcelable(Group.EXTRA);
        if (mGroup != null) {
            mGroupId = String.valueOf(mGroup.getId());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_members_index, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mInviteMembers.setOnClickListener(v -> {
            if (LogUtil.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "onGroupInvite");
            }
            if (mGroup != null) {
                launchGroupMemberInviteActivity(mGroup);
            } else {
                if (LogUtil.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "group is null - cant invite");
                }
            }
        });

        AppCompatActivity groupMembersIndexActivity = ((AppCompatActivity) getActivity());
        groupMembersIndexActivity.setSupportActionBar(mToolbar);
        groupMembersIndexActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        groupMembersIndexActivity.getSupportActionBar().setTitle("");

        mGroupMemberBackButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGroup != null) {
            AuthUser user = mPreferencesUtil.getAuthUser();
            if (user != null && user.getId() != null && user.getId().equals(mGroup.getCreatorId())) {
                mInviteMembers.setVisibility(View.VISIBLE);
            } else {
                Log.e(TAG, "AuthUser or its id is null");
            }
            getGroupMembers(mGroupId);
        } else {
            Log.e(TAG, "mGroup was null");
        }
    }

    private void setupListView() {
        if (getActivity() != null) {
            GroupMembersAdapter adapter = new GroupMembersAdapter(getActivity(), this, mCurrentGroupMembers, mGroup, mPreferencesUtil.getAuthUser().getId());
            mGroupMembersListView.setAdapter(adapter);
            mGroupMembersListView.setOnItemClickListener((parent, view, position, id) -> {
                Adapter adapter1 = mGroupMembersListView.getAdapter();
                User user = (User) adapter1.getItem(position);
                Log.d(TAG, "user.getId: " + user.getId());
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtra("profileId", String.valueOf(user.getId()));
                startActivity(intent);
            });
        }
    }

    private void launchGroupMemberInviteActivity(Group group) {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), GroupMembersInviteActivity.class);
            intent.putExtra(Group.EXTRA, group);
            getActivity().startActivity(intent);
        }
    }

    @Override
    public void onRemoveMemberFromGroupClicked(User user) {
        RemoveUserFromGroupStrategy removeUserFromGroupStrategy = new RemoveUserFromGroupStrategy(getActivity(), user.getId(), mGroup);
        removeUserFromGroupStrategy.launchRemoveUserFromGroupConfirmationDialog();
    }

    protected void getGroupMembers(final String groupId) {
        if (mGetGroupMembersAsync != null) {
            Log.w(TAG, "Already getting group members, new request will be ignored");
            return;
        }

        mGetGroupMembersAsync = new GetGroupMembersAsync(getActivity()) {
            @Override
            protected void onSuccess(Result<Users> result) {
                if (result.getResource().getUsers().size() > 0) {
                    mCurrentGroupMembers = result.getResource().getUsers();
                    setupListView();
                } else {
                    Log.w(TAG, "No users for group");
                }
                mGetGroupMembersAsync = null;
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.w(TAG, "Error getting current group members", error);
                mSnackbarUtil.showSnackBarWithAction(getActivity(), R.string.unable_to_get_group_members, R.string.retry, snackbar -> {
                    getGroupMembers(groupId);
                    SnackbarManager.dismiss();
                });
                mGetGroupMembersAsync = null;
            }
        }.executeInParallel(groupId);
    }

    public void onEventMainThread(RemoveUserFromGroupStrategy.LoadRemoveUserFromGroupSuccessEvent event) {
        Log.d(TAG, "Received a remove user from group success event");
        getGroupMembers(mGroupId);
    }
}
