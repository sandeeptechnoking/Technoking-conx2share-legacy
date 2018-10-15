package com.conx2share.conx2share.ui.groups;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.adapter.GroupIndexAdapter;
import com.conx2share.conx2share.async.SearchGroupsAsync;
import com.conx2share.conx2share.model.Group;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.param.SearchParams;
import com.conx2share.conx2share.network.models.response.GetGroupListResponse;
import com.conx2share.conx2share.ui.base.BaseFragment;
import com.conx2share.conx2share.util.PreferencesUtil;

import android.content.Intent;
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

import javax.inject.Inject;

//import butterknife.InjectView;
import retrofit.RetrofitError;
import roboguice.inject.InjectView;

public class SearchGroupsFragment extends BaseFragment implements GroupIndexAdapter.GroupIndexAdapterCallbacks {

    public static final String TAG = SearchGroupsFragment.class.getSimpleName();

    @Inject
    PreferencesUtil mPreferencesUtil;

    @InjectView(R.id.search_for_groups_edit_text)
    EditText mSearchGroupsEditText;

    @InjectView(R.id.search_groups_list_view)
    ListView mSearchGroupsListView;

    private String mSearchTerms;

    private Integer mCurrentUserSearchPage;

    private ArrayList<Group> mGroups;

    private SearchGroupsAsync mSearchGroupsAsync;

    private GroupIndexAdapter mGroupIndexAdapter;

    private boolean mAtEndOfSearch;

    public static SearchGroupsFragment newInstance() {
        return new SearchGroupsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_groups, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSearchGroupsEditText.addTextChangedListener(new TextWatcher() {
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
                if (mGroups != null) {
                    mGroups.clear();
                } else {
                    mGroups = new ArrayList<>();
                }
                mAtEndOfSearch = false;
                setupGroupsListViewAdapter();
                mCurrentUserSearchPage = 1;
                if (getActivity() != null) {
                    searchGroups(new SearchParams(mSearchTerms, mCurrentUserSearchPage));
                }
            }
        });

        mSearchGroupsListView.setOnItemClickListener((parent, view1, position, id) -> {
            if (getActivity() != null) {
                Intent groupActivityIntent = new Intent(getActivity(), GroupActivity.class);
                groupActivityIntent.putExtra(GroupActivity.EXTRA_GROUP_ID, mGroups.get(position).getId());
                getActivity().startActivity(groupActivityIntent);
            }
        });
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
                        Log.w(TAG, "Page with less than 20 entries returned, at end of search");
                        mAtEndOfSearch = true;
                    }
                } else {
                    mAtEndOfSearch = true;
                    Log.w(TAG, "No groups returned in search, at end of search");
                }
                mSearchGroupsAsync = null;
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.e(TAG, "Searching for groups failed", error);
                mCurrentUserSearchPage--;
                mSearchGroupsAsync = null;
            }
        }.executeInParallel(searchParams);
    }

    private void setupGroupsListViewAdapter() {
        if (mSearchGroupsListView.getAdapter() == null) {
            mGroupIndexAdapter = new GroupIndexAdapter(mGroups, getActivity(), this, mPreferencesUtil.getAuthUser().getId());
            mSearchGroupsListView.setAdapter(mGroupIndexAdapter);
        } else {
            mGroupIndexAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onNearingEndOfList() {
        if (!mAtEndOfSearch) {
            Log.d(TAG, "Grabbing page " + (mCurrentUserSearchPage + 1));
            searchGroups(new SearchParams(mSearchTerms, mCurrentUserSearchPage++));
        }
    }
}
