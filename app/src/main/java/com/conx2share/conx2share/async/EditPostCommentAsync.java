package com.conx2share.conx2share.async;

import com.conx2share.conx2share.model.Comment;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.param.CommentWrapper;
import com.conx2share.conx2share.network.models.response.GetPostCommentsResponse;

import android.content.Context;

public abstract class EditPostCommentAsync extends BaseRetrofitAsyncTask<Comment, Void, GetPostCommentsResponse> {

    private String mCommentId;

    public EditPostCommentAsync(Context context, String commentId) {
        super(context);
        mCommentId = commentId;
    }

    @Override
    protected Result<GetPostCommentsResponse> doInBackground(Comment... params) {
        CommentWrapper wrapper = new CommentWrapper(params[0]);
        return getNetworkClient().updateComment(mCommentId, wrapper);
    }
}
