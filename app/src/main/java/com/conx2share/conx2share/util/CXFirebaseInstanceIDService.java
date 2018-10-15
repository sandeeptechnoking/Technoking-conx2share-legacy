package com.conx2share.conx2share.util;

import android.os.AsyncTask;
import android.util.Log;

import com.conx2share.conx2share.async.RegisterDeviceAsync;
import com.conx2share.conx2share.model.RegisteredDeviceResponse;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.network.Result;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.inject.Key;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import retrofit.RetrofitError;
import roboguice.RoboGuice;
import roboguice.inject.RoboInjector;
import roboguice.util.RoboContext;

/**
 * Created by heathersnepenger on 2/28/17.
 */

public class CXFirebaseInstanceIDService extends FirebaseInstanceIdService implements RoboContext {

    private static final String TAG = "MyFirebaseIIDService";
    protected HashMap<Key<?>, Object> scopedObjects = new HashMap<>();

    @Inject
    PreferencesUtil preferencesUtil;

    @Inject
    NetworkClient networkClient;

    @Override
    public Map<Key<?>, Object> getScopedObjectMap() {
        return scopedObjects;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        final RoboInjector injector = RoboGuice.getInjector(this);
        injector.injectMembersWithoutViews(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RoboGuice.destroyInjector(this);
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        new AsyncTask<String, Void, Result<RegisteredDeviceResponse>>() {
            @Override
            protected Result<RegisteredDeviceResponse> doInBackground(String... params) {
                return networkClient.sendDeviceToken(params[0]);
            }

            @Override
            protected void onPostExecute(Result<RegisteredDeviceResponse> result) {
                super.onPostExecute(result);
                if (result != null) {
                    RegisteredDeviceResponse response = result.getResource();
                    if (response != null) {
                        preferencesUtil.setRegisteredDeviceId(response.device.deviceId);
                        preferencesUtil.setTokenSent(true);
                    } else {
                        preferencesUtil.setTokenSent(false);
                    }
                } else {
                    preferencesUtil.setTokenSent(false);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, token);
    }
}