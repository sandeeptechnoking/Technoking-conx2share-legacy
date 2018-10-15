package com.conx2share.conx2share.ui.business;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.conx2share.conx2share.R;
import com.conx2share.conx2share.adapter.PostsAdapter;
import com.conx2share.conx2share.async.BaseRetrofitAsyncTask;
import com.conx2share.conx2share.async.FollowingBusinessAsync;
import com.conx2share.conx2share.async.GetBusinessAsyncTask;
import com.conx2share.conx2share.async.GetBusinessFeedAsync;
import com.conx2share.conx2share.model.Business;
import com.conx2share.conx2share.model.Post;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.response.BusinessResponse;
import com.conx2share.conx2share.network.models.response.GetPostsResponse;
import com.conx2share.conx2share.strategies.DeletePostStrategy;
import com.conx2share.conx2share.ui.base.BaseProfileFragment;
import com.conx2share.conx2share.ui.events.EventsListActivity;
import com.conx2share.conx2share.ui.feed.post.PostActivity;
import com.conx2share.conx2share.ui.view.SimpleDividerItemDecoration;
import com.conx2share.conx2share.ui.web_view.WebViewActivity;
import com.conx2share.conx2share.ui.web_view.WebViewFragment;
import com.conx2share.conx2share.util.PreferencesUtil;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nispok.snackbar.SnackbarManager;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.OnClick;
import retrofit.RetrofitError;
import roboguice.inject.InjectView;

public class BusinessProfileFragment extends BaseProfileFragment {

    public static final String TAG = BusinessProfileFragment.class.getSimpleName();
    public static final String EXTRA_BUSINESS_ID = "extra_business_id";
    public static final String EXTRA_LOAD_STORE = "extra_load_store";

    @InjectView(R.id.toolbar_up)
    ImageView mToolbarUp;

    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout mSwipeContainer;

    @InjectView(R.id.follow_layout)
    View mFollowView;

    @InjectView(R.id.follow_text)
    TextView mFollowText;

    @InjectView(R.id.business_owner_sign)
    RoundedImageView mBusinessOwnerSign;

    @InjectView(R.id.business_status)
    TextView mBusinessStatus;

    @InjectView(R.id.main_view)
    private View mMainView;

    @InjectView(R.id.business_name)
    private TextView mBusinessNameTextView;

    @InjectView(R.id.business_avatar)
    private ImageView mProfileImageView;

    @InjectView(R.id.header_background)
    private ImageView mBackgroundImageView;

    @InjectView(R.id.posts_listView)
    private RecyclerView mPostRecyclerView;

    @InjectView(R.id.progress_bar)
    private ProgressBar mProgressBar;

    @InjectView(R.id.post_button)
    private FloatingActionButton mPostButton;

    @InjectView(R.id.eshopping_cart)
    private ImageView mEshoppingCart;

    @Inject
    private PreferencesUtil mPreferencesUtil;

    private GetBusinessAsyncTask mGetBusinessAsyncTask;
    private int mBusinessId;
    private ArrayList<Post> mPosts;
    private int mCurrentFeedPage;
    private BaseRetrofitAsyncTask mGetBusinessFeedAsyncTask;
    private Business mBusiness;
    private boolean loadStore;
    private boolean mDisableNewPosts;
    private BaseRetrofitAsyncTask mFollowingBusinessAsync;

    public static BusinessProfileFragment newInstance(int businessId) {
        BusinessProfileFragment fragment = new BusinessProfileFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(EXTRA_BUSINESS_ID, businessId);
        fragment.setArguments(arguments);
        return fragment;
    }

    public static BusinessProfileFragment newInstance(int businessId, boolean loadStore) {
        BusinessProfileFragment fragment = new BusinessProfileFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(EXTRA_BUSINESS_ID, businessId);
        arguments.putBoolean(EXTRA_LOAD_STORE, loadStore);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBusinessId = getArguments().getInt(EXTRA_BUSINESS_ID);
        loadStore = getArguments().getBoolean(EXTRA_LOAD_STORE, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        return inflater.inflate(R.layout.fragment_business_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getBusinessDetails();

        mToolbarUp.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        mSwipeContainer.setOnRefreshListener(() -> {
            if (mPosts != null && mPosts.size() > 0) {
                refreshFeed();
            } else {
                mSwipeContainer.setRefreshing(false);
            }
        });

        mPostButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), PostActivity.class);
                intent.putExtra(Business.EXTRA, mBusiness);
                getActivity().startActivity(intent);
            }
        });

        mFollowView.setOnClickListener(v -> {
            if (mBusiness.getIsFollowing()) {
                unfollowBusiness();
            } else {
                followBusiness();
            }
        });

        if (loadStore) {
            showShoppingCart();
            loadStore = false;
        }
    }

    private void followBusiness() {
        if (mFollowingBusinessAsync != null) {
            return;
        }

        mFollowText.setText("");
        mFollowView.setEnabled(false);

        mFollowingBusinessAsync = new FollowingBusinessAsync(getActivity(), false) {
            @Override
            protected void onSuccess(Result<BusinessResponse> result) {
                mBusiness = result.getResource().getBusiness();
                mBusiness.setIsFollowing(true);             // NOTE: the server returns a business, but the is_owner
                // and is_following fields are null
                if (getActivity() != null) {
                    adjustFollowingView();
                }
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.d(TAG, "could not follow business", error);
                if (getActivity() != null) {
                    mSnackbarUtil.showRetry(getActivity(), R.string.could_not_follow_business, snackbar -> {
                        followBusiness();
                        SnackbarManager.dismiss();
                    });
                }
            }

            @Override
            protected void onPostExecute(Result<BusinessResponse> result) {
                super.onPostExecute(result);
                mFollowView.setEnabled(true);
                mFollowingBusinessAsync = null;
            }
        }.executeInParallel(mBusiness.getId());
    }

    private void unfollowBusiness() {
        if (mFollowingBusinessAsync != null) {
            return;
        }

        mFollowText.setText("");
        mFollowView.setEnabled(false);

        mFollowingBusinessAsync = new FollowingBusinessAsync(getActivity(), true) {
            @Override
            protected void onSuccess(Result<BusinessResponse> result) {
                mBusiness = result.getResource().getBusiness();
                mBusiness.setIsFollowing(false);            // NOTE: the server returns a business, but the is_owner
                // and is_following fields are null
                if (getActivity() != null) {
                    adjustFollowingView();
                }
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.d(TAG, "could not unfollow business", error);
                if (getActivity() != null) {
                    mSnackbarUtil.showRetry(getActivity(), R.string.could_not_unfollow_business, snackbar -> {
                        unfollowBusiness();
                        SnackbarManager.dismiss();
                    });
                }
            }

            @Override
            protected void onPostExecute(Result<BusinessResponse> result) {
                super.onPostExecute(result);
                mFollowView.setEnabled(true);
                mFollowingBusinessAsync = null;
            }
        }.executeInParallel(mBusiness.getId());
    }

    @Override
    protected void refreshPosts() {
        getBusinessDetails();
    }

    @Override
    protected void scrollTo(int position) {
        if (mPostRecyclerView != null) mPostRecyclerView.smoothScrollToPosition(position);
    }

    @Override
    public void onNearingEndOfList() {
        getNextFeedPage();
    }

    @Override
    public void onLikeCountClicked(int postId) {

    }

    @Override
    public void onResume() {
        super.onResume();
        getBusinessDetails();
    }

    private void addPostsToList(ArrayList<Post> posts) {
        if (posts != null && posts.size() > 0) {
            mPosts.addAll(posts);
            mPostsAdapter.notifyDataSetChanged();
        } else {
            mDisableNewPosts = true;
        }
    }

    private void getNextFeedPage() {
        if (mGetBusinessFeedAsyncTask == null && !mDisableNewPosts) {
            mGetBusinessFeedAsyncTask = new GetBusinessFeedAsync(getActivity(), mBusinessId) {
                @Override
                protected void onSuccess(Result<GetPostsResponse> result) {
                    ArrayList<Post> posts = result.getResource().getPosts();
                    addPostsToList(posts);
                    mGetBusinessFeedAsyncTask = null;
                }

                @Override
                protected void onFailure(RetrofitError error) {
                    mCurrentFeedPage--;
                    Log.e(TAG, "Could not retrieve page " + mCurrentFeedPage + " for business " + mBusinessId, error);
                    mGetBusinessFeedAsyncTask = null;
                }
            }.executeInParallel(++mCurrentFeedPage);
        }
    }

    private void setCartVisibility() {
        if (mBusiness.getStore_url() != null && mBusiness.getStore_url().length() > 0) {
            mEshoppingCart.setVisibility(View.VISIBLE);
        } else {
            mEshoppingCart.setVisibility(View.GONE);
        }
        mEshoppingCart.setOnClickListener(v -> {
            showShoppingCart();
        });
    }

    private void showShoppingCart() {
        String url;
        url = mBusiness.getStore_url();

        Intent webViewIntent = new Intent(getActivity(), WebViewActivity.class);
        webViewIntent.putExtra(WebViewFragment.EXTRA_WEB_URI, url);
        webViewIntent.putExtra(WebViewActivity.EXTRA_SCREEN_TITLE, getString(R.string.drawer_eshopping));

        startActivity(webViewIntent);
    }

    private void refreshFeed() {
        if (mGetBusinessFeedAsyncTask != null) {
            mGetBusinessFeedAsyncTask.cancel(true);
        }

        mGetBusinessFeedAsyncTask = new GetBusinessFeedAsync(getActivity(), mBusinessId) {
            @Override
            protected void onSuccess(Result<GetPostsResponse> result) {
                mPosts.clear();
                ArrayList<Post> posts = result.getResource().getPosts();
                addPostsToList(posts);

                if (posts != null && posts.size() > 0) {
                    mPostRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
                    mPostRecyclerView.setAdapter(mPostsAdapter);
                    mSwipeContainer.setRefreshing(false);
                }
                mGetBusinessFeedAsyncTask = null;
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.e(TAG, "Could not retrieve posts for business " + mBusinessId, error);
                mSwipeContainer.setRefreshing(false);
                mCurrentFeedPage = 0;
                mGetBusinessFeedAsyncTask = null;
            }
        }.executeInParallel(1);
        mCurrentFeedPage = 1;
    }

    private void getBusinessDetails() {
        mProgressBar.setVisibility(View.VISIBLE);
        mMainView.setVisibility(View.GONE);

        if (mGetBusinessAsyncTask != null) {
            mGetBusinessAsyncTask.cancel(true);
        }

        mGetBusinessAsyncTask = new GetBusinessAsyncTask(getActivity(), mBusinessId) {
            @Override
            protected void onSuccess(Result<BusinessResponse> result) {
                if (getActivity() != null) {
                    mProgressBar.setVisibility(View.GONE);
                    mMainView.setVisibility(View.VISIBLE);

                    handleBusinessResult(result.getResource().getBusiness());
                    refreshFeed();
                    mGetBusinessAsyncTask = null;
                }
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.e(TAG, "GET BUSINESS failure", error);

                if (getActivity() != null) {
                    mProgressBar.setVisibility(View.GONE);

                    mSnackbarUtil.showSnackBarWithAction(getActivity(), R.string.unable_to_get_business_info, R
                            .string.retry, snackbar -> {
                        getBusinessDetails();
                        SnackbarManager.dismiss();
                    });

                } else {
                    Log.e(TAG, "Activity is null");
                }
                mGetBusinessAsyncTask = null;
            }
        }.executeInParallel();
    }

    private void handleBusinessResult(Business business) {
        mBusiness = business;
        mPosts = new ArrayList<>();

        mPostsAdapter = new PostsAdapter(getActivity(), mPosts, this, true, mPreferencesUtil
                .getAuthUser().getId(), null, mBusiness);

        setCartVisibility();

        mBusinessNameTextView.setText(business.getName());
        mBusinessNameTextView.setShadowLayer(4, -1, -0.3f, Color.BLACK);

        setAboutText(business.getAbout());
        if (business.getAvatarUrl() != null) {
            Glide.with(getActivity()).load(business.getAvatarUrl()).dontAnimate().into(mBackgroundImageView);
            Glide.with(getActivity()).load(business.getAvatarUrl()).dontAnimate().into(mProfileImageView);
        }

        adjustFollowingView();

        if (mBusiness.getIsOwner() != null && mBusiness.getIsOwner()) {
            mPostButton.setVisibility(View.VISIBLE);

            mBusinessOwnerSign.setVisibility(View.VISIBLE);
            mBusinessStatus.setText(getString(R.string.business_owner));
        } else {
            mPostButton.setVisibility(View.GONE);

            mBusinessOwnerSign.setVisibility(View.GONE);
            mBusinessStatus.setText(getString(R.string.empty_string));
        }
    }

    private void adjustFollowingView() {
        if (getActivity() != null) {
            if (mBusiness.getIsOwner() != null && mBusiness.getIsOwner()) {
                mFollowView.setVisibility(View.GONE);
            } else {
                mFollowView.setVisibility(View.VISIBLE);
                if (mBusiness.getIsFollowing()) {
                    mFollowText.setText(R.string.unfollow);
                    mFollowView.setBackgroundColor(getActivity().getResources().getColor(R.color
                            .profile_unfollow_gray));
                } else {
                    mFollowText.setText(R.string.follow);
                    mFollowView.setBackgroundColor(getActivity().getResources().getColor(R.color.conx_teal));
                }
            }
        }
    }

    public void onEventMainThread(DeletePostStrategy.LoadDeletePostSuccessEvent event) {
        Log.d(TAG, "Received a delete post event");
        getBusinessDetails();
    }

    @OnClick(R.id.event_link)
    public void eventsClicked() {
        launchEventsIndexActivity();
    }

    private void launchEventsIndexActivity() {
        Intent intent = new Intent(getActivity(), EventsListActivity.class);
        intent.putExtra(EventsListActivity.EXTRA_GROUP_ID, mBusiness.getId());
        intent.putExtra(EventsListActivity.EXTRA_GROUP_OWNER, mBusiness.getIsOwner());
        intent.putExtra(EventsListActivity.EXTRA_GROUP_TYPE, "business");
        getActivity().startActivity(intent);
    }
}
