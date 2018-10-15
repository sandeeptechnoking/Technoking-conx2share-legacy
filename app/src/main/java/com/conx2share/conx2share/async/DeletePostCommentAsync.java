package com.conx2share.conx2share.async;

import com.conx2share.conx2share.model.CommentHolder;
import com.conx2share.conx2share.network.Result;

import android.content.Context;

public abstract class DeletePostCommentAsync extends BaseRetrofitAsyncTask<String, Void, CommentHolder> {

    public DeletePostCommentAsync(Context context) {
        super(context);
    }

    @Override
    protected Result<CommentHolder> doInBackground(String... params) {
        return getNetworkClient().deletePostComment(params[0]);
    }
}
