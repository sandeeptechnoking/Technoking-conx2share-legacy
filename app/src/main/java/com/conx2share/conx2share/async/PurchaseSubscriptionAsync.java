package com.conx2share.conx2share.async;

import com.conx2share.conx2share.model.PurchaseSubscriptionWrapper;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.response.GetPurchaseResponse;

import android.content.Context;


public abstract class PurchaseSubscriptionAsync extends BaseRetrofitAsyncTask<PurchaseSubscriptionWrapper, Void, GetPurchaseResponse> {

    public static final String TAG = PurchaseSubscriptionAsync.class.getSimpleName();

    public PurchaseSubscriptionAsync(Context context) {
        super(context);
    }

    @Override
    protected Result<GetPurchaseResponse> doInBackground(PurchaseSubscriptionWrapper... params) {
        return getNetworkClient().purchasePlan(params[0]);
    }
}
