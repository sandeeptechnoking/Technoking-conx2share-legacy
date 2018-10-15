package com.conx2share.conx2share.async;

import com.conx2share.conx2share.model.ResponseMessage;
import com.conx2share.conx2share.network.Result;

import android.content.Context;

public abstract class RemoveUserFromGroupAsync extends BaseRetrofitAsyncTask<String, Void, ResponseMessage> {

    private int mGroupId;

    public RemoveUserFromGroupAsync(Context context, int groupId) {
        super(context);
        mGroupId = groupId;
    }

    @Override
    protected Result<ResponseMessage> doInBackground(String... params) {
        return getNetworkClient().removeUserFromGroup(mGroupId, params[0]);
    }
}
