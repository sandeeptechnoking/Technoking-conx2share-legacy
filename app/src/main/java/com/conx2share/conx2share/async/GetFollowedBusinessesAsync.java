package com.conx2share.conx2share.async;

import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.response.BusinessesResponse;

import android.content.Context;

public abstract class GetFollowedBusinessesAsync extends BaseRetrofitAsyncTask<Void, Void, BusinessesResponse> {

    public static final String TAG = GetFollowedBusinessesAsync.class.getSimpleName();

    public GetFollowedBusinessesAsync(Context context) {
        super(context);
    }

    @Override
    protected Result<BusinessesResponse> doInBackground(Void... params) {
        return getNetworkClient().getFollowedBusinesses();
    }
}
