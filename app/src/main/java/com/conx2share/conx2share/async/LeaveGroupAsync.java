package com.conx2share.conx2share.async;

import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.param.GroupLeaveParams;
import com.conx2share.conx2share.network.models.response.GroupLeaveResponse;

import android.content.Context;

public abstract class LeaveGroupAsync extends BaseRetrofitAsyncTask<GroupLeaveParams, Void, GroupLeaveResponse> {

    public LeaveGroupAsync(Context context) {
        super(context);
    }

    @Override
    protected Result<GroupLeaveResponse> doInBackground(GroupLeaveParams... params) {
        return getNetworkClient().leaveGroup(params[0]);
    }
}
