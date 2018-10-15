package com.conx2share.conx2share.model;

import android.text.TextUtils;

import com.conx2share.conx2share.network.models.User;
import com.google.gson.annotations.SerializedName;

import java.util.Comparator;

public class LiveEvent {

    public static final int FIRST_PRORITY = 1;
    public static final int SECOND_PRORITY = 2;
    public static final int THIRD_PRORITY = 3;
    public static final int FOURTH_PRORITY = 4;
    public static final int FIFTH_PRORITY = 5;
    public static final int SIXTH_PRORITY = 6;
    public static final int SEVENTH_PRORITY = 7;

    @SerializedName("id")
    private Integer id;

    @SerializedName("event_token")
    private String eventToken;

    @SerializedName("device_os")
    private String deviceOs;

    @SerializedName("url")
    private String url;

    @SerializedName("disposition")
    private Disposition disposition;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("user")
    private User user;

    @SerializedName("event")
    private LiveStreamEvent event;

    @SerializedName("group")
    private Group group;

    @SerializedName("business")
    private Business business;

    private int priority = 100;

    private Integer getId() {
        return id;
    }

    public String getEventToken() {
        return eventToken;
    }

    public String getDeviceOs() {
        return deviceOs;
    }

    public String getUrl() {
        return url;
    }

    public Disposition getDisposition() {
        return disposition;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public User getUser() {
        return user;
    }

    public LiveStreamEvent getEvent() {
        return event;
    }

    public Group getGroup() {
        return group;
    }

    public Business getBusiness() {
        return business;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public enum Disposition {
        @SerializedName("LEGACY")
        LEGACY,
        @SerializedName("PENDING")
        PENDING,
        @SerializedName("LIVE")
        LIVE,
        @SerializedName("FINISHED")
        FINISHED,
        @SerializedName("VOD")
        VOD,
        @SerializedName("CDN")
        CDN,
        @SerializedName("DELETED")
        DELETED,
        @SerializedName("ERROR")
        ERROR,
        @SerializedName("ORPHANED")
        ORPHANED,
    }

    public boolean isLive() {
        return disposition == Disposition.LIVE;
    }

    public String getImageUrl() {
        if (event != null) {
            if (!TextUtils.isEmpty(event.getImageUrl())) {
                return event.getImageUrl();
            } else if (business != null) {
                return business.getAvatarUrl();
            } else if (group != null) {
                return group.getGroupAvatarUrl();
            }
        } else if (business != null) {
            return business.getAvatarUrl();
        } else if (group != null) {
            return group.getGroupAvatarUrl();
        } else if (user != null) {
            return user.getAvatarUrl();
        }
        return "";
    }

    public String getDescription() {
        if (event != null) {
            String description = "";
            if (!TextUtils.isEmpty(event.getName())) {
                description = event.getName();
            }
            if (!TextUtils.isEmpty(event.getDescription())) {
                return description.equals("") ? event.getDescription() : description.concat(" - ").concat(event.getDescription());
            } else {
                return description;
            }

        } else if (business != null) {
            return "Business Livestream";
        } else if (group != null) {
            return "Group Livestream";
        } else if (user != null) {
            return "Personal Livestream ".concat(user.getHandleText());
        }
        return "";
    }

    public String getTitle() {
        if (event != null) {
            if (business != null) {
                return business.getName();
            } else if (group != null) {
                return group.getName();
            }
            return "";
        } else if (business != null) {
            return business.getName();
        } else if (group != null && !TextUtils.isEmpty(group.getName())) {
            return "Group: ".concat(group.getName());
        } else if (user != null) {
            return user.getDisplayName();
        }
        return "";
    }

     /*  Rules:
         *  0) Api sends "device_os" = ios; and android. Use Android only in our case
         *  1) At very top - People you are following who are currently streaming.
         *  2) Next under those would be livestreams currently going on from groups you are in or follow.
         *  3) Next under those would be livestreams currently going on from businesses you follow
         *  4) Public groups with livestreams currently going on that you are not a part of.
         *  5) Previously recorded Livestreams from People you are following.
         *  6) Previously recorded Public Livestreams from Groups, or businesses you follow or are in, no live icon. Ordered by date.
         *  7) Previously recorded Public Livestreams from Groups you are not in, but public groups.
         *  Notes:  If it's an event then it will have either a business or group object
         *          If there is a group object but no event, then it's a group one
         *          All of the items returned by the server are there bc you are either following them, or
         *          bc the items are public.
         */

    public void calculatePriority() {
        if (isLive() && user != null) {
            priority = FIRST_PRORITY;
        } else if (isLive() && group != null && group.isFollowing()) {
            priority = SECOND_PRORITY;
        } else if (isLive() && business != null && business.getIsFollowing()) {
            priority = THIRD_PRORITY;
        } else if (isLive() && group != null) {
            priority = FOURTH_PRORITY;
        } else if (!isLive() && user != null) {
            priority = FIFTH_PRORITY;
        } else if (!isLive() && ((group != null && group.isFollowing()) || (business != null && business.getIsFollowing()))) {
            priority = SIXTH_PRORITY;
        } else if (!isLive() && (group != null || business != null)) {
            priority = SEVENTH_PRORITY;
        }
    }

    public static class LiveEventComparator implements Comparator<LiveEvent> {

        @Override
        public int compare(LiveEvent lhs, LiveEvent rhs) {
            if (lhs == null || rhs == null) return 0;
            return lhs.priority - rhs.priority;
        }
    }
}
