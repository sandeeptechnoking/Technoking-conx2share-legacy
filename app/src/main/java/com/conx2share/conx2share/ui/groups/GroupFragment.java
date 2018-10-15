package com.conx2share.conx2share.ui.groups;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.conx2share.conx2share.R;
import com.conx2share.conx2share.adapter.PostsAdapter;
import com.conx2share.conx2share.async.FollowGroupAsync;
import com.conx2share.conx2share.async.GetGroupAsync;
import com.conx2share.conx2share.async.GetGroupFeedAsync;
import com.conx2share.conx2share.async.GetGroupPostsAsync;
import com.conx2share.conx2share.model.FeedDirection;
import com.conx2share.conx2share.model.Group;
import com.conx2share.conx2share.model.GroupResponse;
import com.conx2share.conx2share.model.Post;
import com.conx2share.conx2share.model.event.UpdatePostEvent;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.response.GetPostsResponse;
import com.conx2share.conx2share.strategies.DeleteGroupStrategy;
import com.conx2share.conx2share.strategies.LeaveGroupStrategy;
import com.conx2share.conx2share.strategies.UnfollowGroupStrategy;
import com.conx2share.conx2share.streaming.EventVideoActivity;
import com.conx2share.conx2share.ui.base.BaseProfileFragment;
import com.conx2share.conx2share.ui.dialog.ShareDialogFragment;
import com.conx2share.conx2share.ui.events.EventActivity;
import com.conx2share.conx2share.ui.events.EventsListActivity;
import com.conx2share.conx2share.ui.feed.post.PostActivity;
import com.conx2share.conx2share.ui.livestream.StreamHomeActivity;
import com.conx2share.conx2share.ui.view.SimpleDividerItemDecoration;
import com.conx2share.conx2share.util.LogUtil;
import com.conx2share.conx2share.util.PreferencesUtil;
import com.conx2share.conx2share.util.PrivilegeChecker;
import com.conx2share.conx2share.util.SnackbarUtil;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nispok.snackbar.SnackbarManager;

import java.util.ArrayList;

import javax.inject.Inject;

//import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.RetrofitError;
import roboguice.inject.InjectView;

public class GroupFragment extends BaseProfileFragment implements PostsAdapter.PostAdapterCallback {

    public static final String TAG = GroupFragment.class.getSimpleName();
    public static final String EXTRA_GROUP_ID = "extra_group_id";

    @Inject
    PreferencesUtil preferencesUtil;

    @InjectView(R.id.group_toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.group_toolbar_up)
    ImageView mToolbarUp;

    @InjectView(R.id.group_header_background)
    ImageView mHeaderBackground;

    @InjectView(R.id.group_avatar)
    RoundedImageView mUserAvatar;

    @InjectView(R.id.members_count)
    TextView mMembersCount;

    @InjectView(R.id.followers_count)
    TextView mFollowersCount;

    @InjectView(R.id.group_feed_list_view)
    RecyclerView mGroupFeedRecyclerView;

    @InjectView(R.id.group_swipe_container)
    SwipeRefreshLayout mSwipeContainer;

    @InjectView(R.id.member_count_layout)
    LinearLayout mMemberCountLayout;

    @InjectView(R.id.followers_count_layout)
    LinearLayout mFollowersCountLayout;

    @InjectView(R.id.group_name)
    TextView mGroupName;

    @InjectView(R.id.group_settings_button)
    ImageView mGroupSettings;

    @InjectView(R.id.play_button)
    ImageView mPlayStream;

    @InjectView(R.id.main_view)
    RelativeLayout mMainView;

    @InjectView(R.id.group_profile_progress_bar)
    ProgressBar mGroupProfileProgressBar;

    @InjectView(R.id.group_follow_unfollow_layout)
    LinearLayout mGroupFollowUnfollowLayout;

    @InjectView(R.id.group_follow_unfollow_text)
    TextView mGroupFollowUnfollowText;

    @InjectView(R.id.group_status)
    TextView mGroupStatus;

    @InjectView(R.id.group_owner_sign)
    RoundedImageView mGroupOwnerSign;

    @InjectView(R.id.group_multiple_actions)
    FloatingActionMenu multipleActionsFab;

    @InjectView(R.id.group_livestream_button)
    FloatingActionButton livestreamButton;

    @Inject
    SnackbarUtil mSnackbarUtil;

    private String mStreamToPlay;
    private int mGroupId;
    private int mGroupOwnerId;
    private Group mGroup;
    private boolean mAutoPLay;
    private ArrayList<Post> mGroupPosts;
    private FeedDirection mDirection;
    private GetGroupAsync mGetGroupAsync;
    private GetGroupPostsAsync mGetGroupPostsAsync;
    private FollowGroupAsync mFollowGroupAsync;
    private UnfollowGroupStrategy mUnfollowGroupStrategy;
    private LeaveGroupStrategy mLeaveGroupStrategy;

    public static GroupFragment newInstance() {
        return new GroupFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mGroupId = getArguments().getInt("groupId"); // TODO: Extract extra key into a constant
        mGroupOwnerId = getArguments().getInt("ownerId"); // TODO: Extract extra key into a constant
        mAutoPLay = getArguments().getBoolean("autoPlay");

        Log.i(TAG, "GroupId: " + mGroupId + "autoPlay video " + mAutoPLay);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppCompatActivity groupActivity = ((AppCompatActivity) getActivity());

        mToolbarUp.setOnClickListener(v -> NavUtils.navigateUpFromSameTask(getActivity()));

        groupActivity.setSupportActionBar(mToolbar);
        groupActivity.getSupportActionBar().setTitle("");


        mMemberCountLayout.setOnClickListener(v -> {
            if (PrivilegeChecker.isConx2ShareGroup(mGroupId)) {
                if (getActivity() != null) {
                    if (PrivilegeChecker.isConx2ShareUser(String.valueOf(preferencesUtil.getAuthUser().getId()))) {
                        launchGroupMembersActivity();
                    } else {
                        Log.w(TAG, "Cannot see members for the Conx2Share Account");
                    }
                }
            } else {
                launchGroupMembersActivity();
            }
        });

        mFollowersCountLayout.setOnClickListener(v -> {
            if (PrivilegeChecker.isConx2ShareGroup(mGroupId)) {
                if (PrivilegeChecker.isConx2ShareUser(String.valueOf(preferencesUtil.getAuthUser().getId()))) {
                    launchGroupFollowersActivity();
                } else {
                    Log.w(TAG, "Cannot see followers for the Conx2Share Account");
                }
            } else {
                launchGroupFollowersActivity();
            }
        });

        mSwipeContainer.setOnRefreshListener(() -> {
            mDirection = FeedDirection.NEWER;
            refreshGroupFeed(FeedDirection.NEWER);
        });

        mGroupSettings.setOnClickListener(v -> {
            if (LogUtil.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "group settings clicked");
            }
            if (mGroup != null) {
                launchEditActivityWithGroup(mGroup);
            }
        });


        mPlayStream.setVisibility(View.INVISIBLE);
        mPlayStream.setOnClickListener(v -> {
            if (LogUtil.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "play stream clicked");
                playEventVideo(mStreamToPlay);
            }


        });

        mGroupFollowUnfollowLayout.setOnClickListener(v -> {
            if (mGroup.isFollowing()) {
                if (!mGroup.isOwner()) {
                    LeaveGroupStrategy leaveGroupStrategy = new LeaveGroupStrategy(getActivity());
                    UnfollowGroupStrategy unfollowGroupStrategy = new UnfollowGroupStrategy(getActivity());
                    switch (mGroup.getGroupType()) {
                        case Group.PRIVATE_KEY:
                        case Group.DISCUSSION_KEY:
                            leaveGroupStrategy.leaveGroup(mGroup);
                            break;
                        case Group.BLOG_KEY:
                            if (mGroup.isMember()) {
                                leaveGroupStrategy.leaveGroup(mGroup);
                            } else {
                                unfollowGroupStrategy.unfollowGroup(mGroup);
                            }
                            break;
                    }
                } else {
                    new DeleteGroupStrategy(getActivity()).deleteGroup(mGroup);
                }
            } else if (mGroup.isVipGroup()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(mGroup.getGroup_signup_url() + "&auth_token=" + preferencesUtil.getAuthToken()));
                startActivity(intent);
            } else {
                followGroup(mGroup.getId());
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (multipleActionsFab.isOpened()) multipleActionsFab.close(false);
    }

    private void adjustVisibility() {
        if (PrivilegeChecker.isConx2ShareGroup(mGroupId)) {
            mGroupFollowUnfollowLayout.setVisibility(View.GONE);
            if (PrivilegeChecker.isConx2ShareUser(String.valueOf(preferencesUtil.getAuthUser().getId()))) {
                mMemberCountLayout.setVisibility(View.VISIBLE);
                mFollowersCountLayout.setVisibility(View.VISIBLE);
                multipleActionsFab.setVisibility(View.VISIBLE);
            } else {
                mMemberCountLayout.setVisibility(View.GONE);
                mFollowersCountLayout.setVisibility(View.GONE);
                multipleActionsFab.setVisibility(View.GONE);
            }
        } else {
            adjustFollowUnfollowVisibility();
            adjustFollowersVisibility();
            adjustMembersVisibility();
            adjustPostButtonVisibility();
        }
    }

    private void adjustFollowUnfollowVisibility() {
        switch (mGroup.getGroupType()) {
            case Group.PRIVATE_KEY:
                mGroupFollowUnfollowLayout.setVisibility(View.GONE);
                break;
            case Group.DISCUSSION_KEY:
                if (!mGroup.isOwner()) {
                    mGroupFollowUnfollowLayout.setVisibility(View.VISIBLE);
                    if (mGroup.isFollowing()) {
                        mGroupFollowUnfollowText.setText(R.string.unfollow);
                        mGroupFollowUnfollowLayout.setBackgroundColor(getActivity().getResources().getColor(R.color
                                .profile_unfollow_gray));
                    } else {
                        mGroupFollowUnfollowText.setText(R.string.follow);
                        mGroupFollowUnfollowLayout.setBackgroundColor(getActivity().getResources().getColor(R.color
                                .conx_teal));
                    }
                } else {
                    mGroupFollowUnfollowLayout.setVisibility(View.GONE);
                }
                break;
            case Group.BLOG_KEY:
                if (!mGroup.isOwner()) {
                    if (mGroup.isMember()) {
                        mGroupFollowUnfollowLayout.setVisibility(View.GONE);
                    } else {
                        mGroupFollowUnfollowLayout.setVisibility(View.VISIBLE);
                        if (mGroup.isFollowing()) {
                            mGroupFollowUnfollowText.setText(R.string.unfollow);
                            mGroupFollowUnfollowLayout.setBackgroundColor(getActivity().getResources().getColor(R
                                    .color.profile_unfollow_gray));
                        } else {
                            mGroupFollowUnfollowText.setText(R.string.follow);
                            mGroupFollowUnfollowLayout.setBackgroundColor(getActivity().getResources().getColor(R
                                    .color.conx_teal));
                        }
                    }
                } else {
                    mGroupFollowUnfollowLayout.setVisibility(View.GONE);
                }
                break;
        }
    }

    private void adjustFollowersVisibility() {
        switch (mGroup.getGroupType()) {
            case Group.PRIVATE_KEY:
                mFollowersCountLayout.setVisibility(View.GONE);
                break;
            case Group.DISCUSSION_KEY:
                mFollowersCountLayout.setVisibility(View.VISIBLE);
                break;
            case Group.BLOG_KEY:
                mFollowersCountLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void adjustMembersVisibility() {
        switch (mGroup.getGroupType()) {
            case Group.PRIVATE_KEY:
                mMemberCountLayout.setVisibility(View.VISIBLE);
                break;
            case Group.DISCUSSION_KEY:
                mMemberCountLayout.setVisibility(View.GONE);
                break;
            case Group.BLOG_KEY:
                mMemberCountLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void adjustPostButtonVisibility() {
        if (mGroup.isBlocked() != null && mGroup.isBlocked()) {
            multipleActionsFab.setVisibility(View.GONE);
        } else {
            if (mGroup.isMember() != null && mGroup.isMember()) {
                multipleActionsFab.setVisibility(View.VISIBLE);
                livestreamButton.setVisibility(View.GONE);
                if (mGroup.isOwner()) {
                    livestreamButton.setVisibility(View.VISIBLE);
                }
            } else if (mGroup.isOwner() != null && mGroup.isOwner()) {
                multipleActionsFab.setVisibility(View.VISIBLE);
                livestreamButton.setVisibility(View.VISIBLE);
            } else {
                multipleActionsFab.setVisibility(View.GONE);
            }
        }
    }


    private void adjustPlayStreamVisibility() {
        if (mGroup.isBlocked() != null && mGroup.isBlocked()) {
            mPlayStream.setVisibility(View.GONE);
        } else if (mGroup.getLiveStream() != null) {
            if (mGroup.getLiveStream().getAndroid() != null) {
                mStreamToPlay = mGroup.getLiveStream().getAndroid();
                mPlayStream.setVisibility(View.VISIBLE);
            } else if (mGroup.getLiveStream().getIos() != null) {
                mStreamToPlay = mGroup.getLiveStream().getIos();
                mPlayStream.setVisibility(View.VISIBLE);
            }
        } else {
            mPlayStream.setVisibility(View.INVISIBLE);
        }

    }

    private void setGroupStatus() {
        int groupTypeStringId;
        switch (mGroup.getGroupType()) {
            case Group.PRIVATE_KEY:
                groupTypeStringId = R.string.private_group;
                break;
            case Group.DISCUSSION_KEY:
                groupTypeStringId = R.string.discussion_group;
                break;
            case Group.BLOG_KEY:
                groupTypeStringId = R.string.blog_group;
                break;
            default:
                groupTypeStringId = R.string.empty_string;
                break;
        }

        if (mGroup.isOwner() != null && mGroup.isOwner()) {
            mGroupOwnerSign.setVisibility(View.VISIBLE);
            mGroupStatus.setText(getString(R.string.group_owner) + " - " + getString(groupTypeStringId));
        } else {
            mGroupOwnerSign.setVisibility(View.GONE);
            if (mGroup.isMember() != null && mGroup.isMember()) {
                if (mGroup.getGroupType() == Group.DISCUSSION_KEY) {
                    mGroupStatus.setText(getString(R.string.follower) + " - " + getString(groupTypeStringId));
                } else {
                    mGroupStatus.setText(getString(R.string.member) + " - " + getString(groupTypeStringId));
                }
            } else if (mGroup.isFollowing() != null && mGroup.isFollowing()) {
                mGroupStatus.setText(getString(R.string.follower) + " - " + getString(groupTypeStringId));
            } else {
                mGroupStatus.setText(getString(groupTypeStringId));
            }
        }
    }

    @OnClick(R.id.event_link)
    public void eventsClicked() {
        launchEventsIndexActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        getServerTimeOffset();
    }

    public void setupGroupFeedAdapter() {
        if (getActivity() != null) {
            mPostsAdapter = new PostsAdapter(getActivity(), mGroupPosts, this, false,
                    preferencesUtil.getAuthUser().getId(), mGroup, null);
            mGroupFeedRecyclerView.setAdapter(mPostsAdapter);
            mGroupFeedRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        } else {
            Log.w(TAG, "getActivity() was null");
        }
    }

    public void setupGroupInfo() {
        setAboutText(mGroup.getAbout());

        if (!TextUtils.isEmpty(mGroup.getGroupavatar().getGroupAvatar().getUrl())) {
            Glide.with(getActivity()).load(mGroup.getGroupavatar().getGroupAvatar().getUrl()).dontAnimate()
                    .centerCrop().into(mHeaderBackground);
            Glide.with(getActivity()).load(mGroup.getGroupavatar().getGroupAvatar().getUrl()).dontAnimate()
                    .centerCrop().into(mUserAvatar);
        }

        mGroupName.setText(mGroup.getName());
        mMembersCount.setText(String.valueOf(mGroup.getMemberCount()));
        mFollowersCount.setText(String.valueOf(mGroup.getFollowers()));

        if (mGroup.isOwner() != null && mGroup.isOwner()) {
            mGroupSettings.setVisibility(View.VISIBLE);
        } else {
            mGroupSettings.setVisibility(View.GONE);
        }
    }

    private void launchGroupMembersActivity() {
        Intent intent = new Intent(getActivity(), GroupMembersIndexActivity.class);
        intent.putExtra(Group.EXTRA, mGroup);
        getActivity().startActivity(intent);
    }

    private void launchGroupFollowersActivity() {
        Intent intent = new Intent(getActivity(), GroupFollowersIndexActivity.class);
        intent.putExtra(Group.EXTRA, mGroup);
        getActivity().startActivity(intent);
    }

    @Override
    protected void refreshPosts() {
        getGroup(mGroupId);
    }

    @Override
    protected void scrollTo(int position) {
        if (mGroupFeedRecyclerView != null) mGroupFeedRecyclerView.smoothScrollToPosition(position);
    }

    @Override
    public void onLikeClicked(Post post) {
        likePost(post);
    }

    @Override
    public void onUnlikeClicked(Post post) {
        unlikePost(post);
    }

    @Override
    public void onShareClicked(Post post) {
        ShareDialogFragment.sharePost(getActivity().getSupportFragmentManager(), post);
    }

    @Override
    public void onNearingEndOfList() {
        // TODO: refactor next page retrieval to start via this method?
        // is the group feed paginated
    }

    @Override
    public void onLikeCountClicked(int postId) {

    }

    @OnClick(R.id.group_post_button)
    public void postButtonClicked() {
        if (mGroup != null && (mGroup.isOwner() || mGroup.isMember())) {
            launchPostActivityWithGroup(mGroup);
        }
    }

    @OnClick(R.id.group_livestream_button)
    public void livestreamButtonClicked() {
        if (mGroup != null && mGroup.isOwner()) {
            StreamHomeActivity.startFromGroup(getActivity(), mGroup.getId());
        }
    }

    private void launchPostActivityWithGroup(Group group) {
        Intent intent = new Intent(getActivity(), PostActivity.class);
        intent.putExtra(Group.EXTRA, group);
        getActivity().startActivity(intent);
    }

    private void launchEditActivityWithGroup(Group group) {
        Intent intent = new Intent(getActivity(), AddGroupActivity.class);
        intent.putExtra(Group.EXTRA, group);
        getActivity().startActivity(intent);
    }

    private void launchEventsIndexActivity() {
        Intent intent = new Intent(getActivity(), EventsListActivity.class);
        intent.putExtra(EventsListActivity.EXTRA_GROUP_ID, mGroupId);
        intent.putExtra(EventsListActivity.EXTRA_GROUP_OWNER, mGroup.isOwner());
        intent.putExtra(EventsListActivity.EXTRA_GROUP_TYPE, "group");
        getActivity().startActivity(intent);
    }

    public void onEventMainThread(UpdatePostEvent event) {
        Post post = mGroupPosts.get(event.getPosition());
        if (event.getUpdateType() == UpdatePostEvent.PostEventType.COMMENT_COUNT) {
            post.setCommentCount(post.getCommentCount() + 1);
            mPostsAdapter.notifyDataSetChanged();
        } else if (event.getUpdateType() == UpdatePostEvent.PostEventType.LIKE_COUNT_INCREASE) {
            post.setLikesCount(post.getLikesCount() + 1);
            post.setHasLiked(true);
            mPostsAdapter.notifyDataSetChanged();
        } else if (event.getUpdateType() == UpdatePostEvent.PostEventType.LIKE_COUNT_DECREASE) {
            // To prevent negatives
            if (post.getLikesCount() > 0) {
                post.setLikesCount(post.getLikesCount() - 1);
            }
            post.setHasLiked(false);
            mPostsAdapter.notifyDataSetChanged();
        }
    }

    private void getServerTimeOffset() {
        mMainView.setVisibility(View.GONE);
            getGroup(mGroupId);
    }

    private void hideProgressBar() {
        mMainView.setVisibility(View.VISIBLE);
        mGroupProfileProgressBar.setVisibility(View.GONE);
    }

    private void playEventVideo(String stream) {
        Intent intent = new Intent(this.getContext(), EventVideoActivity.class);
        intent.putExtra(EventActivity.EXTRA_EVENT_STREAM, stream);
        startActivity(intent);
    }

    public void onEventMainThread(DeleteGroupStrategy.DeleteGroupSuccessEvent event) {
        Log.d(TAG, "Received a delete group success event");
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    public void onEventMainThread(LeaveGroupStrategy.LeaveGroupSuccessEvent event) {
        Log.d(TAG, "Received a leave group success event");
        getGroup(mGroupId);
    }

    public void onEventMainThread(UnfollowGroupStrategy.UnfollowGroupSuccessEvent event) {
        Log.d(TAG, "Received an unfollow group success event");
        getGroup(mGroupId);
    }

    protected void followGroup(final int groupId) {
        if (mFollowGroupAsync != null) {
            Log.w(TAG, "Group follow already in progress, new group follow request will be ignored");
            // TODO - queue group follow requests
            return;
        }

        mFollowGroupAsync = new FollowGroupAsync(getActivity()) {

            @Override
            protected void onSuccess(Result<GroupResponse> result) {
                getGroup(groupId);
                mFollowGroupAsync = null;
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.e(TAG, "Could not follow group", error);
                if (getActivity() != null) {
                    mSnackbarUtil.showSnackBarWithAction(getActivity(), R.string.unable_to_follow_group, R.string
                            .retry, snackbar -> {
                        followGroup(groupId);
                        SnackbarManager.dismiss();
                    });
                }
                mFollowGroupAsync = null;
            }
        }.executeInParallel(groupId);
    }

    protected void getGroup(final int groupId) {
        if (mGetGroupAsync != null) {
            Log.w(TAG, "Already getting group info, new request to get group info will be ignored");
            // TODO - queue get group request
            return;
        }

        mGetGroupAsync = new GetGroupAsync(getActivity()) {
            @Override
            protected void onSuccess(Result<GroupResponse> result) {
                mGroup = result.getResource().getGroup();
                setupGroupInfo();
                adjustVisibility();
                setGroupStatus();
                adjustPlayStreamVisibility();
                getGroupPosts(groupId);
                if (mAutoPLay == true) {
                    if (mGroup.getLiveStream().getAndroid() != null) {
                        mStreamToPlay = mGroup.getLiveStream().getAndroid();
                        playEventVideo(mStreamToPlay);
                    } else if (mGroup.getLiveStream().getIos() != null) {
                        mStreamToPlay = mGroup.getLiveStream().getIos();
                        playEventVideo(mStreamToPlay);
                    }
                }
                mAutoPLay = false;
                mGetGroupAsync = null;
            }


            @Override
            protected void onFailure(RetrofitError error) {
                Log.e(TAG, "Could not get group info", error);
                hideProgressBar();
                if (getActivity() != null) {
                    mSnackbarUtil.showSnackBarWithAction(getActivity(), R.string.unable_to_get_group_text, R.string
                            .retry, snackbar -> {
                        getGroup(groupId);
                        SnackbarManager.dismiss();
                    });
                }
                mGetGroupAsync = null;
            }

        }.executeInParallel(groupId);
    }

    protected void getGroupPosts(final int groupId) {
        if (mGetGroupPostsAsync != null) {
            Log.w(TAG, "Request to get group posts is already in progress, new request will be ignored.");
            // TODO - queue get group posts request
            return;
        }

        mGetGroupPostsAsync = new GetGroupPostsAsync(getActivity()) {
            @Override
            protected void onSuccess(Result<GroupResponse> result) {
                hideProgressBar();
                mGroupPosts = result.getResource().getGroup().getPosts();
                if (mGroupPosts != null) {
                    setupGroupFeedAdapter();
                }
                mGetGroupPostsAsync = null;
            }

            @Override
            protected void onFailure(RetrofitError error) {
                hideProgressBar();
                Log.e(TAG, "Could not get group posts", error);
                if (getActivity() != null) {
                    mSnackbarUtil.showSnackBarWithAction(getActivity(), R.string.unable_to_get_group_posts_text, R
                            .string.retry, snackbar -> {
                        getGroupPosts(groupId);
                        SnackbarManager.dismiss();
                    });
                }
                mGetGroupPostsAsync = null;
            }

        }.executeInParallel(groupId);
    }

    private void refreshGroupFeed(final FeedDirection direction) {
        Integer postId = null;
        if (mGroupPosts != null && mGroupPosts.size() > 0) {
            if (direction == FeedDirection.NEWER) {
                postId = mGroupPosts.get(0).getId();
            } else {
                postId = mGroupPosts.get(mGroupPosts.size() - 1).getId();
            }
        }

        new GetGroupFeedAsync(getActivity(), mGroupId, direction) {
            @Override
            protected void onSuccess(Result<GetPostsResponse> result) {
                if (getActivity() != null) {
                    mSwipeContainer.setRefreshing(false);
                    if (getDirection() == FeedDirection.NEWER) {
                        if (result.getResource().getPosts() != null) {
                            ArrayList<Post> newerPosts = result.getResource().getPosts();
                            mGroupPosts.addAll(0, newerPosts);
                            setupGroupFeedAdapter();
                        }
                    } else {
                        ArrayList<Post> olderPosts = result.getResource().getPosts();
                        if (olderPosts.isEmpty()) {
                            mGroupFeedRecyclerView.clearOnScrollListeners();
                        } else {
                            mGroupPosts.addAll(mGroupPosts.size(), olderPosts);
                            int index = ((LinearLayoutManager) mGroupFeedRecyclerView.getLayoutManager())
                                    .findFirstVisibleItemPosition();
                            View v = mGroupFeedRecyclerView.getChildAt(0);
                            int top = (v == null) ? 0 : v.getTop();
                            setupGroupFeedAdapter();
                            mGroupFeedRecyclerView.scrollToPosition(index);
                        }

                        mSwipeContainer.setRefreshing(false);
                    }
                }
            }

            @Override
            protected void onFailure(RetrofitError error) {
                if (getActivity() != null) {
                    mSnackbarUtil.showRetry(getActivity(), R.string.unable_to_refresh_feed_text, snackbar -> {
                        refreshGroupFeed(direction);
                        SnackbarManager.dismiss();
                    });

                    mSwipeContainer.setRefreshing(false);
                }
            }
        }.executeInParallel(postId);
    }
}