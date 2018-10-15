package com.conx2share.conx2share.ui.business;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

//import butterknife.InjectView;
import retrofit.RetrofitError;
import roboguice.inject.InjectView;

public class SearchBusinessFragment extends BaseFragment implements BusinessAdapter.BusinessAdapterCallbacks {

    public static final String TAG = SearchBusinessFragment.class.getSimpleName();

    @InjectView(R.id.search_businesses_edit_text)
    EditText mSearchBusinessesEditText;

    @InjectView(R.id.search_businesses_listview)
    ListView mSearchBusinessesListView;

    private Integer mCurrentUserSearchPage;

    private String mSearchTerms;

    private ArrayList<Business> mBusinesses;

    private SearchBusinessesAsync mSearchBusinessesAsync;

    private BusinessAdapter mBusinessAdapter;

    private boolean mAtEndOfSearch;

    public static SearchBusinessFragment newInstance() {
        return new SearchBusinessFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_businesses, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSearchBusinessesEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                mSearchTerms = s.toString();
                if (mBusinesses != null) {
                    mBusinesses.clear();
                } else {
                    mBusinesses = new ArrayList<>();
                }
                mAtEndOfSearch = false;
                setupBusinessesListViewAdapter();
                mCurrentUserSearchPage = 1;
                if (getActivity() != null) {
                    searchBusinesses(new SearchParams(mSearchTerms, mCurrentUserSearchPage));
                }
            }
        });
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
        if (mSearchBusinessesListView.getAdapter() == null) {
            mBusinessAdapter = new BusinessAdapter(mBusinesses, getActivity(), this);
            mSearchBusinessesListView.setAdapter(mBusinessAdapter);
        } else {
            mBusinessAdapter.notifyDataSetChanged();
        }
    }

    protected void searchBusinesses(SearchParams searchParams) {
        if (mSearchBusinessesAsync != null) {
            mSearchBusinessesAsync.cancel(true);
        }

        mSearchBusinessesAsync = new SearchBusinessesAsync(getActivity()) {
            @Override
            protected void onSuccess(Result<BusinessesResponse> result) {
                if (getActivity() != null) {
                    if (result != null && result.getResource() != null && result.getResource().getBusinesses() != null && result.getResource().getBusinesses().size() > 0) {
                        collectAndSortBusinesses(result.getResource().getBusinesses());
                        if (result.getResource().getBusinesses().size() < 20) {
                            Log.w(TAG, "Page with less than 20 entries returned, at end of search");
                            mAtEndOfSearch = true;
                        }
                    } else {
                        mAtEndOfSearch = true;
                        Log.w(TAG, "No businesses returned from search, at end of search");
                    }
                    mSearchBusinessesAsync = null;
                }
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.e(TAG, "Searching businesses failed", error);
                mCurrentUserSearchPage--;
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