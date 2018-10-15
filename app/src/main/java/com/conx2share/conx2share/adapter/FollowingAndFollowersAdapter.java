package com.conx2share.conx2share.adapter;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.network.models.User;
import com.conx2share.conx2share.ui.view.AvatarImageView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class FollowingAndFollowersAdapter extends ArrayAdapter<User> {

    public static final String TAG = FollowingAndFollowersAdapter.class.getSimpleName();

    private FollowingAndFollowersAdapterCallbacks mCallbacks;

    private LayoutInflater mLayoutInflater;

    public FollowingAndFollowersAdapter(Context context, FollowingAndFollowersAdapterCallbacks callbacks, ArrayList<User> users) {
        super(context, R.layout.message_friends_list_item, users);
        mCallbacks = callbacks;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (position >= getCount() - 1) {
            mCallbacks.onNearingEndOfList();
        }

        ViewHolder vh;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.message_friends_list_item, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = ((ViewHolder) convertView.getTag());
            vh.resetViews(convertView);
        }

        User user = getItem(position);

        vh.friendAvatar.initView(user);
        vh.friendName.setText(user.getFirstName() + " " + user.getLastName());
        vh.handleView.setText("@".concat(user.getUsername()));

        return convertView;
    }

    public interface FollowingAndFollowersAdapterCallbacks {
        void onNearingEndOfList();
    }

    private class ViewHolder {

        TextView friendName;
        AvatarImageView friendAvatar;
        TextView handleView;

        public ViewHolder(View view) {
            setupViews(view);
        }

        private void setupViews(View view) {
            friendName = (TextView) view.findViewById(R.id.friend_name);
            friendAvatar = (AvatarImageView) view.findViewById(R.id.friend_avatar);
            handleView = (TextView) view.findViewById(R.id.friend_handle);
        }

        public void resetViews(View view) {
            friendName = null;
            friendAvatar = null;
            handleView  =null;
            setupViews(view);
        }
    }
}
