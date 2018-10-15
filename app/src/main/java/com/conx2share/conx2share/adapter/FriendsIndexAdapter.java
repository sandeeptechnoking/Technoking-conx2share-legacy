package com.conx2share.conx2share.adapter;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.network.models.User;
import com.conx2share.conx2share.ui.view.AvatarImageView;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class FriendsIndexAdapter extends ArrayAdapter<User> {

    public static final String TAG = FriendsIndexAdapter.class.getSimpleName();

    private LayoutInflater mLayoutInflater;

    public FriendsIndexAdapter(ArrayList<User> friends, Context context, Fragment fragment) {
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

        User user = getItem(position);

        // don't know how it related to the new design
//        if (user.getIsFavorite()) {
//            vh.friendAvatar.setBorderColor(convertView.getResources().getColor(R.color.conx_teal));
//            vh.friendAvatar.setBorderWidth(3f);
//        } else {
//            vh.friendAvatar.setBorderColor(convertView.getResources().getColor(R.color.unread_messages_gray));
//            vh.friendAvatar.setBorderWidth(1f);
//        }

        vh.friendAvatar.initView(user);
        vh.friendName.setText(user.getFirstName() + " " + user.getLastName());
        vh.friendHandle.setText("@".concat(user.getUsername()));

        return convertView;
    }

    private class ViewHolder {

        TextView friendName;
        AvatarImageView friendAvatar;
        TextView friendHandle;

        public ViewHolder(View view) {
            setupViews(view);
        }

        private void setupViews(View view) {
            friendName = (TextView) view.findViewById(R.id.friend_name);
            friendAvatar = (AvatarImageView) view.findViewById(R.id.friend_avatar);
            friendHandle = (TextView) view.findViewById(R.id.friend_handle);
        }

        public void resetViews(View view) {
            friendName = null;
            friendAvatar = null;
            friendHandle = null;
            setupViews(view);
        }
    }
}
