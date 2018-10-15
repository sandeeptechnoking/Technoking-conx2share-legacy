package com.conx2share.conx2share.async;

import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.response.GetPostsResponse;

import android.content.Context;

public abstract class GetBusinessFeedAsync extends BaseRetrofitAsyncTask<Integer, Void, GetPostsResponse> {

    public static final String TAG = GetBusinessFeedAsync.class.getSimpleName();

    private int mBusinessId;

    public GetBusinessFeedAsync(Context context, int businessId) {
        super(context);
        mBusinessId = businessId;
    }

    @Override
    protected Result<GetPostsResponse> doInBackground(Integer... params) {
        int page = getSingleParamOrThrow(params);
        return getNetworkClient().getBusinessFeed(mBusinessId, page);
    }
}
