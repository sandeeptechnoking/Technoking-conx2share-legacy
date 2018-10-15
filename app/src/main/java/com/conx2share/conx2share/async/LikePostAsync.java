package com.conx2share.conx2share.async;

import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.Like;

import android.content.Context;

public abstract class LikePostAsync extends BaseRetrofitAsyncTask<LikeUnlikeHelper, Void, Like> {

    private LikeUnlikeHelper helper;

    public LikePostAsync(Context context) {
        super(context);
    }

    @Override
    protected Result<Like> doInBackground(LikeUnlikeHelper... params) {
        helper = getSingleParamOrThrow(params);
        if (helper.liking) {
            return getNetworkClient().likePost(helper.like);
        } else {
            return getNetworkClient().unlikePost(helper.post.getId());
        }
    }

    protected LikeUnlikeHelper getHelper() {
        return helper;
    }
}