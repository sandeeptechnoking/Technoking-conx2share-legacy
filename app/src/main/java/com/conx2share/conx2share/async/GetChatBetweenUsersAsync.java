package com.conx2share.conx2share.async;

import com.conx2share.conx2share.model.ChatHolder;
import com.conx2share.conx2share.network.Result;

import android.content.Context;

public abstract class GetChatBetweenUsersAsync extends BaseRetrofitAsyncTask<Integer, Void, ChatHolder> {

    public static final String TAG = GetChatBetweenUsersAsync.class.getSimpleName();

    public GetChatBetweenUsersAsync(Context context) {
        super(context);
    }

    @Override
    protected Result<ChatHolder> doInBackground(Integer... params) {
        int friendId = getSingleParamOrThrow(params);
        return getNetworkClient().getChatBetweenUsers(friendId);
    }
}