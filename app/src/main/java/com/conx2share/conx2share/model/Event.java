package com.conx2share.conx2share.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.conx2share.conx2share.util.DateUtils;
import com.conx2share.conx2share.util.ValidationUtil;
import com.google.gson.Gson;

public class Event implements Parcelable {

    public enum Status {
        LEGACY, PENDING, LIVE, FINISHED, VOD, CDN, DELETED, ERROR, ORPHANED
    }

    private String checkins;
    private String location;
    private String status;
    private String group_id;
    private ImageMessage image;
    private String users_going;
    private LiveStream live_stream;
    private BroadcastInfo broadcast_info;
    private Integer id;
    private String updated_at;
    private Boolean is_owner;
    private String rsvp_status;
    private String end_time;
    private String description;
    private String name;
    private int business_id;
    private String created_at;
    private String start_time;
    private String users_maybe_going;
//    private String disposition;
    // custom fields for UI purposes
    private String formattedStartDateTime;
    private long startTimeMillis;
    private Status disposition;

    public String getCheckins() {
        return checkins;
    }

    public void setCheckins(String checkins) {
        this.checkins = checkins;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getUrl() {
        if (image.getImage() == null) return null;
        if (image.getImage().getFeedUrl() != null) {
            return image.getImage().getFeedUrl();
        } else if (image.getImage().getUrl() != null) {
            return image.getImage().getUrl();
        } else {
            return null;
        }
    }

    public String getUsers_going() {
        return users_going;
    }

    public void setUsers_going(String users_going) {
        this.users_going = users_going;
    }

    public LiveStream getLive_stream() {
        return live_stream;
    }

    public void setLive_stream(LiveStream live_stream) {
        this.live_stream = live_stream;
    }

    public BroadcastInfo getBroadcast_info() {
        return broadcast_info;
    }

    public void setBroadcast_info(BroadcastInfo broadcast_info) {
        this.broadcast_info = broadcast_info;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public Boolean getIs_owner() {
        return is_owner;
    }

    public void setIs_owner(boolean is_owner) {
        this.is_owner = is_owner;
    }

    public String getRsvp_status() {
        return rsvp_status;
    }

    public void setRsvp_status(String rsvp_status) {
        this.rsvp_status = rsvp_status;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(int business_id) {
        this.business_id = business_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getUsers_maybe_going() {
        return users_maybe_going;
    }

    public void setUsers_maybe_going(String users_maybe_going) {
        this.users_maybe_going = users_maybe_going;
    }

    public Status getDisposition() {
        return disposition;
    }

    public void setDisposition(Status disposition) {
        this.disposition = disposition;
    }

    public String getFormattedStartDateTime() {
        if (!ValidationUtil.checkIfStringIsValid(formattedStartDateTime)) {
            calculateFormattedStartDateTime();
        }
        return formattedStartDateTime;
    }

    private void calculateFormattedStartDateTime() {
        this.formattedStartDateTime = DateUtils.getLocalDateTime(start_time);
    }

    public Event() {
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public long getStartTimeMillis() {
        if (startTimeMillis == 0L) {
            startTimeMillis = DateUtils.isoDateTimeToMillis(start_time);
        }
        return startTimeMillis;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.checkins);
        dest.writeString(this.location);
        dest.writeString(this.status);
        dest.writeString(this.group_id);
        dest.writeParcelable(this.image, flags);
        dest.writeString(this.users_going);
        dest.writeParcelable(this.live_stream, flags);
        dest.writeParcelable(this.broadcast_info, flags);
        dest.writeInt(this.id);
        dest.writeString(this.updated_at);
        dest.writeValue(this.is_owner);
        dest.writeString(this.rsvp_status);
        dest.writeString(this.end_time);
        dest.writeString(this.description);
        dest.writeString(this.name);
        dest.writeInt(this.business_id);
        dest.writeString(this.created_at);
        dest.writeString(this.start_time);
        dest.writeString(this.users_maybe_going);
        dest.writeString(this.disposition.name());
        dest.writeString(this.formattedStartDateTime);
        dest.writeLong(this.startTimeMillis);
    }

    protected Event(Parcel in) {
        this.checkins = in.readString();
        this.location = in.readString();
        this.status = in.readString();
        this.group_id = in.readString();
        this.image = in.readParcelable(ImageMessage.class.getClassLoader());
        this.users_going = in.readString();
        this.live_stream = in.readParcelable(LiveStream.class.getClassLoader());
        this.broadcast_info = in.readParcelable(BroadcastInfo.class.getClassLoader());
        this.id = in.readInt();
        this.updated_at = in.readString();
        this.is_owner = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.rsvp_status = in.readString();
        this.end_time = in.readString();
        this.description = in.readString();
        this.name = in.readString();
        this.business_id = in.readInt();
        this.created_at = in.readString();
        this.start_time = in.readString();
        this.users_maybe_going = in.readString();
        this.disposition = Status.valueOf(in.readString());
        this.formattedStartDateTime = in.readString();
        this.startTimeMillis = in.readLong();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel source) {
            return new Event(source);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}