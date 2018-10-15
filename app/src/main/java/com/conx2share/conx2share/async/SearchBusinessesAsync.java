package com.conx2share.conx2share.async;

import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.param.SearchParams;
import com.conx2share.conx2share.network.models.response.BusinessesResponse;

import android.content.Context;

public abstract class SearchBusinessesAsync extends BaseRetrofitAsyncTask<SearchParams, Void, BusinessesResponse> {

    public static final String TAG = SearchBusinessesAsync.class.getSimpleName();

    public SearchBusinessesAsync(Context context) {
        super(context);
    }

    @Override
    protected Result<BusinessesResponse> doInBackground(SearchParams... params) {
        return getNetworkClient().searchBusinesses(params[0]);
    }
}
