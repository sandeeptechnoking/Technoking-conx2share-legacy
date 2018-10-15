package com.conx2share.conx2share.adapter;


import com.conx2share.conx2share.R;
import com.conx2share.conx2share.model.Friend;
import com.conx2share.conx2share.ui.view.AvatarImageView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MessageFriendsAdapter extends ArrayAdapter<Friend> {

    public static final String TAG = MessageFriendsAdapter.class.getSimpleName();

    private LayoutInflater mLayoutInflater;

    public MessageFriendsAdapter(ArrayList<Friend> friends, Context context) {
        super(context, R.layout.message_friends_list_item, friends);

        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder vh;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.message_friends_list_item, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = ((ViewHolder) convertView.getTag());
            vh.resetViews(convertView);
        }

        Friend friend = getItem(position);
        vh.friendAvatar.initView(friend);
        vh.friendName.setText(friend.getFriendFirstName() + " " + friend.getFriendLastName());
        vh.handleView.setText("@".concat(friend.getHandle()));

        return convertView;
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
            handleView = null;
            setupViews(view);
        }
    }
}