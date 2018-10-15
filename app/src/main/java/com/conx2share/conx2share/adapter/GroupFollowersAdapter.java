package com.conx2share.conx2share.adapter;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.model.Group;
import com.conx2share.conx2share.network.models.User;
import com.conx2share.conx2share.ui.view.AvatarImageView;
import com.conx2share.conx2share.util.PopUpMenuUtil;
import com.conx2share.conx2share.util.SnackbarUtil;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;

public class GroupFollowersAdapter extends ArrayAdapter<User> {

    private static final String TAG = GroupFollowersAdapter.class.getSimpleName();

    private LayoutInflater mLayoutInflater;

    private GroupFollowersAdapterCallbacks mCallbacks;

    private Group mGroup;

    private int mAuthUserId;

    private SnackbarUtil mSnackbarUtil;

    private PopUpMenuUtil mPopUpMenuUtil = new PopUpMenuUtil();

    public GroupFollowersAdapter(Context context, GroupFollowersAdapterCallbacks callback, ArrayList<User> users, Group group, int authUserId) {
        super(context, R.layout.group_invite_list_item, users);
        mLayoutInflater = LayoutInflater.from(context);
        mGroup = group;
        mAuthUserId = authUserId;
        mCallbacks = callback;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder vh;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.group_invite_list_item, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = ((ViewHolder) convertView.getTag());
            vh.resetViews(convertView);
        }

        User user = getItem(position);

        if (user.isBlocked()) {
            vh.groupInviteUserLayout.setBackgroundColor(Color.parseColor("#D1D2D2"));
            vh.name.setTextColor(Color.parseColor("#8B8B8B"));
        } else {
            vh.groupInviteUserLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
            vh.name.setTextColor(Color.parseColor("#FD902C"));
        }

        vh.name.setText(user.getFirstName() + " " + user.getLastName());
        vh.handleView.setText("@".concat(user.getUsername()));
        vh.avatar.initView(user);
        String avatarUrl = user.getAvatarUrl();
        if (avatarUrl != null) {
            if (user.isBlocked()) {
                ColorMatrix matrix = new ColorMatrix();
                matrix.setSaturation(0);
                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                vh.avatar.setColorFilter(filter);
            }
        }

        if (mGroup.getCreatorId().equals(user.getId())) {
            vh.ownerSign.setVisibility(View.VISIBLE);
        } else {
            vh.ownerSign.setVisibility(View.GONE);
        }

        if (mAuthUserId == mGroup.getCreatorId()) {
            if (user.getId() != mAuthUserId) {
                vh.optionCarrot.setVisibility(View.VISIBLE);
            } else {
                vh.optionCarrot.setVisibility(View.GONE);
            }
        } else {
            vh.optionCarrot.setVisibility(View.GONE);
        }

        vh.optionCarrot.setOnClickListener(v -> {

            PopupMenu popupMenu = new PopupMenu(getContext(), vh.optionCarrot);

            if (mGroup.getCreatorId().equals(mAuthUserId)) {
                if (user.isBlocked()) {
                    mPopUpMenuUtil.showPopUpMenu(popupMenu, R.menu.group_followers_menu_blocked);
                } else {
                    mPopUpMenuUtil.showPopUpMenu(popupMenu, R.menu.group_followers_menu_unblocked);
                }
            } else {
                mSnackbarUtil.showSnackBarWithoutAction(getContext(), R.string.you_cant_do_anything_with_this_group_member);
            }

            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.group_follower_block:
                        mCallbacks.onBlockUserClicked(user);
                        break;
                    case R.id.group_follower_unblock:
                        mCallbacks.onUnblockUserClicked(user);
                        break;
                }
                return false;
            });
        });

        return convertView;
    }

    public interface GroupFollowersAdapterCallbacks {
        void onBlockUserClicked(User user);

        void onUnblockUserClicked(User user);
    }

    private class ViewHolder {

        AvatarImageView avatar;
        TextView name;
        LinearLayout ownerSign;
        ImageView optionCarrot;
        LinearLayout groupInviteUserLayout;
        TextView handleView;

        public ViewHolder(View view) {
            setupViews(view);
        }

        private void setupViews(View view) {
            avatar = (AvatarImageView) view.findViewById(R.id.group_invite_avatar);
            name = (TextView) view.findViewById(R.id.group_invite_name);
            ownerSign = (LinearLayout) view.findViewById(R.id.group_invite_owner_sign);
            optionCarrot = (ImageView) view.findViewById(R.id.group_invite_option_carrot);
            groupInviteUserLayout = (LinearLayout) view.findViewById(R.id.group_invite_user_layout);
            handleView = (TextView) view.findViewById(R.id.handle);
        }

        public void resetViews(View view) {
            avatar = null;
            name = null;
            ownerSign = null;
            optionCarrot = null;
            groupInviteUserLayout = null;
            handleView = null;
            setupViews(view);
        }
    }
}
