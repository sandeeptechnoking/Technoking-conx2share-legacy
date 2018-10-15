package com.conx2share.conx2share.network.models.response;


import android.os.Parcel;
import android.os.Parcelable;

import com.conx2share.conx2share.model.Like;
import com.conx2share.conx2share.model.Post;
import com.conx2share.conx2share.model.PostPicture;
import com.conx2share.conx2share.model.StreamingUrl;
import com.conx2share.conx2share.model.Video;
import com.conx2share.conx2share.network.models.UserTag;

import java.util.ArrayList;
import java.util.Date;

public class HashTag implements Parcelable {

    private Integer id;

    private String title;

    private Integer creatorId;

    private ArrayList<Post> posts;

    public HashTag(Integer id, String title, Integer creatorId) {
        this.id = id;
        this.title = title;
        this.creatorId = creatorId;
    }

    public HashTag(Integer id, String title, Integer creatorId,
            ArrayList<Post> posts) {

        this.id = id;
        this.title = title;
        this.creatorId = creatorId;
        this.posts = posts;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    public ArrayList<Post> getPosts() {
        return posts;
    }

    public void setPost(ArrayList<Post> posts) {
        this.posts = posts;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.creatorId);
        dest.writeString(this.title);
        dest.writeList(this.posts);
    }

    protected HashTag(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.creatorId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.title = in.readString();
        this.posts = new ArrayList<Post>();
        in.readList(this.posts, Post.class.getClassLoader());
    }

    public static final Creator<HashTag> CREATOR = new Creator<HashTag>() {
        @Override
        public HashTag createFromParcel(Parcel source) {
            return new HashTag(source);
        }

        @Override
        public HashTag[] newArray(int size) {
            return new HashTag[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HashTag)) {
            return false;
        }

        HashTag hashTag = (HashTag) o;

        if (creatorId != null ? !creatorId.equals(hashTag.creatorId) : hashTag.creatorId != null) {
            return false;
        }
        if (id != null ? !id.equals(hashTag.id) : hashTag.id != null) {
            return false;
        }
        if (posts != null ? !posts.equals(hashTag.posts) : hashTag.posts != null) {
            return false;
        }
        if (title != null ? !title.equals(hashTag.title) : hashTag.title != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (creatorId != null ? creatorId.hashCode() : 0);
        result = 31 * result + (posts != null ? posts.hashCode() : 0);
        return result;
    }
}
