package com.conx2share.conx2share.async;

import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.response.GetGroupListResponse;

import android.content.Context;


public abstract class GetGroupsAsync extends BaseRetrofitAsyncTask<Void, Void, GetGroupListResponse> {

    public static final String TAG = GetGroupAsync.class.getSimpleName();

    public GetGroupsAsync(Context context) {
        super(context);
    }

    @Override
    protected Result<GetGroupListResponse> doInBackground(Void... params) {
        return getNetworkClient().getGroups(true);
    }
}
