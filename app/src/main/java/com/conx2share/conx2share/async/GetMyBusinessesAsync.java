package com.conx2share.conx2share.async;

import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.response.BusinessesResponse;

import android.content.Context;

public abstract class GetMyBusinessesAsync extends BaseRetrofitAsyncTask<Void, Void, BusinessesResponse> {

    public static final String TAG = GetMyBusinessesAsync.class.getSimpleName();

    public GetMyBusinessesAsync(Context context) {
        super(context);
    }

    @Override
    protected Result<BusinessesResponse> doInBackground(Void... params) {
        return getNetworkClient().getMyBusinesses();
    }
}
