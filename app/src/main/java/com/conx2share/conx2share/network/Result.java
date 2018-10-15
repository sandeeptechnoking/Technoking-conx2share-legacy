package com.conx2share.conx2share.network;

import retrofit.RetrofitError;

public class Result<T> {

    private T resource;

    private RetrofitError error;

    public Result(T resource) {
        this.resource = resource;
    }

    public Result(RetrofitError error) {
        this.error = error;
    }

    public Result(T resource, RetrofitError error) {

        this.resource = resource;
        this.error = error;
    }

    public T getResource() {
        return resource;
    }

    public RetrofitError getError() {
        return error;
    }
}
