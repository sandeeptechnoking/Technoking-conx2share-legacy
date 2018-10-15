package com.conx2share.conx2share.async;

import com.conx2share.conx2share.model.ResponseMessage;
import com.conx2share.conx2share.network.Result;

import android.content.Context;

public abstract class UnblockUserAsync extends BaseRetrofitAsyncTask<String, Void, ResponseMessage> {

    private int mGroupId;

    public UnblockUserAsync(Context context, int groupId) {
        super(context);
        mGroupId = groupId;
    }

    @Override
    protected Result<ResponseMessage> doInBackground(String... params) {
        return getNetworkClient().unblockUserFromGroup(mGroupId, params[0]);
    }
}
