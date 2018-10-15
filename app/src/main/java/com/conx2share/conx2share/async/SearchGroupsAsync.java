package com.conx2share.conx2share.async;


import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.param.SearchParams;
import com.conx2share.conx2share.network.models.response.GetGroupListResponse;

import android.content.Context;


public abstract class SearchGroupsAsync extends BaseRetrofitAsyncTask<SearchParams, Void, GetGroupListResponse> {

    public static final String TAG = SearchGroupsAsync.class.getSimpleName();

    public SearchGroupsAsync(Context context) {
        super(context);
    }

    @Override
    protected Result<GetGroupListResponse> doInBackground(SearchParams... params) {
        SearchParams searchParams = getSingleParamOrThrow(params);
        return getNetworkClient().searchGroups(searchParams);
    }
}
