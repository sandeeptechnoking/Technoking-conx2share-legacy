package com.conx2share.conx2share.async;

import android.content.Context;

import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.response.GetEventListResponse;

public abstract class GetEventsForGroupAsync extends BaseRetrofitAsyncTask<Void, Void, GetEventListResponse> {

    public static final String TAG = GetEventsForGroupAsync.class.getSimpleName();

    private final int mGroupId;
    private final String type;

    public GetEventsForGroupAsync(Context context, int groupId, String type) {
        super(context);
        this.mGroupId = groupId;
        this.type = type;
    }

    @Override
    protected Result<GetEventListResponse> doInBackground(Void... params) {

        return getNetworkClient().getEventsForGroup(mGroupId, type);
    }
}
