package com.conx2share.conx2share.adapter;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.model.Friend;
import com.conx2share.conx2share.ui.feed.AddUsersActivity;
import com.conx2share.conx2share.ui.messaging.MessagingActivity;
import com.conx2share.conx2share.ui.view.AvatarImageView;

import java.util.ArrayList;

import static com.conx2share.conx2share.model.Friend.STAR_FRIEND_OBJECT_ID;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    public static final String FRIEND_ID = "favorite_friend_id";
    public static String TAG = FriendsAdapter.class.getSimpleName();

    private ArrayList<Friend> mDataset;
    private StartDragListener startDragListener;
    private Context mContext;

    public FriendsAdapter(ArrayList<Friend> myDataset, Context context, @NonNull StartDragListener startDragListener) {
        mDataset = myDataset;
        mContext = context;
        this.startDragListener = startDragListener;
        Log.i(TAG, "mDataset: " + myDataset.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friend_icon, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        AvatarImageView friendIcon = (AvatarImageView) viewHolder.friendIconView.findViewById(R.id.friend_icon);
        TextView unreadMessageCount = (TextView) viewHolder.friendIconView.findViewById(R.id.unread_message_count);
        TextView nameTextView = (TextView) viewHolder.friendIconView.findViewById(R.id.friend_name);

        Friend friend = mDataset.get(i);

        friendIcon.setOnClickListener(v -> {
            if (mDataset.get(i).getFriendId() == STAR_FRIEND_OBJECT_ID) {
                AddUsersActivity.startForAddFavorite(mContext);
            } else {
                MessagingActivity.start(mContext, mDataset.get(i));
            }
        });

        if (friend.getFriendId() == STAR_FRIEND_OBJECT_ID) {
            friendIcon.setImageResource(R.drawable.v_ic_star_plus);
        } else {
            friendIcon.setOnLongClickListener(v -> {
                friendIcon.startDrag(ClipData.newIntent("",
                        new Intent().putExtra(FRIEND_ID, friend.getFriendId())),
                        new View.DragShadowBuilder(v), v, 0);
                if (startDragListener != null){
                    startDragListener.onStartFriendsDrag();
                }
                return true;
            });
            friendIcon.initView(friend);
        }
        if (mDataset.get(i).getFriendMessageCount() > 0) {
            unreadMessageCount.setVisibility(View.VISIBLE);
            unreadMessageCount.setText(String.valueOf(mDataset.get(i).getFriendMessageCount()));
        } else {
            unreadMessageCount.setVisibility(View.GONE);
        }

        nameTextView.setText(friend.getFriendFirstName());
    }

    public int getItemCount() {
        return mDataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View friendIconView;

        public ViewHolder(View v) {
            super(v);
            friendIconView = v;
        }
    }

    public interface StartDragListener{
        void onStartFriendsDrag();
    }

}
