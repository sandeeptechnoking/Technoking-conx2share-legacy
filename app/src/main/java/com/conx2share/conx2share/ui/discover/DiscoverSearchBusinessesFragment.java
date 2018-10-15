package com.conx2share.conx2share.ui.discover;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.adapter.BusinessAdapter;
import com.conx2share.conx2share.async.SearchBusinessesAsync;
import com.conx2share.conx2share.model.Business;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.param.SearchParams;
import com.conx2share.conx2share.network.models.response.BusinessesResponse;
import com.conx2share.conx2share.ui.base.BaseFragment;

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

public class DiscoverSearchBusinessesFragment extends BaseFragment implements BusinessAdapter.BusinessAdapterCallbacks {

    public static final String TAG = DiscoverSearchBusinessesFragment.class.getSimpleName();

    @InjectView(R.id.discover_businesses_list_view)
    ListView mDiscoverBusinessesListView;

    @InjectView(R.id.discover_businesses_progress_bar)
    ProgressBar mDiscoverBusinessesProgressBar;

    private Integer mCurrentUserSearchPage;

    private String mSearchTerms;

    private ArrayList<Business> mBusinesses;

    private SearchBusinessesAsync mSearchBusinessesAsync;

    private BusinessAdapter mBusinessAdapter;

    private boolean mAtEndOfSearch;

    public static Fragment newInstance() {
        return new DiscoverSearchBusinessesFragment();
    }

    public void onEventMainThread(DiscoverFragment.LoadDiscoverSearchEvent event) {
        mDiscoverBusinessesProgressBar.setVisibility(View.VISIBLE);
        mDiscoverBusinessesListView.setVisibility(View.GONE);
        mSearchTerms = event.getSearchTerms();
        Log.d(TAG, "Received a discover search event. Search terms: " + event.getSearchTerms());
        if (mBusinesses != null) {
            mBusinesses.clear();
        }
        mAtEndOfSearch = false;
        setupBusinessesListViewAdapter();
        mCurrentUserSearchPage = 1;
        if (getActivity() != null) {
            searchBusinesses(new SearchParams(mSearchTerms, mCurrentUserSearchPage));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discover_search_businesses, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBusinesses = new ArrayList<>();
        mCurrentUserSearchPage = 1;
        searchBusinesses(new SearchParams(null, mCurrentUserSearchPage));
    }

    private void collectAndSortBusinesses(ArrayList<Business> newBusinesses) {
        for (Business newBusiness : newBusinesses) {
            if (!mBusinesses.contains(newBusiness)) {
                mBusinesses.add(newBusiness);
            }
        }

        setupBusinessesListViewAdapter();
    }

    private void setupBusinessesListViewAdapter() {
        if(getActivity() != null) {
            if (mDiscoverBusinessesListView.getAdapter() == null) {
                mBusinessAdapter = new BusinessAdapter(mBusinesses, getActivity(), this);
                mDiscoverBusinessesListView.setAdapter(mBusinessAdapter);
            } else {
                mBusinessAdapter.notifyDataSetChanged();
            }
        }
    }

    protected void searchBusinesses(SearchParams searchParams) {
        if (mSearchBusinessesAsync != null) {
            mSearchBusinessesAsync.cancel(true);
        }

        mSearchBusinessesAsync = new SearchBusinessesAsync(getActivity()) {
            @Override
            protected void onSuccess(Result<BusinessesResponse> result) {
                if (result.getResource().getBusinesses() != null && result.getResource().getBusinesses().size() > 0) {
                    collectAndSortBusinesses(result.getResource().getBusinesses());
                    if (result.getResource().getBusinesses().size() < 20) {
                        Log.w(TAG, "Page with less than 20 entries returned, at end of search");
                        mAtEndOfSearch = true;
                    }
                } else {
                    mAtEndOfSearch = true;
                    Log.w(TAG, "No businesses returned from search");
                }
                mDiscoverBusinessesProgressBar.setVisibility(View.GONE);
                mDiscoverBusinessesListView.setVisibility(View.VISIBLE);
                mSearchBusinessesAsync = null;
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.e(TAG, "Searching businesses failed", error);
                mCurrentUserSearchPage--;
                mDiscoverBusinessesProgressBar.setVisibility(View.GONE);
                mDiscoverBusinessesListView.setVisibility(View.VISIBLE);
                mSearchBusinessesAsync = null;
            }
        }.executeInParallel(searchParams);
    }

    @Override
    public void onNearingEndOfList() {
        if (!mAtEndOfSearch) {
            Log.d(TAG, "Grabbing page " + (mCurrentUserSearchPage + 1));
            searchBusinesses(new SearchParams(mSearchTerms, mCurrentUserSearchPage++));
        }
    }
}