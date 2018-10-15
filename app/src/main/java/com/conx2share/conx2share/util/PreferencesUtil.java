package com.conx2share.conx2share.util;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.conx2share.conx2share.model.AuthUser;
import com.conx2share.conx2share.model.InvitationState;
import com.conx2share.conx2share.network.models.User;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.transform.sax.SAXTransformerFactory;

@Singleton
public class PreferencesUtil {

    private static final String TAG = PreferencesUtil.class.getSimpleName();

    public static final int NO_SAY_NO_GROUP = -1;
    public static final String GCM_VERSION = "gcm_version";
    public static final String DEVICE_TOKEN = "device_token";
    public static final String REG_DEVICE_ID = "reg_device_id";
    private static final String AUTH_TOKEN = "auth_token";
    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    private static final String LAST_EMAIL = "last_email";
    private static final String PASSWORD = "password";
    private static final String AUTH_USER = "auth_user";
    private static final String MESSAGING_TIME_VALUE = "messaging_timer_value";
    private static final String SAY_NO_GROUP_ID = "group_id";
    private static final String SAY_NO_GROUP_INVITATION_STATE = "invitation_state";
    private static final String SAY_NO_GROUP_DONT_SHOW_AGAIN = "dont_show_me_again";
    private static final String FRIENDS_BAR_DONT_SHOW_AGAIN = "friends_bar_dont_show_me_again";


    @Inject
    SharedPreferences sharedPreferences;

    public Boolean getTokenSent() {
        return sharedPreferences.getBoolean(Statics.SENT_TOKEN_TO_SERVER, false);
    }

    public void setTokenSent(Boolean tokenSent) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Statics.SENT_TOKEN_TO_SERVER, tokenSent);
        editor.apply();
    }

    public String getDeviceToken() {
        return sharedPreferences.getString(DEVICE_TOKEN, null);
    }

    public void setRegisteredDeviceId(int deviceId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(REG_DEVICE_ID, deviceId);
        editor.apply();
    }

    public int getRegisteredDeviceId() {
        return sharedPreferences.getInt(REG_DEVICE_ID, 0);
    }

    public void setDeviceToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DEVICE_TOKEN, token);
        editor.apply();
    }

    public Integer getGCMTokenAppVersion() {
        return sharedPreferences.getInt(GCM_VERSION, 0);
    }

    public void setGCMTokenAppVersion(Integer version) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(GCM_VERSION, version);
        editor.apply();
    }

    public String getAuthToken() {
        return sharedPreferences.getString(AUTH_TOKEN, null);
    }

    public void setAuthToken(String token) {
        sharedPreferences.edit().putString(AUTH_TOKEN, token).apply();
    }

    public String getUsername() {
        return sharedPreferences.getString(USERNAME, null);
    }

    public void setUsername(String username) {
        sharedPreferences.edit().putString(USERNAME, username).apply();
    }

    public String getEmail() {
        return sharedPreferences.getString(EMAIL, null);
    }

    public void setEmail(String email) {
        sharedPreferences.edit().putString(EMAIL, email).apply();
    }

    public String getLastEmail() {
        return sharedPreferences.getString(LAST_EMAIL, "");
    }

    public void setLastEmail(String email) {
        sharedPreferences.edit().putString(LAST_EMAIL, email).apply();
    }

    public String getPassword() {
        return sharedPreferences.getString(PASSWORD, null);
    }

    public void setPassword(String password) {
        sharedPreferences.edit().putString(PASSWORD, password).apply();
    }

    public AuthUser getAuthUser() {
        String json = sharedPreferences.getString(AUTH_USER, null);
        return AuthUser.fromJsonString(json);
    }

    public void setAuthUser(AuthUser authUser) {
        sharedPreferences.edit().putString(AUTH_USER, authUser.toJsonString()).apply();
    }

    public void setAuthUser(User user) {
        AuthUser authUser = new AuthUser(user.getId(), user.getBirthday(), user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail(), getAuthToken(), user.getAvatar(), user.getPlan());
        setAuthUser(authUser);
    }

    public void setSayNoGroupId(int sayNoGroupId) {
        sharedPreferences.edit().putInt(SAY_NO_GROUP_ID, sayNoGroupId).apply();
    }

    public int getSayNoGroupId() {
        return sharedPreferences.getInt(SAY_NO_GROUP_ID, NO_SAY_NO_GROUP);
    }

    public void setSayNoInvitationState(@NonNull InvitationState invitationState) {
        sharedPreferences.edit().putString(SAY_NO_GROUP_INVITATION_STATE, invitationState.name()).apply();
    }

    public InvitationState getSayNoInvitationState() {
        String invitationStateStr = sharedPreferences.getString(SAY_NO_GROUP_INVITATION_STATE, null);
        return invitationStateStr != null ? InvitationState.valueOf(invitationStateStr) : null;
    }

    public void setSayNoDontShowAgain(boolean isDontShowAgain) {
        sharedPreferences.edit()
                .putBoolean(SAY_NO_GROUP_DONT_SHOW_AGAIN, isDontShowAgain)
                .apply();
    }

    public boolean isSayNoDontShowAgain() {
        return sharedPreferences.getBoolean(SAY_NO_GROUP_DONT_SHOW_AGAIN, false);
    }

    public void setFriendsBarDontShowAgain(boolean isDontShowAgain) {
        sharedPreferences.edit()
                .putBoolean(FRIENDS_BAR_DONT_SHOW_AGAIN, isDontShowAgain)
                .apply();
    }

    public boolean isFriendsDontShowAgain() {
        return sharedPreferences.getBoolean(FRIENDS_BAR_DONT_SHOW_AGAIN, false);
    }

    /**
     * Returns position in timer spinner for last value used
     */
    public int getMessagingTimerValue() {
        // TODO: get a context in this class so that we can stop relying on these hardcoded assumptions about how many choices there are
        return sharedPreferences.getInt(MESSAGING_TIME_VALUE, 4);
    }

    /**
     * Stores position in timer spinner for messaging timer
     *
     * @param value position in spinner of last used timeout
     */
    public void setMessagingTimeValue(int value) {
        // TODO: get a context in this class so that we can stop relying on these hardcoded assumptions about how many choices there are
        if (value >= 0 && value <= 4) {
            sharedPreferences.edit().putInt(MESSAGING_TIME_VALUE, value).apply();
        }
    }

    public void clearPreferences() {
        sharedPreferences.edit()
                .remove(GCM_VERSION)
                .remove(DEVICE_TOKEN)
                .remove(REG_DEVICE_ID)
                .remove(AUTH_TOKEN)
                .remove(USERNAME)
                .remove(EMAIL)
                .remove(MESSAGING_TIME_VALUE)
                .remove(PASSWORD)
                .remove(AUTH_USER)
                .remove(SAY_NO_GROUP_ID)
                .remove(SAY_NO_GROUP_INVITATION_STATE)
                .remove(SAY_NO_GROUP_DONT_SHOW_AGAIN)
                .remove(FRIENDS_BAR_DONT_SHOW_AGAIN)
                .remove(Statics.SENT_TOKEN_TO_SERVER)
                .apply();
    }
}
