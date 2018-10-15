package com.conx2share.conx2share.async;

import com.conx2share.conx2share.model.GroupResponse;
import com.conx2share.conx2share.network.Result;

import android.content.Context;

public abstract class FollowGroupAsync extends BaseRetrofitAsyncTask<Integer, Void, GroupResponse> {

    public FollowGroupAsync(Context context) {
        super(context);
    }

    @Override
    protected Result<GroupResponse> doInBackground(Integer... params) {
        return getNetworkClient().followGroup(params[0]);
    }
}
