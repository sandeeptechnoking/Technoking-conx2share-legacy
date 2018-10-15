package com.conx2share.conx2share.async;

import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.response.HashTagsResponse;

import android.content.Context;

public abstract class SearchHashTagsAsync extends BaseRetrofitAsyncTask<Integer, Void, HashTagsResponse> {

    public static final String TAG = SearchHashTagsAsync.class.getSimpleName();

    private String mSearchTerms;

    public SearchHashTagsAsync(Context context, String searchTerms) {
        super(context);
        mSearchTerms = searchTerms;
    }

    @Override
    protected Result<HashTagsResponse> doInBackground(Integer... params) {
        return getNetworkClient().searchHashTags(mSearchTerms, params[0]);
    }

}