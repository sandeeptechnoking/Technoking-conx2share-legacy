package com.conx2share.conx2share.adapter;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.model.UserAvatar;
import com.conx2share.conx2share.model.Users;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.network.models.User;
import com.conx2share.conx2share.ui.view.AvatarImageView;
import com.conx2share.conx2share.util.PrivilegeChecker;
import com.nispok.snackbar.Snackbar;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class UsersAdapter extends BaseAdapter {

    public static String TAG = UserAvatar.class.getSimpleName();

    private Context mContext;

    private Users mUsers;

    private LayoutInflater mLayoutInflater;

    private NetworkClient mNetworkClient;

    private CheckBox userIsFriendCheckbox;

    private int mAuthUserId;

    private UserAdapterCallbacks mCallbacks;

    public UsersAdapter(Context context, UserAdapterCallbacks callbacks, NetworkClient networkClient, Users users, int authUserId) {
        mCallbacks = callbacks;
        mUsers = users;
        mContext = context;
        mAuthUserId = authUserId;
        mLayoutInflater = LayoutInflater.from(this.mContext);
        mNetworkClient = networkClient;
    }

    @Override
    public int getCount() {
        return mUsers.getUsers().size();
    }

    @Override
    public Object getItem(int position) {
        return mUsers.getUsers().get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.search_friend_list_item, null);
        }

        if (position >= getCount() - 1) {
            mCallbacks.onNearingEndOfList();
        }

        TextView searchUserName = (TextView) convertView.findViewById(R.id.search_user_name);
        userIsFriendCheckbox = (CheckBox) convertView.findViewById(R.id.user_is_friend_checkbox);
        AvatarImageView searchUserPhoto = (AvatarImageView) convertView.findViewById(R.id.search_user_photo);
        TextView handleView = (TextView) convertView.findViewById(R.id.search_user_handle);

        final User user = mUsers.getUsers().get(position);

        searchUserName.setText(user.getFirstName() + " " + user.getLastName());
        handleView.setText("@".concat(user.getUsername()));

        searchUserPhoto.initView(user);

        if (user.getId() == mAuthUserId || PrivilegeChecker.isConx2ShareUser(String.valueOf(user.getId()))) {
            userIsFriendCheckbox.setChecked(true);
            userIsFriendCheckbox.setClickable(false);
        } else {
            userIsFriendCheckbox.setClickable(true);
            if (user.getIsFollowing()) {
                userIsFriendCheckbox.setChecked(true);
            } else {
                userIsFriendCheckbox.setChecked(false);
            }
        }

        userIsFriendCheckbox.setOnClickListener(view -> {
            if (userIsFriendCheckbox.isChecked()) {
                if (user.getId() != mAuthUserId) {
                    new FollowUserAsync(userIsFriendCheckbox, user).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(user.getId()));
                }
            } else {
                if (user.getId() != mAuthUserId) {
                    new UnFollowUserAsync(userIsFriendCheckbox, user).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(user.getId()));
                }
            }
        });

        return convertView;
    }

    public interface UserAdapterCallbacks {

        void onNearingEndOfList();
    }

    private class FollowUserAsync extends AsyncTask<String, Void, Boolean> {

        private CompoundButton mCheckBox;

        private User mUser;

        public FollowUserAsync(CompoundButton checkBox, User user) {
            mCheckBox = checkBox;
            mUser = user;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            return mNetworkClient.followUser(params[0]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (mContext != null) {
                super.onPostExecute(aBoolean);
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mCheckBox.getWindowToken(), 0);
                if (aBoolean != null && aBoolean) {
                    Activity activity = (Activity) mContext;
                    Snackbar.with(mContext).text(mContext.getString(R.string.following)).show(activity);
                    mUser.setIsFollowing(true);
                    notifyDataSetChanged();
                } else {
                    Activity activity = (Activity) mContext;
                    Snackbar.with(mContext).text(mContext.getString(R.string.following_failed_try_again)).show(activity);
                    mUser.setIsFollowing(false);
                    mCheckBox.setChecked(false);
                }
            }
        }
    }

    private class UnFollowUserAsync extends AsyncTask<String, Void, Boolean> {

        private CompoundButton mCheckBox;

        private User mUser;

        public UnFollowUserAsync(CompoundButton checkBox, User user) {
            mCheckBox = checkBox;
            mUser = user;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            return mNetworkClient.unfollowUser(params[0]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (mContext != null) {
                super.onPostExecute(aBoolean);
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mCheckBox.getWindowToken(), 0);
                if (aBoolean != null && aBoolean) {
                    Activity activity = (Activity) mContext;
                    Snackbar.with(mContext).text(mContext.getString(R.string.unfollowed)).show(activity);
                    mUser.setIsFollowing(false);
                    notifyDataSetChanged();
                } else {
                    Activity activity = (Activity) mContext;
                    Snackbar.with(mContext).text(mContext.getString(R.string.unfollow_failed)).show(activity);
                    mUser.setIsFollowing(true);
                    mCheckBox.setChecked(true);
                }
            }
        }
    }
}
