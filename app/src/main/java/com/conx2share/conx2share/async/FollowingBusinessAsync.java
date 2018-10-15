package com.conx2share.conx2share.async;

import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.response.BusinessResponse;

import android.content.Context;

public abstract class FollowingBusinessAsync extends BaseRetrofitAsyncTask<Integer, Void, BusinessResponse> {

    public static final String TAG = FollowingBusinessAsync.class.getSimpleName();

    private final boolean mIsUnfollowing;

    public FollowingBusinessAsync(Context context, boolean unfollowing) {
        super(context);
        mIsUnfollowing = unfollowing;
    }

    @Override
    protected Result<BusinessResponse> doInBackground(Integer... params) {
        int businessId = getSingleParamOrThrow(params);
        if (mIsUnfollowing) {
            return getNetworkClient().unfollowBusiness(businessId);
        } else {
            return getNetworkClient().followBusiness(businessId);
        }
    }

    public boolean isUnfollowing() {
        return mIsUnfollowing;
    }
}
