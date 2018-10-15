package com.conx2share.conx2share.network.models.response;

import com.conx2share.conx2share.model.notification.Notification;
import com.conx2share.conx2share.model.notification.NotificationEntity;

import java.util.ArrayList;

public class GetNotificationResponse {

    ArrayList<NotificationEntity> notifications;

    public GetNotificationResponse(ArrayList<NotificationEntity> notifications) {
        this.notifications = notifications;
    }

    public ArrayList<NotificationEntity> getNotifications() {
        return notifications;
    }

    public void setNotifications(ArrayList<NotificationEntity> notifications) {
        this.notifications = notifications;
    }
}
