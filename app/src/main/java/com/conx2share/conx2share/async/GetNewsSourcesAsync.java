package com.conx2share.conx2share.async;

import com.conx2share.conx2share.model.NewsSources;
import com.conx2share.conx2share.network.Result;

import android.content.Context;

public abstract class GetNewsSourcesAsync extends BaseRetrofitAsyncTask<Void, Void, NewsSources> {

    public GetNewsSourcesAsync(Context context) {
        super(context);
    }

    @Override
    protected Result<NewsSources> doInBackground(Void... Void) {
        return getNetworkClient().getNewsSources();
    }
}
