package com.conx2share.conx2share.async;


import android.content.Context;

import com.conx2share.conx2share.model.RegisteredDeviceResponse;
import com.conx2share.conx2share.network.Result;

import retrofit.client.Response;

public abstract class UnregisterDeviceAsync extends BaseRetrofitAsyncTask<Integer, Void, Response> {

    public UnregisterDeviceAsync(Context context) {
        super(context);
    }

    @Override
    protected Result<Response> doInBackground(Integer... params) {
        return getNetworkClient().unregisterDevice(params[0]);
    }
}
