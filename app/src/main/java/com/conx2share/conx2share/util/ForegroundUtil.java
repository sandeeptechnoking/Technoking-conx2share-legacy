package com.conx2share.conx2share.util;

public class ForegroundUtil {

    private static boolean mAppInForeground = false;

    public static boolean getAppInForeground() {
        return mAppInForeground;
    }

    public static void setAppInForeground(boolean appInForeground) {
        mAppInForeground = appInForeground;
    }
}
