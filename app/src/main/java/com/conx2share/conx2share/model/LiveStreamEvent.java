package com.conx2share.conx2share.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public class LiveStreamEvent implements Parcelable {


    private Integer id;
    @SerializedName("group_id")
    private Integer groupId;
    @SerializedName("business_id")
    private Integer businessId;
    private Picture image;
    private String description;
    private String name;
    private String status;
    private String location;
    @SerializedName("job_id")
    private String jobId;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("start_time")
    private String startTime;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("end_time")
    private String endTime;

    public LiveStreamEvent() {}

    public String getImageUrl() {
        if (image == null) return "";

        if (!TextUtils.isEmpty(image.getFeedUrl())) {
            return image.getFeedUrl();
        } else if (!TextUtils.isEmpty(image.getUrl())) {
            return image.getUrl();
        } else {
            return "";
        }
    }

    public String getThumbUrl() {
        if (image == null) return "";

        if (!TextUtils.isEmpty(image.getThumbUrl())) {
            return image.getThumbUrl();
        } else {
            return "";
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
    }

    public Picture getImage() {
        return image;
    }

    public void setImage(Picture image) {
        this.image = image;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    private LiveStreamEvent(Parcel in) {
        image = in.readParcelable(Picture.class.getClassLoader());
        description = in.readString();
        name = in.readString();
        status = in.readString();
        location = in.readString();
        jobId = in.readString();
        createdAt = in.readString();
        startTime = in.readString();
        updatedAt = in.readString();
        endTime = in.readString();
    }

    public static final Creator<LiveStreamEvent> CREATOR = new Creator<LiveStreamEvent>() {
        @Override
        public LiveStreamEvent createFromParcel(Parcel in) {
            return new LiveStreamEvent(in);
        }

        @Override
        public LiveStreamEvent[] newArray(int size) {
            return new LiveStreamEvent[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(image, flags);
        dest.writeString(description);
        dest.writeString(name);
        dest.writeString(status);
        dest.writeString(location);
        dest.writeString(jobId);
        dest.writeString(createdAt);
        dest.writeString(startTime);
        dest.writeString(updatedAt);
        dest.writeString(endTime);
    }
}
