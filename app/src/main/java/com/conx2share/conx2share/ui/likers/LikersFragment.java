package com.conx2share.conx2share.ui.likers;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.conx2share.conx2share.R;
import com.conx2share.conx2share.adapter.LikersAdapter;
import com.conx2share.conx2share.model.Like;
import com.conx2share.conx2share.model.SimpleLiker;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.ui.base.BaseFragment;
import com.conx2share.conx2share.ui.profile.ProfileActivity;
import com.conx2share.conx2share.ui.view.SimpleDividerItemDecoration;
import com.conx2share.conx2share.util.DateUtils;
import com.conx2share.conx2share.util.SnackbarUtil;
import com.nispok.snackbar.SnackbarManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

//import butterknife.InjectView;
import roboguice.inject.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LikersFragment extends BaseFragment {

    public static final String TAG = LikersFragment.class.getSimpleName();
    public static final String PROFILEID_KEY = "profileId";

    @Inject
    SnackbarUtil mSnackbarUtil;

    @Inject
    NetworkClient mNetworkClient;

    @InjectView(R.id.likers_recycler_view)
    RecyclerView mRecyclerView;

    @InjectView(R.id.likers_progress_bar)
    ProgressBar mProgressBar;

    private ArrayList<Like> mLikes;
    private ArrayList<SimpleLiker> mSimpleLikers;
    private int mPostId;
    private LikersAdapter mAdapter;

    public static LikersFragment newInstance() {
        return new LikersFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        return inflater.inflate(R.layout.fragment_likers, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity().getIntent().hasExtra(LikersActivity.EXTRA_POST_ID)) {
            mPostId = getActivity().getIntent().getIntExtra(LikersActivity.EXTRA_POST_ID, 0);
        } else {
            Log.w(TAG, "Failed to get post ID from extra");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        getLikers();
    }

    private void collectLikers(ArrayList<Like> likers) {
        if (likers == null || likers.size() < 1) return;
        if (mLikes == null) {
            mLikes = new ArrayList<>();
        } else {
            mLikes.clear();
        }
        Collections.sort(likers, new LikersComparator());
        mLikes.addAll(likers);
        setupFollowersAdapter();
    }

    public void setupFollowersAdapter() {
        if (mAdapter == null) {
            mAdapter = new LikersAdapter(extractLikerInformation(), this::showUserInfo);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void showUserInfo(Integer userID) {
        if (userID == null || userID < 1) return;
        Intent profileActivityIntent = new Intent(getActivity(), ProfileActivity.class);
        profileActivityIntent.putExtra(PROFILEID_KEY, String.valueOf(userID));
        startActivity(profileActivityIntent);
    }

    private List<SimpleLiker> extractLikerInformation() {
        if (mSimpleLikers == null) {
            mSimpleLikers = new ArrayList<>();
        } else {
            mSimpleLikers.clear();
        }
        for (Like like : mLikes) {
            mSimpleLikers.add(new SimpleLiker(like));
        }
        return mSimpleLikers;
    }

    protected void getLikers() {
        addSubscription(mNetworkClient.getPostWithLikers(String.valueOf(mPostId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(() -> {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                })
                .subscribe(getPostsResponse -> {
                            if (getPostsResponse != null && getPostsResponse.getPost() != null) {
                                collectLikers(getPostsResponse.getPost().getLikes());
                            } else {
                                Log.w(TAG, "No likers returned, at end of search");
                            }
                        },
                        throwable -> {
                            Log.e(TAG, "Error getting likers", throwable);
                            mSnackbarUtil.showSnackBarWithAction(getActivity(), R.string.unable_to_get_followers, R.string.retry,
                                    snackbar -> {
                                        getLikers();
                                        SnackbarManager.dismiss();
                                    });
                        }));
    }

    private class LikersComparator implements Comparator<Like> {
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DOB_SERVER_FORMAT);

        @Override
        public int compare(Like o1, Like o2) {
            if (o1 == null || o2 == null) return 0;
            if (o1.getUpdated_at() == null || o2.getUpdated_at() == null) return 0;
            try {
                if (sdf.parse(o1.getUpdated_at()).before(sdf.parse(o2.getUpdated_at()))) {
                    return 1;
                } else {
                    return -1;
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
        }
    }
}