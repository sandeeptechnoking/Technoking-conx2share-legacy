package com.conx2share.conx2share.async;


import com.conx2share.conx2share.model.ResponseMessage;
import com.conx2share.conx2share.network.Result;

import android.content.Context;

public abstract class CheckPromoCodeAsync extends BaseRetrofitAsyncTask<String, Void, ResponseMessage> {

    public CheckPromoCodeAsync(Context context) {
        super(context);
    }

    @Override
    protected Result<ResponseMessage> doInBackground(String... params) {
        return getNetworkClient().checkPromoCode(params[0]);
    }
}
