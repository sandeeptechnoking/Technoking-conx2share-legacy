package com.conx2share.conx2share.ui.discover;

import com.google.inject.Inject;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.adapter.GroupIndexAdapter;
import com.conx2share.conx2share.async.SearchGroupsAsync;
import com.conx2share.conx2share.model.Group;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.param.SearchParams;
import com.conx2share.conx2share.network.models.response.GetGroupListResponse;
import com.conx2share.conx2share.ui.base.BaseFragment;
import com.conx2share.conx2share.ui.groups.GroupActivity;
import com.conx2share.conx2share.util.PreferencesUtil;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import retrofit.RetrofitError;
import roboguice.inject.InjectView;

public class DiscoverSearchGroupsFragment extends BaseFragment implements GroupIndexAdapter.GroupIndexAdapterCallbacks {

    public static final String TAG = DiscoverSearchGroupsFragment.class.getSimpleName();

    @Inject
    PreferencesUtil mPreferencesUtil;

    @InjectView(R.id.discover_groups_list_view)
    ListView mDiscoverGroupsListView;

    @InjectView(R.id.discover_groups_progress_bar)
    ProgressBar mDiscoverGroupsProgressBar;

    private String mSearchTerms;

    private Integer mCurrentUserSearchPage;

    private ArrayList<Group> mGroups;

    private SearchGroupsAsync mSearchGroupsAsync;

    private GroupIndexAdapter mGroupIndexAdapter;

    private boolean mAtEndOfSearch;

    public static Fragment newInstance() {
        return new DiscoverSearchGroupsFragment();
    }

    public void onEventMainThread(DiscoverFragment.LoadDiscoverSearchEvent event) {
        mDiscoverGroupsProgressBar.setVisibility(View.VISIBLE);
        mDiscoverGroupsListView.setVisibility(View.GONE);
        mSearchTerms = event.getSearchTerms();
        Log.d(TAG, "Received a discover search event. Search terms: " + mSearchTerms);
        if (mGroups != null) {
            mGroups.clear();
        }
        mAtEndOfSearch = false;
        setupGroupsListViewAdapter();
        mCurrentUserSearchPage = 1;
        if (getActivity() != null) {
            searchGroups(new SearchParams(mSearchTerms, mCurrentUserSearchPage));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discover_search_groups, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDiscoverGroupsListView.setOnItemClickListener((parent, view1, position, id) -> {
            if (getActivity() != null) {
                Intent groupActivityIntent = new Intent(getActivity(), GroupActivity.class);
                groupActivityIntent.putExtra(GroupActivity.EXTRA_GROUP_ID, mGroups.get(position).getId());
                getActivity().startActivity(groupActivityIntent);
            }
        });

        mGroups = new ArrayList<>();
        mCurrentUserSearchPage = 1;
        searchGroups(new SearchParams(null, mCurrentUserSearchPage));
    }

    private void setupGroupsListViewAdapter() {
        if (mDiscoverGroupsListView.getAdapter() == null) {
            mGroupIndexAdapter = new GroupIndexAdapter(mGroups, getActivity(), this, mPreferencesUtil.getAuthUser().getId());
            mDiscoverGroupsListView.setAdapter(mGroupIndexAdapter);
        } else {
            mGroupIndexAdapter.notifyDataSetChanged();
        }
    }

    private void collectAndSortGroups(ArrayList<Group> newGroups) {
        for (Group newGroup : newGroups) {
            if (!mGroups.contains(newGroup)) {
                mGroups.add(newGroup);
            }
        }

        setupGroupsListViewAdapter();
    }

    protected void searchGroups(SearchParams searchParams) {
        if (mSearchGroupsAsync != null) {
            mSearchGroupsAsync.cancel(true);
        }

        mSearchGroupsAsync = new SearchGroupsAsync(getActivity()) {
            @Override
            protected void onSuccess(Result<GetGroupListResponse> result) {
                if (result.getResource().getGroups() != null && result.getResource().getGroups().size() > 0) {
                    collectAndSortGroups(result.getResource().getGroups());
                    if (result.getResource().getGroups().size() < 20) {
                        Log.w(TAG, "A page with less than 20 entries was returned, at end of search");
                        mAtEndOfSearch = true;
                    }
                } else {
                    Log.w(TAG, "No groups returned in search");
                    mAtEndOfSearch = true;
                }
                mDiscoverGroupsProgressBar.setVisibility(View.GONE);
                mDiscoverGroupsListView.setVisibility(View.VISIBLE);
                mSearchGroupsAsync = null;
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.e(TAG, "Searching for groups failed", error);
                mCurrentUserSearchPage--;
                mDiscoverGroupsProgressBar.setVisibility(View.GONE);
                mDiscoverGroupsListView.setVisibility(View.VISIBLE);
                mSearchGroupsAsync = null;
            }
        }.executeInParallel(searchParams);
    }

    @Override
    public void onNearingEndOfList() {
        if (!mAtEndOfSearch) {
            Log.d(TAG, "Grabbing page " + (mCurrentUserSearchPage + 1));
            searchGroups(new SearchParams(mSearchTerms, mCurrentUserSearchPage++));
        }
    }
}