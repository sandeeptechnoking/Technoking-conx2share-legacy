package com.conx2share.conx2share.async;


import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.response.GetPostsResponse;

import android.content.Context;

public abstract class DeletePostAsync extends BaseRetrofitAsyncTask<String, Void, GetPostsResponse> {

    public DeletePostAsync(Context context) {
        super(context);
    }

    @Override
    protected Result<GetPostsResponse> doInBackground(String... params) {
        return getNetworkClient().deletePost(params[0]);
    }
}
