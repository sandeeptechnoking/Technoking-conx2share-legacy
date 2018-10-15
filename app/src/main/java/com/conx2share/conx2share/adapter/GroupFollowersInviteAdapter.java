package com.conx2share.conx2share.adapter;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.network.models.User;
import com.conx2share.conx2share.ui.view.AvatarImageView;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class GroupFollowersInviteAdapter extends ArrayAdapter<User> {

    private static final String TAG = GroupFollowersInviteAdapter.class.getSimpleName();

    private LayoutInflater mLayoutInflater;

    private GroupFollowersInviteCallbacks mCallback;

    private Integer mAuthUserId;

    public GroupFollowersInviteAdapter(Context context, Fragment fragment, ArrayList<User> users, Integer authUserId) {

        super(context, R.layout.search_friend_list_item, users);

        mLayoutInflater = LayoutInflater.from(context);
        mAuthUserId = authUserId;

        try {
            mCallback = (GroupFollowersInviteCallbacks) fragment;
        } catch (ClassCastException e) {
            throw new ClassCastException("Does not implement GroupFollowersInviteCallbacks");
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder vh;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.search_friend_list_item, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = ((ViewHolder) convertView.getTag());
            vh.resetViews(convertView);
        }

        User user = getItem(position);
        vh.searchUserPhoto.initView(user);

        vh.checkBox.setOnClickListener(v -> {
            vh.checkBox.setChecked(true);
            vh.checkBox.setClickable(false);
            mCallback.onCheckboxToInviteUserToFollowGroupClicked(position);
        });

        Log.d(TAG, user.getFirstName() + " " + user.getLastName() + " has id " + user.getId() + " and has a group status of " + user.getGroupStatus());
        String firstName;
        String lastName;
        if (user.getFirstName() != null) {
            firstName = user.getFirstName().trim();
        } else {
            Log.w(TAG, "First name was null");
            firstName = user.getFirstName();
        }
        if (user.getLastName() != null) {
            lastName = user.getLastName().trim();
        } else {
            Log.w(TAG, "Last name was null");
            lastName = user.getLastName();
        }

        Log.d(TAG, firstName + " " + lastName + " has group_status of " + user.getGroupStatus() + " and follow_status of " + user.getGroupFollowStatus());
        vh.searchUserName.setText(firstName + " " + lastName);

        vh.searchUserHandle.setText("@" + user.getUsername());

        if (user.getGroupStatus().equals("pending") || user.getGroupStatus().equals("accepted")) {
            vh.checkBox.setClickable(false);
            vh.checkBox.setChecked(true);
        } else if (user.getGroupFollowStatus()) {
            vh.checkBox.setClickable(false);
            vh.checkBox.setChecked(true);
        } else if (user.getId() == mAuthUserId) {
            vh.checkBox.setClickable(false);
            vh.checkBox.setChecked(true);
        } else {
            vh.checkBox.setClickable(true);
            vh.checkBox.setChecked(false);
        }

        return convertView;
    }

    public interface GroupFollowersInviteCallbacks {
        void onCheckboxToInviteUserToFollowGroupClicked(int position);
    }

    private class ViewHolder {

        AvatarImageView searchUserPhoto;
        TextView searchUserName;
        CheckBox checkBox;
        TextView searchUserHandle;

        public ViewHolder(View view) {
            setupViews(view);
        }

        private void setupViews(View view) {
            searchUserPhoto = (AvatarImageView) view.findViewById(R.id.search_user_photo);
            searchUserName = (TextView) view.findViewById(R.id.search_user_name);
            checkBox = (CheckBox) view.findViewById(R.id.user_is_friend_checkbox);
            searchUserHandle = (TextView) view.findViewById(R.id.search_user_handle);
        }

        public void resetViews(View view) {
            searchUserPhoto = null;
            searchUserName = null;
            checkBox = null;
            searchUserHandle = null;
            setupViews(view);
        }
    }
}
