package com.conx2share.conx2share.adapter;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.model.notification.Notification;
import com.conx2share.conx2share.model.notification.NotificationData;
import com.conx2share.conx2share.model.notification.NotificationEntity;
import com.conx2share.conx2share.ui.view.AvatarImageView;
import com.conx2share.conx2share.util.DateUtils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

public class NotificationsAdapter extends ArrayAdapter<NotificationEntity> {

    private static final String TAG = NotificationsAdapter.class.getSimpleName();

    private LayoutInflater mLayoutInflater;

    private NotificationActionCallback mCallback;

    public NotificationsAdapter(ArrayList<NotificationEntity> notifications, Context context, NotificationActionCallback notificationActionCallback) {
        super(context, R.layout.notification_list_item, notifications);

        mLayoutInflater = LayoutInflater.from(context);
        mCallback = notificationActionCallback;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.notification_list_item, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = ((ViewHolder) convertView.getTag());
            vh.resetViews(convertView);
        }

        NotificationEntity notification = getItem(position);

        vh.notificationName.setText(notification.getTitle());
        vh.notificationTypeText.setText(notification.getBody());

        vh.notificationStatus.setVisibility(View.GONE);
        vh.groupPendingControlsLayout.setVisibility(View.GONE);

        switch (notification.getClickType()) {
            case Notification.GroupInvite:
            case Notification.FollowerInvite:
                updateInviteViews(vh, notification);
                break;
            case Notification.SayNoGroupInviteAccepted:
                vh.notificationTypeText.setText("You were accepted into the group " + notification.getBody());
                vh.notificationName.setText("Say NO!");
                break;
            case Notification.MessageRead:
                // Case 1 - User read your message
                // Fall through to case 7
            case 7:
                // Case 7 - All broadcast
                Log.w(TAG, "Deprecated notification type, no longer in use.");
                break;
            default:
                Log.e(TAG, "Default notification type: " + notification.getClickType());
                break;
        }

        vh.acceptGroupInviteButton.setOnClickListener(v -> mCallback.onInviteAction(position, notification, true));
        vh.declineGroupInvitation.setOnClickListener(v -> mCallback.onInviteAction(position, notification, false));

        String notificationRead = "";
        if (notification.isViewed()) {
            notificationRead = getContext().getString(R.string.viewed);
        }

        vh.notificationDate.setText(DateUtils.getTimeDifference(notification.getCreatedAt()) + " " + getContext().getString(R.string.ago) + " " + notificationRead);

        vh.notificationAvatar.initView(notification.getImageUrl(), notification.getBody());
        return convertView;
    }

    private void updateInviteViews(ViewHolder vh, NotificationEntity notification) {
        NotificationData clickData = notification.getClickData();
        NotificationData.InviteStatus inviteStatus = clickData.getUserInviteStatus() != null
                ? clickData.getUserInviteStatus() : clickData.getGroupInviteStatus();
        if (inviteStatus != null) {
            switch (inviteStatus) {
                case ACCEPTED:
                    vh.notificationStatus.setVisibility(View.VISIBLE);
                    vh.groupPendingControlsLayout.setVisibility(View.GONE);
                    vh.notificationStatus.setText(getContext().getString(R.string.accepted));
                    break;
                case DECLINED:
                    vh.notificationStatus.setVisibility(View.VISIBLE);
                    vh.groupPendingControlsLayout.setVisibility(View.GONE);
                    vh.notificationStatus.setText(getContext().getString(R.string.declined));
                    break;
                case PENDING:
                    vh.groupPendingControlsLayout.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    public interface NotificationActionCallback {
        void onInviteAction(int position, NotificationEntity notification, boolean accept);
    }

    private class ViewHolder {

        TextView notificationName;
        TextView notificationTypeText;
        TextView notificationDate;
        TextView notificationStatus;
        AvatarImageView notificationAvatar;
        LinearLayout groupPendingControlsLayout;
        Button acceptGroupInviteButton;
        Button declineGroupInvitation;

        public ViewHolder(View view) {
            setupViews(view);
        }

        private void setupViews(View view) {
            notificationName = (TextView) view.findViewById(R.id.notification_name);
            notificationTypeText = (TextView) view.findViewById(R.id.notification_type_text);
            notificationDate = (TextView) view.findViewById(R.id.notification_date);
            notificationStatus = (TextView) view.findViewById(R.id.notification_status);
            notificationAvatar = (AvatarImageView) view.findViewById(R.id.notification_avatar);
            groupPendingControlsLayout = (LinearLayout) view.findViewById(R.id.group_pending_controls);
            acceptGroupInviteButton = (Button) view.findViewById(R.id.accept_group_invite_button);
            declineGroupInvitation = (Button) view.findViewById(R.id.decline_group_invite_button);
        }

        public void resetViews(View view) {
            notificationName = null;
            notificationTypeText = null;
            notificationDate = null;
            notificationStatus = null;
            notificationAvatar = null;
            groupPendingControlsLayout = null;
            acceptGroupInviteButton = null;
            declineGroupInvitation = null;
            setupViews(view);
        }
    }
}
