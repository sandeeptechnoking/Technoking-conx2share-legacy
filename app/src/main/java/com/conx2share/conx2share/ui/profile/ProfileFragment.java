package com.conx2share.conx2share.ui.profile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.conx2share.conx2share.R;
import com.conx2share.conx2share.adapter.PostsAdapter;
import com.conx2share.conx2share.async.GetFollowingUsersAsync;
import com.conx2share.conx2share.model.FeedDirection;
import com.conx2share.conx2share.model.Friend;
import com.conx2share.conx2share.model.Post;
import com.conx2share.conx2share.model.event.UpdatePostEvent;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.User;
import com.conx2share.conx2share.network.models.response.GetPostsResponse;
import com.conx2share.conx2share.network.models.response.GetUserResponse;
import com.conx2share.conx2share.streaming.EventVideoActivity;
import com.conx2share.conx2share.ui.base.BaseProfileFragment;
import com.conx2share.conx2share.ui.events.EventActivity;
import com.conx2share.conx2share.ui.followers.FollowersActivity;
import com.conx2share.conx2share.ui.following.FollowingActivity;
import com.conx2share.conx2share.ui.messaging.MediaViewerActivity;
import com.conx2share.conx2share.ui.messaging.MessagingActivity;

import com.conx2share.conx2share.ui.view.SimpleDividerItemDecoration;
import com.conx2share.conx2share.util.PreferencesUtil;
import com.conx2share.conx2share.util.PrivilegeChecker;
import com.conx2share.conx2share.util.SnackbarUtil;
import com.conx2share.conx2share.util.ViewUtil;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.OnClick;
import roboguice.inject.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ProfileFragment extends BaseProfileFragment implements PostsAdapter.PostAdapterCallback {

    public static final String PROFILEID_KEY = "profileId";
    private static final String FOLLOWING_USERS_KEY = "numFollowingUsers";
    private static final String FOLLOWERS_KEY = "numFollowers";
    private static String TAG = ProfileFragment.class.getSimpleName();

    @Inject
    NetworkClient networkClient;

    @Inject
    PreferencesUtil mPreferencesUtil;

    @InjectView(R.id.profile_toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.profile_toolbar_up)
    ImageView mToolbarUp;

    @InjectView(R.id.profile_header_background)
    ImageView mProfileHeaderBackground;

    @InjectView(R.id.profile_user_avatar)
    RoundedImageView mProfileUserAvatar;

    @InjectView(R.id.profile_user_name)
    TextView mProfileUserNameTextView;

    @InjectView(R.id.profile_user_handle)
    TextView mUserHandleTextView;

    @InjectView(R.id.followers_count)
    TextView mFollowersCount;

    @InjectView(R.id.following_count)
    TextView mFollowingCount;

    @InjectView(R.id.profile_feed_list_view)
    RecyclerView mProfileFeedRecyclerView;

    @InjectView(R.id.profile_follow_layout)
    RelativeLayout mFollowLayout;

    @InjectView(R.id.profile_follow_text)
    TextView mFollowText;

    @InjectView(R.id.profile_swipe_container)
    SwipeRefreshLayout mSwipeContainer;

    @InjectView(R.id.main_view)
    RelativeLayout mMainView;

    @InjectView(R.id.about_text)
    TextView mAboutText;

    @InjectView(R.id.play_button)
    ImageView mPlayStream;

    @Inject
    SnackbarUtil mSnackbarUtil;

    @InjectView(R.id.profile_progress_bar)
    ProgressBar mProfileProgressBar;

    @InjectView(R.id.check_favorite_view)
    ImageView mFavoriteView;

    @InjectView(R.id.unchecked_favorite_view)
    ImageView mUnfilledFavoriteView;

    @InjectView(R.id.followers_section)
    View mFollowersContainer;

    @InjectView(R.id.following_section)
    View mFollowingContainer;

    @InjectView(R.id.user_chat_fab)
    FloatingActionButton startChatFab;

    private String mProfileUserId;
    private User mProfileUser;
    private ArrayList<Post> mPosts;
    private Boolean mIsFollowing;
    private FeedDirection mDirection;
    private Boolean mLoading = true;
    private boolean mFollowing;
    private boolean mUnfollowing;
    private GetFollowingUsersAsync mGetFollowingUsersAsync;
    private String mStreamToPlay;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_screen, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppCompatActivity profileActivity = ((AppCompatActivity) getActivity());

        mToolbarUp.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        profileActivity.setSupportActionBar(mToolbar);
        profileActivity.getSupportActionBar().setTitle("");

        mSwipeContainer.setOnRefreshListener(() -> {
            if (mPosts != null && mPosts.size() > 0) {
                mDirection = FeedDirection.NEWER;
                new RefreshFeed().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, FeedDirection.NEWER);
            } else {
                mSwipeContainer.setRefreshing(false);
            }
        });

        mPlayStream.setVisibility(View.INVISIBLE);
        mPlayStream.setOnClickListener(v -> playEventVideo(mStreamToPlay));

        startChatFab.setOnClickListener(v -> {
            if (mProfileUser != null) {
                MessagingActivity.start(getActivity(), new Friend(mProfileUser));
            }
        });

        mProfileFeedRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    final int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                            .findLastVisibleItemPosition();
                    if (lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1) {
                        if (!mLoading) {
                            mLoading = true;
                            mDirection = FeedDirection.OLDER;
                            new RefreshFeed().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, FeedDirection.OLDER);
                        }
                    }
                }
            }
        });

        View followingSection = getActivity().findViewById(R.id.following_section);
        followingSection.setOnClickListener(v -> {
            if (PrivilegeChecker.isConx2ShareUser(mProfileUserId)) {
                if (getActivity() != null) {
                    if (PrivilegeChecker.isConx2ShareUser(String.valueOf(mPreferencesUtil.getAuthUser().getId()))) {
                        startFollowingActivity();
                    } else {
                        Log.w(TAG, "Cannot see users who are following the Conx2Share Account");
                    }
                }
            } else {
                startFollowingActivity();
            }
        });

        View followersSection = getActivity().findViewById(R.id.followers_section);
        followersSection.setOnClickListener(v -> {
            if (PrivilegeChecker.isConx2ShareUser(mProfileUserId)) {
                if (getActivity() != null) {
                    if (PrivilegeChecker.isConx2ShareUser(String.valueOf(mPreferencesUtil.getAuthUser().getId()))) {
                        startFollowersActivity();
                    } else {
                        Log.w(TAG, "Cannot see followers for the Conx2Share Account");
                    }
                }
            } else {
                startFollowersActivity();
            }
        });
    }

    private void startFollowingActivity() {
        Intent followingIntent = new Intent(getActivity(), FollowingActivity.class);
        followingIntent.putExtra(PROFILEID_KEY, mProfileUser.getId());
        startActivity(followingIntent);
    }

    private void startFollowersActivity() {
        Intent followersIntent = new Intent(getActivity(), FollowersActivity.class);
        followersIntent.putExtra(PROFILEID_KEY, mProfileUser.getId());
        startActivity(followersIntent);
    }

    @Override
    public void onResume() {
        super.onResume();

        mFollowLayout.setClickable(false);

        mProfileUserId = getArguments().getString(PROFILEID_KEY);

        Log.i(TAG, "Profile ID: " + mProfileUserId);

        if (PrivilegeChecker.isConx2ShareUser(mProfileUserId)) {
            if (getActivity() != null) {
                if (!PrivilegeChecker.isConx2ShareUser(String.valueOf(mPreferencesUtil.getAuthUser().getId()))) {
                    mFollowLayout.setVisibility(View.GONE);
                    mFollowersContainer.setVisibility(View.GONE);
                    mFollowingContainer.setVisibility(View.GONE);
                }
            }
        }

        new GetProfileUser(getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mProfileUserId);
    }

    private void adjustPlayStreamVisibility() {
        if (mProfileUser.isBlocked() == true) {
            mPlayStream.setVisibility(View.GONE);
        } else if (mProfileUser.getLiveStream() != null) {
            if (mProfileUser.getLiveStream().getAndroid() != null) {
                mStreamToPlay = mProfileUser.getLiveStream().getAndroid();
                mPlayStream.setVisibility(View.VISIBLE);
            } else if (mProfileUser.getLiveStream().getIos() != null) {
                mStreamToPlay = mProfileUser.getLiveStream().getIos();
                mPlayStream.setVisibility(View.VISIBLE);
            }
        } else {
            mPlayStream.setVisibility(View.INVISIBLE);
        }

    }


    private void setupViews() {

        mFollowLayout.setOnClickListener(v -> {

            if (mIsFollowing) {
                if (!mUnfollowing) {
                    new UnfollowUserAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf
                            (mProfileUser.getId()));
                }
            } else {
                if (!mFollowing) {
                    runFollowUserInvite(String.valueOf(mProfileUserId));
                }
            }
        });
    }

    private void runFollowUserInvite(String id) {
        addSubscription(networkClient.followUserRequest(id, true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseMessage -> {
                    mSnackbarUtil.displaySnackBar(getActivity(), getString(R.string.invite_was_sent));
                    mFollowLayout.setEnabled(false);
                    mFollowText.setText(getString(R.string.invite_status));
                }, throwable -> {
                    mSnackbarUtil.displaySnackBar(getActivity(), throwable.getMessage());
                }));
    }

    @OnClick(R.id.unchecked_favorite_view)
    public void onFavoriteClick() {
        if (getActivity() != null && mProfileUser != null) {
            mFavoriteView.setEnabled(false);
            mUnfilledFavoriteView.setEnabled(false);
            mProfileUser.setIsFavorite(true);
            mFavoriteView.setVisibility(View.VISIBLE);
            favoriteUserAsync(String.valueOf(mProfileUser.getId()));
        }
    }

    @OnClick(R.id.check_favorite_view)
    public void onUnfavoriteClick() {
        if (getActivity() != null && mProfileUser != null) {
            mFavoriteView.setEnabled(false);
            mUnfilledFavoriteView.setEnabled(false);
            mProfileUser.setIsFavorite(false);
            mFavoriteView.setVisibility(View.GONE);
            unfavoriteUserAsync(String.valueOf(mProfileUser.getId()));
        }
    }

    @OnClick(R.id.profile_user_avatar)
    public void onAvatarClick() {
        if (!TextUtils.isEmpty(mProfileUser.getAvatar().getAvatar().getUrl())) {
            MediaViewerActivity.startInImageViewMode(getActivity(), mProfileUser.getAvatar().getAvatar().getUrl());
        }
    }

    @OnClick(R.id.profile_header_background)
    public void onCoverClick() {
        if (!TextUtils.isEmpty(mProfileUser.getCover().getCoverPhoto().getUrl())) {
            MediaViewerActivity.startInImageViewMode(getActivity(), mProfileUser.getCover().getCoverPhoto().getUrl());
        } else {
            onAvatarClick();
        }
    }

    public void setupUserFeed() {
        if (getActivity() != null) {
            mPostsAdapter = new PostsAdapter(getActivity(), mPosts, this, false, mPreferencesUtil
                    .getAuthUser().getId(), null, null);
            mProfileFeedRecyclerView.setAdapter(mPostsAdapter);
            mProfileFeedRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        }
    }

    // TODO: may be able to abstract this event listener into a base fragment
    public void onEventMainThread(UpdatePostEvent event) {
        Post post = mPosts.get(event.getPosition());
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

    // tightly coupled to some of the inner AsyncTask classes below
    private void setFavoriteButtons() {
        if (mProfileUser != null) {
            if (mProfileUser.isMutualFollower()) {
                if (mProfileUser.getIsFavorite()) {
                    mFavoriteView.setVisibility(View.VISIBLE);
                    mUnfilledFavoriteView.setVisibility(View.VISIBLE);
                } else {
                    mFavoriteView.setVisibility(View.GONE);
                    mUnfilledFavoriteView.setVisibility(View.VISIBLE);
                }
            } else {
                mFavoriteView.setVisibility(View.GONE);
                mUnfilledFavoriteView.setVisibility(View.GONE);
                mProfileUser.setIsFavorite(false);
            }
        }
    }

    @Override
    protected void refreshPosts() {
        new GetUserPosts().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mProfileUserId);
    }

    @Override
    protected void scrollTo(int position) {
        if(mProfileFeedRecyclerView != null) mProfileFeedRecyclerView.scrollToPosition(position);
    }

    private void getPosts() {
        new GetUserPosts().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mProfileUserId);
    }

    @Override
    public void onLikeCountClicked(int postId) {

    }

    public class GetProfileUser extends AsyncTask<String, Void, Result<GetUserResponse>> {

        private Activity mActivity;

        public GetProfileUser(Activity activity) {
            mActivity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (getActivity() != null) {
                mProfileProgressBar.setVisibility(View.VISIBLE);
                mMainView.setVisibility(View.GONE);
            }
        }

        @Override
        protected Result<GetUserResponse> doInBackground(String... params) {
            return networkClient.getUser(params[0]);
        }

        @Override
        protected void onPostExecute(Result<GetUserResponse> userResult) {
            super.onPostExecute(userResult);

            if (getActivity() != null) {
                mProfileProgressBar.setVisibility(View.GONE);
                mMainView.setVisibility(View.VISIBLE);
                if (userResult != null && userResult.getResource() != null && userResult.getError() == null) {

                    mProfileUser = userResult.getResource().getUser();

                    showProfilePictures(mProfileUser);

                    mProfileUserNameTextView.setText(mProfileUser.getFirstName().trim() + " " + mProfileUser
                            .getLastName().trim());
                    mProfileUserNameTextView.setShadowLayer(4, -1, -0.3f, Color.BLACK);

                    mUserHandleTextView.setText("@".concat(mProfileUser.getUsername()));
                    mProfileUserNameTextView.setShadowLayer(4, -1, -0.3f, Color.BLACK);

                    mFollowersCount.setText(String.valueOf(mProfileUser.getFollowers()));
                    mFollowingCount.setText(String.valueOf(mProfileUser.getFollowing()));
                    setAboutText(mProfileUser.getAbout());

                    if (mProfileUserId.equals(String.valueOf(mPreferencesUtil.getAuthUser().getId()))) {
                        mFollowLayout.setVisibility(View.GONE);
                    } else {

                        if (!mProfileUser.getIsFollowing()) {
                            mIsFollowing = false;
                            mFollowLayout.setBackgroundColor(getActivity().getResources().getColor(R.color
                                    .conx_teal));
                            mFollowText.setText(mActivity.getString(R.string.follow));
                        } else {
                            mIsFollowing = true;
                        }
                    }

                    mFollowLayout.setClickable(true);

                    setupViews();
                    setupChatBt();
                    setFavoriteButtons();
                    getPosts();
                    adjustPlayStreamVisibility();
                } else {
                    SnackbarManager.show(Snackbar.with(getActivity().getApplicationContext()).type(SnackbarType
                            .MULTI_LINE).text(getString(R.string.unable_to_load_profile_text)).actionLabel(getString
                            (R.string.retry)).actionListener(snackbar -> {
                        new GetProfileUser(getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                                mProfileUserId);
                        SnackbarManager.dismiss();
                    }), getActivity());
                }
            }
        }
    }

    private void setupChatBt() {
        if (mProfileUser != null && mProfileUser.getIsFollowing() && mProfileUser.getIsFollower()) {
            startChatFab.setVisibility(View.VISIBLE);
        } else {
            startChatFab.setVisibility(View.GONE);
        }
    }

    private void showProfilePictures(User profileUser) {
        if (!TextUtils.isEmpty(profileUser.getAvatar().getAvatar().getThumbUrl())) {
            Glide.with(getActivity()).load(profileUser.getAvatar().getAvatar().getThumbUrl()).centerCrop()
                    .dontAnimate().into(mProfileUserAvatar);
        }
        if (!ViewUtil.pictureWasSetted(getActivity(), profileUser.getCover().getCoverPhoto().getUrl(), mProfileHeaderBackground)) {
            ViewUtil.pictureWasSetted(getActivity(), profileUser.getAvatar().getAvatar().getUrl(), mProfileHeaderBackground);
        }
    }

    public class GetUserPosts extends AsyncTask<String, Void, Result<GetPostsResponse>> {

        @Override
        protected Result<GetPostsResponse> doInBackground(String... params) {
            return networkClient.getUserFeed(params[0]);
        }

        @Override
        protected void onPostExecute(Result<GetPostsResponse> getPostsResponseResult) {
            super.onPostExecute(getPostsResponseResult);

            if (getActivity() != null) {
                if (getPostsResponseResult != null && getPostsResponseResult.getResource() != null &&
                        getPostsResponseResult.getError() == null) {
                    mLoading = false;
                    mPosts = getPostsResponseResult.getResource().getPosts();
                    setupUserFeed();
                } else {
                    SnackbarManager.show(Snackbar.with(getActivity().getApplicationContext()).type(SnackbarType
                            .MULTI_LINE).text(getString(R.string.unable_to_get_posts_text)).actionLabel(getString(R
                            .string.retry)).actionListener(snackbar -> {
                        new GetUserPosts().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mProfileUserId);
                        SnackbarManager.dismiss();
                    }), getActivity());
                }
            }
        }
    }

    private class UnfollowUserAsync extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            return networkClient.unfollowUser(params[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mUnfollowing = true;
            mFollowLayout.setClickable(false);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (getActivity() != null) {
                if (aBoolean) {
                    mIsFollowing = false;
                    mFollowLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.conx_teal));
                    mFollowText.setText(getString(R.string.follow));
                    Snackbar.with(getActivity()).text(getString(R.string.unfollowed)).show(getActivity());
                    mProfileUser.setIsFollowing(false);
                    setFavoriteButtons();
                    new UpdateFollowersCountAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mProfileUserId);
                } else {
                    mFollowLayout.setClickable(true);
                    mUnfollowing = false;
                    SnackbarManager.show(Snackbar.with(getActivity().getApplicationContext()).type(SnackbarType
                            .MULTI_LINE).text(getString(R.string.unable_to_unfollow_user_text)).actionLabel(getString
                            (R.string.retry)).actionListener(snackbar -> {
                        new UnfollowUserAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf
                                (mProfileUser.getId()));
                        SnackbarManager.dismiss();
                    }), getActivity());
                }
            }
        }
    }

    public class UpdateFollowersCountAsync extends AsyncTask<String, Void, Result<GetUserResponse>> {

        @Override
        protected Result<GetUserResponse> doInBackground(String... params) {
            return networkClient.getUser(params[0]);
        }

        @Override
        protected void onPostExecute(Result<GetUserResponse> getProfileUserResponse) {
            super.onPostExecute(getProfileUserResponse);
            if (getActivity() != null) {
                if (getProfileUserResponse.getResource() != null && getProfileUserResponse.getError() == null) {
                    mProfileUser = getProfileUserResponse.getResource().getUser();
                    String followersText = mProfileUser.getFollowers() + "";
                    mFollowersCount.setText(followersText);
                    mFollowLayout.setClickable(true);
                    mUnfollowing = false;
                    mFollowing = false;
                } else {
                    SnackbarManager.show(Snackbar.with(getActivity().getApplicationContext()).type(SnackbarType
                            .MULTI_LINE).text(getString(R.string.unable_to_update_followers_count)).actionLabel
                            (getString(R.string.retry)).actionListener(snackbar -> {
                        new UpdateFollowersCountAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                                mProfileUserId);
                        SnackbarManager.dismiss();
                    }), getActivity());
                }
            }
        }
    }

    public class RefreshFeed extends AsyncTask<FeedDirection, Void, Result<GetPostsResponse>> {

        @Override
        protected Result<GetPostsResponse> doInBackground(FeedDirection... params) {
            String postId = "";
            FeedDirection direction = params[0];
            if (direction == FeedDirection.NEWER) {
                if (mPosts.size() > 0) {
                    postId = String.valueOf(mPosts.get(0).getId());
                }
            } else if (direction == FeedDirection.OLDER) {
                if (mPosts.size() > 0) {
                    postId = String.valueOf(mPosts.get(mPosts.size() - 1).getId());
                }
            }
            return networkClient.refreshUserFeed(direction.toString(), postId, String.valueOf(mProfileUser.getId()));
        }

        @Override
        protected void onPostExecute(Result<GetPostsResponse> getPostsResponseResult) {
            super.onPostExecute(getPostsResponseResult);

            mProfileFeedRecyclerView.scrollToPosition(0);

            if (getPostsResponseResult != null) {
                mSwipeContainer.setRefreshing(false);
                if (mDirection == FeedDirection.NEWER) {
                    ArrayList<Post> newerPosts = getPostsResponseResult.getResource().getPosts();
                    mPosts.addAll(0, newerPosts);
                    setupUserFeed();
                } else if (mDirection == FeedDirection.OLDER) {
                    ArrayList<Post> olderPosts = getPostsResponseResult.getResource().getPosts();
                    if (olderPosts.isEmpty()) {
                        mProfileFeedRecyclerView.setOnScrollListener(null);
                    } else {
                        mPosts.addAll(mPosts.size(), olderPosts);

                        int index = ((LinearLayoutManager) mProfileFeedRecyclerView.getLayoutManager())
                                .findFirstVisibleItemPosition();
                        View v = mProfileFeedRecyclerView.getChildAt(0);
                        int top = (v == null) ? 0 : v.getTop();
                        setupUserFeed();
                        mProfileFeedRecyclerView.scrollToPosition(index);
                    }

                }
                mLoading = false;
            }
        }
    }


    private void playEventVideo(String stream) {
        Intent intent = new Intent(this.getContext(), EventVideoActivity.class);
        intent.putExtra(EventActivity.EXTRA_EVENT_STREAM, stream);
        startActivity(intent);
    }


    private void favoriteUserAsync(String userId) {

        addSubscription(networkClient.favoriteUser(userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                            mFavoriteView.setEnabled(true);
                            mUnfilledFavoriteView.setEnabled(true);
                        },
                        throwable -> {
                            Snackbar.with(getActivity()).text(getString(R.string.user_favoriting_failed)).show(getActivity());
                            mFavoriteView.setVisibility(View.GONE);
                            mProfileUser.setIsFavorite(false);
                        }));
    }

    void unfavoriteUserAsync(String id) {
        addSubscription(networkClient.unfavoriteUser(id)
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(() -> {
                    mFavoriteView.setEnabled(true);
                    mUnfilledFavoriteView.setEnabled(true);
                })
                .subscribe(aVoid -> {
                }, throwable -> {
                    Snackbar.with(getActivity()).text(getString(R.string.user_unfavoriting_failed)).show(getActivity());
                    mFavoriteView.setVisibility(View.GONE);
                    mProfileUser.setIsFavorite(true);
                }));
    }
}
