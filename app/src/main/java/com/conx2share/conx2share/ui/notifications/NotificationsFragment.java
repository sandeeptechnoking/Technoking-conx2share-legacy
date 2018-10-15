package com.conx2share.conx2share.ui.notifications;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.adapter.NotificationsAdapter;
import com.conx2share.conx2share.model.Event;
import com.conx2share.conx2share.model.FeedDirection;
import com.conx2share.conx2share.model.Friend;
import com.conx2share.conx2share.model.GroupInviteActionWrapper;
import com.conx2share.conx2share.model.Message;
import com.conx2share.conx2share.model.notification.NewBusinessPostPushEvent;
import com.conx2share.conx2share.model.notification.NewCommentPushEvent;
import com.conx2share.conx2share.model.notification.NewFollowPushEvent;
import com.conx2share.conx2share.model.notification.NewGroupInvitePushEvent;
import com.conx2share.conx2share.model.notification.NewGroupPostPushEvent;
import com.conx2share.conx2share.model.notification.NewInviteToFollowGroupEvent;
import com.conx2share.conx2share.model.notification.NewMessagePushEvent;
import com.conx2share.conx2share.model.notification.NewUserPostPushEvent;
import com.conx2share.conx2share.model.notification.NotificationData;
import com.conx2share.conx2share.model.notification.NotificationEntity;
import com.conx2share.conx2share.model.notification.UserTaggedYouPushEvent;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.ui.base.BaseFragment;
import com.conx2share.conx2share.ui.events.EventActivity;
import com.conx2share.conx2share.ui.feed.post_comments.PostCommentsActivity;
import com.conx2share.conx2share.ui.groups.GroupActivity;
import com.conx2share.conx2share.ui.messaging.MessagingActivity;
import com.conx2share.conx2share.ui.profile.ProfileActivity;
import com.conx2share.conx2share.ui.sayno.SayNoFlowInteractor;
import com.conx2share.conx2share.util.CXFirebaseMessagingService;
import com.conx2share.conx2share.util.SnackbarUtil;
import com.nispok.snackbar.SnackbarManager;

import java.util.ArrayList;

import javax.inject.Inject;

import roboguice.inject.InjectView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NotificationsFragment extends BaseFragment implements NotificationsAdapter.NotificationActionCallback {

    private static final String TAG = NotificationsFragment.class.getSimpleName();

    public static final String GROUPID_KEY = "groupId";
    public static final String AUTO_PLAY = "autoPlay";
    public static final String POSTID_KEY = "postId";
    public static final String PROFILEID_KEY = "profileId";

    private Event mEvent;

    @Inject
    SnackbarUtil mSnackbarUtil;

    @Inject
    SayNoFlowInteractor sayNoFlowInteractor;

    @Inject
    NetworkClient mNetworkClient;

    @InjectView(R.id.notifications_listview)
    ListView mNotificationsListView;

    @InjectView(R.id.notifications_progress_bar)
    ProgressBar mNotificationsProgressBar;

    @InjectView(R.id.notifications_progress_bar_bottom)
    ProgressBar mProgressBottom;

    private int mNotificationsListViewLastPosition = 0;
    private ArrayList<NotificationEntity> mNotifications;
    private NotificationsAdapter mNotificationsAdapter;
    private Integer notificationPosition;
    private ArrayList<String> mIdsAlreadyUsed;
    private String mLastNotificationId;
    private boolean mHardResetNotificationsAdapter = false;

    public static NotificationsFragment newInstance() {
        return new NotificationsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        mNotificationsListViewLastPosition = mNotificationsListView.getFirstVisiblePosition();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Scrolling to position: " + mNotificationsListViewLastPosition);
        mNotificationsListView.smoothScrollToPosition(mNotificationsListViewLastPosition);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getNotifications();

        mNotificationsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // Do nothing
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount >= totalItemCount - 7 && totalItemCount != 0) {
                    NotificationEntity lastNotification = (NotificationEntity) mNotificationsListView.getAdapter().getItem(totalItemCount - 1);
                    mLastNotificationId = String.valueOf(lastNotification.getId());
                    Log.d(TAG, "Last notification in list has id: " + mLastNotificationId);
                    if (mIdsAlreadyUsed == null) {
                        mIdsAlreadyUsed = new ArrayList<>();
                    }
                    boolean alreadyUsed = false;
                    for (String id : mIdsAlreadyUsed) {
                        if (mLastNotificationId.equals(id)) {
                            alreadyUsed = true;
                        }
                    }
                    if (!alreadyUsed) {
                        getMoreNotifications(lastNotification.getId());
                    } else {
                        Log.w(TAG, "Id " + mLastNotificationId + " was already used in a search");
                    }
                    mIdsAlreadyUsed.add(mLastNotificationId);
                }
            }
        });

        mNotificationsListView.setOnItemClickListener((parent, view1, position, id) -> onListItemClick(position));

    }

    private void onListItemClick(int position) {
        Log.d(TAG, "Notification was clicked at position: " + position);
        NotificationEntity notification = (NotificationEntity) mNotificationsListView.getAdapter().getItem(position);
        NotificationData clickData = notification.getClickData();

        Integer notificationType = notification.getClickType();

        Log.d(TAG, "notificationId: " + notification.getId());
        Log.d(TAG, "notificationType: " + notificationType);
        Log.d(TAG, "notificationIsViewed: " + notification.isViewed());

        // This check is in here so that we don't send multiple requests for the same notification to mark it as viewed
        if (!notification.isViewed()) {
            Log.d(TAG, "Need to update the notification as viewed");

            Observable.from(mNotifications)
                    .filter(notificationEntity -> notificationEntity.getId() == notification.getId())
                    .map(notificationEntity -> {
                        notificationEntity.setViewed(true);
                        return notification;
                    })
                    .subscribe();

            setupNotificationsAdapter();

            markAsViewed(notification.getId());
        }

        // There are no finish() calls for the activities here or app would have been exited when user hit the back button on the intent
        switch (notificationType) {
            case NotificationEntity.MessageNew:
                // Case 0 - New message
                getMessage(clickData.getMessageId());
                break;
            case NotificationEntity.FollowerNew:
            case NotificationEntity.FollowerInvite:
                // Case 3, 13 - go to user profile
                startActivity(new Intent(getActivity(), ProfileActivity.class)
                        .putExtra(PROFILEID_KEY, String.valueOf(clickData.getUserId())));
                break;
            case NotificationEntity.GroupInvite:
                // Case 4 - New group invite
                // Fall through to case 8
            case NotificationEntity.GroupFollowInvite:
                // Case 8 - New invite to follow group
                launchGroupActivity(clickData.getGroupId());
                break;
            case NotificationEntity.CommentNew:
                // Case 2 - New comment on post
                // Fall through to case 11
            case NotificationEntity.PostUserNew:
                // Case 5 - New user post
                // Fall through to case 11
            case NotificationEntity.PostGroupNew:
                // Case 6 - New group post
                // Fall through to case 11
            case NotificationEntity.TaggedPostNew:
                // Case 9 - User tagged you in a post
                // Fall through to case 11
            case NotificationEntity.TaggedCommentNew:
                // Case 10 - User tagged you in a comment
                // Fall through to case 11
            case NotificationEntity.PostBusinessNew:
                // Case 11 - New business post
                launchPostActivity(clickData.getPostId());
                break;
            case NotificationEntity.EventStarting:
                // Case 12 Event Post
                // Fall through to case 16
            case NotificationEntity.EventCreated:
                // Case 16 Event Post
                getEvent(clickData.getEventId());
                break;
            case NotificationEntity.LiveStreamStarting:
                // Case 14 Live Streaming
                int groupId = clickData.getGroupId();
                if (groupId != 0) {
                    launchGroupProfileActivity(groupId);
                } else {
                    startActivity(new Intent(getActivity(), ProfileActivity.class)
                            .putExtra(PROFILEID_KEY, String.valueOf(clickData.getUserId())));
                }
                break;
            case NotificationEntity.SayNoGroupInviteAccepted:
                sayNoFlowInteractor.startSayNo(((AppCompatActivity) getActivity()));
                break;
            case NotificationEntity.MessageRead:
                // Case 1 - User has read your messages
                // Fall through to case 7
            case 7:
                Log.w(TAG, "Deprecated notification type, no longer in use");
                break;
            default:
                Log.e(TAG, "Unknown notification type: " + notificationType);
                break;
        }
        cancelBaseNotification();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.clear_all_item).getActionView().setOnClickListener(v -> clearAllNotification());
        super.onPrepareOptionsMenu(menu);
    }

    private void cancelNotification(int notifyId) {
        Activity activity = getActivity();
        if (activity != null) {
            NotificationManagerCompat nMgr = NotificationManagerCompat.from(getActivity());
            nMgr.cancel(notifyId);
            if (notifyId == CXFirebaseMessagingService.NOTIFICATION_BASE_IDENTIFIER) {
                CXFirebaseMessagingService.clearBaseText();
            }
        }
    }

    private void cancelBaseNotification() {
       cancelNotification(CXFirebaseMessagingService.NOTIFICATION_BASE_IDENTIFIER);
    }

    private void cancelMessageNotification() {
        cancelNotification(CXFirebaseMessagingService.NOTIFICATION_MESSAGES_IDENTIFIER);
    }

    private void clearAllNotification() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity())
                .setMessage(getActivity().getString(R.string.clearAllNotificationRequest))
                .setPositiveButton(getActivity().getString(R.string.yes), (dialog, which) -> runClearAllNotificationRequest())
                .setNegativeButton(getActivity().getString(R.string.no), (dialog, which) -> {
                });

        dialogBuilder.show();
    }

    private void runClearAllNotificationRequest() {
        addSubscription(mNetworkClient.clearAllNotification().subscribe(
                badgeCount -> {
                    if (getActivity() != null) {
                        mHardResetNotificationsAdapter = true;
                        getNotifications();
                    }
                },
                throwable -> mSnackbarUtil.displaySnackBar(getActivity(), R.string.cant_clear_notifications)
        ));
        cancelNotification(CXFirebaseMessagingService.NOTIFICATION_BASE_IDENTIFIER);
    }

    private void launchPostActivity(int postId) {
        Intent postIntent = new Intent(getActivity(), PostCommentsActivity.class);
        postIntent.putExtra(POSTID_KEY, String.valueOf(postId));
        startActivity(postIntent);
    }

    private void launchGroupActivity(int groupId) {
        Intent groupActivityIntent = new Intent(getActivity(), GroupActivity.class);
        groupActivityIntent.putExtra(GROUPID_KEY, groupId);
        startActivity(groupActivityIntent);
    }

    private void launchGroupProfileActivity(int groupId) {
        Intent groupActivityIntent = new Intent(getActivity(), GroupActivity.class);
        groupActivityIntent.putExtra(GROUPID_KEY, groupId);
        groupActivityIntent.putExtra(AUTO_PLAY, true);
        startActivity(groupActivityIntent);
    }

    public void setupNotificationsAdapter() {
        if (mNotificationsListView.getAdapter() == null) {
            Log.d(TAG, "set new adapter");
            mNotificationsAdapter = new NotificationsAdapter(mNotifications, getActivity(), this);
            mNotificationsListView.setAdapter(mNotificationsAdapter);
        } else {
            Log.d(TAG, "update adapter");
            mNotificationsAdapter.notifyDataSetChanged();
        }
    }

    public void hardResetNotificationsAdapter() {
        mNotificationsAdapter = new NotificationsAdapter(mNotifications, getActivity(), this);
        mNotificationsListView.setAdapter(mNotificationsAdapter);
    }

    @Override
    public void onInviteAction(int position, NotificationEntity notification, boolean accept) {
        notificationPosition = position;
        if (notification.getClickType() == NotificationEntity.GroupInvite) {
            if (accept) {
                acceptGroupInvite(new GroupInviteActionWrapper(String.valueOf(notification.getClickData().getGroupId())));
            } else {
                declineGroupInvite(new GroupInviteActionWrapper(String.valueOf(notification.getClickData().getGroupId())));
            }
        } else if (notification.getClickType() == NotificationEntity.FollowerInvite) {
            if (accept) {
                acceptFollowUserInvite(notification.getClickData().getUserId());
            } else {
                declineFollowUserInvite(notification.getClickData().getUserId());
            }

        } else {
            Log.d(TAG, String.format("onInviteAction: not serviced notification %d", notification.getClickType()));
        }
    }

    private void declineFollowUserInvite(Integer userId) {
        addSubscription(mNetworkClient.declineFollowInvite(String.valueOf(userId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseMessage -> updateNotificationStatus(NotificationData.InviteStatus.DECLINED, true),
                        throwable -> mSnackbarUtil.showSnackBarWithoutAction(getActivity(), R.string.cant_decline_invite)));
    }

    private void acceptFollowUserInvite(Integer userId) {
        addSubscription(mNetworkClient.acceptFollowInvite(String.valueOf(userId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseMessage -> updateNotificationStatus(NotificationData.InviteStatus.ACCEPTED, true),
                        throwable -> mSnackbarUtil.showSnackBarWithoutAction(getActivity(), R.string.cant_accept_invite)));
    }

    private void updateNotificationStatus(NotificationData.InviteStatus status, boolean userInvite) {
        NotificationEntity notification = mNotifications.get(notificationPosition);
        if (userInvite) {
            notification.getClickData().setUserInviteStatus(status);
        } else {
            notification.getClickData().setGroupInviteStatus(status);
        }
        notification.setViewed(true);
        mNotificationsAdapter.notifyDataSetChanged();
    }

    private void launchFailureDialog(int messageId) {
        if (getActivity() != null) {
            mSnackbarUtil.showSnackBarWithAction(getActivity(), R.string.unable_to_go_to_chat, R.string.retry, snackbar -> {
                getMessage(messageId);
                SnackbarManager.dismiss();
            });
        }
    }

    protected void getEvent(int eventId) {
        addSubscription(mNetworkClient
                .getEventAsObservable(eventId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(eventResponse -> {
                    mEvent = eventResponse.getEvent();
                    Intent eventActivityIntent = new Intent(getActivity(), EventActivity.class);
                    eventActivityIntent.putExtra(EventActivity.EXTRA_EVENT_ID, mEvent.getId());
                    eventActivityIntent.putExtra(EventActivity.EXTRA_EVENT_OWNER, mEvent.getIs_owner());
                    eventActivityIntent.putExtra(GROUPID_KEY, mEvent.getGroup_id());
                    startActivity(eventActivityIntent);
                }, throwable -> {
                    Log.e(TAG, "Could not get group info", throwable);
                }));
    }


    @Override
    public void onEventMainThread(NewMessagePushEvent newMessagePush) {
        refreshNotificationsPrompt();
    }

    @Override
    public void onEventMainThread(NewCommentPushEvent newCommentPush) {
        refreshNotificationsPrompt();
    }

    @Override
    public void onEventMainThread(NewFollowPushEvent newFollowPush) {
        refreshNotificationsPrompt();
    }

    @Override
    public void onEventMainThread(NewGroupInvitePushEvent newGroupInvitePush) {
        refreshNotificationsPrompt();
    }

    @Override
    public void onEventMainThread(NewUserPostPushEvent newUserPostPush) {
        refreshNotificationsPrompt();
    }

    @Override
    public void onEventMainThread(NewInviteToFollowGroupEvent newInviteToFollowGroupEvent) {
        refreshNotificationsPrompt();
    }

    @Override
    public void onEventMainThread(UserTaggedYouPushEvent userTaggedYouPushEvent) {
        refreshNotificationsPrompt();
    }

    @Override
    public void onEventMainThread(NewBusinessPostPushEvent newBusinessPostPushEvent) {
        refreshNotificationsPrompt();
    }

    @Override
    public void onEventMainThread(NewGroupPostPushEvent newGroupPostPush) {
        refreshNotificationsPrompt();
    }

    public void refreshNotificationsPrompt() {
        if (getActivity() != null) {
            mSnackbarUtil.showIndefiniteWithAction(getActivity(), R.string.new_notification_was_received_text, R.string.refresh_text, snackbar -> {
                mHardResetNotificationsAdapter = true;
                getNotifications();
                SnackbarManager.dismiss();
            });
        }
    }

    private void getMessage(int messageId) {
        addSubscription(mNetworkClient
                .getMessageById(messageId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(messagesHolder -> {
                    if (getActivity() != null) {
                        Message message = messagesHolder.getMessage();
                        Friend friend = new Friend(message.getUserId(), 0, message.getUserFirstName(), message.getUserLastName(), message.getUserAvatar().getAvatar().getUrl(), new ArrayList<>(),
                                message.getUserUsername());

                        Intent messagingActivityIntent = new Intent(getActivity(), MessagingActivity.class);
                        messagingActivityIntent.putExtra(MessagingActivity.EXTRA_FRIEND, friend);
                        startActivity(messagingActivityIntent);
                    }
                }, throwable -> {
                    if (getActivity() != null) {
                        launchFailureDialog(messageId);
                    }
                }));

        cancelMessageNotification();
    }

    private void markAsViewed(int notificationId) {
        cancelNotification(notificationId);
        ArrayList<String> notificationIds = new ArrayList<>();
        notificationIds.add(String.valueOf(notificationId));

        addSubscription(mNetworkClient
                .updateNotificationsAsViewed(notificationIds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(notificationsUpdateViewedResponse -> Log.i(TAG, "Notification marked as viewed"), throwable -> {
                    Log.e(TAG, "Could not mark notifications as viewed", throwable);
                    mSnackbarUtil.showRetry(getActivity(), R.string.unable_to_update_notification_as_viewed_text, snackbar -> {
                        markAsViewed(notificationId);
                        SnackbarManager.dismiss();
                    });
                }));
        cancelNotification(notificationId);
    }

    private void getMoreNotifications(int lastNotificationId) {
        addSubscription(mNetworkClient
                .getNotificationsWithDirection(lastNotificationId, FeedDirection.OLDER.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(() -> mProgressBottom.setVisibility(View.VISIBLE))
                .doOnTerminate(() -> mProgressBottom.setVisibility(View.GONE))
                .subscribe(notificationResponse -> {
                    if (getActivity() != null) {
                        for (NotificationEntity potentialNewNotification : notificationResponse.getNotifications()) {
                            Boolean bDuplicate = false;
                            for (NotificationEntity notification : mNotifications) {
                                if (potentialNewNotification.getId() == notification.getId()) {
                                    bDuplicate = true;
                                }
                            }
                            if (!bDuplicate) {
                                mNotifications.add(potentialNewNotification);
                            }
                        }
                        if (mNotifications.size() > 0) {
                            setupNotificationsAdapter();
                        }
                    }
                }, throwable -> Log.e(TAG, "Error getting more notifications", throwable)));
    }

    private void getNotifications() {
        addSubscription(mNetworkClient
                .getNotifications()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(notificationResponse -> {
                    if (getActivity() != null) {
                        mNotificationsProgressBar.setVisibility(View.GONE);
                        mNotificationsListView.setVisibility(View.VISIBLE);
                        mNotifications = notificationResponse.getNotifications();
                        if (mHardResetNotificationsAdapter) {
                            hardResetNotificationsAdapter();
                        } else {
                            setupNotificationsAdapter();
                        }
                    }
                    mHardResetNotificationsAdapter = false;
                }, throwable -> {
                    Log.e(TAG, "Error getting notifications: ", throwable);
                    if (getActivity() != null) {
                        mNotificationsProgressBar.setVisibility(View.GONE);
                        mNotificationsListView.setVisibility(View.VISIBLE);
                        mSnackbarUtil.showRetry(getActivity(), R.string.unable_to_get_notifications_text, snackbar -> {
                            getNotifications();
                            SnackbarManager.dismiss();
                        });
                    }
                    mHardResetNotificationsAdapter = false;
                }));
    }

    private void acceptGroupInvite(GroupInviteActionWrapper groupInviteActionWrapper) {
        addSubscription(mNetworkClient
                .acceptGroupInvite(groupInviteActionWrapper)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(groupInviteResponse -> {
                    if (getActivity() != null) {
                        // TODO - two issues with this 1) we should get a failure code, not a success code from the web side 2) string comparisons are icky...boo. This was kept for backwards compatibility :(
                        if (groupInviteResponse != null && groupInviteResponse.getMessage() != null
                                && groupInviteResponse.getMessage().toLowerCase().equals("user not added to group.")) {
                            mSnackbarUtil.showSnackBarWithoutAction(getActivity(), R.string.unable_to_accept_group_invite);
                        } else {
                            updateNotificationStatus(NotificationData.InviteStatus.ACCEPTED, false);
                        }
                    }
                }, throwable -> {
                    Log.e(TAG, "Could not accept group invite", throwable);
                    if (getActivity() != null) {
                        mSnackbarUtil.showRetry(getActivity(), R.string.unable_to_accept_group_invite, snackbar -> {
                            acceptGroupInvite(groupInviteActionWrapper);
                            SnackbarManager.dismiss();
                        });
                    }
                }));
    }

    private void declineGroupInvite(final GroupInviteActionWrapper groupInviteActionWrapper) {
        addSubscription(mNetworkClient
                .declineGroupInvite(groupInviteActionWrapper)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(groupInviteResponse -> {
                    if (getActivity() != null) {
                        updateNotificationStatus(NotificationData.InviteStatus.DECLINED, false);
                    }
                }, throwable -> {
                    Log.e(TAG, "Could not decline group invite", throwable);
                    if (getActivity() != null) {
                        mSnackbarUtil.showRetry(getActivity(), R.string.unable_to_decline_group_invite, snackbar -> {
                            declineGroupInvite(groupInviteActionWrapper);
                            SnackbarManager.dismiss();
                        });
                    }
                }));
    }
}
