package com.conx2share.conx2share.async;

import android.content.Context;

import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.response.RsvpResponse;

public abstract class RegisterAttendanceAsync extends BaseRetrofitAsyncTask<Void, Void, RsvpResponse> {

    public static final String TAG = RegisterAttendanceAsync.class.getSimpleName();

    private int mEventId, mStatus;

    public RegisterAttendanceAsync(Context context, int eventId, int status) {
        super(context);
        this.mEventId = eventId;
        this.mStatus = status;
    }

    @Override
    protected Result<RsvpResponse> doInBackground(Void... params) {
        return getNetworkClient().registerAttendance(mEventId, mStatus);
    }
}
