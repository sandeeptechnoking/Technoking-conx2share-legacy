package com.conx2share.conx2share.async;

import com.conx2share.conx2share.model.FeedDirection;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.response.GetPostsResponse;

import android.content.Context;

public abstract class GetGroupFeedAsync extends BaseRetrofitAsyncTask<Integer, Void, GetPostsResponse> {

    public static final String TAG = GetGroupFeedAsync.class.getSimpleName();

    private final int mGroupId;

    private final FeedDirection mDirection;

    public GetGroupFeedAsync(Context context, int groupId, FeedDirection direction) {
        super(context);
        mGroupId = groupId;
        mDirection = direction;
    }

    @Override
    protected Result<GetPostsResponse> doInBackground(Integer... params) {
        Integer postId = getSingleParamOrThrow(params);
        return getNetworkClient().refreshGroupFeed(mDirection.toString(), postId, mGroupId);
    }

    protected FeedDirection getDirection() {
        return mDirection;
    }
}