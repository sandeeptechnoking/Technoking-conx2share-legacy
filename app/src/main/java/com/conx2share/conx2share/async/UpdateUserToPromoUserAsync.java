package com.conx2share.conx2share.async;

import com.conx2share.conx2share.model.PromoCodeWrapper;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.response.GetUserResponse;

import android.content.Context;

public abstract class UpdateUserToPromoUserAsync extends BaseRetrofitAsyncTask<PromoCodeWrapper, Void, GetUserResponse> {

    private int mUserId;

    public UpdateUserToPromoUserAsync(Context context, int userId) {
        super(context);
        mUserId = userId;
    }

    @Override
    protected Result<GetUserResponse> doInBackground(PromoCodeWrapper... params) {
        return getNetworkClient().updateToPromoUser(mUserId, params[0]);
    }
}
