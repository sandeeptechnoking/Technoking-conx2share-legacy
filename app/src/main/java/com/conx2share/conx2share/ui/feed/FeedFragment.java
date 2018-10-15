package com.conx2share.conx2share.ui.feed;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.conx2share.conx2share.BuildConfig;
import com.conx2share.conx2share.R;
import com.conx2share.conx2share.adapter.FriendsAdapter;
import com.conx2share.conx2share.adapter.PostsAdapter;
import com.conx2share.conx2share.model.AuthUser;
import com.conx2share.conx2share.model.FeedDirection;
import com.conx2share.conx2share.model.Friend;
import com.conx2share.conx2share.model.Message;
import com.conx2share.conx2share.model.Post;
import com.conx2share.conx2share.model.event.UpdatePostEvent;
import com.conx2share.conx2share.model.event.UpdateProfileImageEvent;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.network.models.User;
import com.conx2share.conx2share.strategies.DeletePostStrategy;
import com.conx2share.conx2share.ui.base.BasePostListFragment;
import com.conx2share.conx2share.ui.feed.post.PostActivity;
import com.conx2share.conx2share.ui.likers.LikersActivity;
import com.conx2share.conx2share.ui.livestream.StreamHomeActivity;
import com.conx2share.conx2share.ui.view.DeleteInsideView;
import com.conx2share.conx2share.ui.view.SimpleDividerItemDecoration;
import com.conx2share.conx2share.util.EmergencyUtil;
import com.conx2share.conx2share.util.ForegroundUtil;
import com.conx2share.conx2share.util.PreferencesUtil;
import com.conx2share.conx2share.util.SnackbarUtil;
import com.github.clans.fab.FloatingActionMenu;
import com.mopub.nativeads.MoPubNativeAdPositioning;
import com.mopub.nativeads.MoPubRecyclerAdapter;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.ViewBinder;
import com.nispok.snackbar.SnackbarManager;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
//import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FeedFragment extends BasePostListFragment implements PostsAdapter.PostAdapterCallback, FriendsAdapter.StartDragListener {

    public static final String TAG = FeedFragment.class.getSimpleName();
    private static final String DELETE_VIEW_TAG = "com.conx2share.conx2share.ui.feed.delete_view";

    @Inject
    PreferencesUtil preferencesUtil;

    @Inject
    NetworkClient networkClient;

    @Inject
    SnackbarUtil mSnackbarUtil;

    @BindView(R.id.friend_recycler_view)
    RecyclerView mFriendRecyclerView;

    @BindView(R.id.feed_list_view)
    RecyclerView mFeedListView;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout mSwipeContainer;

    @BindView(R.id.feed_progress_bar)
    ProgressBar mFeedProgressBar;

    @BindView(R.id.multiple_actions)
    FloatingActionMenu multipleActionsFab;

    @BindView(R.id.add_friend_iv)
    ImageView addFriendIv;

    @BindView(R.id.friends_add_dismiss_bt)
    ImageView friendsAddDismissBt;

    @BindView(R.id.star_friend_left_iv)
    ImageView starFriendLeftIv;

    @BindView(R.id.friends_add_description_rl)
    RelativeLayout friendsAddDescriptionRl;

    @BindView(R.id.friends_list_add_layout)
    RelativeLayout friendsListAddLayout;

    @BindView(R.id.feed_fragment_root_layout)
    RelativeLayout feedFragmentRootLayout;

    private RecyclerView.Adapter mAdapter;
    private ArrayList<User> mFriends;
    private ArrayList<Message> mMessages;
    private ArrayList<Post> mPosts = new ArrayList<>();
    private FeedDirection mDirection;
    private boolean mLoading = true;
    private boolean mInitialCreateFriendData = false;
    private boolean mGoToCreatePost = false;
    private MoPubRecyclerAdapter mAdAdapter;
    private int mFeedPage = 1;
    private int mMaxPage = 0;
    private int mStreamer;
    Unbinder unbinder;

    public static FeedFragment newInstance() {
        return new FeedFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
         unbinder =  ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFriendRecyclerView.setHasFixedSize(true);

        mSwipeContainer.setOnRefreshListener(() -> {
            mDirection = FeedDirection.NEWER;
            refreshGroupFeed(FeedDirection.NEWER);
            getFavoritesAsync();
            getFriendsAsync();
        });

        mFeedListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    final int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                            .findLastVisibleItemPosition();

                    if (recyclerView.getAdapter() != null && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1
                            && mMaxPage != mFeedPage) {
                        if (!mLoading) {
                            mLoading = true;
                            getFeedPage();
                        }
                    }
                }
            }
        });

        if (getActivity() != null) {
            if (mFeedProgressBar == null) {
                mFeedProgressBar = new ProgressBar(getActivity());
            }
            getFavoritesAsync();
            refreshPosts();
            if (mInitialCreateFriendData) {
                getUnreadMessages();

                if (mGoToCreatePost) {
                    mDirection = FeedDirection.NEWER;
                    refreshGroupFeed(FeedDirection.NEWER);
                }
            }
        }
        feedFragmentRootLayout.setOnDragListener((v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_ENDED:
                case DragEvent.ACTION_DROP:
                    removeDeleteInsideViewFromScreen();
                    break;
            }
            return true;
        });
    }

    private void removeDeleteInsideViewFromScreen() {
        ViewGroup viewGroup = (ViewGroup) getActivity().getWindow().getDecorView();
        View deleteInsideView = viewGroup.findViewWithTag(DELETE_VIEW_TAG);
        if (deleteInsideView != null) {
            viewGroup.removeView(deleteInsideView);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getFriendsAsync();
    }

    @Override
    public void onPause() {
        super.onPause();
        removeDeleteInsideViewFromScreen();
    }

    @OnClick(R.id.post_button)
    public void postButtonClicked() {
        mGoToCreatePost = true;
        Intent createPostIntent = new Intent(getActivity(), PostActivity.class);
        startActivity(createPostIntent);
    }

    @OnClick(R.id.livestream_button)
    public void livestreamButtonClicked() {
        StreamHomeActivity.startFromHome(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mAdAdapter != null) {
            mAdAdapter.destroy();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (multipleActionsFab.isOpened()) multipleActionsFab.close(false);
    }

    @Override
    protected void refreshPosts() {
        getNewestPosts();
    }

    @Override
    protected void scrollTo(int position) {
        if (mFeedListView != null) mFeedListView.scrollToPosition(position);
    }

    public void createFriendData() {
        ArrayList<Friend> friendList = new ArrayList<>();

        if (mFriends != null && mFriends.size() > 0) {
            for (User friend : mFriends) {
                if (friend.getIsFavorite()) {

                    Log.d(TAG, "Friend: " + friend.getFirstName() + " " + friend.getLastName());
                    friendList.add(new Friend(friend.getId(), 0, friend.getFirstName(), friend.getLastName(), friend
                            .getAvatar().getAvatar().getUrl(), null, friend.getUsername()));
                }
            }
            friendList.add(Friend.getStarFriendObject());
            if (friendList.size() > 0) {
                mAdapter = new FriendsAdapter(friendList, getActivity(), this);
                mFriendRecyclerView.setAdapter(mAdapter);
            }
        }
        manageFriendsBarState();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void setupFeedListView() {
        if (getActivity() != null) {
            AuthUser authUser = preferencesUtil.getAuthUser();
            if (authUser != null && authUser.getId() != null) {
                mStreamer = authUser.getId();

                if (mPostsAdapter == null) {
                    mPostsAdapter = new PostsAdapter(getActivity(), mPosts,
                            this, false, authUser.getId(), null, null);
                    ViewBinder viewBinder = new ViewBinder.Builder(R.layout.native_ad_layout)
                            .titleId(R.id.native_ad_title)
                            .mainImageId(R.id.native_ad_main_image)
                            .build();

                    MoPubNativeAdPositioning.MoPubServerPositioning adPositioning = MoPubNativeAdPositioning
                            .serverPositioning();
                    MoPubStaticNativeAdRenderer adRenderer = new MoPubStaticNativeAdRenderer(viewBinder);
                    mAdAdapter = new MoPubRecyclerAdapter(getActivity(), mPostsAdapter, adPositioning);
                    mAdAdapter.registerAdRenderer(adRenderer);
                    mAdAdapter.loadAds(BuildConfig.AD_UNIT_ID);
                    mFeedListView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
                    mFeedListView.setAdapter(mAdAdapter);
                }
                mPostsAdapter.notifyDataSetChanged();
            } else {
                EmergencyUtil.emergencyLogoutWithNotification(getActivity(), preferencesUtil);
            }
        }
    }

    private void manageFriendsBarState() {
        if ((mFriendRecyclerView.getAdapter() != null && mFriendRecyclerView.getAdapter().getItemCount() > 1) |
                preferencesUtil.isFriendsDontShowAgain()) {
            mFriendRecyclerView.setVisibility(View.VISIBLE);
            addFriendIv.setVisibility(View.GONE);
            friendsAddDescriptionRl.setVisibility(View.GONE);
        } else {
            if (friendsAddDescriptionRl.getVisibility() != View.VISIBLE) {
                addFriendIv.setVisibility(View.VISIBLE);
                friendsAddDescriptionRl.setVisibility(View.INVISIBLE);
                addFriendIv.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shake));
            }
            mFriendRecyclerView.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.add_friend_iv)
    void onShakingStarClick() {
        addFriendIv.clearAnimation();
        int[] oldPosition = new int[2];
        int[] newPosition = new int[2];
        addFriendIv.getLocationOnScreen(oldPosition);
        starFriendLeftIv.getLocationOnScreen(newPosition);
        addFriendIv.animate()
                .x(newPosition[0])
                .setDuration(400)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mFriendRecyclerView.setVisibility(View.GONE);
                        addFriendIv.setVisibility(View.GONE);
                        friendsAddDescriptionRl.setVisibility(View.VISIBLE);
                    }
                });
    }

    @OnClick(R.id.friends_add_dismiss_bt)
    void onRedCrossClick() {
        preferencesUtil.setFriendsBarDontShowAgain(true);
        mFriendRecyclerView.setVisibility(View.VISIBLE);
        addFriendIv.setVisibility(View.GONE);
        friendsAddDescriptionRl.setVisibility(View.GONE);
    }

    public void onEventMainThread(DeletePostStrategy.LoadDeletePostSuccessEvent event) {
        Log.d(TAG, "Received a delete post success event");
        if (ForegroundUtil.getAppInForeground() && getActivity() != null) {
            Post deletedPost = event.getPost();
            if (deletedPost != null && mPostsAdapter != null) {
                mPostsAdapter.removePost(deletedPost);
            }
        }
    }

    public void onEventMainThread(UpdateProfileImageEvent event) {
        getFavoritesAsync();
        getNewestPosts();
    }

    public void onEventMainThread(UpdatePostEvent event) {
        if (event.getUpdateType() == UpdatePostEvent.PostEventType.COMMENT_COUNT) {
            Post post = mPosts.get(event.getPosition());
            post.setCommentCount(post.getCommentCount() + 1);
            mPostsAdapter.notifyDataSetChanged();
        } else if (event.getUpdateType() == UpdatePostEvent.PostEventType.LIKE_COUNT_INCREASE) {
            Post post = mPosts.get(event.getPosition());
            post.setLikesCount(post.getLikesCount() + 1);
            post.setHasLiked(true);
            mPostsAdapter.notifyDataSetChanged();
        } else if (event.getUpdateType() == UpdatePostEvent.PostEventType.LIKE_COUNT_DECREASE) {
            Post post = mPosts.get(event.getPosition());
            // To prevent negatives
            if (post.getLikesCount() > 0) {
                post.setLikesCount(post.getLikesCount() - 1);
            }
            post.setHasLiked(false);
            mPostsAdapter.notifyDataSetChanged();
        } else if (event.getUpdateType() == UpdatePostEvent.PostEventType.POST_BODY_WAS_CHANGED) {
            mPosts.set(event.getPosition(), event.getPost());
            mPostsAdapter.notifyItemChanged(event.getPosition());
        }
    }

    @Override
    public void onLikeCountClicked(int postId) {
        startActivity(new Intent(getActivity(), LikersActivity.class)
                .putExtra(LikersActivity.EXTRA_POST_ID, postId));
    }

    private void getFavoritesAsync() {

        addSubscription(networkClient.getFavorites()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getFriendsResponse -> getUnreadMessages(),
                        throwable -> mSnackbarUtil.showSnackBarWithAction(getActivity(),
                                R.string.unable_to_get_friends_text, R.string.retry, snackbar -> {
                                    getFavoritesAsync();
                                    SnackbarManager.dismiss();
                                })));
    }

    @Override
    public void onStartFriendsDrag() {
        DeleteInsideView deleteInsideView = new DeleteInsideView(getActivity(), id -> {
            if (id == 0) return;
            addSubscription(networkClient.unfavoriteUser(String.valueOf(id))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            aVoid -> getFriendsAsync(),
                            throwable -> mSnackbarUtil.displaySnackBar(getActivity(), R.string.friend_wasnt_deleted))
            );
        });
        deleteInsideView.setTag(DELETE_VIEW_TAG);
        ViewGroup viewGroup = (ViewGroup) getActivity().getWindow().getDecorView();
        viewGroup.addView(deleteInsideView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void getUnreadMessages() {
        addSubscription(networkClient
                .getUnreadMessages()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(messagesResponse -> {
                    if (getActivity() != null) {
                        if (messagesResponse != null) {
                            mMessages = messagesResponse.getMessages();
                        } else {
                            Log.e(TAG, "Could not get unread messages");
                            mSnackbarUtil.showSnackBarWithAction(getActivity(), R.string.unable_to_get_unread_messages_text,
                                    R.string.retry, snackbar -> {
                                        getUnreadMessages();
                                        SnackbarManager.dismiss();
                                    });
                        }
                    }
                }, throwable -> Log.e(TAG, "Could not get unread messages", throwable)));
    }

    public void getNewestPosts() {
        showProgressBarIfNeeded();

        addSubscription(networkClient.getNewestPosts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(() -> {
                    mFeedProgressBar.setVisibility(View.GONE);
                    mFeedListView.setVisibility(View.VISIBLE);
                    mLoading = false;
                })
                .subscribe(getPostsResponse -> {
                    mPosts.clear();
                    mPosts.addAll(getPostsResponse.getPosts());
                    mFeedPage = Integer.getInteger(getPostsResponse.getMeta().currentPage, mFeedPage);
                    mMaxPage = getPostsResponse.getMeta().totalPages;
                    setupFeedListView();
                }, throwable ->
                        mSnackbarUtil.showSnackBarWithAction(getActivity(),
                                R.string.unable_to_get_posts_text, R.string.retry, snackbar -> {
                                    getNewestPosts();
                                    SnackbarManager.dismiss();
                                })));
    }

    public void refreshGroupFeed(FeedDirection feedDirection) {
        mSwipeContainer.setRefreshing(true);
        mLoading = true;
        String postId = "";
        if (mPosts != null && mPosts.size() <= 0) {
            postId = null;
        } else if (feedDirection == FeedDirection.NEWER) {
            if (mPosts != null) {
                postId = String.valueOf(mPosts.get(0).getId());
            } else {
                postId = null;
            }
        }
        addSubscription(networkClient.refreshFeed(feedDirection.toString(), postId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(() -> {
                    mSwipeContainer.setRefreshing(false);
                    mLoading = false;
                })
                .subscribe(getPostsResponse -> {
                    mGoToCreatePost = false;
                    mFeedListView.scrollToPosition(0);

                    if (mDirection == FeedDirection.NEWER) {
                        ArrayList<Post> newerPosts = getPostsResponse.getPosts();
                        mPosts.clear();
                        mPosts.addAll(0, newerPosts);
                        mFeedPage = Integer.getInteger(getPostsResponse.getMeta().currentPage, 1);
                        mMaxPage = getPostsResponse.getMeta().totalPages;
                        setupFeedListView();
                    }
                }, throwable -> mSnackbarUtil.displaySnackBar(getActivity(), R.string.unable_to_refresh_feed_text)));
    }

    public void getFriendsAsync() {
        addSubscription(networkClient.getFriends(true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getFriendsResponse -> {
                            mFriends = getFriendsResponse != null ? getFriendsResponse.getUsers() : null;
                            mInitialCreateFriendData = true;
                            createFriendData();
                        },
                        throwable -> {
                            manageFriendsBarState();
                        }));
    }

    private void showProgressBarIfNeeded() {
        if (mPostsAdapter == null) {
            mFeedProgressBar.setVisibility(View.VISIBLE);
            mFeedListView.setVisibility(View.GONE);
        } else {
            mFeedListView.setVisibility(View.VISIBLE);
            mFeedProgressBar.setVisibility(View.GONE);
        }
    }

    public void getFeedPage() {
        mSwipeContainer.setRefreshing(true);
        mLoading = true;
        addSubscription(networkClient.getFeedPage(mFeedPage + 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(() -> {
                    mLoading = false;
                    mSwipeContainer.setRefreshing(false);
                })
                .subscribe(getPostsResponse -> {
                            mGoToCreatePost = false;
                            mFeedListView.scrollToPosition(0);
                            ArrayList<Post> olderPosts = getPostsResponse.getPosts();
                            if (!olderPosts.isEmpty()) {
                                mPosts.addAll(mPosts.size(), olderPosts);
                                final int index = ((LinearLayoutManager) mFeedListView.getLayoutManager())
                                        .findFirstVisibleItemPosition();
//                                View v = mFeedListView.getChildAt(0);
//                                int top = (v == null) ? 0 : v.getTop();
                                setupFeedListView();
                                mFeedListView.scrollToPosition(index);
                                mFeedPage = Integer.getInteger(getPostsResponse.getMeta().currentPage, mFeedPage + 1);
                                mMaxPage = getPostsResponse.getMeta().totalPages;
                            }
                        },
                        throwable -> {
                        }));

    }
}
