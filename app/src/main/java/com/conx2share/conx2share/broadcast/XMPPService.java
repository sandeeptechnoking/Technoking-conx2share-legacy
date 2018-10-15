package com.conx2share.conx2share.broadcast;

import com.google.gson.Gson;

import com.conx2share.conx2share.BuildConfig;
import com.conx2share.conx2share.model.Expiration;
import com.conx2share.conx2share.model.Message;
import com.conx2share.conx2share.model.event.AddChatEvent;
import com.conx2share.conx2share.model.event.ExpirationEvent;
import com.conx2share.conx2share.model.event.PushNotificationEvent;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.util.EventBusUtil;
import com.conx2share.conx2share.util.PreferencesUtil;
import com.saulpower.fayeclient.FayeClient;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import java.net.URI;

import javax.inject.Inject;

import roboguice.service.RoboService;

public class XMPPService extends RoboService {

    public static final String CHAT_ID_KEY = "chatId";

    public final String TAG = this.getClass().getSimpleName();

    public int numbTimeCalled = 0;

    FayeClient mClient;

    @Inject
    private PreferencesUtil mPreferencesUtil;

    @Inject
    private NetworkClient mNetworkClient;

    private int mChatId;

    private Gson mGson;

    private Activity mCallingActivity;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service Created");
        EventBusUtil.getEventBus().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Killing service");
        EventBusUtil.getEventBus().unregister(this);
        if (mClient != null) {
            mClient.unsubscribe();
            mClient.disconnect();
            mClient.disconnectFromServer();
            mClient.setFayeListener(null);
            mClient.closeWebSocketConnection();
        }
    }

    private void connectToServer() {
        try {
            URI uri = URI.create("ws://" + BuildConfig.BASE_URL_NO_ORIGIN_SCHEME + ":9292/faye");
            String channel = "/chats/" + mChatId;

            JSONObject ext = new JSONObject();
            ext.put("authToken", mPreferencesUtil.getAuthToken());

            if (mClient != null) {
                mClient.unsubscribe();
                mClient.disconnect();
                mClient.setFayeListener(null);
            }

            mClient = new FayeClient(new Handler(Looper.getMainLooper()), uri, channel);
            mClient.setFayeListener(new XMPPLister(mChatId));
            mClient.connectToServer(ext);

        } catch (JSONException ex) {
            Log.e(TAG, ex.toString());
        }
    }

    public void publishPushNotificationEvent(final String message) {

        Log.i(TAG, "Message: " + message);

        if (mCallingActivity != null) {
            new Handler(Looper.getMainLooper()).post(() -> {
                try {
                    Log.d(TAG, message);
                    Gson gson = NetworkClient.getGson();
                    EventBusUtil.getEventBus().post(new PushNotificationEvent(gson.fromJson(message, Message.class), false));
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void publishExpirationSetEvent(final String message) {
        Log.i(TAG, "New Expiration Event: " + message);

        if (mCallingActivity != null) {
            new Handler(Looper.getMainLooper()).post(() -> {
                try {
                    Log.d(TAG, message);
                    Gson gson = NetworkClient.getGson();
                    EventBusUtil.getEventBus().post(new ExpirationEvent(gson.fromJson(message, Expiration.class)));
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void onEvent(AddChatEvent event) {
        Log.i(TAG, "Setting Channel");
        setChatChannel(event.getChatId(), event.getActivity());
    }

    private void setChatChannel(Integer chatId, Activity activity) {
        Log.i(TAG, (++numbTimeCalled) + "");
        mChatId = chatId;
        mCallingActivity = activity;
        connectToServer();
    }

    private class XMPPLister implements FayeClient.FayeListener {

        Integer chatId;

        public XMPPLister(Integer chatId) {
            this.chatId = chatId;
        }

        @Override
        public void connectedToServer() {
            Log.i(TAG, "Chat: " + chatId + " is connected to XMPP server");
        }

        @Override
        public void disconnectedFromServer() {
            Log.e(TAG, "Chat:" + chatId + " disconnected from server");
        }

        @Override
        public void subscribedToChannel(String subscription) {
            Log.i(TAG, "Chat: " + chatId + " is subscribed to XMPP server");
        }

        @Override
        public void subscriptionFailedWithError(String error) {
            Log.e(TAG, "Error" + error + "while subscribing " + chatId);
        }

        @Override
        public void messageReceived(JSONObject json) {
            Log.i(TAG, "Message Received");
            boolean isMessage = false;
            String body = null;
            try {
                body = json.getString("body");
                Log.i(TAG, "setting message");
            } catch (JSONException e) {
                Log.i(TAG, "setting expiration");
            }

            if (body != null) {
                publishPushNotificationEvent(json.toString());
            } else {
                publishExpirationSetEvent(json.toString());
            }
        }
    }

}



