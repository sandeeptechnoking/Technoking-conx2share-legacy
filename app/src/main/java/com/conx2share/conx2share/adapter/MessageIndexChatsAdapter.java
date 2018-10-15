package com.conx2share.conx2share.adapter;


import com.conx2share.conx2share.network.models.User;
import com.conx2share.conx2share.ui.view.AvatarImageView;
import com.google.gson.Gson;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.model.Chat;
import com.conx2share.conx2share.util.PreferencesUtil;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.ButterKnife;
//import butterknife.InjectView;

import roboguice.inject.InjectView;

public class MessageIndexChatsAdapter extends BaseAdapter {

    public static final String TAG = MessageIndexChatsAdapter.class.getSimpleName();

    private ArrayList<Chat> mChats;

    private LayoutInflater mLayoutInflater;

    private Context mContext;

    private PreferencesUtil mPreferencesUtil;

    public MessageIndexChatsAdapter(Context context, ArrayList<Chat> chats, PreferencesUtil preferencesUtil) {
        mChats = chats;
        mContext = context;
        mPreferencesUtil = preferencesUtil;
        mLayoutInflater = LayoutInflater.from(this.mContext);
    }

    @Override
    public int getCount() {
        return mChats.size();
    }

    @Override
    public Chat getItem(int position) {
        return mChats.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.message_index_chats_item, null);
        }

        Chat chat = getItem(position);

        int userPosition;
        if (chat.getUsers().get(0).getId() != mPreferencesUtil.getAuthUser().getId() || chat.getUsers().size() == 1) {
            userPosition = 0;
        } else {
            userPosition = 1;
        }

        Holder holder = new Holder(convertView);
        User user = chat.getUsers().get(userPosition);
        holder.userImage.initView(user);
        if (chat.getMissedMessages() == 0) {
            holder.userName.setText(user.getFirstName().trim() + " " + user.getLastName().trim());
        } else {
            holder.userName.setText(user.getFirstName().trim() + " " + user.getLastName().trim() + " (" + chat.getMissedMessages() + ")");
        }

        holder.handle.setText("@".concat(user.getUsername()));

        Gson gson = new Gson();
        if (chat.getLatestMessage() == null) {
            holder.message.setText(mContext.getString(R.string.media));
        } else if (chat.getLatestMessage().getBody() == null || chat.getLatestMessage().getBody().equals("")) {
            holder.message.setText(mContext.getString(R.string.media));
        } else {
            holder.message.setText(chat.getLatestMessage().getBody());
        }

        return convertView;
    }

    public class Holder {

        @InjectView(R.id.messageIndexItem_name)
        TextView userName;

        @InjectView(R.id.messageIndexItem_message)
        TextView message;

        @InjectView(R.id.messageIndexItem_picture)
        AvatarImageView userImage;

        @InjectView(R.id.messageIndexItem_handle)
        TextView handle;

        public Holder(View view) {
            ButterKnife.bind(this, view);
        }

    }

}
