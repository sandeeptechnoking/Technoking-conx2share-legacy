package com.conx2share.conx2share.async;

import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.param.GetHashTagFeedParams;
import com.conx2share.conx2share.network.models.response.GetPostsResponse;

import android.content.Context;

public abstract class GetHashTagFeedAsync extends BaseRetrofitAsyncTask<GetHashTagFeedParams, Void, GetPostsResponse> {

    public static final String TAG = GetHashTagFeedAsync.class.getSimpleName();

    public GetHashTagFeedAsync(Context context) {
        super(context);
    }

    @Override
    protected Result<GetPostsResponse> doInBackground(GetHashTagFeedParams... params) {
        return getNetworkClient().getHashTagFeed(params[0]);
    }
}
