package com.conx2share.conx2share.async;

import com.conx2share.conx2share.model.GroupResponse;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.param.GroupParam;

import android.content.Context;

public abstract class CreateGroupAsync extends BaseRetrofitAsyncTask<GroupParam, Void, GroupResponse> {

    public static final String TAG = CreateGroupAsync.class.getSimpleName();

    public CreateGroupAsync(Context context) {
        super(context);
    }

    @Override
    protected Result<GroupResponse> doInBackground(GroupParam... params) {
        GroupParam groupParam = getSingleParamOrThrow(params);
        return getNetworkClient().createGroup(groupParam);
    }
}
