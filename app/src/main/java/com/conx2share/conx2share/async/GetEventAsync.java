package com.conx2share.conx2share.async;

import android.content.Context;

import com.conx2share.conx2share.model.EventResponse;
import com.conx2share.conx2share.network.Result;

public abstract class GetEventAsync extends BaseRetrofitAsyncTask<Integer, Void, EventResponse> {

    public static final String TAG = GetEventAsync.class.getSimpleName();

    public GetEventAsync(Context context) {
        super(context);
    }

    @Override
    protected Result<EventResponse> doInBackground(Integer... params) {
        return getNetworkClient().getEvent(params[0]);
    }
}
