package com.conx2share.conx2share.async;


import com.conx2share.conx2share.model.Users;
import com.conx2share.conx2share.network.Result;

import android.content.Context;

public abstract class GetFollowingUsersAsync extends BaseRetrofitAsyncTask<Integer, Void, Users> {

    public GetFollowingUsersAsync(Context context) {
        super(context);
    }

    @Override
    protected Result<Users> doInBackground(Integer... params) {
        return getNetworkClient().getFollowingUsers(params[0], params[1]);
    }
}
