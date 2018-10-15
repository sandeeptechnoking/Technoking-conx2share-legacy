package com.conx2share.conx2share.async;

import com.conx2share.conx2share.model.GroupResponse;
import com.conx2share.conx2share.network.Result;

import android.content.Context;

public abstract class GetGroupPostsAsync extends BaseRetrofitAsyncTask<Integer, Void, GroupResponse> {

    public GetGroupPostsAsync(Context context) {
        super(context);
    }

    @Override
    protected Result<GroupResponse> doInBackground(Integer... params) {
        return getNetworkClient().getGroupPosts(params[0]);
    }
}
