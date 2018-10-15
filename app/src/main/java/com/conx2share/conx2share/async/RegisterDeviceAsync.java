package com.conx2share.conx2share.async;


import android.content.Context;

import com.conx2share.conx2share.model.RegisteredDeviceResponse;
import com.conx2share.conx2share.network.Result;

public abstract class RegisterDeviceAsync extends BaseRetrofitAsyncTask<String, Void, RegisteredDeviceResponse> {

    public RegisterDeviceAsync(Context context) {
        super(context);
    }

    @Override
    protected Result<RegisteredDeviceResponse> doInBackground(String... params) {
        return getNetworkClient().sendDeviceToken(params[0]);
    }
}
