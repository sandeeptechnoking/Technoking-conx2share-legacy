package com.conx2share.conx2share.async;

import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.network.Result;

import android.content.Context;
import android.os.AsyncTask;

import javax.inject.Inject;

import retrofit.RetrofitError;
import roboguice.RoboGuice;

public abstract class BaseRetrofitAsyncTask<ParamsT, ProgressT, ResultT> extends AsyncTask<ParamsT, ProgressT, Result<ResultT>> {

    public static final String TAG = BaseRetrofitAsyncTask.class.getSimpleName();

    @Inject
    @SuppressWarnings("unused")
    private NetworkClient mNetworkClient;

    private Result<ResultT> mRawResult;

    public BaseRetrofitAsyncTask(Context context) {
        RoboGuice.injectMembers(context, this);
    }

    @Override
    protected void onPostExecute(Result<ResultT> result) {
        mRawResult = result;
        super.onPostExecute(result);
        callSuccessOrFailureDeterminedByResult(result);
    }

    protected final void callSuccessOrFailureDeterminedByResult(Result<ResultT> result) {
        //noinspection ThrowableResultOfMethodCallIgnored
        if (isSuccess(result)) {
            onSuccess(result);
        } else if (result != null) {
            onFailure(result.getError());
        } else {
            onFailure(RetrofitError.unexpectedError("", new UnknownError("Result was null")));
        }
    }

    protected boolean isSuccess(Result<ResultT> result) {
        return result != null && result.getError() == null;
    }

    protected abstract void onSuccess(Result<ResultT> result);

    protected abstract void onFailure(RetrofitError error);

    public final <T extends BaseRetrofitAsyncTask> T executeInParallel(ParamsT... params) {
        this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        // noinspection unchecked
        return (T) this;
    }

    protected final ParamsT getSingleParamOrThrow(ParamsT... params) {
        if (params.length < 1 || params[0] == null) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + " expected exactly 1 param");
        }

        return params[0];
    }

    protected final NetworkClient getNetworkClient() {
        return mNetworkClient;
    }

    protected final Result<ResultT> getRawResult() {
        return mRawResult;
    }
}
