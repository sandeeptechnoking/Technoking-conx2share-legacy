package com.conx2share.conx2share.async;

import android.content.Context;

import com.conx2share.conx2share.model.EventResponse;
import com.conx2share.conx2share.network.Result;

public abstract class DeleteEventAsync extends BaseRetrofitAsyncTask<Void, Void, EventResponse> {

    public static final String TAG = DeleteEventAsync.class.getSimpleName();

    private int mEventId;

    public DeleteEventAsync(Context context, int eventId) {
        super(context);
        this.mEventId = eventId;
    }

    @Override
    protected Result<EventResponse> doInBackground(Void... params) {
        return getNetworkClient().deleteEvent(mEventId);
    }
}
