package com.conx2share.conx2share.util;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.async.UnregisterDeviceAsync;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.ui.login.LoginActivity;
import com.crashlytics.android.Crashlytics;

import retrofit.RetrofitError;
import retrofit.client.Response;

public class EmergencyUtil {

    public static final String TAG = EmergencyUtil.class.getSimpleName();

    private EmergencyUtil() {
        // prevent instantiation
    }

    public static void emergencyLogoutWithNotification(Activity activity, PreferencesUtil preferencesUtil) {
        new UnregisterDeviceAsync(activity) {
            @Override
            protected void onSuccess(Result<Response> result) {
                logout(activity, preferencesUtil);
            }

            @Override
            protected void onFailure(RetrofitError error) {
                logout(activity, preferencesUtil);
            }
        }.executeInParallel(preferencesUtil.getRegisteredDeviceId());

        // some feedback so they aren't so confused about why they are suddenly at the login screen.
        Toast.makeText(activity, R.string.emergency_logout_toast_text, Toast.LENGTH_LONG).show();
    }

    public static void logout(Activity activity,
                              PreferencesUtil preferencesUtil) {
        if (activity == null || preferencesUtil == null) {
            RuntimeException ex = new RuntimeException("Terrible failures have occurred, cannot recover");
            Crashlytics.logException(ex);
            throw ex;
        }
        cancelAllNotifications(activity);

        preferencesUtil.clearPreferences();
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void cancelAllNotifications(Activity activity) {
        NotificationManagerCompat nMgr = NotificationManagerCompat.from(activity);
        nMgr.cancelAll();
    }

}
