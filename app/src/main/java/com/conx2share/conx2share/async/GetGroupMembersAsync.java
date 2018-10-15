package com.conx2share.conx2share.async;

import com.conx2share.conx2share.model.Users;
import com.conx2share.conx2share.network.Result;

import android.content.Context;

public abstract class GetGroupMembersAsync extends BaseRetrofitAsyncTask<String, Void, Users> {

    public GetGroupMembersAsync(Context context) {
        super(context);
    }

    @Override
    protected Result<Users> doInBackground(String... params) {
        return getNetworkClient().getCurrentGroupMembers(params[0]);
    }
}
