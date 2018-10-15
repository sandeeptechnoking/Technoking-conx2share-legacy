package com.conx2share.conx2share.util;

import com.conx2share.conx2share.network.NetworkClient;

import android.util.Log;

import retrofit.RestAdapter;

public class LogUtil {

    public static boolean isLoggable(String tag, int level) {
//        return BuildConfig.DEBUG || Log.isLoggable(tag, level);
        return true; // TODO: uncomment above and remove this line when we're out of alpha- and beta-testing
    }

    public static RestAdapter.LogLevel getRestAdapterLogLevel() {
        if (isLoggable(NetworkClient.TAG, Log.DEBUG)) {
            return RestAdapter.LogLevel.FULL;
        } else {
            return RestAdapter.LogLevel.NONE;
        }
    }

    public static RestAdapter.LogLevel getMediaRestAdapterLogLevel() {
        if (isLoggable(NetworkClient.TAG, Log.DEBUG)) {
            return RestAdapter.LogLevel.FULL;
        } else {
            return RestAdapter.LogLevel.NONE;
        }
    }
}
