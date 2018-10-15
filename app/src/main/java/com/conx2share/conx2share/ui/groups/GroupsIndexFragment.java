package com.conx2share.conx2share.ui.groups;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.adapter.GroupIndexAdapter;
import com.conx2share.conx2share.async.GetGroupsAsync;
import com.conx2share.conx2share.model.Group;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.response.GetGroupListResponse;
import com.conx2share.conx2share.strategies.DeleteGroupStrategy;
import com.conx2share.conx2share.strategies.LeaveGroupStrategy;
import com.conx2share.conx2share.strategies.UnfollowGroupStrategy;
import com.conx2share.conx2share.ui.base.BaseFragment;
import com.conx2share.conx2share.util.ComparatorUtil;
import com.conx2share.conx2share.util.EmergencyUtil;
import com.conx2share.conx2share.util.PreferencesUtil;
import com.conx2share.conx2share.util.PrivilegeChecker;
import com.conx2share.conx2share.util.SnackbarUtil;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;

import java.util.ArrayList;
import java.util.Collections;

import javax.inject.Inject;

import butterknife.OnClick;
import retrofit.RetrofitError;
import roboguice.inject.InjectView;

public class GroupsIndexFragment extends BaseFragment implements GroupIndexAdapter.GroupIndexAdapterCallbacks {

    public static final String TAG = GroupsIndexFragment.class.getSimpleName();

    @Inject
    PreferencesUtil mPreferencesUtil;

    @InjectView(R.id.group_index_listview)
    ListView mGroupsIndexListview;

    @InjectView(R.id.group_index__progress_bar)
    ProgressBar mGroupIndexProgressBar;

    @Inject
    SnackbarUtil mSnackbarUtil;

    private ArrayList<Group> mGroups;

    private Integer mAuthUserId;

    private GetGroupsAsync mGetGroupsAsync;

    public static GroupsIndexFragment newInstance() {
        return new GroupsIndexFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        return inflater.inflate(R.layout.fragment_goups_index, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mPreferencesUtil.getAuthUser() != null && mPreferencesUtil.getAuthUser().getId() != null) {
            mAuthUserId = mPreferencesUtil.getAuthUser().getId();
        } else {
            EmergencyUtil.emergencyLogoutWithNotification(getActivity(), mPreferencesUtil);
        }

        mGroupsIndexListview.setOnItemClickListener((parent, view1, position, id) -> {
            if (getActivity() != null) {
                Intent groupActivityIntent = new Intent(getActivity(), GroupActivity.class);
                groupActivityIntent.putExtra(GroupActivity.EXTRA_GROUP_ID, mGroups.get(position).getId());
                getActivity().startActivity(groupActivityIntent);
            }
        });

        mGroupsIndexListview.setOnItemLongClickListener((parent, view1, position, id) -> {
            if (getActivity() != null) {
                Log.d(TAG, "position during onLongClick: " + position);
                Group group = mGroups.get(position);
                if (mGroups != null && !PrivilegeChecker.isConx2ShareGroup(group.getId())) {
                    if (!group.isOwner()) {
                        LeaveGroupStrategy leaveGroupStrategy = new LeaveGroupStrategy(getActivity());
                        UnfollowGroupStrategy unfollowGroupStrategy = new UnfollowGroupStrategy(getActivity());
                        switch (group.getGroupType()) {
                            case Group.PRIVATE_KEY:
                            case Group.DISCUSSION_KEY:
                                leaveGroupStrategy.launchLeaveGroupConfirmation(group);
                                break;
                            case Group.BLOG_KEY:
                                if (group.isMember()) {
                                    leaveGroupStrategy.launchLeaveGroupConfirmation(group);
                                } else {
                                    unfollowGroupStrategy.launchUnfollowConfirmationDialog(group);
                                }
                                break;
                        }
                    } else {
                        new DeleteGroupStrategy(getActivity()).launchDeleteGroupConfirmation(group);
                    }
                } else {
                    SnackbarManager.show(Snackbar.with(getActivity().getApplicationContext()).type(SnackbarType
                            .MULTI_LINE).text(getString(R.string.you_cannot_leave_or_delete_the_conx2share_group)),
                            getActivity());
                }
            }

            return true;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        showProgressBar();
        getGroups();
    }

    private void hideProgressBar() {
        if (getActivity() != null) {
            mGroupsIndexListview.setVisibility(View.VISIBLE);
            mGroupIndexProgressBar.setVisibility(View.GONE);
        }
    }

    public void setupGroupsIndexAdapter() {
        if (getActivity() != null) {
            GroupIndexAdapter groupIndexAdapter = new GroupIndexAdapter(mGroups, getActivity(), this, mAuthUserId);
            mGroupsIndexListview.setAdapter(groupIndexAdapter);
        }
    }

    private void showProgressBar() {
        if (getActivity() != null) {
            mGroupsIndexListview.setVisibility(View.GONE);
            mGroupIndexProgressBar.setVisibility(View.VISIBLE);
        }
    }

    public void onEventMainThread(DeleteGroupStrategy.DeleteGroupSuccessEvent event) {
        Log.d(TAG, "Received a delete group success event");
        if (getActivity() != null) {
            getGroups();
        }
    }

    public void onEventMainThread(LeaveGroupStrategy.LeaveGroupSuccessEvent event) {
        Log.d(TAG, "Received a leave group success event");
        if (getActivity() != null) {
            getGroups();
        }
    }

    public void onEventMainThread(UnfollowGroupStrategy.UnfollowGroupSuccessEvent event) {
        Log.d(TAG, "Received an unfollow group success event");
        if (getActivity() != null) {
            getGroups();
        }
    }

    protected void getGroups() {
        if (mGetGroupsAsync != null) {
            Log.w(TAG, "Get group list request already in progress, new request will be ignored");
            return;
        }

        mGetGroupsAsync = new GetGroupsAsync(getActivity()) {
            @Override
            protected void onSuccess(Result<GetGroupListResponse> result) {
                hideProgressBar();
                mGroups = result.getResource().getGroups();
                if (mGroups.size() > 0) {
                    Collections.sort(mGroups, ComparatorUtil.MASTER_GROUP_COMPARATOR);
                    setupGroupsIndexAdapter();
                } else {
                    Log.d(TAG, "No groups for user");
                }
                mGetGroupsAsync = null;
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.w(TAG, "Unable to get group list", error);
                hideProgressBar();
                if (getActivity() != null) {
                    mSnackbarUtil.showSnackBarWithAction(getActivity(), R.string.unable_to_get_group_list_text, R
                            .string.retry, snackbar -> getGroups());
                }
                mGetGroupsAsync = null;
            }
        }.executeInParallel();
    }

    @Override
    public void onNearingEndOfList() {

    }

    @OnClick(R.id.group_index_new_group)
    public void launchAddGroupActivity() {
        Intent intent = new Intent(getActivity(), AddGroupActivity.class);
        startActivity(intent);
    }
}
