package com.conx2share.conx2share.async;

import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.response.BusinessResponse;

import android.content.Context;

public abstract class GetBusinessAsyncTask extends BaseRetrofitAsyncTask<Void, Void, BusinessResponse> {

    public static final String TAG = GetBusinessAsyncTask.class.getSimpleName();

    private final int mBusinessId;

    public GetBusinessAsyncTask(Context context, int businessId) {
        super(context);
        mBusinessId = businessId;
    }

    @Override
    protected Result<BusinessResponse> doInBackground(Void... params) {
        return getNetworkClient().getBusiness(mBusinessId);
    }
}
