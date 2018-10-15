package com.conx2share.conx2share.async;

import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.response.MessagesResponse;

import android.content.Context;

public abstract class GetChatMessagesAsync extends BaseRetrofitAsyncTask<Integer, Void, MessagesResponse> {

    public static final String TAG = GetChatMessagesAsync.class.getSimpleName();

    public GetChatMessagesAsync(Context context) {
        super(context);
    }

    @Override
    protected Result<MessagesResponse> doInBackground(Integer... params) {
        Integer chatId = getSingleParamOrThrow(params);
        return getNetworkClient().getContextMessages(chatId);
    }
}