package com.conx2share.conx2share.ui.groups;


import com.conx2share.conx2share.R;
import com.conx2share.conx2share.adapter.GroupFollowersAdapter;
import com.conx2share.conx2share.async.GetGroupFollowersAsync;
import com.conx2share.conx2share.async.GetGroupMembersAsync;
import com.conx2share.conx2share.model.Group;
import com.conx2share.conx2share.model.UserIdWrapper;
import com.conx2share.conx2share.model.Users;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.User;
import com.conx2share.conx2share.strategies.BlockUserStrategy;
import com.conx2share.conx2share.strategies.UnblockUserStrategy;
import com.conx2share.conx2share.ui.base.BaseFragment;
import com.conx2share.conx2share.ui.profile.ProfileActivity;
import com.conx2share.conx2share.util.LogUtil;
import com.conx2share.conx2share.util.PreferencesUtil;
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
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

import javax.inject.Inject;

//import butterknife.InjectView;
import retrofit.RetrofitError;
import roboguice.inject.InjectView;

public class GroupFollowersIndexFragment extends BaseFragment implements GroupFollowersAdapter.GroupFollowersAdapterCallbacks {

    private static final String TAG = GroupFollowersIndexFragment.class.getSimpleName();

    @Inject
    PreferencesUtil mPreferencesUtil;

    @Inject
    SnackbarUtil mSnackbarUtil;

    @InjectView(R.id.group_followers_back_button)
    ImageButton mGroupFollowersBackButton;

    @InjectView(R.id.invite_followers_button)
    ImageButton mInviteFollowersButton;

    @InjectView((R.id.group_followers_list_view))
    ListView mGroupFollowersListView;

    private Group mGroup;

    private String mGroupId;

    private ArrayList<User> mGroupFollowers;

    private GetGroupFollowersAsync mGetGroupFollowersAsync;

    private GetGroupMembersAsync mGetGroupMembersAsync;

    public static GroupFollowersIndexFragment newInstance() {
        return new GroupFollowersIndexFragment();
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
        return inflater.inflate(R.layout.fragment_group_followers_index, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mInviteFollowersButton.setOnClickListener(v -> {
            if (mGroup != null) {
                launchGroupFollowersInviteActivity(mGroup);
            } else {
                if (LogUtil.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "group is null - cant invite");
                }
            }
        });

        mGroupFollowersBackButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGroup != null) {
            getUsers();
        } else {
            Log.e(TAG, "mGroup was null");
        }
    }

    private void getUsers() {
        // For a discussion group, members are followers
        if (mGroup.getGroupType() == Group.DISCUSSION_KEY) {
            getGroupMembers(mGroupId);
        } else {
            getGroupFollowers(mGroupId);
        }
    }

    @Override
    public void onBlockUserClicked(User user) {
        UserIdWrapper userIdWrapper = new UserIdWrapper(user.getId());
        BlockUserStrategy blockUserStrategy = new BlockUserStrategy(getActivity(), mGroup.getId());
        blockUserStrategy.blockUser(userIdWrapper);
    }

    @Override
    public void onUnblockUserClicked(User user) {
        UnblockUserStrategy unblockUserStrategy = new UnblockUserStrategy(getActivity(), mGroup.getId());
        unblockUserStrategy.unblockUser(String.valueOf(user.getId()));
    }

    private void launchGroupFollowersInviteActivity(Group group) {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), GroupFollowersInviteActivity.class);
            intent.putExtra(Group.EXTRA, group);
            getActivity().startActivity(intent);
        }
    }

    private void setupListView() {
        if (getActivity() != null) {
            GroupFollowersAdapter adapter = new GroupFollowersAdapter(getActivity(), this, mGroupFollowers, mGroup, mPreferencesUtil.getAuthUser().getId());
            mGroupFollowersListView.setAdapter(adapter);
            mGroupFollowersListView.setOnItemClickListener((parent, view, position, id) -> {
                Adapter adapter1 = mGroupFollowersListView.getAdapter();
                User user = (User) adapter1.getItem(position);
                Log.d(TAG, "user.getId: " + user.getId());
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtra("profileId", String.valueOf(user.getId()));
                startActivity(intent);
            });
        }
    }

    protected void getGroupFollowers(final String groupId) {
        if (mGetGroupFollowersAsync != null) {
            Log.w(TAG, "Already getting group followers, new request to get followers will be ignored");
            return;
        }

        mGetGroupFollowersAsync = new GetGroupFollowersAsync(getActivity()) {
            @Override
            protected void onSuccess(Result<Users> result) {
                if (result.getResource().getUsers().size() > 0) {
                    mGroupFollowers = result.getResource().getUsers();
                    setupListView();
                } else {
                    Log.w(TAG, "No users for group");
                }
                mGetGroupFollowersAsync = null;
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.w(TAG, "Error getting current group followers", error);
                mSnackbarUtil.showSnackBarWithAction(getActivity(), R.string.unable_to_get_group_followers, R.string.retry, snackbar -> {
                    getGroupFollowers(groupId);
                    SnackbarManager.dismiss();
                });
                mGetGroupFollowersAsync = null;
            }
        }.executeInParallel(groupId);
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
                    mGroupFollowers = result.getResource().getUsers();
                    setupListView();
                } else {
                    Log.w(TAG, "No users for group");
                }
                mGetGroupMembersAsync = null;
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.w(TAG, "Error getting current group members", error);
                // For a discussion group, members are followers
                mSnackbarUtil.showSnackBarWithAction(getActivity(), R.string.unable_to_get_group_followers, R.string.retry, snackbar -> {
                    getGroupMembers(groupId);
                    SnackbarManager.dismiss();
                });
                mGetGroupMembersAsync = null;
            }
        }.executeInParallel(groupId);
    }

    public void onEventMainThread(UnblockUserStrategy.LoadUnblockUserSuccessEvent event) {
        Log.d(TAG, "Received an unblock user success event");
        getUsers();
    }

    public void onEventMainThread(BlockUserStrategy.LoadBlockUserSuccessEvent event) {
        Log.d(TAG, "Received a block user success event");
        getUsers();
    }
}
