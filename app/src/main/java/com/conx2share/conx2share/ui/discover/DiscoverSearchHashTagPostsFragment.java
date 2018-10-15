package com.conx2share.conx2share.ui.discover;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.adapter.DiscoverHashTagPostsAdapter;
import com.conx2share.conx2share.async.GetHashTagFeedAsync;
import com.conx2share.conx2share.model.FeedDirection;
import com.conx2share.conx2share.model.Post;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.param.GetHashTagFeedParams;
import com.conx2share.conx2share.network.models.response.GetPostsResponse;
import com.conx2share.conx2share.ui.base.BaseFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import retrofit.RetrofitError;
import roboguice.inject.InjectView;

public class DiscoverSearchHashTagPostsFragment extends BaseFragment implements DiscoverHashTagPostsAdapter.DiscoverHashTagPostsAdapterCallBacks {

    public static final String TAG = DiscoverSearchHashTagPostsFragment.class.getSimpleName();

    @InjectView(R.id.discover_hash_tag_posts_recycler_view)
    RecyclerView mDiscoverHashTagPostsRecyclerView;

    @InjectView(R.id.discover_hash_tag_posts_progress_bar)
    ProgressBar mDiscoverHashTagPostsProgressBar;

    private String mSearchTerms;

    private ArrayList<Integer> mPostIdsUsedInSearch;

    private ArrayList<Post> mPosts;

    private GetHashTagFeedAsync mGetHashTagFeedAsync;

    private DiscoverHashTagPostsAdapter mDiscoverHashTagPostsAdapter;

    public static Fragment newInstance() {
        return new DiscoverSearchHashTagPostsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discover_search_hash_tag_posts, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPosts = new ArrayList<>();
        mPostIdsUsedInSearch = new ArrayList<>();
        getHashTagFeed(new GetHashTagFeedParams(null));
    }

    public void onEventMainThread(DiscoverFragment.LoadDiscoverSearchEvent event) {
        mDiscoverHashTagPostsProgressBar.setVisibility(View.VISIBLE);
        mSearchTerms = event.getSearchTerms();
        if (mSearchTerms != null) {
            mSearchTerms = mSearchTerms.replace("#", "");
        }
        Log.d(TAG, "Received a discover search event. Search terms: " + mSearchTerms);
        if (mPostIdsUsedInSearch != null) {
            mPostIdsUsedInSearch.clear();
        }
        if (mPosts != null) {
            mPosts.clear();
        }
        setupHashTagPostListViewAdapter();
        if (getActivity() != null) {
            getHashTagFeed(new GetHashTagFeedParams(mSearchTerms));
        }
    }

    private void collectAndSortHashTagPosts(ArrayList<Post> newPosts) {
        for (Post newPost : newPosts) {
            if (!mPosts.contains(newPost)) {
                mPosts.add(newPost);
            }
        }

        setupHashTagPostListViewAdapter();
    }

    private void setupHashTagPostListViewAdapter() {
        mDiscoverHashTagPostsRecyclerView.setHasFixedSize(true);

        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        mDiscoverHashTagPostsRecyclerView.setLayoutManager(gridLayoutManager);
        if (mDiscoverHashTagPostsRecyclerView.getAdapter() == null) {
            mDiscoverHashTagPostsAdapter = new DiscoverHashTagPostsAdapter(mPosts, this);//new DiscoverHashTagPostsAdapter(getActivity(), this, mPosts);
            mDiscoverHashTagPostsRecyclerView.setAdapter(mDiscoverHashTagPostsAdapter);
        } else {
            mDiscoverHashTagPostsAdapter.notifyDataSetChanged();
        }
    }

    protected void getHashTagFeed(final GetHashTagFeedParams params) {
        if (mGetHashTagFeedAsync != null) {
            mGetHashTagFeedAsync.cancel(true);
        }

        mGetHashTagFeedAsync = new GetHashTagFeedAsync(getActivity()) {
            @Override
            protected void onSuccess(Result<GetPostsResponse> result) {
                if (result != null && result.getResource() != null && result.getResource().getPosts() != null && result.getResource().getPosts().size() > 0) {
                    Log.d(TAG, "# of posts for hash tag feed: " + result.getResource().getPosts().size());
                    collectAndSortHashTagPosts(result.getResource().getPosts());
                } else {
                    Log.w(TAG, "No posts were returned for the hash tag");
                }
                mPostIdsUsedInSearch.add(params.getPostId());
                mDiscoverHashTagPostsProgressBar.setVisibility(View.GONE);
                mDiscoverHashTagPostsRecyclerView.setVisibility(View.VISIBLE);
                mGetHashTagFeedAsync = null;
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.w(TAG, "Error retrieving posts for hash tag", error);
                mDiscoverHashTagPostsProgressBar.setVisibility(View.GONE);
                mDiscoverHashTagPostsRecyclerView.setVisibility(View.VISIBLE);
                mGetHashTagFeedAsync = null;
            }
        }.executeInParallel(params);
    }

    @Override
    public void onNearingEndOfList(Post post) {
        if (mPostIdsUsedInSearch != null && !mPostIdsUsedInSearch.contains(post.getId())) {
            GetHashTagFeedParams params = new GetHashTagFeedParams(mSearchTerms, post.getId(), FeedDirection.OLDER.toString());
            getHashTagFeed(params);
        }
    }
}
