package com.conx2share.conx2share.async;

import com.conx2share.conx2share.model.ResponseMessage;
import com.conx2share.conx2share.model.UserIdWrapper;
import com.conx2share.conx2share.network.Result;

import android.content.Context;

public abstract class InviteToFollowGroupAsync extends BaseRetrofitAsyncTask<UserIdWrapper, Void, ResponseMessage> {

    private int mGroupId;

    public InviteToFollowGroupAsync(Context context, int groupId) {
        super(context);
        mGroupId = groupId;
    }

    @Override
    protected Result<ResponseMessage> doInBackground(UserIdWrapper... params) {
        return getNetworkClient().inviteToFollowGroup(mGroupId, params[0]);
    }
}
