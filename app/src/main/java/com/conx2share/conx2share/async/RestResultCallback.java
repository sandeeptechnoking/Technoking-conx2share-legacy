package com.conx2share.conx2share.async;

import retrofit.RetrofitError;

public abstract class RestResultCallback<T> {

    public static final String TAG = RestResultCallback.class.getSimpleName();

    public abstract void onSuccess(T result);

    public abstract void onFailure(RetrofitError error);
}
