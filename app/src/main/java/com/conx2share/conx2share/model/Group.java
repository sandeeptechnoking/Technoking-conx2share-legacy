package com.conx2share.conx2share.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.conx2share.conx2share.network.models.User;
import com.conx2share.conx2share.ui.feed.post.PostReceiver;
import com.google.gson.annotations.SerializedName;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Group implements Parcelable, PostReceiver {

    // TODO - consolidate this with the GroupType enum DMays made
    public static final int PRIVATE_KEY = 1;
    public static final int DISCUSSION_KEY = 2;
    public static final int BLOG_KEY = 3;

    // TODO - maybe make this an enum?// These are used both by Post and Comments, this seemed like the most reasonable place to place these keys
    public static final int GROUP_STATUS_OWNER = 0; // Not return from the server, but here for the comparator work in GroupIndexFrag
    public static final int GROUP_STATUS_MEMBER = 1;
    public static final int GROUP_STATUS_FOLLOWER = 2;
    public static final int GROUP_STATUS_BLOCKED = 3;
    public static final int GROUP_STATUS_NOT_ASSOCIATED = 4;
    public static final String EXTRA = "extra_group";

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        public Group createFromParcel(Parcel source) {
            return new Group(source);
        }

        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

    private Integer id;
    private Integer creatorId;
    private String name;
    private String about;
    private Integer badgeCount;

    @SerializedName("groupavatar")
    private GroupAvatarHolder groupavatar;

    private Integer memberCount;
    private Integer followers;
    private ArrayList<User> users;
    private ArrayList<Post> posts;
    private Boolean isMember;
    private Boolean isFollowing;
    private Boolean isOwner;
    private StreamingUrl liveStream;
    private Boolean isBlocked;
    private Integer groupType;
    private String group_signup_url;

    @SerializedName("say_no_reporter_invitation_status")
    private InvitationState sayNoInvitationState;

    public Group(Integer id, Integer creatorId, String name, String about, GroupAvatarHolder groupavatar, Integer memberCount) {
        this.id = id;
        this.creatorId = creatorId;
        this.name = name;
        this.about = about;
        this.groupavatar = groupavatar;
        this.memberCount = memberCount;
    }

    public Group(Integer id, Integer creatorId, String name, String about, GroupAvatarHolder groupavatar, Integer memberCount, Integer groupType) {
        this.id = id;
        this.creatorId = creatorId;
        this.name = name;
        this.about = about;
        this.groupavatar = groupavatar;
        this.memberCount = memberCount;
        this.groupType = groupType;
    }

    public Group(Integer id, Integer creatorId, String name, GroupAvatarHolder groupavatar, Integer memberCount) {
        this.id = id;
        this.creatorId = creatorId;
        this.name = name;
        this.groupavatar = groupavatar;
        this.memberCount = memberCount;
    }

    public Group(Integer id, Integer creatorId, String name, GroupAvatarHolder groupavatar, Integer memberCount, Integer groupType) {
        this.id = id;
        this.creatorId = creatorId;
        this.name = name;
        this.groupavatar = groupavatar;
        this.memberCount = memberCount;
        this.groupType = groupType;
    }

    public Group(Integer id, Integer creatorId, String name, GroupAvatarHolder groupavatar, Integer memberCount, Integer groupType, Boolean isOwner, Boolean isMember, Boolean isFollowing,
                 Boolean isBlocked) {
        this.id = id;
        this.creatorId = creatorId;
        this.name = name;
        this.groupavatar = groupavatar;
        this.memberCount = memberCount;
        this.groupType = groupType;
        this.isOwner = isOwner;
        this.isMember = isMember;
        this.isFollowing = isFollowing;
        this.isBlocked = isBlocked;
    }

    public Group(Integer id, Integer creatorId, String name, GroupAvatarHolder groupavatar, Integer memberCount, ArrayList<User> users) {
        this.id = id;
        this.creatorId = creatorId;
        this.name = name;
        this.groupavatar = groupavatar;
        this.memberCount = memberCount;
        this.users = users;
    }

    public Group(Integer id, Integer creatorId, String name, String about, GroupAvatarHolder groupavatar, Integer memberCount, Integer followers, Boolean isMember, Boolean isFollowing,
                 Boolean isOwner, Boolean isBlocked, Integer groupType, ArrayList<Post> posts) {
        this.id = id;
        this.creatorId = creatorId;
        this.name = name;
        this.about = about;
        this.groupavatar = groupavatar;
        this.memberCount = memberCount;
        this.followers = followers;
        this.isMember = isMember;
        this.isFollowing = isFollowing;
        this.isOwner = isOwner;
        this.isBlocked = isBlocked;
        this.groupType = groupType;
        this.posts = posts;
    }

    public Group(Integer id, Integer creatorId, String name, String about, Integer badgeCount, GroupAvatarHolder groupavatar, Integer memberCount, Integer followers, Boolean isMember,
            Boolean isFollowing, Boolean isOwner, StreamingUrl liveStream, Boolean isBlocked, Integer groupType, ArrayList<Post> posts ) {
        this.id = id;
        this.creatorId = creatorId;
        this.name = name;
        this.about = about;
        this.badgeCount = badgeCount;
        this.groupavatar = groupavatar;
        this.memberCount = memberCount;
        this.followers = followers;
        this.isMember = isMember;
        this.isFollowing = isFollowing;
        this.isOwner = isOwner;
        this.liveStream = liveStream;
        this.isBlocked = isBlocked;
        this.groupType = groupType;
        this.posts = posts;

    }

    public Group() {

    }

    private Group(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.creatorId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.name = in.readString();
        this.about = in.readString();
        this.badgeCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.groupavatar = in.readParcelable(GroupAvatarHolder.class.getClassLoader());
        this.memberCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.followers = (Integer) in.readValue(Integer.class.getClassLoader());
        this.isMember = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isFollowing = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isOwner = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.liveStream = in.readParcelable(StreamingUrl.class.getClassLoader());
        this.isBlocked = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.groupType = (Integer) in.readValue(Integer.class.getClassLoader());

        this.users = new ArrayList<>();
        in.readList(this.users, User.class.getClassLoader());

        this.posts = new ArrayList<>();
        in.readList(this.posts, Post.class.getClassLoader());
        if (in.readString() != null) {
            this.sayNoInvitationState = InvitationState.valueOf(in.readString());
        }
        this.group_signup_url = in.readString();
    }

    @Override
    public String toString() {
        return "Group{" +
                "name='" + name + '\'' +
                ", about='" + about + '\'' +
                ", id=" + id +
                ", groupavatar=" + groupavatar.toString() +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    @Override
    public PostReceiverType getType() {
        return PostReceiverType.GROUP;
    }

    @Override
    public Integer getReceiverId() {
        return getId();
    }

    public GroupAvatarHolder getGroupavatar() {
        return groupavatar;
    }

    public String getGroupAvatarUrl() {
        return getGroupavatar().getGroupAvatar().getUrl();
    }

    public void setGroupAvatar(GroupAvatarHolder groupavatar) {
        this.groupavatar = groupavatar;
    }

    public Integer getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public ArrayList<Post> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
    }


    public Integer getBadgeCount() {
        return badgeCount;
    }

    public void setBadgeCount(Integer badgeCount) {
        this.badgeCount = badgeCount;
    }

    public Integer getFollowers() {
        // NOTE - for a discussion group followers are members
        if (getGroupType() == Group.DISCUSSION_KEY) {
            return getMemberCount();
        } else {
            return followers;
        }
    }

    public void setFollowers(Integer followers) {
        this.followers = followers;
    }

    public Integer getGroupType() {
        return groupType;
    }

    public void setGroupType(Integer groupType) {
        this.groupType = groupType;
    }

    public Boolean isMember() {
        return isMember;
    }

    public void setMember(Boolean isMember) {
        this.isMember = isMember;
    }

    public Boolean isFollowing() {
        // NOTE - for a discussion group followers are members
        Integer groupType = getGroupType();
        if (groupType != null && groupType == Group.DISCUSSION_KEY) {
            return isMember();
        } else {
            return isFollowing;
        }
    }

    public void setFollowing(Boolean isFollowing) {
        this.isFollowing = isFollowing;
    }


    public Boolean isOwner() {
        return isOwner;
    }

    public void setOwner(Boolean isOwner) {
        this.isOwner = isOwner;
    }

    public StreamingUrl getLiveStream() {
        return liveStream;
    }

    public void setLiveStream(StreamingUrl liveStream) {
        this.liveStream = liveStream;
    }

    public Boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(Boolean isBlocked) {
        this.isBlocked = isBlocked;
    }

    public InvitationState getSayNoInvitationState() {
        return sayNoInvitationState;
    }

    public String getGroup_signup_url() {
        return group_signup_url;
    }

    public boolean isVipGroup() {
        return groupType == 4 && group_signup_url != null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeValue(this.creatorId);
        dest.writeString(this.name);
        dest.writeString(this.about);
        dest.writeValue(this.badgeCount);
        dest.writeParcelable(this.groupavatar, 0);
        dest.writeValue(this.memberCount);
        dest.writeValue(this.followers);
        dest.writeValue(this.isMember);
        dest.writeValue(this.isFollowing);
        dest.writeValue(this.isOwner);
        dest.writeParcelable(this.liveStream, 0);
        dest.writeValue(this.isBlocked);
        dest.writeValue(this.groupType);
        dest.writeList(this.users);
        dest.writeList(this.posts);
        if (this.sayNoInvitationState != null) {
            dest.writeString(this.sayNoInvitationState.name());
        }
        dest.writeString(this.group_signup_url);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Group)) {
            return false;
        }

        Group group = (Group) o;

        if (about != null ? !about.equals(group.about) : group.about != null) {
            return false;
        }
        if (badgeCount != null ? !badgeCount.equals(group.badgeCount) : group.badgeCount != null) {
            return false;
        }
        if (creatorId != null ? !creatorId.equals(group.creatorId) : group.creatorId != null) {
            return false;
        }
        if (followers != null ? !followers.equals(group.followers) : group.followers != null) {
            return false;
        }
        if (groupType != null ? !groupType.equals(group.groupType) : group.groupType != null) {
            return false;
        }
        if (groupavatar != null ? !groupavatar.equals(group.groupavatar)
                : group.groupavatar != null) {
            return false;
        }
        if (id != null ? !id.equals(group.id) : group.id != null) {
            return false;
        }
        if (isBlocked != null ? !isBlocked.equals(group.isBlocked) : group.isBlocked != null) {
            return false;
        }
        if (isFollowing != null ? !isFollowing.equals(group.isFollowing)
                : group.isFollowing != null) {
            return false;
        }
        if (isMember != null ? !isMember.equals(group.isMember) : group.isMember != null) {
            return false;
        }
        if (isOwner != null ? !isOwner.equals(group.isOwner) : group.isOwner != null) {
            return false;
        }
        if (memberCount != null ? !memberCount.equals(group.memberCount)
                : group.memberCount != null) {
            return false;
        }
        if (name != null ? !name.equals(group.name) : group.name != null) {
            return false;
        }
        if (posts != null ? !posts.equals(group.posts) : group.posts != null) {
            return false;
        }
        if (users != null ? !users.equals(group.users) : group.users != null) {
            return false;
        }

        if (liveStream != null ? !liveStream.equals(group.liveStream) : group.liveStream != null) {
            return false;
        }


        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (creatorId != null ? creatorId.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (about != null ? about.hashCode() : 0);
        result = 31 * result + (badgeCount != null ? badgeCount.hashCode() : 0);
        result = 31 * result + (groupavatar != null ? groupavatar.hashCode() : 0);
        result = 31 * result + (memberCount != null ? memberCount.hashCode() : 0);
        result = 31 * result + (followers != null ? followers.hashCode() : 0);
        result = 31 * result + (users != null ? users.hashCode() : 0);
        result = 31 * result + (posts != null ? posts.hashCode() : 0);
        result = 31 * result + (isMember != null ? isMember.hashCode() : 0);
        result = 31 * result + (isFollowing != null ? isFollowing.hashCode() : 0);
        result = 31 * result + (isOwner != null ? isOwner.hashCode() : 0);
        result = 31 * result + (liveStream != null ? liveStream.hashCode() : 0);
        result = 31 * result + (isBlocked != null ? isBlocked.hashCode() : 0);
        result = 31 * result + (groupType != null ? groupType.hashCode() : 0);
        return result;
    }
}
