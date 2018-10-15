package com.conx2share.conx2share.util;

import com.conx2share.conx2share.BuildConfig;

public class PrivilegeChecker {

    public static final String TAG = PrivilegeChecker.class.getSimpleName();

    private PrivilegeChecker() {
        // prevent instantiation
    }

    /**
     * The Conx2Share user should not be able to be unfollowed
     *
     * @param userId user id to check
     * @return true if this user id belongs to the conx2share user
     */
    public static boolean isConx2ShareUser(String userId) {
        String privilegeId;
        if (BuildConfig.FLAVOR.equals("production")) {
            privilegeId = "5";      // conx2share user id is different on production for whatever reason
        } else {
            privilegeId = "1";
        }

        return privilegeId.equals(userId);
    }

    /**
     * Users should not be able to view members of the Conx2Share group
     *
     * @param groupId group id to check
     * @return true if this group id belongs to the conx2share group
     */
    public static boolean isConx2ShareGroup(int groupId) {
        return groupId == 1;
    }
}