package com.conx2share.conx2share.async;

import com.conx2share.conx2share.model.FlagHolder;
import com.conx2share.conx2share.model.ResponseMessage;
import com.conx2share.conx2share.network.Result;

import android.content.Context;

public abstract class FlagPostAsync extends BaseRetrofitAsyncTask<FlagHolder, Void, ResponseMessage> {

    public FlagPostAsync(Context context) {
        super(context);
    }

    @Override
    protected Result<ResponseMessage> doInBackground(FlagHolder... params) {
        return getNetworkClient().flagPost(params[0]);
    }
}
