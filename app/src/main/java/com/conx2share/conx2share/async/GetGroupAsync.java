package com.conx2share.conx2share.async;

import com.conx2share.conx2share.model.GroupResponse;
import com.conx2share.conx2share.network.Result;

import android.content.Context;

public abstract class GetGroupAsync extends BaseRetrofitAsyncTask<Integer, Void, GroupResponse> {

    public static final String TAG = GetGroupAsync.class.getSimpleName();

    public GetGroupAsync(Context context) {
        super(context);
    }

    @Override
    protected Result<GroupResponse> doInBackground(Integer... params) {
        return getNetworkClient().getGroup(params[0]);
    }
}
