package com.conx2share.conx2share.network.models.param;

import com.conx2share.conx2share.ui.groups.GroupType;
import com.conx2share.conx2share.util.TypedUri;

public class GroupParam {

    private String mGroupName;

    private String mGroupAbout;

    private TypedUri mAttachmentUri;

    private GroupType mGroupType;

    public GroupParam() {
        // NO OP
    }

    public GroupParam(String groupName, String groupAbout, TypedUri attachmentUri, GroupType groupType) {
        mGroupName = groupName;
        mGroupAbout = groupAbout;
        mAttachmentUri = attachmentUri;
        mGroupType = groupType;
    }

    public String getGroupName() {
        return mGroupName;
    }

    public void setGroupName(String groupName) {
        mGroupName = groupName;
    }

    public String getGroupAbout() {
        return mGroupAbout;
    }

    public void setGroupAbout(String groupAbout) {
        mGroupAbout = groupAbout;
    }

    public TypedUri getAttachmentUri() {
        return mAttachmentUri;
    }

    public void setAttachmentUri(TypedUri attachmentUri) {
        mAttachmentUri = attachmentUri;
    }

    public GroupType getGroupType() {
        return mGroupType;
    }

    public void setGroupType(GroupType groupType) {
        mGroupType = groupType;
    }
}