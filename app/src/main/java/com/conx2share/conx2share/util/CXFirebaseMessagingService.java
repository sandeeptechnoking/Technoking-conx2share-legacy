package com.conx2share.conx2share.util;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.chat.ChatStateHolder;
import com.conx2share.conx2share.model.Friend;
import com.conx2share.conx2share.model.InvitationState;
import com.conx2share.conx2share.model.Message;
import com.conx2share.conx2share.model.notification.NotificationData;
import com.conx2share.conx2share.model.notification.NotificationEntity;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.ui.messaging.MessagingActivity;
import com.conx2share.conx2share.ui.notifications.NotificationsActivity;
import com.conx2share.conx2share.ui.sayno.SayNoFlowInteractor;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Key;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import roboguice.RoboGuice;
import roboguice.inject.RoboInjector;
import roboguice.util.RoboContext;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.conx2share.conx2share.ui.messaging.MessagingActivity.EXTRA_FRIEND;

/**
 * Created by heathersnepenger on 2/28/17.
 */

public class CXFirebaseMessagingService extends FirebaseMessagingService implements RoboContext {

    public final String TAG = CXFirebaseMessagingService.class.getSimpleName();

    public static final int NOTIFICATION_BASE_IDENTIFIER = 1;
    public static final int NOTIFICATION_MESSAGES_IDENTIFIER = 2;

    private static final int SAY_NO_GROUP_ID_TYPE = 15;

    private final int MessageNew = 0;
    private final int MessageRead = 1;
    private final int CommentNew = 2;
    private final int FollowerNew = 3;
    private final int GroupInvite = 4;
    private final int PostUserNew = 5;
    private final int PostGroupNew = 6;
    private final int GroupFollowInvite = 8;
    private final int TaggedPostNew = 9;
    private final int TaggedCommentNew = 10;
    private final int PostBusinessNew = 11;
    private final int EventStarting = 12;
    private final int FollowerInvite = 13;
    private final int LiveStreamStarting = 14;
    private final int SayNoGroupInviteAccepted = 15;
    private final int EventCreated = 16;

    public static final String ALERT_TEXT = "alert_text";
    public static final String CLICK_TYPE = "click_type";
    public static final String CLICK_DATA = "click_data";
    public static final String UNREAD_COUNT = "unread_count";
    public static final String TITLE = "title";
    public static final String NOTIFICATION_ID = "notification_id";

    @Inject
    NetworkClient networkClient;

    @Inject
    SayNoFlowInteractor sayNoFlowInteractor;

    protected HashMap<Key<?>, Object> scopedObjects = new HashMap<>();

    private int notificationId;
    private int clickType;
    private int unreadCount;
    private String notificationTitle;
    private String notificationText;

    private Message message;
    private NotificationData clickData;

    private static NotificationCompat.InboxStyle inboxStyle;

    @Override
    public void onCreate() {
        super.onCreate();

        final RoboInjector injector = RoboGuice.getInjector(this);
        injector.injectMembersWithoutViews(this);
    }

    public static NotificationCompat.InboxStyle getInboxStyle() {
        if (inboxStyle == null) {
             inboxStyle = new NotificationCompat.InboxStyle();
        }
        return inboxStyle;
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        if (remoteMessage != null) {
            Log.e("PUSH", "message received: " + remoteMessage.getData());
            JsonObject object = new JsonParser().parse(remoteMessage.getData().get("other")).getAsJsonObject();
            unreadCount = object.get(UNREAD_COUNT).getAsInt();
            if (object.has(CLICK_TYPE)) {
                clickType = object.get(CLICK_TYPE).getAsInt();
            }
            notificationId = object.get(NOTIFICATION_ID).getAsInt();
            if (object.has(TITLE)) {
                notificationTitle = object.get(TITLE).getAsString();
            }
            notificationText = remoteMessage.getData().get(ALERT_TEXT);

            clickData = new Gson().fromJson(object.get(CLICK_DATA).getAsJsonObject(), NotificationData.class);

            if (clickType == SAY_NO_GROUP_ID_TYPE) {
                sayNoFlowInteractor.setGroupId(clickData.getGroupId());
                sayNoFlowInteractor.setInvitationState(InvitationState.ACCEPTED);
            }

            displayPushNotification(clickType);
        } else {
            Log.d(TAG, "User is not logged in, nothing will be done with received push notification");
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]


    @Override
    public void onDestroy() {
        super.onDestroy();
        RoboGuice.destroyInjector(this);
    }

    public void displayPushNotification(int clickType) {

        if (clickType == NotificationEntity.MessageNew) {
            if (clickData.getMessageId() == 0) return;

            networkClient.getMessageById(clickData.getMessageId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(messagesHolder -> {
                        if (messagesHolder != null && messagesHolder.getMessage() != null) {
                            message = messagesHolder.getMessage();
                            getUnreadMessages();
                        } else {
                            Log.e(TAG, "Error getting message by Id");
                            pushBaseNotification();
                        }
                    }, throwable -> {
                        Log.e(TAG, "Error getting message by Id" + throwable);
                        pushBaseNotification();
                    });
        } else {
            pushBaseNotification();
        }
    }

    public void pushBaseNotification() {

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.cx2s_logo_icon)
                .setContentTitle(getString(R.string.conx2share_notification))
                .setContentText(notificationText)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(this, R.color.conx_primary))
                .setDefaults(Notification.DEFAULT_ALL);

        getInboxStyle().setBigContentTitle(getString(R.string.conx2share_notification));
        getInboxStyle().addLine(notificationText);

        notificationBuilder.setStyle(getInboxStyle());

        Intent notificationsActivityIntent = new Intent(this, NotificationsActivity.class);
        notificationsActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_BASE_IDENTIFIER, notificationsActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_BASE_IDENTIFIER, notificationBuilder.build());
    }

    public static void clearBaseText() {
        inboxStyle = null;
    }

    public void pushMessageNotification(Friend friend) {
        if (ChatStateHolder.getInstance().isChatActive()
                && ChatStateHolder.getInstance().getUserId() == friend.getFriendId()) return;

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_status_bar_message)
                .setContentTitle(getString(R.string.conx2share_notification_message))
                .setContentText(notificationText)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(this, R.color.conx_primary))
                .setDefaults(Notification.DEFAULT_ALL);

        Intent messagingActivityIntent = new Intent(this, MessagingActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_FRIEND, friend);
        messagingActivityIntent.putExtras(bundle);
        messagingActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_MESSAGES_IDENTIFIER, messagingActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_MESSAGES_IDENTIFIER, notificationBuilder.build());
    }

    @Override
    public Map<Key<?>, Object> getScopedObjectMap() {
        return scopedObjects;
    }

    private void getUnreadMessages() {
        networkClient
                .getUnreadMessages()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(messagesResponse -> {
                    if (messagesResponse != null) {
                        if (message != null) {
                            ArrayList<Message> allUnreadMessages = messagesResponse.getMessages();
                            ArrayList<Message> friendMessages = new ArrayList<>();
                            if (allUnreadMessages != null && allUnreadMessages.size() > 0) {
                                for (int i = 0; i < allUnreadMessages.size(); i++) {
                                    if (allUnreadMessages.get(i).getUserId() == message.getUserId()) {
                                        friendMessages.add(allUnreadMessages.get(i));
                                    }
                                }
                            }

                            String userAvatar = message.getUserAvatarUrl();
                            Friend friend = new Friend(message.getUserId(), friendMessages.size(),
                                    message.getUserFirstName(), message.getUserLastName(), userAvatar,
                                    friendMessages, message.getUserUsername());
                            pushMessageNotification(friend);
                        } else {
                            Log.e(TAG, "message was null");
                            pushBaseNotification();
                        }
                    } else {
                        Log.e(TAG, "Unable to get unread messages");
                        pushBaseNotification();
                    }
                }, throwable -> Log.e(TAG, "Unable to get unread messages", throwable));
    }

}