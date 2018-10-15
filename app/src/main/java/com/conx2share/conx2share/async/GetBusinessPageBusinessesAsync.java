package com.conx2share.conx2share.async;

import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.response.BusinessesResponse;

import android.content.Context;

public abstract class GetBusinessPageBusinessesAsync extends BaseRetrofitAsyncTask<Void, Void, BusinessesResponse> {

    public static final String TAG = GetBusinessPageBusinessesAsync.class.getSimpleName();

    public GetBusinessPageBusinessesAsync(Context context) {
        super(context);
    }

    @Override
    protected Result<BusinessesResponse> doInBackground(Void... params) {
        return getNetworkClient().getFollowedBusinesses();
    }
}
