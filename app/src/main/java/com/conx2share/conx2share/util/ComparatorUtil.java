package com.conx2share.conx2share.util;

import com.conx2share.conx2share.model.Business;
import com.conx2share.conx2share.model.Event;
import com.conx2share.conx2share.model.Group;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

public class ComparatorUtil {

    public static final Comparator<String> STRING_ALPHA_COMPARATOR = (lhs, rhs) -> lhs.toLowerCase().compareTo(rhs
            .toLowerCase());

    public static final Comparator<Business> BUSINESS_ALPHA_COMPARATOR = (lhs, rhs) -> lhs.getName().toLowerCase()
            .compareTo(rhs.getName().toLowerCase());

    public static final Comparator<Group> GROUP_ALPHA_COMPARATOR = (lhs, rhs) -> lhs.getName().toLowerCase()
            .compareTo(rhs.getName().toLowerCase());

    public static final Comparator<Event> EVENT_DATE_COMPARATOR = (lhs, rhs) -> {
        if (lhs.getStartTimeMillis() > rhs.getStartTimeMillis()) {
            return -1;
        } else if (lhs.getStartTimeMillis() < rhs.getStartTimeMillis()) {
            return 1;
        } else {
            return 0;
        }
    };

    private static final Comparator<Group> CONX2SHARE_COMPARATOR = (lhs, rhs) -> {
        int result;
        if (PrivilegeChecker.isConx2ShareGroup(lhs.getId())) {
            result = -1;
        } else if (PrivilegeChecker.isConx2ShareGroup(rhs.getId())) {
            result = 1;
        } else {
            result = 0;
        }
        return result;
    };

    private static final Comparator<Group> GROUP_STATUS_COMPARATOR = (lhs, rhs) -> {
        int result;
        int groupStatusRhs;
        int groupStatusLhs;

        if (lhs.isOwner() != null && lhs.isOwner()) {
            groupStatusLhs = Group.GROUP_STATUS_OWNER;
        } else {
            if (lhs.isMember() != null && lhs.isMember()) {
                if (lhs.getGroupType() == Group.DISCUSSION_KEY) {
                    groupStatusLhs = Group.GROUP_STATUS_FOLLOWER;
                } else {
                    groupStatusLhs = Group.GROUP_STATUS_MEMBER;
                }
            } else if (lhs.isFollowing() != null && lhs.isFollowing()) {
                groupStatusLhs = Group.GROUP_STATUS_FOLLOWER;
            } else {
                groupStatusLhs = Group.GROUP_STATUS_NOT_ASSOCIATED;
            }
        }

        if (rhs.isOwner() != null && rhs.isOwner()) {
            groupStatusRhs = Group.GROUP_STATUS_OWNER;
        } else {
            if (rhs.isMember() != null && rhs.isMember()) {
                if (rhs.getGroupType() == Group.DISCUSSION_KEY) {
                    groupStatusRhs = Group.GROUP_STATUS_FOLLOWER;
                } else {
                    groupStatusRhs = Group.GROUP_STATUS_MEMBER;
                }
            } else if (rhs.isFollowing() != null && rhs.isFollowing()) {
                groupStatusRhs = Group.GROUP_STATUS_FOLLOWER;
            } else {
                groupStatusRhs = Group.GROUP_STATUS_NOT_ASSOCIATED;
            }
        }

        if (groupStatusLhs < groupStatusRhs) {
            result = -1;
        } else if (groupStatusRhs < groupStatusLhs) {
            result = 1;
        } else {
            result = 0;
        }

        return result;
    };

    private static final ArrayList<Comparator<Group>> GROUP_COMPARATORS = new ArrayList<>();

    public static final Comparator<Group> MASTER_GROUP_COMPARATOR = (lhs, rhs) -> {
        int result = 0;
        Iterator<Comparator<Group>> it = GROUP_COMPARATORS.iterator();
        while (result == 0 && it.hasNext()) {
            Comparator<Group> comparator = it.next();
            result = comparator.compare(lhs, rhs);
        }
        return result;
    };

    static {
        GROUP_COMPARATORS.add(CONX2SHARE_COMPARATOR);
        GROUP_COMPARATORS.add(GROUP_STATUS_COMPARATOR);
        GROUP_COMPARATORS.add(GROUP_ALPHA_COMPARATOR);
    }
}