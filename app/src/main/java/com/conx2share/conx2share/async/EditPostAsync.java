package com.conx2share.conx2share.async;

import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.param.PostParams;
import com.conx2share.conx2share.network.models.response.GetPostsResponse;

import android.content.Context;

public abstract class EditPostAsync extends BaseRetrofitAsyncTask<PostParams, Void, GetPostsResponse>{

    public EditPostAsync(Context context) {
        super(context);
    }

    @Override
    protected Result<GetPostsResponse> doInBackground(PostParams... params) {
       return getNetworkClient().editPost(params[0]);
    }
}
