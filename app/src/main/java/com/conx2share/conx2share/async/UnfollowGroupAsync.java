package com.conx2share.conx2share.async;

import com.conx2share.conx2share.model.GroupResponse;
import com.conx2share.conx2share.network.Result;

import android.content.Context;

public abstract class UnfollowGroupAsync extends BaseRetrofitAsyncTask<Integer, Void, GroupResponse> {

    public UnfollowGroupAsync(Context context) {
        super(context);
    }

    @Override
    protected Result<GroupResponse> doInBackground(Integer... params) {
        return getNetworkClient().unfollowGroup(params[0]);
    }
}
