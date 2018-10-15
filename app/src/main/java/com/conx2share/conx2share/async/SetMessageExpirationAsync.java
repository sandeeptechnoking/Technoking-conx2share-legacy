package com.conx2share.conx2share.async;

import com.conx2share.conx2share.model.ExpirationWrapper;
import com.conx2share.conx2share.model.Message;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.response.MessagesResponse;

import android.content.Context;

import java.util.ArrayList;

public abstract class SetMessageExpirationAsync extends BaseRetrofitAsyncTask<Message, Void, MessagesResponse> {

    public static final String TAG = SetMessageExpirationAsync.class.getSimpleName();

    public SetMessageExpirationAsync(Context context) {
        super(context);
    }

    @Override
    protected final Result<MessagesResponse> doInBackground(Message... params) {
        ArrayList<String> ids = new ArrayList<>();
        for (Message message : params) {
            ids.add(String.valueOf(message.getId()));
        }

        int index;
        if (params.length > 0) {
            index = params.length - 1;
        } else {
            index = 0;
        }

        return getNetworkClient().updateExpiration(new ExpirationWrapper(ids, params[index]));
    }
}
