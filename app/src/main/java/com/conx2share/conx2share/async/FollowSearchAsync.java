package com.conx2share.conx2share.async;

import com.conx2share.conx2share.model.Users;
import com.conx2share.conx2share.network.Result;

import android.content.Context;

public abstract class FollowSearchAsync extends BaseRetrofitAsyncTask<String, Void, Users> {

    public FollowSearchAsync(Context context) {
        super(context);
    }

    @Override
    protected Result<Users> doInBackground(String... params) {
        return getNetworkClient().searchFollowUsers(params[0], params[1], params[2]);
    }
}
