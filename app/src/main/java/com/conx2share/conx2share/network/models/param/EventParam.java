package com.conx2share.conx2share.network.models.param;

import com.conx2share.conx2share.util.TypedUri;
import com.google.gson.Gson;

public class EventParam {

    private int mGroupId, mEventId, mBusinessId;

    private String mEventAbout, mEventDescription, mEventStartTime, mEventEndTime, mLocation, mEventName;

    private transient TypedUri mAttachmentUri;

    public EventParam() {
    }

    public EventParam(String type, int groupId, int eventId, String eventAbout, String eventDescription, String eventStartTime,
                      String eventEndTime, String location, String eventName, TypedUri attachmentUri) {
        if (type.equals("business")) {
            mBusinessId = groupId;
        } else {
            mGroupId = groupId;
        }
        mEventId = eventId;
        mEventAbout = eventAbout;
        mEventDescription = eventDescription;
        mEventStartTime = eventStartTime;
        mEventEndTime = eventEndTime;
        mLocation = location;
        mEventName = eventName;
        mAttachmentUri = attachmentUri;
    }

    public int getmBusinessId() {
        return mBusinessId;
    }

    public int getGroupId() {
        return mGroupId;
    }

    public void setGroupId(int groupId) {
        mGroupId = groupId;
    }

    public String getEventAbout() {
        return mEventAbout;
    }

    public void setEventAbout(String eventAbout) {
        mEventAbout = eventAbout;
    }

    public String getEventDescription() {
        return mEventDescription;
    }

    public void setEventDescription(String eventDescription) {
        mEventDescription = eventDescription;
    }

    public String getEventStartTime() {
        return mEventStartTime;
    }

    public void setEventStartTime(String eventStartTime) {
        mEventStartTime = eventStartTime;
    }

    public String getEventEndTime() {
        return mEventEndTime;
    }

    public void setEventEndTime(String eventEndTime) {
        mEventEndTime = eventEndTime;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public String getEventName() {
        return mEventName;
    }

    public void setEventName(String eventName) {
        mEventName = eventName;
    }

    public TypedUri getAttachmentUri() {
        return mAttachmentUri;
    }

    public void setAttachmentUri(TypedUri attachmentUri) {
        mAttachmentUri = attachmentUri;
    }

    public int getEventId() {
        return mEventId;
    }

    public void setEventId(int eventId) {
        mEventId = eventId;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}