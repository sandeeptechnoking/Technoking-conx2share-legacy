package com.conx2share.conx2share.adapter;

import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.network.models.User;
import com.conx2share.conx2share.ui.view.AvatarImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
//import butterknife.InjectView;


public class AddFriendsAdapter extends RecyclerView.Adapter<AddFriendsAdapter.ViewHolder> {

    private static final long CLICK_TIME_INTERVAL = 600;
    List<User> users;
    OnUserClick onClickListener;
    private long mLastClickTime = SystemClock.elapsedRealtime();

    public AddFriendsAdapter(List<User> users, @NonNull OnUserClick onClickListener) {
        this.users = users;
        this.onClickListener = onClickListener;
    }

    @Override
    public AddFriendsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_add_friends, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AddFriendsAdapter.ViewHolder viewHolder, int i) {
        viewHolder.addFriendCheckbox.setOnCheckedChangeListener(null);
        User user = users.get(i);
        viewHolder.addFriendAvatar.initView(user);
        viewHolder.addFriendName.setText(user.getDisplayName());
        viewHolder.addFriendUsername.setText("@".concat(user.getUsername()));
        viewHolder.addFriendCheckbox.setChecked(user.getIsFavorite());
        viewHolder.addFriendCheckbox.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    if (isClickAllowed()) {
                        onClickListener.checkedChanged(user.getId(), isChecked);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.add_friend_avatar)
        AvatarImageView addFriendAvatar;
        @BindView(R.id.add_friend_name)
        TextView addFriendName;
        @BindView(R.id.add_friend_username)
        TextView addFriendUsername;
        @BindView(R.id.add_friend_checkbox)
        CheckBox addFriendCheckbox;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface OnUserClick {
        void checkedChanged(int userId, boolean isCheckedNow);
    }

    private boolean isClickAllowed() {
        long now = SystemClock.elapsedRealtime();
        if (now - mLastClickTime > CLICK_TIME_INTERVAL) {
            mLastClickTime = now;
            return true;
        } else return false;
    }
}
